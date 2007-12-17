/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.edition.EditionDecorator;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.persistence.DataSourceLayerMemento;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.DefaultSourceManager;
import org.gdms.source.SourceManager;
import org.gdms.sql.instruction.Adapter;
import org.gdms.sql.instruction.CreateAdapter;
import org.gdms.sql.instruction.SelectAdapter;
import org.gdms.sql.instruction.UnionAdapter;
import org.gdms.sql.instruction.Utilities;
import org.gdms.sql.parser.Node;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.strategies.Strategy;
import org.gdms.sql.strategies.StrategyManager;

/**
 * Factory of DataSource implementations. It has method to register
 * DataSourceDefinitions and to create DataSource from this asociations.
 *
 * It's also possible to execute SQL statements with the executeSQL method.
 *
 * After using the DataSourceFactory it's strongly recomended to call
 * freeResources method.
 *
 * @author Fernando Gonzlez Corts
 */
public class DataSourceFactory {

	public final static int NORMAL = 0;

	public final static int STATUS_CHECK = 1;

	public final static int EDITABLE = 2;

	public final static int DEFAULT = EDITABLE | STATUS_CHECK;

	private File tempDir = new File(".");

	private StrategyManager sm = new StrategyManager();

	private List<DataSourceFactoryListener> listeners = new ArrayList<DataSourceFactoryListener>();

	private WarningListener warningListener = new NullWarningListener();

	private DefaultSourceManager sourceManager;

	private IndexManager indexManager;

	public DataSourceFactory() {
		initialize(System.getProperty("user.home") + File.separator + ".gdms",
				".");
	}

	public DataSourceFactory(String sourceInfoDir) {
		initialize(sourceInfoDir, ".");
	}

	public DataSourceFactory(String sourceInfoDir, String tempDir) {
		initialize(sourceInfoDir, tempDir);
	}

	/**
	 * Creates a data source defined by the DataSourceCreation object
	 *
	 * @param dsc
	 *
	 * @throws DriverException
	 *             if the source creation fails
	 */
	public DataSourceDefinition createDataSource(DataSourceCreation dsc)
			throws DriverException {
		return sourceManager.createDataSource(dsc);
	}

	/**
	 * Saves the specified contents into the source specified by the tableName
	 * parameter. A source must be registered with that name before
	 */
	public void saveContents(String tableName, DataSource contents)
			throws DriverException {
		sourceManager.saveContents(tableName, contents);
	}

	/**
	 * Constructs the stack of DataSources to achieve the functionality
	 * specified in the mode parameter
	 *
	 * @param ds
	 *            DataSource
	 * @param mode
	 *            opening mode
	 * @param indexes
	 *
	 * @return DataSource
	 * @throws DataSourceCreationException
	 */
	private DataSource getModedDataSource(DataSource ds, int mode) {
		DataSource ret = ds;

		// Decorator Stack, "()" means optional
		//
		// RightValueDecorator
		// (StatusCheckDecorator)
		// OCCounterDecorator
		// (UndoableDataSourceDecorator)
		// (EditionDecorator)
		// CacheDecorator

		ret = new CacheDecorator(ret);

		if ((mode & EDITABLE) == EDITABLE) {
			ret = new EditionDecorator(ret);
		}

		if ((mode & EDITABLE) != 0) {
			ret = new OCCounterDecorator(ret);
		}

		if ((mode & STATUS_CHECK) == STATUS_CHECK) {
			ret = new StatusCheckDecorator(ret);
		}

		return new RightValueDecorator(ret);
	}

	/**
	 * Gets a DataSource instance to access the file
	 *
	 * @param file
	 *            file to access
	 *
	 * @return
	 *
	 * @throws DriverLoadException
	 *             If there isn't a suitable driver for such a file
	 * @throws DataSourceCreationException
	 *             If the instance creation fails
	 * @throws DriverException
	 */
	public DataSource getDataSource(ObjectDriver object)
			throws DriverLoadException, DataSourceCreationException,
			DriverException {
		return getDataSource(object, DEFAULT);
	}

	/**
	 * Gets a DataSource instance to access the file
	 *
	 * @param file
	 *            file to access
	 * @param mode
	 *            To enable undo/redo operations UNDOABLE. NORMAL otherwise
	 * @return
	 *
	 * @throws DriverLoadException
	 *             If there isn't a suitable driver for such a file
	 * @throws DataSourceCreationException
	 *             If the instance creation fails
	 * @throws DriverException
	 */
	public DataSource getDataSource(ObjectDriver object, int mode)
			throws DriverLoadException, DataSourceCreationException,
			DriverException {
		return getDataSource(new ObjectSourceDefinition(object), mode);
	}

	/**
	 * Gets a DataSource instance to access the file
	 *
	 * @param file
	 *            file to access
	 *
	 * @return
	 *
	 * @throws DriverLoadException
	 *             If there isn't a suitable driver for such a file
	 * @throws DataSourceCreationException
	 *             If the instance creation fails
	 * @throws DriverException
	 */
	public DataSource getDataSource(File file) throws DriverLoadException,
			DataSourceCreationException, DriverException {
		return getDataSource(file, DEFAULT);
	}

	/**
	 * Gets a DataSource instance to access the file
	 *
	 * @param file
	 *            file to access
	 * @param mode
	 *            To enable undo/redo operations UNDOABLE. NORMAL otherwise
	 * @return
	 *
	 * @throws DriverLoadException
	 *             If there isn't a suitable driver for such a file
	 * @throws DataSourceCreationException
	 *             If the instance creation fails
	 * @throws DriverException
	 */
	public DataSource getDataSource(File file, int mode)
			throws DriverLoadException, DataSourceCreationException,
			DriverException {
		return getDataSource(new FileSourceDefinition(file), mode);
	}

	private DataSource getDataSource(DataSourceDefinition def, int mode)
			throws DriverLoadException, DataSourceCreationException {
		try {
			String name = sourceManager.nameAndRegister(def);
			return getDataSource(name, mode);
		} catch (NoSuchTableException e) {
			throw new RuntimeException(e);
		} catch (SourceAlreadyExistsException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets a DataSource instance to access the database source
	 *
	 * @param dbSource
	 *            source to access
	 *
	 * @return
	 *
	 * @throws DriverLoadException
	 *             If there isn't a suitable driver for such a file
	 * @throws DataSourceCreationException
	 *             If the instance creation fails
	 * @throws DriverException
	 */
	public DataSource getDataSource(DBSource dbSource)
			throws DriverLoadException, DataSourceCreationException,
			DriverException {
		return getDataSource(dbSource, DEFAULT);
	}

	/**
	 * Gets a DataSource instance to access the database source
	 *
	 * @param dbSource
	 *            source to access
	 * @param mode
	 *            To enable undo/redo operations UNDOABLE. NORMAL otherwise
	 * @return
	 *
	 * @throws DriverLoadException
	 *             If there isn't a suitable driver for such a file
	 * @throws DataSourceCreationException
	 *             If the instance creation fails
	 * @throws DriverException
	 */
	public DataSource getDataSource(DBSource dbSource, int mode)
			throws DriverLoadException, DataSourceCreationException,
			DriverException {
		return getDataSource(new DBTableSourceDefinition(dbSource), mode);
	}

	/**
	 * Dado el nombre de una tabla, se busca la fuente de datos asociada a dicha
	 * tabla y se obtiene un datasource adecuado en funcion del tipo de fuente
	 * de datos accediendo al subsistema de drivers
	 *
	 * @param tableName
	 *            Nombre de la fuente de datos
	 *
	 * @return DataSource que accede a dicha fuente
	 *
	 * @throws DriverLoadException
	 *             If the driver loading fails
	 * @throws NoSuchTableException
	 *             If the 'tableName' data source does not exists
	 * @throws DataSourceCreationException
	 *             If the DataSource could not be created
	 */
	public DataSource getDataSource(String tableName)
			throws DriverLoadException, NoSuchTableException,
			DataSourceCreationException {
		return getDataSource(tableName, null);
	}

	/**
	 * Dado el nombre de una tabla, se busca la fuente de datos asociada a dicha
	 * tabla y se obtiene un datasource adecuado en funcion del tipo de fuente
	 * de datos accediendo al subsistema de drivers
	 *
	 * @param tableName
	 *            Nombre de la fuente de datos
	 * @param mode
	 *            To enable undo/redo operations UNDOABLE. NORMAL otherwise
	 *
	 * @return DataSource que accede a dicha fuente
	 *
	 * @throws DriverLoadException
	 *             If the driver loading fails
	 * @throws NoSuchTableException
	 *             If the 'tableName' data source does not exists
	 * @throws DataSourceCreationException
	 *             If the DataSource could not be created
	 */
	public DataSource getDataSource(String tableName, int mode)
			throws DriverLoadException, NoSuchTableException,
			DataSourceCreationException {
		return getDataSource(tableName, null, mode);
	}

	/**
	 * Dado el nombre de una tabla, se busca la fuente de datos asociada a dicha
	 * tabla y se obtiene un datasource adecuado en funcion del tipo de fuente
	 * de datos accediendo al subsistema de drivers. Se utiliza internamente
	 * como nombre del DataSource el alias que se pasa como par�metro
	 *
	 * @param tableName
	 *            Nombre de la fuente de datos
	 * @param tableAlias
	 *            Alias que tiene el DataSource en una instrucci�n
	 *
	 * @return DataSource que accede a dicha fuente de datos si la fuente de
	 *         datos es alfanum�rica o SpatialDataSource si la fuente de datos
	 *         es espacial
	 *
	 * @throws DriverLoadException
	 *             If the driver loading fails
	 * @throws NoSuchTableException
	 *             If the 'tableName' data source does not exists
	 * @throws DataSourceCreationException
	 *             If the DataSource could not be created
	 */
	public DataSource getDataSource(String tableName, String tableAlias)
			throws NoSuchTableException, DriverLoadException,
			DataSourceCreationException {
		return getDataSource(tableName, tableAlias, DEFAULT);
	}

	/**
	 * Dado el nombre de una tabla, se busca la fuente de datos asociada a dicha
	 * tabla y se obtiene un datasource adecuado en funcion del tipo de fuente
	 * de datos accediendo al subsistema de drivers. Se utiliza internamente
	 * como nombre del DataSource el alias que se pasa como par�metro
	 *
	 * @param tableName
	 *            Nombre de la fuente de datos
	 * @param tableAlias
	 *            Alias que tiene el DataSource en una instrucci�n
	 * @param mode
	 *            To enable undo/redo operations UNDOABLE. NORMAL otherwise
	 *
	 * @return DataSource que accede a dicha fuente de datos si la fuente de
	 *         datos es alfanum�rica o SpatialDataSource si la fuente de datos
	 *         es espacial
	 *
	 * @throws DriverLoadException
	 *             If the driver loading fails
	 * @throws NoSuchTableException
	 *             If the 'tableName' data source does not exists
	 * @throws DataSourceCreationException
	 *             If the DataSource could not be created
	 */
	public DataSource getDataSource(String tableName, String tableAlias,
			int mode) throws NoSuchTableException, DriverLoadException,
			DataSourceCreationException {

		DataSource ds = sourceManager.getDataSource(tableName);
		ds = getModedDataSource(ds, mode);
		if (tableAlias != null) {
			ds = new AliasDecorator(ds, tableAlias);
		}

		return ds;
	}

	/**
	 * Creates a DataSource from a memento object with the specified opening
	 * mode
	 *
	 * @param m
	 *            memento
	 *
	 * @throws DataSourceCreationException
	 *             If the DataSource creation fails
	 * @throws NoSuchTableException
	 *             If the memento information is wrong
	 * @throws ExecutionException
	 *             If DataSource execution fails
	 */
	public DataSource getDataSource(Memento m) throws NoSuchTableException,
			DataSourceCreationException, ExecutionException {
		if (m instanceof DataSourceLayerMemento) {
			DataSourceLayerMemento mem = (DataSourceLayerMemento) m;

			return getDataSource(mem.getTableName(), mem.getTableAlias());
		} else {
			OperationLayerMemento mem = (OperationLayerMemento) m;

			return executeSQL(mem.getSql());
		}
	}

	/**
	 * A partir de una instrucci�n select se encarga de obtener el DataSource
	 * resultado de la ejecuci�n de dicha instrucci�n
	 *
	 * @param instr
	 *            Instrucci�n select origen del datasource
	 *
	 * @return DataSource que accede a los datos resultado de ejecutar la select
	 * @throws ExecutionException
	 */
	private DataSource getDataSource(String name, boolean wellKnownName,
			boolean register, SelectAdapter instr, int mode)
			throws ExecutionException {
		Strategy strategy = sm.getStrategy(instr);

		DataSource ret;

		ret = strategy.select(name, instr);
		if (ret != null) {
			if (register) {
				sourceManager.register(name, wellKnownName, ret, instr
						.getInstructionContext().getSql());
			}
			return getModedDataSource(ret, mode);
		} else {
			return null;
		}
	}

	/**
	 * A partir de una instrucci�n select se encarga de obtener el DataSource
	 * resultado de la ejecuci�n de dicha instrucci�n
	 *
	 * @param instr
	 *            Instrucci�n select origen del datasource
	 *
	 * @return DataSource que accede a los datos resultado de ejecutar la select
	 * @throws ExecutionException
	 */
	public DataSource getDataSource(SelectAdapter instr, int mode)
			throws ExecutionException {
		String name = getUID();
		return getDataSource(name, false, true, instr, mode);
	}

	/**
	 * Obtiene el DataSource resultado de ejecutar la instrucci�n de union
	 *
	 * @param instr
	 *            instrucci�n de union
	 * @param mode
	 *
	 * @throws ExecutionException
	 */
	private DataSource getDataSource(String name, boolean wellKnownName,
			boolean register, UnionAdapter instr, int mode)
			throws ExecutionException {
		Strategy strategy = sm.getStrategy(instr);

		DataSource ret;

		ret = strategy.union(name, instr);
		if (register) {
			sourceManager.register(name, wellKnownName, ret, instr
					.getInstructionContext().getSql());
		}
		return getModedDataSource(ret, mode);
	}

	DataSource executeSQL(String tableName, String sql) throws SyntaxException,
			DriverLoadException, NoSuchTableException, ExecutionException {
		return executeSQL(tableName, true, false, sql, NORMAL);
	}

	/**
	 * Executes the SQL using the NORMAL mode
	 *
	 * @param sql
	 * @return
	 * @throws SyntaxException
	 * @throws DriverLoadException
	 * @throws NoSuchTableException
	 * @throws ExecutionException
	 * @deprecated This method is in alpha version.
	 */
	public DataSource executeSQL(String sql) throws SyntaxException,
			DriverLoadException, NoSuchTableException, ExecutionException {
		String tableName = sourceManager.getUID();
		return executeSQL(tableName, false, true, sql, NORMAL);
	}

	/**
	 * Executes a SQL statement where the table names must be valid data source
	 * names.
	 *
	 * @param sql
	 *            sql statement
	 *
	 * @return DataSource con el resultado
	 *
	 * @throws SyntaxException
	 *             If instruction parsing fails
	 * @throws DriverLoadException
	 *             If a driver cannot be loaded
	 * @throws NoSuchTableException
	 *             If the instruction references a data source that doesn't
	 *             exist
	 * @throws ExecutionException
	 *             If the execution of the statement fails
	 */
	public DataSource executeSQL(String sql, int mode) throws SyntaxException,
			DriverLoadException, NoSuchTableException, ExecutionException {
		return executeSQL(getUID(), false, true, sql, mode);
	}

	/**
	 * Executes a SQL statement where the table names must be valid data source
	 * names.
	 *
	 * @param sql
	 *            sql statement
	 *
	 * @return DataSource con el resultado
	 *
	 * @throws SyntaxException
	 *             If instruction parsing fails
	 * @throws DriverLoadException
	 *             If a driver cannot be loaded
	 * @throws NoSuchTableException
	 *             If the instruction references a data source that doesn't
	 *             exist
	 * @throws ExecutionException
	 *             If the execution of the statement fails
	 */
	private DataSource executeSQL(String name, boolean wellKnownName,
			boolean register, String sql, int mode) throws SyntaxException,
			DriverLoadException, NoSuchTableException, ExecutionException {

		fireInstructionExecuted(sql);

		Adapter rootAdapter = getAdapter(sql);

		DataSource result = null;

		if (rootAdapter instanceof SelectAdapter) {
			result = getDataSource(name, wellKnownName, register,
					(SelectAdapter) rootAdapter, mode);
		} else if (rootAdapter instanceof UnionAdapter) {
			result = getDataSource(name, wellKnownName, register,
					(UnionAdapter) rootAdapter, mode);
		} else if (rootAdapter instanceof CreateAdapter) {
			executeSQL((CreateAdapter) rootAdapter);
		}

		return result;
	}

	private Adapter getAdapter(String sql) {
		if (!sql.trim().endsWith(";")) {
			sql += ";";
		}
		ByteArrayInputStream bytes = new ByteArrayInputStream(sql.getBytes());
		SQLEngine parser = new SQLEngine(bytes);

		try {
			parser.SQLStatement();
		} catch (ParseException e) {
			throw new SyntaxException(sql, e);
		}

		Node root = parser.getRootNode();
		Adapter rootAdapter = Utilities.buildTree(root.jjtGetChild(0), sql,
				this);

		Utilities.simplify(rootAdapter);
		return rootAdapter;
	}

	public String[] getSources(String sql) {
		Adapter rootAdapter = getAdapter(sql);

		if (rootAdapter instanceof SelectAdapter) {
			return ((SelectAdapter) rootAdapter).getSources();
		} else if (rootAdapter instanceof UnionAdapter) {
			return ((UnionAdapter) rootAdapter).getSources();
		} else {
			return new String[0];
		}
	}

	private void fireInstructionExecuted(String sql) {
		for (DataSourceFactoryListener listener : listeners) {
			listener.sqlExecuted(new SQLEvent(sql, this));
		}
	}

	private void executeSQL(CreateAdapter instr) throws ExecutionException {
		Strategy strategy = sm.getStrategy(instr);
		strategy.create(instr);
	}

	/**
	 * Frees all resources used during execution
	 *
	 * @throws DataSourceFinalizationException
	 *             If cannot free resources
	 */
	public void freeResources() throws DataSourceFinalizationException {

		sourceManager.shutdown();

		File[] tempFiles = tempDir.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().startsWith("gdms");
			}
		});

		for (int i = 0; i < tempFiles.length; i++) {
			tempFiles[i].delete();
		}
	}

	/**
	 * Initializes the system
	 *
	 * @param tempDir
	 *            temporary directory to write data
	 * @param tempDir
	 *
	 * @throws InitializationException
	 *             If the initialization fails
	 */
	private void initialize(String sourceInfoDir, String tempDir)
			throws InitializationException {
		try {
			sourceManager = new DefaultSourceManager(this, sourceInfoDir);

			indexManager = new IndexManager(this);
			indexManager.addIndex(new SpatialIndex());

			setTempDir(tempDir);

			Class.forName("org.hsqldb.jdbcDriver");

		} catch (ClassNotFoundException e) {
			throw new InitializationException(e);
		} catch (IOException e) {
			throw new InitializationException(e);
		}
	}

	public void setTempDir(String tempDir) {
		this.tempDir = new File(tempDir);

		if (!this.tempDir.exists()) {
			this.tempDir.mkdirs();
		}
	}

	/**
	 * Gets the URL of a file in the temporary directory. Does not creates any
	 * file
	 *
	 * @return String
	 */
	public String getTempFile() {
		return tempDir.getAbsolutePath() + File.separator + "gdms"
				+ System.currentTimeMillis();
	}

	public boolean addDataSourceFactoryListener(DataSourceFactoryListener e) {
		return listeners.add(e);
	}

	public boolean removeDataSourceFactoryListener(DataSourceFactoryListener o) {
		return listeners.remove(o);
	}

	public WarningListener getWarningListener() {
		return warningListener;
	}

	public void setWarninglistener(WarningListener listener) {
		this.warningListener = listener;
	}

	public String getUID() {
		return sourceManager.getUID();
	}

	public SourceManager getSourceManager() {
		return sourceManager;
	}

	public File getTempDir() {
		return tempDir;
	}

	public StrategyManager getSm() {
		return sm;
	}

	public List<DataSourceFactoryListener> getListeners() {
		return listeners;
	}

	public IndexManager getIndexManager() {
		return indexManager;
	}

	public void registerDataSource(String sourceName, DataSourceDefinition def)
			throws SourceAlreadyExistsException {
		sourceManager.register(sourceName, def);
	}

	public boolean exists(String sourceName) {
		return sourceManager.exists(sourceName);
	}

	public void remove(String sourceName) {
		sourceManager.remove(sourceName);
	}

}

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
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.DefaultSourceManager;
import org.gdms.source.SourceManager;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.Instruction;
import org.gdms.sql.strategies.SemanticException;

/**
 * Factory of DataSource implementations. It has method to register
 * DataSourceDefinitions and to create DataSource from this associations.
 *
 * It's also possible to execute SQL statements with the executeSQL method.
 *
 * After using the DataSourceFactory it's strongly recommended to call
 * freeResources method.
 *
 * @author Fernando Gonzalez Cortes
 */
public class DataSourceFactory {

	/**
	 * No editing capabilities, no status check
	 */
	public final static int NORMAL = 0;

	/**
	 * Checks that the source is opened before accessing it
	 */
	public final static int STATUS_CHECK = 1;

	/**
	 * Editing capabilities
	 */
	public final static int EDITABLE = 2;

	/**
	 * EDITABLE | STATUS_CHECK
	 */
	public final static int DEFAULT = EDITABLE | STATUS_CHECK;

	private File tempDir = new File(".");

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
	public DataSource getDataSource(ObjectDriver object) throws DriverException {
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
			throws DriverException {
		try {
			return getDataSource(new ObjectSourceDefinition(object), mode);
		} catch (DriverLoadException e) {
			throw new RuntimeException("bug!");
		} catch (DataSourceCreationException e) {
			throw new RuntimeException("bug!");
		}
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
	 * @throws SemanticException
	 * @throws ParseException
	 */
	public DataSource getDataSourceFromSQL(String sql)
			throws DriverLoadException, DataSourceCreationException,
			DriverException, ParseException, SemanticException {
		return getDataSourceFromSQL(sql, DEFAULT);
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
	 * @throws SemanticException
	 * @throws ParseException
	 */
	public DataSource getDataSourceFromSQL(String sql, int mode)
			throws DriverLoadException, DataSourceCreationException,
			DriverException, ParseException, SemanticException {
		SQLProcessor sqlProcessor = new SQLProcessor(this);
		Instruction instruction = sqlProcessor.prepareInstruction(sql);
		return getDataSource(new SQLSourceDefinition(instruction), mode);
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
	 * Returns a DataSource to access the source associated to the specified
	 * name
	 *
	 * @param tableName
	 *            source name
	 *
	 * @return DataSource
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
		return getDataSource(tableName, DEFAULT);
	}

	/**
	 * Returns a DataSource to access the source associated to the specified
	 * name
	 *
	 * @param tableName
	 *            source name
	 * @param mode
	 *            Any combination of DEFAULT, EDITABLE, NORMAL, STATUS_CHECK
	 *
	 * @return DataSource
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
		DataSource ds = sourceManager.getDataSource(tableName);
		ds = getModedDataSource(ds, mode);

		return ds;
	}

	/**
	 * Executes a SQL statement
	 *
	 * @param sql
	 * @throws ParseException
	 * @throws SemanticException
	 * @throws DriverException
	 * @throws ExecutionException
	 */
	public void executeSQL(String sql) throws ParseException,
			SemanticException, DriverException, ExecutionException {
		executeSQL(sql, DEFAULT);
	}

	/**
	 * Executes a SQL statement
	 *
	 * @param sql
	 *            sql statement
	 *
	 * @throws ParseException
	 *             If the sql is not well formed
	 * @throws SemanticException
	 *             If the instruction contains semantic errors: unknown or
	 *             ambiguous field references, operations with incompatible
	 *             types, etc.
	 * @throws DriverException
	 *             If there is a problem accessing the sources
	 * @throws ExecutionException
	 *             If there is a problem while executing the SQL
	 */
	public void executeSQL(String sql, int mode) throws ParseException,
			SemanticException, DriverException, ExecutionException {

		if (!sql.trim().endsWith(";")) {
			sql += ";";
		}

		fireInstructionExecuted(sql);

		SQLProcessor ag = new SQLProcessor(this);
		ag.execute(sql);
	}

	void fireInstructionExecuted(String sql) {
		for (DataSourceFactoryListener listener : listeners) {
			listener.sqlExecuted(new SQLEvent(sql, this));
		}
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
			sourceManager.init();

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

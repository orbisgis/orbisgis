/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
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
import org.gdms.data.indexes.BTreeIndex;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.indexes.RTreeIndex;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.wms.WMSSource;
import org.gdms.data.wms.WMSSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.DefaultSourceManager;
import org.gdms.source.SourceManager;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.Instruction;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;

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

	private File resultDir;

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
	 * 
	 * @param pm
	 */
	public void saveContents(String tableName, DataSource contents,
			IProgressMonitor pm) throws DriverException {
		sourceManager.saveContents(tableName, contents, pm);
	}

	/**
	 * Saves the specified contents into the source specified by the tableName
	 * parameter. A source must be registered with that name before
	 * 
	 * @param pm
	 */
	public void saveContents(String tableName, DataSource contents)
			throws DriverException {
		saveContents(tableName, contents, new NullProgressMonitor());
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
			return getDataSource(new ObjectSourceDefinition(object), mode,
					new NullProgressMonitor());
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
		return getDataSource(new FileSourceDefinition(file), mode,
				new NullProgressMonitor());
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
		return getDataSourceFromSQL(sql, DEFAULT, new NullProgressMonitor());
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
		return getDataSourceFromSQL(sql, mode, new NullProgressMonitor());
	}

	/**
	 * Gets a DataSource instance to access the file with the default mode
	 * 
	 * @param file
	 *            file to access
	 * @param pm
	 *            Instance that monitors the process. Can be null
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
	public DataSource getDataSourceFromSQL(String sql, IProgressMonitor pm)
			throws DriverLoadException, DataSourceCreationException,
			DriverException, ParseException, SemanticException {
		return getDataSourceFromSQL(sql, DEFAULT, pm);
	}

	/**
	 * Gets a DataSource instance to access the result of the SQL
	 * 
	 * @param file
	 *            file to access
	 * @param mode
	 *            To enable undo/redo operations UNDOABLE. NORMAL otherwise
	 * @param pm
	 *            Instance that monitors the process. Can be null
	 * @return The result of the instruction or null if the execution was
	 *         cancelled
	 * 
	 * @throws DriverLoadException
	 *             If there isn't a suitable driver for such a file
	 * @throws DataSourceCreationException
	 *             If the instance creation fails
	 * @throws DriverException
	 * @throws SemanticException
	 * @throws ParseException
	 */
	public DataSource getDataSourceFromSQL(String sql, int mode,
			IProgressMonitor pm) throws DriverLoadException,
			DataSourceCreationException, DriverException, ParseException,
			SemanticException {
		if (pm == null) {
			pm = new NullProgressMonitor();
		}
		SQLProcessor sqlProcessor = new SQLProcessor(this);
		Instruction instruction = sqlProcessor.prepareInstruction(sql);
		return getDataSource(instruction, mode, pm);
	}

	/**
	 * Gets a DataSource instance to access the result of the instruction
	 * 
	 * @param instruction
	 *            Instruction to evaluate. {@link SQLProcessor}
	 * @param mode
	 *            The DataSource mode {@link #EDITABLE} {@link #STATUS_CHECK}
	 *            {@link #NORMAL} {@link #DEFAULT}
	 * @param pm
	 *            To monitor progress and cancel
	 * 
	 * @return
	 * @throws DataSourceCreationException
	 */
	public DataSource getDataSource(Instruction instruction, int mode,
			IProgressMonitor pm) throws DataSourceCreationException {
		return getDataSource(new SQLSourceDefinition(instruction), mode, pm);
	}

	private DataSource getDataSource(DataSourceDefinition def, int mode,
			IProgressMonitor pm) throws DriverLoadException,
			DataSourceCreationException {
		try {
			String name = sourceManager.nameAndRegister(def);
			return getDataSource(name, mode, pm);
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
		return getDataSource(new DBTableSourceDefinition(dbSource), mode,
				new NullProgressMonitor());
	}

	/**
	 * Gets a DataSource instance to access the wms source
	 * 
	 * @param wmsSource
	 *            source to access
	 * @param mode
	 *            To enable undo/redo operations UNDOABLE. NORMAL otherwise
	 * @return
	 * 
	 * @throws DataSourceCreationException
	 *             If the instance creation fails
	 * @throws DriverException
	 */
	public DataSource getDataSource(WMSSource wmsSource, int mode)
			throws DataSourceCreationException, DriverException {
		return getDataSource(new WMSSourceDefinition(wmsSource), mode,
				new NullProgressMonitor());
	}

	/**
	 * Gets a DataSource instance to access the wms source with the
	 * {@link #DEFAULT} mode
	 * 
	 * @param wmsSource
	 *            source to access
	 * @return
	 * 
	 * @throws DataSourceCreationException
	 *             If the instance creation fails
	 * @throws DriverException
	 */
	public DataSource getDataSource(WMSSource wmsSource)
			throws DataSourceCreationException, DriverException {
		return getDataSource(new WMSSourceDefinition(wmsSource), DEFAULT,
				new NullProgressMonitor());
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
		return getDataSource(tableName, mode, new NullProgressMonitor());
	}

	private DataSource getDataSource(String tableName, int mode,
			IProgressMonitor pm) throws NoSuchTableException,
			DataSourceCreationException {
		if (pm == null) {
			pm = new NullProgressMonitor();
		}
		DataSource ds = sourceManager.getDataSource(tableName, pm);
		if (pm.isCancelled()) {
			ds = null;
		} else {
			ds = getModedDataSource(ds, mode);
		}

		return ds;
	}

	/**
	 * Executes a SQL statement
	 * 
	 * @param sql
	 * @param pm
	 * @throws ParseException
	 * @throws SemanticException
	 * @throws DriverException
	 * @throws ExecutionException
	 */
	public void executeSQL(String sql, IProgressMonitor pm)
			throws ParseException, SemanticException, DriverException,
			ExecutionException {
		executeSQL(sql, pm, DEFAULT);
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
		executeSQL(sql, new NullProgressMonitor(), DEFAULT);
	}

	/**
	 * Executes a SQL statement
	 * 
	 * @param sql
	 *            sql statement
	 * @param pm
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
	public void executeSQL(String sql, IProgressMonitor pm, int mode)
			throws ParseException, SemanticException, DriverException,
			ExecutionException {

		if (!sql.trim().endsWith(";")) {
			sql += ";";
		}

		fireInstructionExecuted(sql);

		SQLProcessor ag = new SQLProcessor(this);
		ag.execute(sql, pm);
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
			indexManager = new IndexManager(this);
			indexManager.addIndex(IndexManager.RTREE_SPATIAL_INDEX,
					RTreeIndex.class);
			indexManager.addIndex(IndexManager.BTREE_ALPHANUMERIC_INDEX,
					BTreeIndex.class);

			setTempDir(tempDir);
			setResultDir(new File(tempDir));

			Class.forName("org.hsqldb.jdbcDriver");

			sourceManager = new DefaultSourceManager(this, sourceInfoDir);
			sourceManager.init();

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
	 * Gets the path of a file in the temporary directory. Does not creates any
	 * file
	 * 
	 * @return String
	 */
	public String getTempFile() {
		String path;
		do {
			path = tempDir.getAbsolutePath() + File.separator + getUID();
		} while (new File(path).exists());

		return path;
	}

	/**
	 * Gets the path of a file in the temporary directory with the specified
	 * extension. Does not creates any file
	 * 
	 * @return String
	 */
	public String getTempFile(String extension) {
		String path;
		do {
			path = tempDir.getAbsolutePath() + File.separator + getUID() + "."
					+ extension;
		} while (new File(path).exists());

		return path;
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

	/**
	 * Sets the result directory. All SQL execution that implicitly creates a
	 * new source will create a GDMS source in this directory. Initially it's
	 * equal to the temporal directory
	 * 
	 * @param resultDir
	 */
	public void setResultDir(File resultDir) {
		this.resultDir = resultDir;
	}

	/**
	 * Gets the result directory.
	 * 
	 * @return
	 */
	public File getResultDir() {
		return resultDir;
	}

	/**
	 * Gets a new file in the results directory with "gdms" extension
	 * 
	 * @return
	 */
	public File getResultFile() {
		return getResultFile("gdms");
	}

	/**
	 * Get a new file in the results directory with the specified extension
	 * 
	 * @param extension
	 * @return
	 */
	public File getResultFile(String extension) {
		File file;
		do {
			file = new File(resultDir, getUID() + "." + extension);
		} while (file.exists());

		return file;
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

	/**
	 * Registers on the source manager associated to this factory the specified
	 * DataSourceDefinition with the specified name
	 * 
	 * @param name
	 * @param def
	 * @throws DriverException
	 * @throws SourceAlreadyExistsException
	 */
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

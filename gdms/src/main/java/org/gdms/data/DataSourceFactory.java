package org.gdms.data;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.gdms.data.command.UndoableDataSourceDecorator;
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
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.csvstring.CSVStringDriver;
import org.gdms.driver.dbf.DBFDriver;
import org.gdms.driver.h2.H2spatialDriver;
import org.gdms.driver.hsqldb.HSQLDBDriver;
import org.gdms.driver.shapefile.ShapefileDriver;
import org.gdms.sql.instruction.Adapter;
import org.gdms.sql.instruction.CreateAdapter;
import org.gdms.sql.instruction.CustomAdapter;
import org.gdms.sql.instruction.SelectAdapter;
import org.gdms.sql.instruction.TableNotFoundException;
import org.gdms.sql.instruction.UnionAdapter;
import org.gdms.sql.instruction.Utilities;
import org.gdms.sql.parser.Node;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.strategies.Strategy;
import org.gdms.sql.strategies.StrategyManager;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.driverManager.DriverManager;

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

	public final static int UNDOABLE = 4 | EDITABLE;

	public final static int DEFAULT = UNDOABLE | STATUS_CHECK;

	/**
	 * Asocia los nombres de las tablas con la informaci�n del origen de datos
	 */
	private HashMap<String, DataSourceDefinition> tableSource = new HashMap<String, DataSourceDefinition>();

	/** Associates a name with the operation layer DataSource with that name */
	private HashMap<String, DataSource> nameDataSource = new HashMap<String, DataSource>();

	private DriverManager dm = new DriverManager();

	private File tempDir = new File(".");

	private StrategyManager sm = new StrategyManager();

	private IndexManager indexManager;

	private HashMap<String, String> nameMapping = new HashMap<String, String>();

	private List<DataSourceFactoryListener> listeners = new ArrayList<DataSourceFactoryListener>();

	public DataSourceFactory() {
		initialize(".");
	}

	public DataSourceFactory(String tempDir) {
		initialize(tempDir);
	}

	/**
	 * Get's a unique id in the tableSource and nameOperationDataSource key sets
	 * 
	 * @return unique id
	 */
	public String getUID() {
		String name = "gdms" + System.currentTimeMillis();

		while (tableSource.get(name) != null) {
			name = "gdms" + System.currentTimeMillis();
		}

		return name;
	}

	/**
	 * Removes all associations between names and data sources.
	 * 
	 * @throws DriverException
	 *             If the resources could not be freed
	 */
	public void removeAllDataSources() {
		tableSource.clear();
		nameDataSource.clear();
	}

	/**
	 * Removes the association between the name and the data sources
	 * 
	 * @param ds
	 *            Name of the data source to remove
	 * 
	 */
	public void remove(DataSource ds) {
		remove(ds.getName());
	}

	/**
	 * Removes the association between the name and the data sources according
	 * to its name
	 * 
	 * @param name
	 *            Alias of the data source to remove
	 * 
	 */
	public void remove(String name) {
		if ((!tableSource.containsKey(name))
				&& (!nameDataSource.containsKey(name))) {
			throw new RuntimeException("No datasource with the name " + name);

		}
		tableSource.remove(name);
		nameDataSource.remove(name);
		List<String> namesToRemove = getNamesFor(name);
		for (String nameToRemove : namesToRemove) {
			nameMapping.remove(nameToRemove);
		}
		fireSourceRemoved(name);
	}

	private void fireSourceRemoved(String name) {
		for (DataSourceFactoryListener listener : listeners) {
			listener
					.sourceRemoved(new DataSourceFactoryEvent(name, null, this));
		}
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
		dsc.setDataSourceFactory(this);
		return dsc.create();
	}

	/**
	 * Saves the specified contents into the source specified by the tableName
	 * aparameter. The source have to be registered with that name before
	 */
	public void saveContents(String tableName, DataSource contents)
			throws DriverException {
		DataSourceDefinition dsd = tableSource.get(tableName);
		dsd.setDataSourceFactory(this);
		dsd.createDataSource(getDriver(dsd), contents);
	}

	/**
	 * A�ade una fuente de datos de objeto. Dado un objeto que implemente la
	 * interfaz del driver, se toma como fuente de datos y se le asocia un
	 * nombre
	 * 
	 * @param rd
	 *            objeto con la informaci�n
	 * 
	 * @return the name of the data source
	 * @throws SourceAlreadyExistsException
	 */
	public String nameAndRegisterDataSource(DataSourceDefinition dsd)
			throws SourceAlreadyExistsException {
		String name = getUID();
		register(name, false, dsd);

		return name;
	}

	/**
	 * Registers a DataSource by name. An instance of the DataSource can be
	 * obtained by calling getDataSource(String name)
	 * 
	 * @param name
	 * @param dsd
	 */
	public void registerDataSource(String name, DataSourceDefinition dsd)
			throws SourceAlreadyExistsException {
		register(name, true, dsd);
	}

	private void register(String name, boolean wellKnownName,
			DataSourceDefinition dsd) throws SourceAlreadyExistsException {
		if (existDS(name)) {
			throw new SourceAlreadyExistsException(name);
		}
		tableSource.put(name, dsd);
		dsd.setDataSourceFactory(this);
		fireSourceAdded(name, wellKnownName, this);
	}

	private void fireSourceAdded(String name, boolean wellKnownName,
			DataSourceFactory factory) {
		for (DataSourceFactoryListener listener : listeners) {
			listener.sourceAdded(new DataSourceFactoryEvent(name,
					wellKnownName, this));
		}
	}

	/**
	 * Obtiene la informaci�n de la fuente de datos cuyo nombre se pasa como
	 * par�metro
	 * 
	 * @param dataSourceName
	 *            Nombre de la base de datos
	 * 
	 * @return Debido a las distintas formas en las que se puede registrar un
	 *         datasource, se devuelve un Object, que podr� ser una instancia de
	 *         DataSourceFactory.FileDriverInfo, DataSourceFactory.DBDriverInfo
	 *         o ReadDriver
	 */
	public DataSourceDefinition getDataSourceDefinition(String dataSourceName) {
		return (DataSourceDefinition) tableSource.get(dataSourceName);
	}

	/**
	 * Gets the information of all data sources registered in the system
	 * 
	 * @return DataSourceDefinition[]
	 */
	public DataSourceDefinition[] getDataSourcesDefinition() {
		ArrayList<DataSourceDefinition> ret = new ArrayList<DataSourceDefinition>();
		Iterator<DataSourceDefinition> it = tableSource.values().iterator();

		while (it.hasNext()) {
			ret.add(it.next());
		}

		return ret.toArray(new DataSourceDefinition[0]);
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

		if ((mode & UNDOABLE) == UNDOABLE) {
			ret = new UndoableDataSourceDecorator(ret);
		}

		if ((mode & (EDITABLE | UNDOABLE)) != 0) {
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
	 */
	public DataSource getDataSource(ObjectDriver object)
			throws DriverLoadException, DataSourceCreationException {
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
	 */
	public DataSource getDataSource(ObjectDriver object, int mode)
			throws DriverLoadException, DataSourceCreationException {
		try {
			ObjectSourceDefinition fsd = new ObjectSourceDefinition(object);
			String name = nameAndRegisterDataSource(fsd);
			return getDataSource(name, mode);
		} catch (SourceAlreadyExistsException e) {
			throw new RuntimeException(e);
		} catch (NoSuchTableException e) {
			throw new RuntimeException(e);
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
	 */
	public DataSource getDataSource(File file) throws DriverLoadException,
			DataSourceCreationException {
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
	 */
	public DataSource getDataSource(File file, int mode)
			throws DriverLoadException, DataSourceCreationException {
		try {
			FileSourceDefinition fsd = new FileSourceDefinition(file);
			String name = nameAndRegisterDataSource(fsd);
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
	 */
	public DataSource getDataSource(DBSource dbSource)
			throws DriverLoadException, DataSourceCreationException {
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
	 */
	public DataSource getDataSource(DBSource dbSource, int mode)
			throws DriverLoadException, DataSourceCreationException {
		try {
			DBTableSourceDefinition fsd = new DBTableSourceDefinition(dbSource);
			String name = nameAndRegisterDataSource(fsd);
			return (DataSource) getDataSource(name, mode);
		} catch (NoSuchTableException e) {
			throw new RuntimeException(e);
		} catch (SourceAlreadyExistsException e) {
			throw new RuntimeException(e);
		}
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

		if (nameMapping.containsKey(tableName)) {
			tableName = nameMapping.get(tableName);
		}

		DataSource dataSource = nameDataSource.get(tableName);
		if (dataSource != null) {
			dataSource = getModedDataSource(dataSource, mode);
		} else {
			DataSourceDefinition dsd = tableSource.get(tableName);

			if (dsd == null) {
				throw new NoSuchTableException(tableName);
			} else {
				DataSource ds = dsd.createDataSource(tableName, getDriver(dsd));
				ds.setDataSourceFactory(this);
				ds = new OCCounterDecorator(ds);
				nameDataSource.put(tableName, ds);
				dataSource = getModedDataSource(ds, mode);
			}
		}
		if (tableAlias != null) {
			dataSource = new AliasDecorator(dataSource, tableAlias);
		}

		return dataSource;
	}

	public String getDriverName(String prefix) {
		String[] names = dm.getDriverNames();
		for (int i = 0; i < names.length; i++) {
			Driver driver = dm.getDriver(names[i]);
			if (driver instanceof DBDriver) {
				if (((DBDriver) driver).prefixAccepted(prefix)) {
					return names[i];
				}
			}
		}

		throw new DriverLoadException("No suitable driver for " + prefix);
	}

	public String getDriverName(File file) {
		String[] names = dm.getDriverNames();
		for (int i = 0; i < names.length; i++) {
			Driver driver = dm.getDriver(names[i]);
			if (driver instanceof FileDriver) {
				if (((FileDriver) driver).fileAccepted(file)) {
					return names[i];
				}
			}
		}

		throw new DriverLoadException("No suitable driver for "
				+ file.getAbsolutePath());
	}

	private String getDriver(DataSourceDefinition dsd) {
		if (dsd instanceof FileSourceDefinition) {
			return getDriverName(((FileSourceDefinition) dsd).getFile());
		} else if (dsd instanceof DBTableSourceDefinition) {
			return getDriverName(((DBTableSourceDefinition) dsd).getPrefix());
		} else if (dsd instanceof ObjectSourceDefinition) {
			return ((ObjectSourceDefinition) dsd).driver.getName();
		}

		throw new DriverLoadException("No suitable driver");
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
	public DataSource getDataSource(SelectAdapter instr, int mode)
			throws ExecutionException {
		Strategy strategy = sm.getStrategy(instr);

		DataSource ret;

		ret = strategy.select(instr);
		ret.setDataSourceFactory(this);
		nameDataSource.put(ret.getName(), ret);
		fireSourceAdded(ret.getName(), false, this);
		return getModedDataSource(ret, mode);
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
	private DataSource getDataSource(UnionAdapter instr, int mode)
			throws ExecutionException {
		Strategy strategy = sm.getStrategy(instr);

		DataSource ret;

		ret = strategy.union(instr);
		ret.setDataSourceFactory(this);
		nameDataSource.put(ret.getName(), ret);
		fireSourceAdded(ret.getName(), false, this);
		return getModedDataSource(ret, mode);
	}

	/**
	 * Creates a DataSource as a result of a custom query
	 * 
	 * @param instr
	 *            Root node of the adapter tree of the custom query instruction
	 * @param mode
	 * 
	 * @return DataSource with the custom query result
	 * 
	 * @throws ExecutionException
	 */
	public DataSource getDataSource(CustomAdapter instr, int mode)
			throws ExecutionException {
		Strategy strategy = sm.getStrategy(instr);

		DataSource ret;

		ret = strategy.custom(instr, this);
		if (ret != null) {
			ret.setDataSourceFactory(this);
			nameDataSource.put(ret.getName(), ret);
			fireSourceAdded(ret.getName(), false, this);
			return getModedDataSource(ret, mode);
		} else {
			return null;
		}
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
	 */
	public DataSource executeSQL(String sql) throws SyntaxException,
			DriverLoadException, NoSuchTableException, ExecutionException {
		return executeSQL(sql, NORMAL);
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

		fireInstructionExecuted(sql);

		if (!sql.trim().endsWith(";")) {
			sql += ";";
		}
		ByteArrayInputStream bytes = new ByteArrayInputStream(sql.getBytes());
		SQLEngine parser = new SQLEngine(bytes);

		try {
			parser.SQLStatement();
		} catch (ParseException e) {
			throw new SyntaxException(e);
		}

		Node root = parser.getRootNode();
		Adapter rootAdapter = Utilities.buildTree(root.jjtGetChild(0), sql,
				this);

		Utilities.simplify(rootAdapter);

		DataSource result = null;

		if (rootAdapter instanceof SelectAdapter) {
			result = getDataSource((SelectAdapter) rootAdapter, mode);
		} else if (rootAdapter instanceof UnionAdapter) {
			result = getDataSource((UnionAdapter) rootAdapter, mode);
		} else if (rootAdapter instanceof CustomAdapter) {
			result = getDataSource((CustomAdapter) rootAdapter, mode);
		} else if (rootAdapter instanceof CreateAdapter) {
			executeSQL((CreateAdapter) rootAdapter);
		}

		return result;
	}

	private void fireInstructionExecuted(String sql) {
		for (DataSourceFactoryListener listener : listeners) {
			listener.sqlExecuted(new DataSourceFactoryEvent(sql, null, this));
		}
	}

	private void executeSQL(CreateAdapter instr) throws ExecutionException {
		Strategy strategy = sm.getStrategy(instr);
		strategy.create(instr);
	}

	/**
	 * Establece el DriverManager que se usar� para instanciar DataSource's.
	 * Este metodo debe ser inprivatevocado antes que ning�n otro
	 * 
	 * @param dm
	 *            El manager que se encarga de cargar los drivers
	 */
	public void setDriverManager(DriverManager dm) {
		this.dm = dm;
	}

	/**
	 * Gets a driver manager reference
	 * 
	 * @return DriverManagers.
	 */
	public DriverManager getDriverManager() {
		return dm;
	}

	/**
	 * Frees all resources used during execution
	 * 
	 * @throws DataSourceFinalizationException
	 *             If cannot free resources
	 */
	public void freeResources() throws DataSourceFinalizationException {
		for (String name : tableSource.keySet()) {
			tableSource.get(name).freeResources(name);
		}

		tableSource.clear();

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
	 * 
	 * @throws InitializationException
	 *             If the initialization fails
	 */
	private void initialize(String tempDir) throws InitializationException {
		try {
			this.tempDir = new File(tempDir);

			if (!this.tempDir.exists()) {
				this.tempDir.mkdirs();
			}

			Class.forName("org.hsqldb.jdbcDriver");

			dm.registerDriver(CSVStringDriver.DRIVER_NAME,
					CSVStringDriver.class);
			dm.registerDriver(DBDriver.DRIVER_NAME, DBFDriver.class);
			dm.registerDriver(ShapefileDriver.DRIVER_NAME,
					ShapefileDriver.class);
			dm.registerDriver(HSQLDBDriver.DRIVER_NAME, HSQLDBDriver.class);
			dm.registerDriver(H2spatialDriver.DRIVER_NAME,
					H2spatialDriver.class);

			indexManager = new IndexManager(this);
			indexManager.addIndex(new SpatialIndex());
		} catch (ClassNotFoundException e) {
			throw new InitializationException(e);
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

	public IndexManager getIndexManager() {
		return indexManager;
	}

	/**
	 * Search for a registered DataSource according to its name
	 * 
	 * @param name
	 * @return true if such a DataSource is registered
	 */
	public boolean existDS(String name) {
		return tableSource.containsKey(name)
				|| nameDataSource.containsKey(name)
				|| nameMapping.containsKey(name);
	}

	/**
	 * Adds a new name to the specified data source name. The main name of the
	 * data source will not change but the new name can be used to refer to the
	 * source in the same way as the main one
	 * 
	 * @param dsName
	 * @param newName
	 * @throws TableNotFoundException
	 */
	public void addName(String dsName, String newName)
			throws TableNotFoundException, SourceAlreadyExistsException {
		if (!existDS(dsName)) {
			throw new TableNotFoundException(dsName);
		}
		if (existDS(newName)) {
			throw new SourceAlreadyExistsException(newName);
		}
		nameMapping.put(newName, dsName);
	}

	public void rename(String dsName, String newName)
			throws SourceAlreadyExistsException {
		if (tableSource.containsKey(dsName)) {
			if (existDS(newName)) {
				throw new SourceAlreadyExistsException(newName);
			}
			DataSourceDefinition value = tableSource.remove(dsName);
			tableSource.put(newName, value);

			changeNameMapping(dsName, newName);

			fireNameChanged(dsName, newName);
		}

		if (nameDataSource.containsKey(dsName)) {
			if (existDS(newName)) {
				throw new SourceAlreadyExistsException(newName);
			}
			DataSource ds = nameDataSource.remove(dsName);
			nameDataSource.put(newName, ds);

			changeNameMapping(dsName, newName);

			fireNameChanged(dsName, newName);
		}

		if (nameMapping.containsKey(dsName)) {
			if (existDS(newName)) {
				throw new SourceAlreadyExistsException(newName);
			}
			String ds = nameMapping.remove(dsName);
			nameMapping.put(newName, ds);
		}

	}

	private void fireNameChanged(String dsName, String newName) {
		for (DataSourceFactoryListener listener : listeners) {
			listener.sourceNameChanged(new DataSourceFactoryEvent(dsName, this,
					newName));
		}

	}

	private void changeNameMapping(String dsName, String newName) {
		ArrayList<String> namesToChange = getNamesFor(dsName);
		for (int i = 0; i < namesToChange.size(); i++) {
			nameMapping.put(namesToChange.get(i), newName);
		}
	}

	private ArrayList<String> getNamesFor(String dsName) {
		Iterator<String> names = nameMapping.keySet().iterator();
		ArrayList<String> namesToChange = new ArrayList<String>();
		while (names.hasNext()) {
			String name = names.next();
			if (nameMapping.get(name).equals(dsName)) {
				namesToChange.add(name);
			}
		}
		return namesToChange;
	}

	public boolean addDataSourceFactoryListener(DataSourceFactoryListener e) {
		return listeners.add(e);
	}

	public boolean removeDataSourceFactoryListener(DataSourceFactoryListener o) {
		return listeners.remove(o);
	}

	public String getMainNameFor(String dsName) throws NoSuchTableException {
		if (nameMapping.containsKey(dsName)) {
			return nameMapping.get(dsName);
		} else if ((tableSource.containsKey(dsName))
				|| (nameDataSource.containsKey(dsName))) {
			return dsName;
		} else {
			throw new NoSuchTableException(dsName);
		}
	}

	/**
	 * Returns true if the specified source points to existing data or not
	 * 
	 * @param dsName
	 * @return
	 */
	public boolean hasSource(String dsName) {
		// TODO we have to implement this method with the source
		// control system
		return true;
	}

	/**
	 * Returns the driver class of the specified source. Returns null if the
	 * soure has no driver (sql queries).
	 * 
	 * @param dsName
	 * @return
	 * @throws NoSuchTableException
	 *             If there is no source with the specified name
	 */
	public String getDriver(String dsName) throws NoSuchTableException {
		dsName = getMainNameFor(dsName);
		DataSourceDefinition dsd = tableSource.get(dsName);
		if (dsd != null) {
			return getDriver(dsd);
		} else {
			return null;
		}
	}

}

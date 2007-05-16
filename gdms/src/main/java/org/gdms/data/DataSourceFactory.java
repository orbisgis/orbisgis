package org.gdms.data;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.gdms.data.command.UndoableDataSource;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.db.DelegatingStrategy;
import org.gdms.data.file.FileSourceDefinition;
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
import org.gdms.driver.hsqldb.HSQLDBDriver;
import org.gdms.driver.hsqldbTransactional.HSQLDBTransactionalDriver;
import org.gdms.driver.shapefile.ShapefileDriver;
import org.gdms.sql.instruction.Adapter;
import org.gdms.sql.instruction.CustomAdapter;
import org.gdms.sql.instruction.SelectAdapter;
import org.gdms.sql.instruction.UnionAdapter;
import org.gdms.sql.instruction.Utilities;
import org.gdms.sql.parser.Node;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.strategies.OperationDataSource;
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
 * After using the DataSourceFactory it's hardly recomended to call
 * freeResources method.
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class DataSourceFactory {

	public final static int UNDOABLE = 4;

	public final static int NORMAL = 0;

	/**
	 * Asocia los nombres de las tablas con la informaci�n del origen de datos
	 */
	private HashMap<String, DataSourceDefinition> tableSource = new HashMap<String, DataSourceDefinition>();

	/** Associates a name with the operation layer DataSource with that name */
	private HashMap<String, DataSource> nameResultDataSource = new HashMap<String, DataSource>();

	private DriverManager dm = new DriverManager();

	private File tempDir = new File(".");

	private DelegatingStrategy delegatingStrategy = new DelegatingStrategy(this);

	private StrategyManager sm = new StrategyManager(delegatingStrategy);

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
		String name = "gdbms" + System.currentTimeMillis();

		while (tableSource.get(name) != null) {
			name = "gdbms" + System.currentTimeMillis();
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
		nameResultDataSource.clear();
	}

	/**
	 * Removes the association between the name and the data sources
	 *
	 * @param ds
	 *            Name of the data source to remove
	 *
	 */
	public void remove(DataSource ds) {
		String name = ds.getName();

		if (tableSource.remove(name) == null) {
			if (nameResultDataSource.remove(name) == null) {
				throw new RuntimeException(
						"No datasource with the name. Data source name changed since the DataSource instance was retrieved?");
			}
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
	public void createDataSource(DataSourceCreation dsc) throws DriverException {
		dsc.setDataSourceFactory(this);
		dsc.create();
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
	 */
	public String nameAndRegisterDataSource(DataSourceDefinition dsd) {
		String name = getUID();
		registerDataSource(name, dsd);

		return name;
	}

	/**
	 * Registers a DataSource by name. An instance of the DataSource can be
	 * obtained by calling getDataSource(String name)
	 *
	 * @param name
	 * @param dsd
	 */
	public void registerDataSource(String name, DataSourceDefinition dsd) {
		tableSource.put(name, dsd);
		dsd.setDataSourceFactory(this);
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
	 * Gets the data source passed by adding the AutomaticDataSource decorator
	 * if factory mode is AUTOMATIC.
	 *
	 * @param ds
	 *            DataSource
	 * @param mode
	 *            opening mode
	 *
	 * @return DataSource
	 */
	private DataSource getModedDataSource(DataSource ds, int mode) {
		DataSource ret = ds;

		if ((mode & UNDOABLE) == UNDOABLE) {
			ret = new UndoableDataSource(ret);
		}

		return ret;
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
		return getDataSource(object, NORMAL);
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
		ObjectSourceDefinition fsd = new ObjectSourceDefinition(object);
		String name = nameAndRegisterDataSource(fsd);
		try {
			return getDataSource(name, mode);
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
	public DataSource getDataSource(File file)
			throws DriverLoadException, DataSourceCreationException {
		return getDataSource(file, NORMAL);
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
		FileSourceDefinition fsd = new FileSourceDefinition(file);
		String name = nameAndRegisterDataSource(fsd);
		try {
			return getDataSource(name, mode);
		} catch (NoSuchTableException e) {
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
		return getDataSource(dbSource, NORMAL);
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
		DBTableSourceDefinition fsd = new DBTableSourceDefinition(dbSource);
		String name = nameAndRegisterDataSource(fsd);
		try {
			return (DataSource) getDataSource(name, mode);
		} catch (NoSuchTableException e) {
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
		return getDataSource(tableName, tableName);
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
		return getDataSource(tableName, tableName, mode);
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
		return getDataSource(tableName, tableAlias, NORMAL);
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
		Object o = tableSource.get(tableName);

		if (o == null) {
			// may be a secondary DataSource
			OperationDataSource ret = (OperationDataSource) nameResultDataSource
					.get(tableName);

			if (ret != null) {
				ret.setName(tableAlias);

				return getModedDataSource(ret, mode);
			}

			// table not found
			throw new NoSuchTableException(tableName);
		}

		DataSourceDefinition dsd = (DataSourceDefinition) o;
		DataSource ds = dsd.createDataSource(tableName, tableAlias,
				getDriver(dsd));
		ds.setDataSourceFactory(this);

		return getModedDataSource(ds, mode);
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
			return "";
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
	public DataSource getDataSource(SelectAdapter instr)
			throws ExecutionException {
		Strategy strategy = sm.getStrategy(instr);

		DataSource ret;

		ret = strategy.select(instr);

		return ret;
	}

	/**
	 * Obtiene el DataSource resultado de ejecutar la instrucci�n de union
	 *
	 * @param instr
	 *            instrucci�n de union
	 *
	 * @throws ExecutionException
	 */
	private DataSource getDataSource(UnionAdapter instr)
			throws ExecutionException {
		Strategy strategy = sm.getStrategy(instr);

		DataSource ret;

		ret = strategy.union(instr);

		return getModedDataSource(ret, NORMAL);
	}

	/**
	 * Creates a DataSource as a result of a custom query
	 *
	 * @param instr
	 *            Root node of the adapter tree of the custom query instruction
	 *
	 * @return DataSource with the custom query result
	 *
	 * @throws ExecutionException
	 */
	public DataSource getDataSource(CustomAdapter instr)
			throws ExecutionException {
		Strategy strategy = sm.getStrategy(instr);

		DataSource ret;

		ret = strategy.custom(instr);

		return getModedDataSource(ret, NORMAL);
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
	public DataSource executeSQL(String sql) throws SyntaxException,
			DriverLoadException, NoSuchTableException, ExecutionException {
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
			result = getDataSource((SelectAdapter) rootAdapter);
		} else if (rootAdapter instanceof UnionAdapter) {
			result = getDataSource((UnionAdapter) rootAdapter);
		} else if (rootAdapter instanceof CustomAdapter) {
			result = getDataSource((CustomAdapter) rootAdapter);
		}

		// if operation was delegated it isn't a OperationDataSource
		if (result instanceof OperationDataSource) {
			((OperationDataSource) result).setSQL(sql);
		}

		result.setDataSourceFactory(this);
		nameResultDataSource.put(result.getName(), result);

		return result;
	}

	/**
	 * Establece el DriverManager que se usar� para instanciar DataSource's.
	 * Este metodo debe ser invocado antes que ning�n otro
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
				return pathname.getName().toLowerCase().startsWith("gdbms");
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

			dm.registerDriver("csv string", CSVStringDriver.class);
			dm.registerDriver("dbf driver", DBFDriver.class);
			dm.registerDriver("shapefile driver", ShapefileDriver.class);
			dm.registerDriver("GDBMS HSQLDB Transactional driver",
					HSQLDBTransactionalDriver.class);
			dm.registerDriver("GDBMS HSQLDB driver", HSQLDBDriver.class);

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
		return tempDir.getAbsolutePath() + File.separator + "gdbms"
				+ System.currentTimeMillis();
	}

	public DelegatingStrategy getDelegatingStrategy() {
		return delegatingStrategy;
	}

}

package org.gdms.source;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DataSourceFinalizationException;
import org.gdms.data.InitializationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.OCCounterDecorator;
import org.gdms.data.SQLSourceDefinition;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.csvstring.CSVStringDriver;
import org.gdms.driver.dbf.DBFDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.h2.H2spatialDriver;
import org.gdms.driver.hsqldb.HSQLDBDriver;
import org.gdms.driver.postgresql.PostgreSQLDriver;
import org.gdms.driver.shapefile.ShapefileDriver;
import org.gdms.driver.solene.CirDriver;
import org.gdms.driver.solene.ValDriver;
import org.gdms.source.directory.Source;
import org.gdms.source.directory.Sources;
import org.gdms.sql.instruction.TableNotFoundException;

public class DefaultSourceManager implements SourceManager {

	/**
	 * Associates the names of the tables with the information of the data
	 * source
	 */
	private HashMap<String, ExtendedSource> nameSource = new HashMap<String, ExtendedSource>();

	private HashMap<String, String> nameMapping = new HashMap<String, String>();

	private List<SourceListener> listeners = new ArrayList<SourceListener>();

	private DataSourceFactory dsf;

	DriverManager dm = new DriverManager();

	private String baseDir;

	private Sources sources;

	private JAXBContext jc;

	private String lastUID = "gdms" + System.currentTimeMillis();

	public DefaultSourceManager(DataSourceFactory dsf, String baseDir)
			throws IOException {
		this.baseDir = baseDir;
		dm.registerDriver(CSVStringDriver.DRIVER_NAME, CSVStringDriver.class);
		dm.registerDriver(DBFDriver.DRIVER_NAME, DBFDriver.class);
		dm.registerDriver(ShapefileDriver.DRIVER_NAME, ShapefileDriver.class);
		dm.registerDriver(CirDriver.DRIVER_NAME, CirDriver.class);
		dm.registerDriver(ValDriver.DRIVER_NAME, ValDriver.class);
		dm.registerDriver(PostgreSQLDriver.DRIVER_NAME, PostgreSQLDriver.class);
		dm.registerDriver(HSQLDBDriver.DRIVER_NAME, HSQLDBDriver.class);
		dm.registerDriver(H2spatialDriver.DRIVER_NAME, H2spatialDriver.class);
		this.dsf = dsf;

		File file = getDirectoryFile();
		createFile(file);
		try {
			jc = JAXBContext.newInstance("org.gdms.source.directory");
			sources = (Sources) jc.createUnmarshaller().unmarshal(file);

			List<Source> source = sources.getSource();
			for (Source xmlSrc : source) {
				String name = xmlSrc.getName();
				ExtendedSource newSource = new ExtendedSource(dsf, sources,
						name, true, baseDir, null, null);
				register(name, newSource);
			}
		} catch (JAXBException e) {
			throw new InitializationException(e);
		} catch (InstantiationException e) {
			throw new InitializationException(e);
		} catch (IllegalAccessException e) {
			throw new InitializationException(e);
		} catch (ClassNotFoundException e) {
			throw new InitializationException(e);
		}
	}

	private File getDirectoryFile() {
		File file = new File(baseDir, "directory.xml");
		return file;
	}

	public void saveStatus() throws DriverException {
		try {
			Sources sourcesToStore = new Sources();
			List<Source> sourceElements = sourcesToStore.getSource();
			for (Source sourceElement : sources.getSource()) {
				ExtendedSource src = nameSource.get(sourceElement.getName());
				if (src.isWellKnownName()) {
					sourceElement.setChecksum(src.getChecksum());
					sourceElements.add(sourceElement);
				}
			}
			createFile(getDirectoryFile());
			jc.createMarshaller().marshal(sourcesToStore,
					new FileOutputStream(getDirectoryFile()));
		} catch (JAXBException e) {
			throw new DriverException(e);
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	private void createFile(File file) throws IOException {
		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				if (!file.getParentFile().mkdirs()) {
					throw new IOException("Cannot create dirs: " + file);
				}
			}
			if (!file.createNewFile()) {
				throw new IOException("Cannot create file: " + file);
			}
			PrintWriter pw = new PrintWriter(file);
			pw.println("<sources>");
			pw.println("</sources>");
			pw.close();
		}
	}

	/**
	 * @param dm
	 */
	public void setDriverManager(DriverManager dm) {
		this.dm = dm;
	}

	public DriverManager getDriverManager() {
		return this.dm;
	}

	/**
	 * @param e
	 * @return
	 */
	public boolean addDataSourceFactoryListener(SourceListener e) {
		return listeners.add(e);
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean removeDataSourceFactoryListener(SourceListener o) {
		return listeners.remove(o);
	}

	/**
	 * @throws IOException
	 *
	 */
	public void removeAll() throws IOException {
		File[] files = new File(baseDir).listFiles();
		for (File file : files) {
			if (!file.getName().equals("directory.xml")) {
				if (!file.delete()) {
					throw new IOException(
							"Cannot delete file associated with property: "
									+ file.getAbsolutePath());
				}
			}
		}
		nameSource.clear();
		nameMapping.clear();
		sources.getSource().clear();
	}

	/**
	 * @param name
	 * @return
	 */
	public boolean remove(String name) {
		if (!nameSource.containsKey(name)) {
			return false;
		}

		ExtendedSource toRemove = nameSource.get(name);
		// Check if some source depends on it
		String[] referencingSources = toRemove.getReferencingSources();
		if (referencingSources.length > 0) {
			boolean anyWellKnown = false;
			for (String referencingSource : referencingSources) {
				if (getSource(referencingSource).isWellKnownName()) {
					anyWellKnown = true;
				}
			}
			if (anyWellKnown) {
				String msg = "The source is used by the following sources: ";
				for (String dep : referencingSources) {
					msg += dep + ", ";
				}
				msg = msg.substring(0, msg.length() - 2);
				throw new IllegalStateException(msg);
			}
		}

		toRemove.removeFromXML();
		nameSource.remove(name);

		List<String> namesToRemove = getNamesFor(name);
		for (String nameToRemove : namesToRemove) {
			nameMapping.remove(nameToRemove);
		}

		fireSourceRemoved(name);

		return true;
	}

	private void fireSourceRemoved(String name) {
		for (SourceListener listener : listeners) {
			listener.sourceRemoved(new SourceEvent(name, true, this));
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

	public ExtendedSource getExtendedSource(String name) {
		return nameSource.get(name);
	}

	public org.gdms.source.Source getSource(String name) {
		return getExtendedSource(name);
	}

	/**
	 * @param name
	 * @param file
	 * @throws DriverException
	 * @throws SourceAlreadyExistsException
	 */
	public void register(String name, File file)
			throws SourceAlreadyExistsException {
		register(name, new FileSourceDefinition(file));
	}

	/**
	 * @param name
	 * @param dbTable
	 * @throws DriverException
	 * @throws SourceAlreadyExistsException
	 */
	public void register(String name, DBSource dbTable)
			throws SourceAlreadyExistsException {
		register(name, new DBTableSourceDefinition(dbTable));
	}

	/**
	 * @param name
	 * @param driver
	 * @throws DriverException
	 * @throws SourceAlreadyExistsException
	 */
	public void register(String name, ObjectDriver driver)
			throws SourceAlreadyExistsException {
		register(name, new ObjectSourceDefinition(driver));
	}

	/**
	 * @param name
	 * @param driver
	 * @throws DriverException
	 * @throws SourceAlreadyExistsException
	 */
	public void register(String name, String sql)
			throws SourceAlreadyExistsException {
		register(name, true, new SQLSourceDefinition(sql));
	}

	/**
	 * @param name
	 * @param def
	 * @throws DriverException
	 * @throws SourceAlreadyExistsException
	 */
	public void register(String name, DataSourceDefinition def)
			throws SourceAlreadyExistsException {
		register(name, true, def);
	}

	private void register(String name, boolean wellKnownName,
			DataSourceDefinition dsd) throws SourceAlreadyExistsException {
		try {
			dsd.setDataSourceFactory(dsf);
			ExtendedSource src = new ExtendedSource(dsf, sources, name,
					wellKnownName, baseDir, null, dsd);
			register(name, src);
		} catch (InstantiationException e) {
			// should ever raise these exceptions
			throw new RuntimeException("bug!");
		} catch (IllegalAccessException e) {
			// should ever raise these exceptions
			throw new RuntimeException("bug!");
		} catch (ClassNotFoundException e) {
			// should ever raise these exceptions
			throw new RuntimeException("bug!");
		}
	}

	private void register(String name, ExtendedSource src) {
		if (exists(name)) {
			throw new SourceAlreadyExistsException(name);
		}
		nameSource.put(name, src);
		fireSourceAdded(name, src.isWellKnownName());
	}

	private void fireSourceAdded(String name, boolean wellKnownName) {
		for (SourceListener listener : listeners) {
			listener.sourceAdded(new SourceEvent(name, wellKnownName, this));
		}
	}

	/**
	 * @return
	 */
	public String getUID() {
		String name = "gdms" + System.currentTimeMillis();

		while (name.equals(lastUID)) {
			name = "gdms" + System.currentTimeMillis();
		}

		lastUID = name;
		return name;
	}

	/**
	 * @param file
	 * @return
	 */
	public String nameAndRegister(File file) {
		String name = getUID();
		register(name, false, new FileSourceDefinition(file));
		return name;
	}

	/**
	 * @param dbTable
	 * @return
	 */
	public String nameAndRegister(DBSource dbTable) {
		String name = getUID();
		register(name, false, new DBTableSourceDefinition(dbTable));
		return name;
	}

	/**
	 * @param driver
	 * @return
	 */
	public String nameAndRegister(ObjectDriver driver) {
		String name = getUID();
		register(name, false, new ObjectSourceDefinition(driver));
		return name;
	}

	/**
	 * @param def
	 * @return
	 * @throws DriverException
	 * @throws
	 */
	public String nameAndRegister(DataSourceDefinition def) {
		String name = getUID();
		register(name, false, def);
		return name;
	}

	/**
	 * @param ret
	 * @param sql
	 * @return
	 * @throws DriverException
	 */
	public String register(String name, boolean isWellKnownName,
			DataSource ret, String sql) {
		try {
			ret.setDataSourceFactory(dsf);
			ExtendedSource src = new ExtendedSource(dsf, sources, name,
					isWellKnownName, baseDir, ret, new SQLSourceDefinition(sql));
			register(name, src);
			return name;
		} catch (InstantiationException e) {
			// should ever raise these exceptions
			throw new RuntimeException("bug!");
		} catch (IllegalAccessException e) {
			// should ever raise these exceptions
			throw new RuntimeException("bug!");
		} catch (ClassNotFoundException e) {
			// should ever raise these exceptions
			throw new RuntimeException("bug!");
		}
	}

	public DataSource getDataSource(String name) throws NoSuchTableException,
			DataSourceCreationException {

		name = getMainNameFor(name);

		ExtendedSource src = nameSource.get(name);

		DataSource dataSource = src.getDataSource();
		if (dataSource != null) {
			return dataSource;
		} else {
			DataSourceDefinition dsd = src.getDataSourceDefinition();

			if (dsd == null) {
				throw new NoSuchTableException(name);
			} else {
				DataSource ds = dsd.createDataSource(name);
				ds = new OCCounterDecorator(ds);
				ds.setDataSourceFactory(dsf);
				src.setDatasource(ds);
				return ds;
			}
		}
	}

	/**
	 * @param dsName
	 * @param newName
	 * @throws TableNotFoundException
	 * @throws SourceAlreadyExistsException
	 */
	public void addName(String dsName, String newName)
			throws TableNotFoundException, SourceAlreadyExistsException {
		if (!exists(dsName)) {
			throw new TableNotFoundException(dsName);
		}
		if (exists(newName)) {
			throw new SourceAlreadyExistsException(newName);
		}
		nameMapping.put(newName, dsName);
	}

	/**
	 * @param dsName
	 * @param newName
	 * @throws SourceAlreadyExistsException
	 */
	public void rename(String dsName, String newName)
			throws SourceAlreadyExistsException {
		if (nameSource.containsKey(dsName)) {
			if (exists(newName)) {
				throw new SourceAlreadyExistsException(newName);
			}
			ExtendedSource value = nameSource.remove(dsName);
			nameSource.put(newName, value);
			value.setName(newName);

			changeNameMapping(dsName, newName);

			fireNameChanged(dsName, newName);
		}

		if (nameMapping.containsKey(dsName)) {
			if (exists(newName)) {
				throw new SourceAlreadyExistsException(newName);
			}
			String ds = nameMapping.remove(dsName);
			nameMapping.put(newName, ds);
		}

	}

	private void fireNameChanged(String dsName, String newName) {
		for (SourceListener listener : listeners) {
			listener.sourceNameChanged(new SourceEvent(dsName, true, this,
					newName));
		}

	}

	private void changeNameMapping(String dsName, String newName) {
		ArrayList<String> namesToChange = getNamesFor(dsName);
		for (int i = 0; i < namesToChange.size(); i++) {
			nameMapping.put(namesToChange.get(i), newName);
		}
	}

	/**
	 * @param sourceName
	 * @return
	 */
	public boolean exists(String sourceName) {
		return nameSource.containsKey(sourceName)
				|| nameMapping.containsKey(sourceName);
	}

	/**
	 * @param dsName
	 * @return
	 * @throws NoSuchTableException
	 */
	public String getMainNameFor(String dsName) throws NoSuchTableException {
		if (nameMapping.containsKey(dsName)) {
			return nameMapping.get(dsName);
		} else if (nameSource.containsKey(dsName)) {
			return dsName;
		} else {
			throw new NoSuchTableException(dsName);
		}
	}

	/**
	 * @throws DataSourceFinalizationException
	 */
	public void shutdown() throws DataSourceFinalizationException {
		for (String name : nameSource.keySet()) {
			DataSourceDefinition dataSourceDefinition = nameSource.get(name)
					.getDataSourceDefinition();
			if (dataSourceDefinition != null) {
				dataSourceDefinition.freeResources(name);
			}
		}

		nameSource.clear();
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return nameSource.isEmpty() && nameMapping.isEmpty();
	}

	/**
	 * @param dsc
	 * @return
	 * @throws DriverException
	 */
	public DataSourceDefinition createDataSource(DataSourceCreation dsc)
			throws DriverException {
		dsc.setDataSourceFactory(dsf);
		return dsc.create();
	}

	public void saveContents(String sourceName, DataSource contents)
			throws DriverException {
		DataSourceDefinition dsd = getExtendedSource(sourceName)
				.getDataSourceDefinition();
		dsd.createDataSource(contents);
	}

	public File getSourceInfoDirectory() {
		return new File(baseDir);
	}

	public void setSourceInfoDirectory(String newDir) throws DriverException {
		saveStatus();

		File newDirectory = new File(newDir);
		if (!newDirectory.exists()) {
			newDirectory.mkdirs();
		}
		File[] childs = new File(baseDir).listFiles();
		for (File file : childs) {
			try {
				DriverUtilities.copy(file, new File(newDirectory, file
						.getName()));
			} catch (IOException e) {
				throw new DriverException(e);
			}
		}
		this.baseDir = newDir;
	}

	public String getMemento() throws IOException {
		StringBuffer ret = new StringBuffer();
		Iterator<String> it = nameSource.keySet().iterator();
		while (it.hasNext()) {
			String sourceName = it.next();
			org.gdms.source.Source source = nameSource.get(sourceName);
			ret.append(sourceName).append("(");
			ArrayList<String> aliases = getNamesFor(sourceName);
			for (String alias : aliases) {
				ret.append("-").append(alias).append("-");
			}
			String[] stringPropertyNames = source.getStringPropertyNames();
			for (String propertyName : stringPropertyNames) {
				ret.append("(").append(propertyName).append(",").append(
						source.getProperty(propertyName)).append(")");
			}
			String[] filePropertyNames = source.getFilePropertyNames();
			for (String propertyName : filePropertyNames) {
				ret.append("(").append(propertyName).append(",").append(
						source.getFilePropertyContentsAsString(propertyName))
						.append(")");
			}
			ret.append(")");
		}

		return ret.toString();
	}

	public String getDriverName(String sourceName) throws NoSuchTableException {
		ExtendedSource src = getExtendedSource(sourceName);
		if (src == null) {
			throw new NoSuchTableException(sourceName);
		} else {
			return src.getDriverName();
		}
	}
}

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
import org.gdms.driver.asc.AscDriver;
import org.gdms.driver.csvstring.CSVStringDriver;
import org.gdms.driver.dbf.DBFDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.gdms.GdmsDriver;
import org.gdms.driver.geotif.TifDriver;
import org.gdms.driver.h2.H2spatialDriver;
import org.gdms.driver.hsqldb.HSQLDBDriver;
import org.gdms.driver.jpg.JPGDriver;
import org.gdms.driver.png.PngDriver;
import org.gdms.driver.postgresql.PostgreSQLDriver;
import org.gdms.driver.shapefile.ShapefileDriver;
import org.gdms.driver.solene.CirDriver;
import org.gdms.driver.solene.ValDriver;
import org.gdms.source.directory.Source;
import org.gdms.source.directory.Sources;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.Instruction;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SemanticException;
import org.gdms.sql.strategies.TableNotFoundException;
import org.orbisgis.progress.IProgressMonitor;

public class DefaultSourceManager implements SourceManager {

	/**
	 * Associates the names of the tables with the information of the data
	 * source
	 */
	private HashMap<String, ExtendedSource> nameSource;

	private HashMap<String, String> nameMapping;

	private List<SourceListener> listeners;

	private DataSourceFactory dsf;

	DriverManager dm = new DriverManager();

	private String baseDir;

	private Sources sources;

	private JAXBContext jc;

	private String lastUID = "gdms" + System.currentTimeMillis();

	public DefaultSourceManager(DataSourceFactory dsf, String baseDir)
			throws IOException {
		dm.registerDriver(CSVStringDriver.DRIVER_NAME, CSVStringDriver.class);
		dm.registerDriver(DBFDriver.DRIVER_NAME, DBFDriver.class);
		dm.registerDriver(ShapefileDriver.DRIVER_NAME, ShapefileDriver.class);
		dm.registerDriver(CirDriver.DRIVER_NAME, CirDriver.class);
		dm.registerDriver(ValDriver.DRIVER_NAME, ValDriver.class);
		dm.registerDriver(PostgreSQLDriver.DRIVER_NAME, PostgreSQLDriver.class);
		dm.registerDriver(HSQLDBDriver.DRIVER_NAME, HSQLDBDriver.class);
		dm.registerDriver(H2spatialDriver.DRIVER_NAME, H2spatialDriver.class);
		dm.registerDriver(new GdmsDriver().getName(), GdmsDriver.class);
		dm.registerDriver(new TifDriver().getName(), TifDriver.class);
		dm.registerDriver(new AscDriver().getName(), AscDriver.class);
		dm.registerDriver(new JPGDriver().getName(), JPGDriver.class);
		dm.registerDriver(new PngDriver().getName(), PngDriver.class);
		this.dsf = dsf;
		changeSourceInfoDirectory(baseDir);
	}

	public void init() {
		Iterator<ExtendedSource> it = nameSource.values().iterator();
		while (it.hasNext()) {
			ExtendedSource source = it.next();
			try {
				source.init();
			} catch (DriverException e) {
				dsf.getWarningListener().throwWarning(
						"The source could not be created: " + source.getName(),
						e, null);
			}
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
			// Get the well known sources
			for (Source sourceElement : sources.getSource()) {
				ExtendedSource src = nameSource.get(sourceElement.getName());
				if (src.isWellKnownName()) {
					sourceElements.add(sourceElement);
				}
			}
			// Calculate the checksum
			for (Source source : sourceElements) {
				ExtendedSource src = nameSource.get(source.getName());
				source.setChecksum(src.getChecksum());
			}
			createFile(getDirectoryFile());
			FileOutputStream fileOutputStream = new FileOutputStream(
					getDirectoryFile());
			jc.createMarshaller().marshal(sourcesToStore, fileOutputStream);
			fileOutputStream.close();
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
	public boolean addSourceListener(SourceListener e) {
		return listeners.add(e);
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean removeSourceListener(SourceListener o) {
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
		Iterator<String> it = nameSource.keySet().iterator();
		while (it.hasNext()) {
			String sourceName = it.next();
			fireSourceRemoved(sourceName);
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
		try {
			name = getMainNameFor(name);
		} catch (NoSuchTableException e) {
			return false;
		}
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

		fireSourceRemoved(name);

		return true;
	}

	private void fireSourceRemoved(String name) {
		ArrayList<String> names = getNamesFor(name);
		List<String> namesToRemove = names;
		for (String nameToRemove : namesToRemove) {
			nameMapping.remove(nameToRemove);
		}

		fireSourceRemoved(name, names);
	}

	private void fireSourceRemoved(String name, ArrayList<String> names) {
		for (SourceListener listener : listeners) {
			listener.sourceRemoved(new SourceRemovalEvent(name, names
					.toArray(new String[0]), true, this));
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
		try {
			name = getMainNameFor(name);
			return nameSource.get(name);
		} catch (NoSuchTableException e) {
			return null;
		}
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
	 * @throws DriverException
	 * @throws SemanticException
	 * @throws ParseException
	 */
	public void register(String name, String sql)
			throws SourceAlreadyExistsException, ParseException,
			SemanticException, DriverException {
		SQLProcessor sqlProcessor = new SQLProcessor(dsf);
		Instruction instruction = sqlProcessor.prepareInstruction(sql);
		register(name, true, new SQLSourceDefinition(instruction));
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
		register(name, wellKnownName, dsd, null);
	}

	private void register(String name, boolean isWellKnownName,
			DataSourceDefinition dsd, DataSource ds) {
		try {
			dsd.setDataSourceFactory(dsf);
			String sourceName = getSourceName(dsd);
			if (sourceName != null) {
				throw new SourceAlreadyExistsException(
						"The source already exists with the name: "
								+ sourceName);
			} else {
				ExtendedSource src = new ExtendedSource(dsf, sources, name,
						isWellKnownName, baseDir, ds, dsd);
				register(name, src);
			}
		} catch (InstantiationException e) {
			// should ever raise these exceptions
			throw new RuntimeException("bug!");
		} catch (IllegalAccessException e) {
			// should ever raise these exceptions
			throw new RuntimeException("bug!");
		} catch (ClassNotFoundException e) {
			// should ever raise these exceptions
			throw new RuntimeException("bug!");
		} catch (DriverException e) {
			throw new RuntimeException(e);
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
	 * @param file
	 * @return
	 * @throws DriverException
	 * @throws SemanticException
	 * @throws ParseException
	 */
	public String nameAndRegister(String sql) throws ParseException,
			SemanticException, DriverException {
		String name = getUID();
		SQLProcessor sqlProcessor = new SQLProcessor(dsf);
		Instruction instruction = sqlProcessor.prepareInstruction(sql);
		register(name, false, new SQLSourceDefinition(instruction));
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
		String name = getSourceName(def);
		if (name != null) {
			return name;
		} else {
			name = getUID();
			register(name, false, def);
			return name;
		}
	}

	public String getSourceName(DataSourceDefinition dsd) {
		Iterator<String> it = nameSource.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next();
			ExtendedSource src = nameSource.get(name);
			if (src.getDef().equals(dsd)) {
				return name;
			}
		}

		return null;
	}

	public DataSource getDataSource(String name, IProgressMonitor pm)
			throws NoSuchTableException, DataSourceCreationException {

		name = getMainNameFor(name);

		ExtendedSource src = nameSource.get(name);

		DataSource dataSource = src.getDataSource();
		if (dataSource == null) {
			DataSourceDefinition dsd = src.getDataSourceDefinition();

			if (dsd == null) {
				throw new NoSuchTableException(name);
			} else {
				DataSource ds = dsd.createDataSource(name, pm);
				ds = new OCCounterDecorator(ds);
				ds.setDataSourceFactory(dsf);
				src.setDatasource(ds);
				dataSource = ds;
			}
		}

		return dataSource;
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
				throw new SourceAlreadyExistsException(
						"The source already exists: " + newName);
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

	public void saveContents(String sourceName, DataSource contents,
			IProgressMonitor pm) throws DriverException {
		DataSourceDefinition dsd = getExtendedSource(sourceName)
				.getDataSourceDefinition();
		dsd.createDataSource(contents, pm);
	}

	public File getSourceInfoDirectory() {
		return new File(baseDir);
	}

	public void setSourceInfoDirectory(String newDir) throws DriverException {
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

	public int getSourceType(String sourceName) throws NoSuchTableException {
		org.gdms.source.Source src = getExtendedSource(sourceName);
		if (src == null) {
			throw new NoSuchTableException(sourceName);
		} else {
			return src.getType();
		}
	}

	public void removeName(String secondName) {
		if (nameMapping.containsKey(secondName)) {
			nameMapping.remove(secondName);
		}
	}

	public void changeSourceInfoDirectory(String newSourceInfoDir)
			throws IOException {
		this.baseDir = newSourceInfoDir;
		nameSource = new HashMap<String, ExtendedSource>();
		nameMapping = new HashMap<String, String>();
		listeners = new ArrayList<SourceListener>();

		File file = getDirectoryFile();
		createFile(file);
		try {
			jc = JAXBContext.newInstance("org.gdms.source.directory", this
					.getClass().getClassLoader());
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
		} catch (DriverException e) {
			throw new InitializationException(e);
		}
	}

	public String getSourceTypeName(String name) throws NoSuchTableException {
		int sourceType = getSourceType(name);
		if ((sourceType & ASC_GRID) == ASC_GRID) {
			return "ASCII GRID";
		} else if ((sourceType & BPW) == BPW) {
			return "BMP";
		} else if ((sourceType & CIR) == CIR) {
			return "CIR";
		} else if ((sourceType & CSV) == CSV) {
			return "CSV";
		} else if ((sourceType & DBF) == DBF) {
			return "DBF";
		} else if ((sourceType & H2) == H2) {
			return "H2";
		} else if ((sourceType & HSQLDB) == HSQLDB) {
			return "HSQLDB";
		} else if ((sourceType & JGW) == JGW) {
			return "JGW";
		} else if ((sourceType & MEMORY) == MEMORY) {
			return "MEMORY";
		} else if ((sourceType & PGW) == PGW) {
			return "PGW";
		} else if ((sourceType & POSTGRESQL) == POSTGRESQL) {
			return "POSTGRESQL";
		} else if ((sourceType & GDMS) == GDMS) {
			return "GDMS";
		} else if ((sourceType & SHP) == SHP) {
			return "SHP";
		} else if ((sourceType & TFW) == TFW) {
			return "TFW";
		} else if ((sourceType & VAL) == VAL) {
			return "VAL";
		} else if ((sourceType & XYZDEM) == XYZDEM) {
			return "XYZDEM";
		} else {
			throw new RuntimeException("bug!");
		}
	}

	public String[] getSourceNames() {
		return nameSource.keySet().toArray(new String[0]);
	}
}

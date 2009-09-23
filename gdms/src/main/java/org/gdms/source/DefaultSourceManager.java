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

import org.apache.log4j.Logger;
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
import org.gdms.data.wms.WMSSource;
import org.gdms.data.wms.WMSSourceDefinition;
import org.gdms.driver.DriverException;
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
import org.gdms.driver.vrml.VrmlDriver;
import org.gdms.source.directory.Source;
import org.gdms.source.directory.Sources;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.Instruction;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SemanticException;
import org.gdms.sql.strategies.TableNotFoundException;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.utils.FileUtils;

public class DefaultSourceManager implements SourceManager {

	private final static Logger logger = Logger
			.getLogger(DefaultSourceManager.class);

	/**
	 * Associates the names of the tables with the information of the data
	 * source
	 */
	private HashMap<String, ExtendedSource> nameSource;

	private HashMap<String, String> nameMapping;

	private ArrayList<SourceListener> listeners = new ArrayList<SourceListener>();;

	ArrayList<CommitListener> commitListeners = new ArrayList<CommitListener>();;

	private DataSourceFactory dsf;

	DriverManager dm = new DriverManager();

	private String baseDir;

	private Sources sources;

	private JAXBContext jc;

	private String lastUID = "gdms" + System.currentTimeMillis();

	public DefaultSourceManager(DataSourceFactory dsf, String baseDir)
			throws IOException {
		dm.registerDriver(CSVStringDriver.class);
		dm.registerDriver(DBFDriver.class);
		dm.registerDriver(ShapefileDriver.class);
		dm.registerDriver(CirDriver.class);
		dm.registerDriver(ValDriver.class);
		dm.registerDriver(VrmlDriver.class);
		dm.registerDriver(PostgreSQLDriver.class);
		dm.registerDriver(HSQLDBDriver.class);
		dm.registerDriver(H2spatialDriver.class);
		dm.registerDriver(GdmsDriver.class);
		dm.registerDriver(TifDriver.class);
		dm.registerDriver(AscDriver.class);
		dm.registerDriver(JPGDriver.class);
		dm.registerDriver(PngDriver.class);
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
					removeNonWellKnownDependencies(sourceElement);
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

	private void removeNonWellKnownDependencies(Source sourceElement) {
		removeNonWellKnown(sourceElement.getReferencedSource());
		removeNonWellKnown(sourceElement.getReferencingSource());
	}

	private void removeNonWellKnown(List<String> sourceNameList) {
		ArrayList<String> toRemove = new ArrayList<String>();
		for (String referenced : sourceNameList) {
			if (!getSource(referenced).isWellKnownName()) {
				toRemove.add(referenced);
			}
		}
		for (String depToRemove : toRemove) {
			sourceNameList.remove(depToRemove);
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
		ArrayList<String> notWellKnown = new ArrayList<String>();
		if (referencingSources.length > 0) {
			boolean anyWellKnown = false;
			for (String referencingSource : referencingSources) {
				if (getSource(referencingSource).isWellKnownName()) {
					anyWellKnown = true;
				} else {
					notWellKnown.add(referencingSource);
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

		for (String nwk : notWellKnown) {
			remove(nwk);
		}

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

	@SuppressWarnings("unchecked")
	private void fireSourceRemoved(String name, ArrayList<String> names) {
		ArrayList<SourceListener> list = (ArrayList<SourceListener>) listeners
				.clone();
		for (SourceListener listener : list) {
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

	@Override
	public void register(String name, WMSSource wmsSource)
			throws SourceAlreadyExistsException {
		register(name, new WMSSourceDefinition(wmsSource));
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
	 * Registers the specified DataSourceDefinition with the specified name
	 *
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

	@SuppressWarnings("unchecked")
	private void fireSourceAdded(String name, boolean wellKnownName) {
		ArrayList<SourceListener> list = (ArrayList<SourceListener>) listeners
				.clone();
		for (SourceListener listener : list) {
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

	public String getUniqueName(String base) {
		String tmpName = base;
		int i = 0;
		while (exists(tmpName)) {
			tmpName = base + "_" + i;
			i++;
		}

		return tmpName;
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

	@Override
	public String nameAndRegister(DBSource dbTable) {
		String name = getUID();
		register(name, false, new DBTableSourceDefinition(dbTable));
		return name;
	}

	@Override
	public String nameAndRegister(WMSSource dbTable) {
		String name = getUID();
		register(name, false, new WMSSourceDefinition(dbTable));
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
				if (pm.isCancelled()) {
					dataSource = null;
				} else {
					ds = new OCCounterDecorator(ds);
					ds.setDataSourceFactory(dsf);
					src.setDatasource(ds);
					dataSource = ds;
				}
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

		}

		if (nameMapping.containsKey(dsName)) {
			if (exists(newName)) {
				throw new SourceAlreadyExistsException(
						"The source already exists: " + newName);
			}
			String ds = nameMapping.remove(dsName);
			nameMapping.put(newName, ds);
		}

		fireNameChanged(dsName, newName);
	}

	@SuppressWarnings("unchecked")
	private void fireNameChanged(String dsName, String newName) {
		ArrayList<SourceListener> list = (ArrayList<SourceListener>) listeners
				.clone();
		for (SourceListener listener : list) {
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
		ExtendedSource extendedSource = getExtendedSource(sourceName);
		if (extendedSource == null) {
			throw new IllegalArgumentException(
					"There is no source with the specified name: " + sourceName);
		} else {
			DataSourceDefinition dsd = extendedSource.getDataSourceDefinition();
			dsd.createDataSource(contents, pm);
		}
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
				FileUtils.copy(file, new File(newDirectory, file.getName()));
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

	public String[] getSourceNames() {
		return nameSource.keySet().toArray(new String[0]);
	}

	public void addCommitListener(CommitListener listener) {
		commitListeners.add(listener);
	}

	public void removeCommitListener(CommitListener listener) {
		commitListeners.remove(listener);
	}

	@SuppressWarnings("unchecked")
	public void fireIsCommiting(String name, Object source)
			throws DriverException {
		List<CommitListener> listenerCopy = (List<CommitListener>) commitListeners
				.clone();
		for (CommitListener listener : listenerCopy) {
			if (listener.getName().equals(name)) {
				listener.isCommiting(name, source);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void fireCommitDone(String name) {
		List<CommitListener> listenerCopy = (List<CommitListener>) commitListeners
				.clone();
		for (CommitListener listener : listenerCopy) {
			if (listener.getName().equals(name)) {
				try {
					listener.commitDone(name);
				} catch (DriverException e) {
					logger.error("Cannot refresh commit listener: " + name, e);
				}
			}
		}
		ExtendedSource src = getExtendedSource(name);
		String[] referencing = src.getReferencingSources();
		for (String referencingSource : referencing) {
			fireCommitDone(referencingSource);
		}
	}

	@Override
	public String[] getAllNames(String sourceName) throws NoSuchTableException {
		return getNamesFor(getMainNameFor(sourceName)).toArray(new String[0]);
	}
}

package org.gdms.source;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SQLSourceDefinition;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.source.directory.DbDefinitionType;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.FileDefinitionType;
import org.gdms.source.directory.FileProperty;
import org.gdms.source.directory.ObjectDefinitionType;
import org.gdms.source.directory.Property;
import org.gdms.source.directory.Source;
import org.gdms.source.directory.Sources;
import org.gdms.source.directory.SqlDefinitionType;

public class ExtendedSource implements org.gdms.source.Source {

	private DataSource dataSource;

	DataSourceDefinition def;

	private String baseDir;

	private String name;

	private Sources sources;

	private boolean isWellKnownName;

	public ExtendedSource(DataSourceFactory dsf, Sources sources, String name,
			boolean isWellKnownName, String baseDir, DataSource dataSource,
			DataSourceDefinition def) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		this.baseDir = baseDir;
		this.name = name;
		this.sources = sources;
		this.isWellKnownName = isWellKnownName;

		Source xmlSrc = getSource();
		// If the source is new we put it in the XML
		if (xmlSrc == null) {
			this.dataSource = dataSource;
			this.def = def;
			this.def.setDataSourceFactory(dsf);
			Source source = new Source();
			source.setName(name);
			source.setDefinition(def.getDefinition());
			sources.getSource().add(source);

			// Add this dependencies
			ArrayList<String> depNames = def.getSourceDependencies();
			List<String> referencedSources = source.getReferencedSource();
			referencedSources.addAll(depNames);

			if (isWellKnownName) {
				// Add dependencies to other sources
				List<Source> srcList = sources.getSource();
				for (Source src : srcList) {
					if (depNames.contains(src.getName())) {
						src.getReferencingSource().add(name);
					}
				}
			}
		} else {
			// The source already exists so we read the values from it
			DefinitionType definitionType = xmlSrc.getDefinition();
			if (definitionType instanceof FileDefinitionType) {
				this.def = FileSourceDefinition
						.createFromXML((FileDefinitionType) definitionType);
			} else if (definitionType instanceof DbDefinitionType) {
				this.def = DBTableSourceDefinition
						.createFromXML((DbDefinitionType) definitionType);
			} else if (definitionType instanceof ObjectDefinitionType) {
				this.def = ObjectSourceDefinition
						.createFromXML((ObjectDefinitionType) definitionType);
			} else if (definitionType instanceof SqlDefinitionType) {
				this.def = SQLSourceDefinition
						.createFromXML((SqlDefinitionType) definitionType);
			} else {
				throw new RuntimeException("Not recognized source type: "
						+ definitionType.getClass().getCanonicalName());
			}

			this.def.setDataSourceFactory(dsf);
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public DataSourceDefinition getDataSourceDefinition() {
		return def;
	}

	public void setDatasource(DataSource ds) {
		this.dataSource = ds;
	}

	public File createFileProperty(String propertyName) throws IOException {
		FileProperty fp = getFilePropertyObject(propertyName);
		if (fp != null) {
			return new File(fp.getFile());
		} else {
			String name = "gdms" + System.currentTimeMillis();
			while (new File(baseDir + "/" + name).exists()) {
				name = "gdms" + System.currentTimeMillis();
			}
			File ret = new File(baseDir + "/" + name);
			Source source = getSource();
			fp = new FileProperty();
			fp.setName(propertyName);
			fp.setFile(ret.getName());
			source.getFileProperty().add(fp);
			return createFile(ret);
		}
	}

	public void removeFromXML() {
		String[] fp = getFilePropertyNames();
		for (String filePropertyName : fp) {
			File fileProperty = getFileProperty(filePropertyName);
			fileProperty.delete();
		}

		List<String> referencedSources = getSource().getReferencedSource();
		List<Source> sourceList = sources.getSource();
		for (Source source : sourceList) {
			if (referencedSources.contains(source.getName())) {
				List<String> referencingList = source.getReferencingSource();
				referencingList.remove(name);
			}
		}

		sources.getSource().remove(getSource());
	}

	private Source getSource() {
		return getSource(name);
	}

	private Source getSource(String name) {
		for (int i = 0; i < sources.getSource().size(); i++) {
			Source source = sources.getSource().get(i);
			if (source.getName().equals(name)) {
				return source;
			}
		}

		return null;
	}

	private File createFile(File file) throws IOException {
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			if (!parentFile.mkdirs()) {
				throw new IOException("Cannot create the file: " + file);
			}
		}
		if (file.exists()) {
			return file;
		} else if (file.createNewFile()) {
			return file;
		} else {
			throw new IOException("Cannot create the file: " + file);
		}
	}

	public FileProperty getFilePropertyObject(String propertyName) {
		List<FileProperty> fps = getSource().getFileProperty();
		for (FileProperty fileProperty : fps) {
			if (fileProperty.getName().equals(propertyName)) {
				return fileProperty;
			}
		}
		return null;
	}

	public File getFileProperty(String propertyName) {
		FileProperty fp = getFilePropertyObject(propertyName);
		if (fp != null) {
			return new File(baseDir, fp.getFile());
		}
		return null;
	}

	public byte[] getFilePropertyContents(String propertyName)
			throws IOException {
		File fileProperty = getFileProperty(propertyName);
		if (fileProperty != null) {
			FileInputStream fis = new FileInputStream(fileProperty);
			DataInputStream dis = new DataInputStream(fis);
			byte[] content = new byte[(int) fis.getChannel().size()];
			dis.readFully(content);
			dis.close();

			return content;
		} else {
			return null;
		}
	}

	public String getFilePropertyContentsAsString(String propertyName)
			throws IOException {
		byte[] filePropertyContents = getFilePropertyContents(propertyName);
		if (filePropertyContents == null) {
			return null;
		} else {
			return new String(filePropertyContents);
		}
	}

	public String getProperty(String propertyName) {
		Property p = getPropertyObject(propertyName);
		if (p == null) {
			return null;
		} else {
			return p.getValue();
		}
	}

	private Property getPropertyObject(String propertyName) {
		List<Property> props = getSource().getProperty();
		for (Property property : props) {
			if (property.getName().equals(propertyName)) {
				return property;
			}
		}

		return null;
	}

	public void putProperty(String propertyName, String value) {
		Property p = new Property();
		p.setName(propertyName);
		p.setValue(value);
		getSource().getProperty().add(p);
	}

	public void deleteProperty(String propertyName) throws IOException {
		FileProperty fp = getFilePropertyObject(propertyName);
		if (fp != null) {
			File file = new File(baseDir, fp.getFile());
			if (!file.delete()) {
				throw new IOException(
						"Cannot delete property. Cannot delete associated file: "
								+ file.getAbsolutePath());
			}
			getSource().getFileProperty().remove(fp);
		} else {
			Property p = getPropertyObject(propertyName);
			if (p != null) {
				getSource().getProperty().remove(p);
			}
		}
	}

	public boolean hasProperty(String propertyName) {
		boolean fileProperty = getFileProperty(propertyName) != null;
		boolean stringProperty = getProperty(propertyName) != null;

		return fileProperty || stringProperty;
	}

	public String[] getFilePropertyNames() {
		List<FileProperty> fp = getSource().getFileProperty();
		String[] ret = new String[fp.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = fp.get(i).getName();
		}
		return ret;
	}

	public String[] getStringPropertyNames() throws IOException {
		List<Property> props = getSource().getProperty();
		String[] ret = new String[props.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = props.get(i).getName();
		}
		return ret;
	}

	public void setName(String name) {
		getSource().setName(name);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public DataSourceDefinition getDef() {
		return def;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public Sources getSources() {
		return sources;
	}

	public boolean isWellKnownName() {
		return isWellKnownName;
	}

	public Boolean isUpToDate() throws DriverException {
		String checksumFromXML = getSource().getChecksum();
		if (checksumFromXML == null) {
			return null;
		} else {
			if (getChecksum().equals(checksumFromXML)) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		}
	}

	public String getChecksum() throws DriverException {
		return def.calculateChecksum();
	}

	private HashSet<String> getReferencingSources(String name) {
		List<String> deps = getSource(name).getReferencingSource();
		HashSet<String> ret = new HashSet<String>();
		for (String dep : deps) {
			ret.add(dep);
			ret.addAll(getReferencingSources(dep));
		}

		return ret;
	}

	private HashSet<String> getReferencedSources(String name) {
		List<String> deps = getSource(name).getReferencedSource();
		HashSet<String> ret = new HashSet<String>();
		for (String dep : deps) {
			ret.add(dep);
			ret.addAll(getReferencedSources(dep));
		}

		return ret;
	}

	public String[] getReferencingSources() {
		HashSet<String> deps = getReferencingSources(name);
		return deps.toArray(new String[0]);
	}

	public String[] getReferencedSources() {
		HashSet<String> deps = getReferencedSources(name);
		return deps.toArray(new String[0]);
	}

	public int getType() {
		return def.getType();
	}

	public DBSource getDBSource() {
		if (def instanceof DBTableSourceDefinition) {
			return ((DBTableSourceDefinition)def).getSourceDefinition();
		}
		return null;
	}

	public File getFile() {
		if (def instanceof FileSourceDefinition) {
			return ((FileSourceDefinition) def).getFile();
		}
		return null;
	}

	public ObjectDriver getObject() {
		if (def instanceof ObjectSourceDefinition) {
			return ((ObjectSourceDefinition) def).getObject();
		}
		return null;
	}

	public String getSQL() {
		if (def instanceof SQLSourceDefinition) {
			return ((SQLSourceDefinition) def).getSQL();
		}
		return null;
	}

	public boolean isDBSource() {
		return (getType() & SourceManager.DB) == SourceManager.DB;
	}

	public boolean isFileSource() {
		return (getType() & SourceManager.FILE) == SourceManager.FILE;
	}

	public boolean isObjectSource() {
		return (getType() & SourceManager.MEMORY) == SourceManager.MEMORY;
	}

	public boolean isSQLSource() {
		return (getType() & SourceManager.SQL) == SourceManager.SQL;
	}

}

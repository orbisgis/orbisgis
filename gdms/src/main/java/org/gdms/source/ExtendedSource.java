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
import org.gdms.data.wms.WMSSource;
import org.gdms.data.wms.WMSSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.directory.DbDefinitionType;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.FileDefinitionType;
import org.gdms.source.directory.FileProperty;
import org.gdms.source.directory.ObjectDefinitionType;
import org.gdms.source.directory.Property;
import org.gdms.source.directory.Source;
import org.gdms.source.directory.Sources;
import org.gdms.source.directory.SqlDefinitionType;
import org.gdms.source.directory.WmsDefinitionType;

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
			IllegalAccessException, ClassNotFoundException, DriverException {
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

			// Add dependencies to other sources
			List<Source> srcList = sources.getSource();
			for (Source src : srcList) {
				if (depNames.contains(src.getName())) {
					src.getReferencingSource().add(name);
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
				this.def = SQLSourceDefinition.createFromXML(dsf,
						(SqlDefinitionType) definitionType);
			} else if (definitionType instanceof WmsDefinitionType) {
				this.def = WMSSourceDefinition.createFromXML(dsf,
						(WmsDefinitionType) definitionType);
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
			File parentFile = ret.getParentFile();
			if (!parentFile.exists()) {
				if (!parentFile.mkdirs()) {
					throw new IOException("Cannot create the file: " + ret);
				}
			}

			return ret;
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
		return def.calculateChecksum(getDataSource());
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

	@Override
	public String getDriverId() {
		return def.getDriverId();
	}

	public int getType() {
		try {
			return def.getType();
		} catch (DriverLoadException e) {
			return SourceManager.UNKNOWN;
		}
	}

	@Override
	public String getTypeName() {
		return def.getTypeName();
	}

	public DBSource getDBSource() {
		if (def instanceof DBTableSourceDefinition) {
			return ((DBTableSourceDefinition) def).getSourceDefinition();
		}
		return null;
	}

	@Override
	public WMSSource getWMSSource() {
		if (def instanceof WMSSourceDefinition) {
			return ((WMSSourceDefinition) def).getWMSSource();
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

	@Override
	public boolean isWMSSource() {
		return (getType() & SourceManager.WMS) == SourceManager.WMS;
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

	public void init() throws DriverException {
		def.initialize();
	}

}

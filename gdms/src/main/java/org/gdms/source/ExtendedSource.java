/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.source;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.InitializationException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.wms.WMSSource;
import org.gdms.data.wms.WMSSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.directory.FileProperty;
import org.gdms.source.directory.Property;
import org.gdms.source.directory.Source;
import org.gdms.source.directory.Sources;

/**
 * Default implementation for a Source.
 *
 * This source is linked to a DataSourceDefinition, and optionally to a DataSource.
 * @author Antoine Gourlay
 */
public final class ExtendedSource implements org.gdms.source.Source {

        private DataSource dataSource;
        DataSourceDefinition def;
        private String baseDir;
        private String name;
        private Sources sources;
        private boolean isWellKnownName;
        private static final Logger LOG = Logger.getLogger(ExtendedSource.class);

        /**
         * Creates a new source
         * @param dsf the {@code DataSourceFactory} associated with this source and
         *      all objects linked to it
         * @param sources the XML sources associated with the {@code DefaultSourceManager}
         * @param name the name of this source
         * @param isWellKnownName true if the name of the source was not automatically
         *      generated, false otherwise
         * @param baseDir the directory this source will store its properties into
         * @param dataSource the {@code Datasource} associated with this source.
         *      Can be null. This value is not taken into account if the source is already
         *      present in the serialized XML.
         * @param def the {@code DataSourceDefinition} associated with this source.
         *      Can be null. This value is not taken into account if the source is already
         *      present in the serialized XML.
         */
        public ExtendedSource(DataSourceFactory dsf, Sources sources, String name,
                boolean isWellKnownName, String baseDir, DataSource dataSource,
                DataSourceDefinition def) {
                this.baseDir = baseDir;
                this.name = name;
                this.sources = sources;
                this.isWellKnownName = isWellKnownName;
                LOG.trace("Constructor for source " + name);
                Source xmlSrc = getSource();
                // If the source is new we put it in the XML
                if (xmlSrc == null) {
                        LOG.trace("New source");
                        this.dataSource = dataSource;
                        this.def = def;
                        this.def.setDataSourceFactory(dsf);
                        try {
                                refreshXml();
                        } catch (DriverException e) {
                                throw new InitializationException(e);
                        }
                } else {
                        LOG.trace("Source already exists, reading from XML");
                        try {
                                this.def = xmlSrc.getDefinition().toDataSourceDefinition();
                        } catch (InstantiationException e) {
                                // should ever raise these exceptions
                                throw new InitializationException(e);
                        } catch (IllegalAccessException e) {
                                // should ever raise these exceptions
                                throw new InitializationException(e);
                        } catch (ClassNotFoundException e) {
                                // should ever raise these exceptions
                                throw new InitializationException(e);
                        }

                        this.def.setDataSourceFactory(dsf);
                }
        }

        private void refreshXml() throws DriverException {
                Source source = new Source();
                source.setName(name);
                source.setDefinition(def.getDefinition());
                sources.getSource().add(source);
                // Add this dependencies
                List<String> depNames = def.getSourceDependencies();
                List<String> referencedSources = source.getReferencedSource();
                referencedSources.addAll(depNames);
                // Add dependencies to other sources
                List<Source> srcList = sources.getSource();
                for (Source src : srcList) {
                        if (depNames.contains(src.getName())) {
                                src.getReferencingSource().add(name);
                        }
                }
        }

        /**
         * Returns the {@code DataSource] associated with this source.
         *
         * If this source was loaded from XML, this returns null unless a
         * datasource was provided with {@code setDataSource}
         * @return
         */
        public DataSource getDataSource() {
                return dataSource;
        }

        @Override
        public DataSourceDefinition getDataSourceDefinition() {
                return def;
        }

        /**
         * Sets the {@code DataSource} associated with this source.
         * @param ds
         */
        public void setDatasource(DataSource ds) {
                this.dataSource = ds;
        }

        @Override
        public File createFileProperty(String propertyName) throws IOException {
                LOG.trace("Creating file for property : " + propertyName);
                FileProperty fp = getFilePropertyObject(propertyName);
                if (fp != null) {
                        return new File(fp.getFile());
                } else {
                        String sourceName = "gdms" + System.currentTimeMillis();
                        while (new File(baseDir + "/" + sourceName).exists()) {
                                sourceName = "gdms" + System.currentTimeMillis();
                        }
                        File ret = new File(baseDir + "/" + sourceName);
                        Source source = getSource();
                        fp = new FileProperty();
                        fp.setName(propertyName);
                        fp.setFile(ret.getName());
                        source.getFileProperty().add(fp);
                        File parentFile = ret.getParentFile();
                        if (!parentFile.exists() && !parentFile.mkdirs()) {
                                throw new IOException("Cannot create the file: " + ret);
                        }

                        return ret;
                }
        }

        /**
         * Unregisters this source from the XML serialized view.
         */
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

        /**
         * Gets the {@code FileProperty} associated with this source
         *  which has the name <tt>propertyName</tt>.
         * @param propertyName the name of the property
         * @return the property, of null if not found
         */
        public FileProperty getFilePropertyObject(String propertyName) {
                List<FileProperty> fps = getSource().getFileProperty();
                for (FileProperty fileProperty : fps) {
                        if (fileProperty.getName().equals(propertyName)) {
                                return fileProperty;
                        }
                }
                return null;
        }

        @Override
        public File getFileProperty(String propertyName) {
                FileProperty fp = getFilePropertyObject(propertyName);
                if (fp != null) {
                        return new File(baseDir, fp.getFile());
                }
                return null;
        }

        @Override
        public byte[] getFilePropertyContents(String propertyName)
                throws IOException {
                File fileProperty = getFileProperty(propertyName);
                if (fileProperty != null) {
                        FileInputStream fis = new FileInputStream(fileProperty);
                        DataInputStream dis = null;
                        byte[] content;
                        try {
                                dis = new DataInputStream(fis);
                                content = new byte[(int) fis.getChannel().size()];
                                dis.readFully(content);
                        } finally {
                                if (dis != null) {
                                        dis.close();
                                }
                        }
                        return content;
                } else {
                        return null;
                }
        }

        @Override
        public String getFilePropertyContentsAsString(String propertyName)
                throws IOException {
                byte[] filePropertyContents = getFilePropertyContents(propertyName);
                if (filePropertyContents == null) {
                        return null;
                } else {
                        return new String(filePropertyContents);
                }
        }

        @Override
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

        @Override
        public void putProperty(String propertyName, String value) {
                Property p = new Property();
                p.setName(propertyName);
                p.setValue(value);
                getSource().getProperty().add(p);
        }

        @Override
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

        @Override
        public boolean hasProperty(String propertyName) {
                boolean fileProperty = getFileProperty(propertyName) != null;
                boolean stringProperty = getProperty(propertyName) != null;

                return fileProperty || stringProperty;
        }

        @Override
        public String[] getFilePropertyNames() {
                List<FileProperty> fp = getSource().getFileProperty();
                String[] ret = new String[fp.size()];
                for (int i = 0; i < ret.length; i++) {
                        ret[i] = fp.get(i).getName();
                }
                return ret;
        }

        @Override
        public String[] getStringPropertyNames() throws IOException {
                List<Property> props = getSource().getProperty();
                String[] ret = new String[props.size()];
                for (int i = 0; i < ret.length; i++) {
                        ret[i] = props.get(i).getName();
                }
                return ret;
        }

        /**
         * Sets the name of this source
         * @param name a non-empty string
         */
        public void setName(String name) {
                getSource().setName(name);
                this.name = name;
        }

        @Override
        public String getName() {
                return name;
        }

        /**
         * Gets the directory this source saves its properties into
         * @return a path to the directory
         */
        public String getBaseDir() {
                return baseDir;
        }

        /**
         * Gets the XML sources associated with the {@code DefaultSourceManager}
         * @return the XML sources
         */
        public Sources getSources() {
                return sources;
        }

        @Override
        public boolean isWellKnownName() {
                return isWellKnownName;
        }

        @Override
        public boolean isUpToDate() throws DriverException {
                String checksumFromXML = getSource().getChecksum();
                if (checksumFromXML == null) {
                        return false;
                } else {
                        return getChecksum().equals(checksumFromXML);
                }
        }

        /**
         * Gets the checksum of this source, calculated from its
         *      {@code DataSourceDefinition} and its {@code DataSource}
         * @return a String checksum
         * @throws DriverException
         */
        public String getChecksum() throws DriverException {
                return def.calculateChecksum(getDataSource());
        }

        private Set<String> getReferencingSources(String name) {
                List<String> deps = getSource(name).getReferencingSource();
                Set<String> ret = new HashSet<String>();
                for (String dep : deps) {
                        ret.add(dep);
                        ret.addAll(getReferencingSources(dep));
                }

                return ret;
        }

        private Set<String> getReferencedSources(String name) {
                List<String> deps = getSource(name).getReferencedSource();
                Set<String> ret = new HashSet<String>();
                for (String dep : deps) {
                        ret.add(dep);
                        ret.addAll(getReferencedSources(dep));
                }

                return ret;
        }

        @Override
        public String[] getReferencingSources() {
                Set<String> deps = getReferencingSources(name);
                return deps.toArray(new String[deps.size()]);
        }

        @Override
        public String[] getReferencedSources() {
                Set<String> deps = getReferencedSources(name);
                return deps.toArray(new String[deps.size()]);
        }

        @Override
        public String getDriverId() {
                return def.getDriverId();
        }

        @Override
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

        @Override
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

        @Override
        public File getFile() {
                if (def instanceof FileSourceDefinition) {
                        return ((FileSourceDefinition) def).getFile();
                }
                return null;
        }

        @Override
        public ObjectDriver getObject() {
                if (def instanceof ObjectSourceDefinition) {
                        return ((ObjectSourceDefinition) def).getObject();
                }
                return null;
        }

        @Override
        public boolean isDBSource() {
                return (getType() & SourceManager.DB) == SourceManager.DB;
        }

        @Override
        public boolean isWMSSource() {
                return (getType() & SourceManager.WMS) == SourceManager.WMS;
        }

        @Override
        public boolean isFileSource() {
                return (getType() & SourceManager.FILE) == SourceManager.FILE;
        }

        @Override
        public boolean isObjectSource() {
                return (getType() & SourceManager.MEMORY) == SourceManager.MEMORY;
        }

        @Override
        public boolean isSQLSource() {
                return (getType() & SourceManager.SQL) == SourceManager.SQL;
        }

        @Override
        public boolean isSystemTableSource() {
                return (getType() & SourceManager.SYSTEM_TABLE) == SourceManager.SYSTEM_TABLE;
        }

        /**
         * Initializes this source's {@code DataSourceDefinition}.
         *
         * This method is a shorthand for
         * <code>
         * getDataSourceDefinition().initialize();
         * </code>
         * @throws DriverException
         */
        public void init() throws DriverException {
                def.initialize();
        }
}

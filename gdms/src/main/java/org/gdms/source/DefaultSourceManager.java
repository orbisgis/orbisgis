/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.source;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DataSourceFinalizationException;
import org.gdms.data.InitializationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.OCCounterDecorator;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.exporter.ExportSourceDefinition;
import org.gdms.data.exporter.FileExportDefinition;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.importer.FileImportDefinition;
import org.gdms.data.importer.ImportSourceDefinition;
import org.gdms.data.memory.MemorySourceCreation;
import org.gdms.data.memory.MemorySourceDefinition;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.schema.RootSchema;
import org.gdms.data.schema.Schema;
import org.gdms.data.sql.SQLSourceDefinition;
import org.gdms.data.stream.StreamSource;
import org.gdms.data.stream.StreamSourceDefinition;
import org.gdms.data.system.SystemSource;
import org.gdms.data.system.SystemSourceDefinition;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.asc.AscDriver;
import org.gdms.driver.csv.CSVDriver;
import org.gdms.driver.dbf.DBFDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.dxf.DXFDriver;
import org.gdms.driver.gdms.GdmsDriver;
import org.gdms.driver.geojson.GeoJsonImporter;
import org.gdms.driver.geotif.TifDriver;
import org.gdms.driver.hsqldb.HSQLDBDriver;
import org.gdms.driver.jpg.JPGDriver;
import org.gdms.driver.mifmid.MifMidDriver;
import org.gdms.driver.png.PngDriver;
import org.gdms.driver.postgresql.PostgreSQLDriver;
import org.gdms.driver.shapefile.ShapefileDriver;
import org.gdms.driver.wms.SimpleWMSDriver;
import org.gdms.source.directory.Source;
import org.gdms.source.directory.Sources;
import org.gdms.sql.engine.Engine;
import org.gdms.sql.engine.ParseException;
import org.gdms.sql.engine.SQLStatement;

public final class DefaultSourceManager implements SourceManager {

        private static final Logger LOG = Logger.getLogger(DefaultSourceManager.class);
        private Map<String, ExtendedSource> nameSource;
        private Map<String, String> nameMapping;
        private final List<SourceListener> listeners = new ArrayList<SourceListener>();
        private final List<CommitListener> commitListeners = new ArrayList<CommitListener>();
        private DataSourceFactory dsf;
        private DriverManager dm = new DriverManager();
        private String baseDir;
        private Sources sources;
        private JAXBContext jc;
        private Schema schema;
        private String lastUID = "gdms" + System.currentTimeMillis();
        private List<String> contextPaths = new ArrayList<String>();
        private static final Pattern DOT = Pattern.compile("\\.");
        public static final String SPATIAL_REF_SYSTEM = "spatial_ref_table";
        public static final String SPATIAL_REF_TABLE_SYSTEM_PATH = "spatial_ref_sys_extended.gdms";

        public DefaultSourceManager(DataSourceFactory dsf, String baseDir) {
                dm.registerDriver(CSVDriver.class);
                dm.registerDriver(DBFDriver.class);
                dm.registerDriver(ShapefileDriver.class);
                dm.registerDriver(PostgreSQLDriver.class);
                dm.registerDriver(HSQLDBDriver.class);
                dm.registerDriver(GdmsDriver.class);
                dm.registerDriver(TifDriver.class);
                dm.registerDriver(AscDriver.class);
                dm.registerDriver(JPGDriver.class);
                dm.registerDriver(PngDriver.class);
                dm.registerDriver(SimpleWMSDriver.class);
                dm.registerImporter(DXFDriver.class);
                dm.registerImporter(MifMidDriver.class);
                dm.registerImporter(GeoJsonImporter.class);
                this.dsf = dsf;
                this.baseDir = baseDir;
                contextPaths.add("org.gdms.source.directory");
        }

        @Override
        public void init() throws IOException {
                if (nameSource != null && !nameSource.isEmpty()) {
                        throw new InitializationException("Cannot be initialized: "
                                + "there already are sources associated with this SourceManager.");
                }
                changeSourceInfoDirectory(baseDir);
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
                LOG.trace("Source manager initialized");
        }

        private File getDirectoryFile() {
                return new File(baseDir, "directory.xml");
        }

        @Override
        public void saveStatus() throws DriverException {
                try {
                        Sources sourcesToStore = new Sources();
                        List<Source> sourceElements = sourcesToStore.getSource();
                        // Get the well known sources
                        for (Source sourceElement : sources.getSource()) {
                                ExtendedSource src = nameSource.get(sourceElement.getName());
                                if (src.isSystemTableSource()) {
                                        continue;
                                }
                                if (src.isWellKnownName()) {
                                        removeNonWellKnownDependencies(sourceElement);
                                        sourceElements.add(sourceElement);
                                }
                        }
                        // Calculate the checksum
                        for (Source source : sourceElements) {
                                ExtendedSource src = nameSource.get(source.getName());
                                if (src.isSystemTableSource() || src.isLiveSource()) {
                                        continue;
                                }
                                source.setChecksum(src.getChecksum());
                        }
                        createFile(getDirectoryFile());
                        FileOutputStream fileOutputStream = null;
                        try {
                                fileOutputStream = new FileOutputStream(
                                        getDirectoryFile());
                                jc.createMarshaller().marshal(sourcesToStore, fileOutputStream);
                        } finally {
                                if (fileOutputStream != null) {
                                        fileOutputStream.close();
                                }
                        }
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
                        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                                throw new IOException("Cannot create dirs: " + file);
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

        @Override
        public void setDriverManager(DriverManager dm) {
                this.dm = dm;
        }

        @Override
        public DriverManager getDriverManager() {
                return this.dm;
        }

        @Override
        public boolean addSourceListener(SourceListener e) {
                return listeners.add(e);
        }

        @Override
        public boolean removeSourceListener(SourceListener o) {
                return listeners.remove(o);
        }

        @Override
        public void removeAll() throws IOException {
                File f = new File(baseDir);
                File[] files = f.listFiles();
                for (File file : files) {
                        if (file.getName().startsWith(".")) {
                                continue;
                        }
                        if (!file.getName().equals("directory.xml") && !file.isDirectory()
                                && !file.delete()) {
                                throw new IOException(
                                        "Cannot delete file associated with property: "
                                        + file.getAbsolutePath());

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

                loadSystemTables();
        }

        @Override
        public boolean remove(String name) {
                return remove(name, false);
        }

        /**
         * Removes a source and delete its physic storage.
         *
         * @param name
         * @param purge
         * @return false if there was no source to remove, true otherwise
         */
        public boolean remove(String name, boolean purge) {
                LOG.trace("Removing source");
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
                                StringBuilder msg = new StringBuilder();
                                msg.append("The source is used by the following sources: ");
                                for (String dep : referencingSources) {
                                        msg.append(dep).append(", ");
                                }
                                String s = msg.substring(0, msg.length() - 2);
                                throw new IllegalStateException(s);
                        }
                }

                toRemove.removeFromXML();
                nameSource.remove(name);

                removeFromSchema(name);

                for (String nwk : notWellKnown) {
                        remove(nwk);
                }

                fireSourceRemoved(name);

                if (purge) {
                        try {
                                toRemove.getDataSourceDefinition().delete();
                        } catch (DriverException ex) {
                                throw new IllegalStateException(ex);
                        }
                }
                //We have a <File,Driver> association in the DriverManager, we must get rid of it.
                File assoc = toRemove.getFile();
                if (assoc != null) {
                        dm.removeFile(assoc);
                }

                return true;
        }

        @Override
        public boolean delete(String name) {
                return remove(name, true);
        }

        private void fireSourceRemoved(String name) {
                List<String> names = getNamesFor(name);
                List<String> namesToRemove = names;
                for (String nameToRemove : namesToRemove) {
                        nameMapping.remove(nameToRemove);
                }

                fireSourceRemoved(name, names);
        }

        private void fireSourceRemoved(String name, List<String> names) {
                SourceListener[] list = listeners.toArray(new SourceListener[listeners.size()]);
                for (SourceListener listener : list) {
                        listener.sourceRemoved(new SourceRemovalEvent(name, names.toArray(new String[names.size()]), true, this));
                }
        }

        private List<String> getNamesFor(String dsName) {
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

        @Override
        public org.gdms.source.Source getSource(String name) {
                return getExtendedSource(name);
        }

        @Override
        public void register(String name, URI uri) {
                registerURI(name, true, uri);
        }

        private void registerURI(String name, boolean wellKnown, URI uri) {
                final String scheme = uri.getScheme().toLowerCase();
                if ("file".equals(scheme)) {
                        if (wellKnown) {
                                register(name, new FileSourceCreation(new File(uri), null));
                        } else {
                                register(name, false, new FileSourceDefinition(new File(uri), DriverManager.DEFAULT_SINGLE_TABLE_NAME));
                        }
                } else if (scheme.startsWith("http")) {
                        throw new UnsupportedOperationException("Unsupported URI: " + uri);
                } else {
                        String table = null;
                        String sch = null;
                        String user = null;
                        String password = null;
                        boolean ssl = false;
                        String dbName = uri.getPath();
                        if (dbName.startsWith("/")) {
                                dbName = dbName.substring(1);
                        }
                        String[] params = uri.getQuery().split("&");
                        for (int i = 0; i < params.length; i++) {
                                String[] vals = params[i].split("=");
                                if ("table".equalsIgnoreCase(vals[0])) {
                                        table = vals[1];
                                } else if ("schema".equalsIgnoreCase(vals[0])) {
                                        sch = vals[1];
                                } else if ("user".equalsIgnoreCase(vals[0])) {
                                        user = vals[1];
                                } else if ("password".equalsIgnoreCase(vals[0])) {
                                        password = vals[1];
                                } else if ("ssl".equalsIgnoreCase(vals[0]) && "true".equalsIgnoreCase(vals[1])) {
                                        ssl = true;
                                }
                        }
                        DBSource s = new DBSource(uri.getHost(), uri.getPort(), dbName, user,
                                password, sch, table, "jdbc:" + scheme, ssl);
                        register(name, wellKnown, new DBTableSourceDefinition(s));
                }
        }

        @Override
        public void register(String name, File file) {
                register(name, new FileSourceCreation(file, null));
        }

        @Override
        public void register(String name, String sql) throws ParseException {
                SQLStatement[] s = Engine.parse(sql, dsf.getProperties());
                if (s.length > 1) {
                        throw new ParseException("Cannot create a DataSource from multiple SQL instructions!");
                }
                register(name, new SQLSourceDefinition(s[0]));
        }

        @Override
        public void register(String name, DBSource dbTable) {
                register(name, new DBTableSourceDefinition(dbTable));
        }

        @Override
        public void register(String name, StreamSource streamSource) {
                register(name, new StreamSourceDefinition(streamSource));
        }

        @Override
        public void register(String name, MemoryDriver driver) {
                register(name, new MemorySourceCreation(driver));
        }

        @Override
        public void register(String name, DataSourceCreation cr) {
                try {
                        cr.setDataSourceFactory(dsf);
                        String[] t = cr.getAvailableTables();
                        if (t.length > 1) {
                                for (int i = 0; i < t.length; i++) {
                                        register(name + "." + t[i], cr.create(t[i]));
                                }
                        } else if (t.length == 1) {
                                register(name, cr.create(t[0]));
                        } else {
                                throw new IllegalStateException("Driver for " + name + " declares no tables!");
                        }
                } catch (DriverException ex) {
                        LOG.error("Could not list tables for " + name, ex);
                }
        }

        @Override
        public void register(String name, DataSourceDefinition def) {
                register(name, true, def);
        }

        private void register(String name, boolean wellKnownName,
                DataSourceDefinition dsd) {
                register(name, wellKnownName, dsd, null);
        }

        private void register(String name, boolean isWellKnownName,
                DataSourceDefinition dsd, DataSource ds) {

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
        }

        private void register(String name, ExtendedSource src) {
                if (exists(name)) {
                        throw new SourceAlreadyExistsException(name);
                }
                LOG.trace("Registering source " + name);
                nameSource.put(name, src);
                try {
                        addToSchema(name, src);
                } catch (DriverException ex) {
                        LOG.error("Failed to add " + name + " to Gdms schema.", ex);
                }
                fireSourceAdded(name, src.isWellKnownName());
        }

        private void addToSchema(String name, ExtendedSource src) throws DriverException {
                if (name.isEmpty()) {
                        throw new IllegalArgumentException("Empty table name!");
                }

                // split on the dots '.' into
                // schema1.schema2.schema3.table1

                String[] l = DOT.split(name);
                final DataSourceDefinition dsd = src.getDataSourceDefinition();

                if (l.length == 0) {
                        // just a table, we add it to the root schema
                        String table = dsd.getDriverTableName();
                        schema.addTable(name, dsd.getSchema().getTableByName(table));
                } else {
                        Schema s = schema;
                        // we get down (possibly creating the schemas on the way)
                        // to the last schema before the table
                        for (int i = 0; i < l.length - 1; i++) {
                                Schema n = s.getSubSchemaByName(l[i]);
                                if (n == null) {
                                        n = new DefaultSchema(l[i]);
                                        s.addSubSchema(n);
                                }
                                s = n;
                        }
                        s.addTable(l[l.length - 1], dsd.getSchema().getTableByName(l[l.length - 1]));
                }
        }

        private void removeFromSchema(String name) {
                if (name.isEmpty()) {
                        throw new IllegalArgumentException("Empty table name!");
                }

                // split on the dots '.' into
                // schema1.schema2.schema3.table1

                String[] l = DOT.split(name);

                if (l.length <= 1) {
                        // just a table, we remove it from the root schema
                        schema.removeTable(name);
                } else {
                        Deque<Schema> path = new ArrayDeque<Schema>();
                        path.add(schema);
                        // we get down
                        // to the last schema before the table
                        for (int i = 0; i < l.length - 1; i++) {
                                final Schema n = path.getFirst().getSubSchemaByName(l[i]);
                                path.addFirst(n);
                        }

                        boolean stop = false;
                        while (!path.isEmpty() && !stop) {
                                // take the last schema in the path (top of the pile)
                                final Schema n = path.pollFirst();
                                n.removeTable(l[l.length - 1]);
                                if (n.getTableCount() != 0 || n.getSubSchemaNames().length != 0) {
                                        // the schema is still needed, we must not remove it
                                        stop = true;
                                } else {
                                        Schema p = n.getParentSchema();
                                        if (p != null) {
                                                p.removeSubSchema(n.getName());
                                        } else {
                                                // we have reached root, it stays were it is...
                                                stop = true;
                                        }
                                }
                        }
                }
        }

        private void fireSourceAdded(String name, boolean wellKnownName) {
                SourceListener[] list = listeners.toArray(new SourceListener[listeners.size()]);
                for (SourceListener listener : list) {
                        listener.sourceAdded(new SourceEvent(name, wellKnownName, this));
                }
        }

        @Override
        public String getUID() {
                String name = "gdms" + System.currentTimeMillis();

                while (name.equals(lastUID)) {
                        name = "gdms" + System.currentTimeMillis();
                }

                lastUID = name;
                return name;
        }

        @Override
        public String getUniqueName(String base) {
                String tmpName = base;
                int i = 0;
                while (exists(tmpName)) {
                        tmpName = base + "_" + i;
                        i++;
                }

                return tmpName;
        }

        @Override
        public String nameAndRegister(URI uri) {
                String name = getUID();
                registerURI(name, false, uri);
                return name;
        }

        @Override
        public String nameAndRegister(File file) {
                String name = getUID();
                register(name, false, new FileSourceDefinition(file, DriverManager.DEFAULT_SINGLE_TABLE_NAME));
                return name;
        }

        @Override
        public String nameAndRegister(String sql) throws ParseException {
                SQLStatement[] s = Engine.parse(sql, dsf.getProperties());
                if (s.length > 1) {
                        throw new ParseException("Cannot create a DataSource from multiple SQL instructions!");
                }
                return nameAndRegister(new SQLSourceDefinition(s[0]));
        }

        @Override
        public String nameAndRegister(DBSource dbTable) {
                String name = getUID();
                register(name, false, new DBTableSourceDefinition(dbTable));
                return name;
        }

        @Override
        public String nameAndRegister(StreamSource streamSource) {
                String name = getUID();
                register(name, false, new StreamSourceDefinition(streamSource));
                return name;
        }

        @Override
        public String nameAndRegister(MemoryDriver driver, String tableName) {
                String name = getUID();
                register(name, false, new MemorySourceDefinition(driver, tableName));
                return name;
        }

        @Override
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

        @Override
        public String nameAndRegister(DataSourceCreation dsc) {
                String name = getUID();
                register(name, dsc);
                return name;
        }

        @Override
        public String getSourceName(DataSourceDefinition dsd) {
                Iterator<String> it = nameSource.keySet().iterator();
                while (it.hasNext()) {
                        String name = it.next();
                        ExtendedSource src = nameSource.get(name);
                        if (src.getDataSourceDefinition().equals(dsd)) {
                                return name;
                        }
                }

                return null;
        }

        public DataSource getDataSource(String name, ProgressMonitor pm)
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
                                        try {
                                                if ((dsd.getType() & SourceManager.LIVE) == 0) {
                                                        src.setDatasource(ds);
                                                }
                                        } catch (DriverException ex) {
                                                throw new DataSourceCreationException(ex);
                                        }
                                        dataSource = ds;
                                }
                        }
                }

                return dataSource;
        }

        @Override
        public void addName(String dsName, String newName)
                throws NoSuchTableException {
                if (!exists(dsName)) {
                        throw new NoSuchTableException(dsName);
                }
                if (exists(newName)) {
                        throw new SourceAlreadyExistsException(newName);
                }
                nameMapping.put(newName, dsName);
        }

        @Override
        public void rename(String dsName, String newName) {
                if (nameSource.containsKey(dsName)) {
                        if (exists(newName)) {
                                throw new SourceAlreadyExistsException(newName);
                        }
                        ExtendedSource value = nameSource.remove(dsName);
                        nameSource.put(newName, value);
                        value.setName(newName);

                        // update schema
                        removeFromSchema(dsName);
                        try {
                                addToSchema(newName, value);
                        } catch (DriverException ex) {
                                LOG.error("Failed to add " + newName + " to Gdms schema.", ex);
                        }
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

        private void fireNameChanged(String dsName, String newName) {
                SourceListener[] list = listeners.toArray(new SourceListener[listeners.size()]);
                for (SourceListener listener : list) {
                        listener.sourceNameChanged(new SourceEvent(dsName, true, this,
                                newName));
                }

        }

        private void changeNameMapping(String dsName, String newName) {
                List<String> namesToChange = getNamesFor(dsName);
                for (int i = 0; i < namesToChange.size(); i++) {
                        nameMapping.put(namesToChange.get(i), newName);
                }
        }

        @Override
        public boolean exists(String sourceName) {
                return nameSource.containsKey(sourceName)
                        || nameMapping.containsKey(sourceName);
        }

        @Override
        public String getMainNameFor(String dsName) throws NoSuchTableException {
                if (dsName.startsWith("PUBLIC.")) {
                        dsName = dsName.substring(7);
                }
                if (nameMapping.containsKey(dsName)) {
                        return nameMapping.get(dsName);
                } else if (nameSource.containsKey(dsName)) {
                        return dsName;
                } else {
                        throw new NoSuchTableException(dsName);
                }
        }

        @Override
        public void shutdown() throws DataSourceFinalizationException {
                LOG.trace("Shutdown");
                for (String name : nameSource.keySet()) {
                        DataSourceDefinition dataSourceDefinition = nameSource.get(name).getDataSourceDefinition();
                        if (dataSourceDefinition != null) {
                                dataSourceDefinition.freeResources(name);
                        }
                }
                nameSource.clear();
        }

        @Override
        public boolean isEmpty() {
                return nameSource.isEmpty() && nameMapping.isEmpty();
        }

        @Override
        public boolean isEmpty(boolean ignoreSystem) {
                if (!ignoreSystem) {
                        return isEmpty();
                } else {
                        for (ExtendedSource s : nameSource.values()) {
                                if (!s.isSystemTableSource()) {
                                        return false;
                                }
                        }
                        return true;
                }
        }

        @Override
        public DataSourceDefinition createDataSource(DataSourceCreation dsc, String tableName)
                throws DriverException {
                LOG.trace("Creating datasource");
                dsc.setDataSourceFactory(dsf);
                return dsc.create(tableName);
        }

        @Override
        public DataSourceDefinition createDataSource(DataSourceCreation dsc) throws DriverException {
                return createDataSource(dsc, DriverManager.DEFAULT_SINGLE_TABLE_NAME);
        }

        public void saveContents(String sourceName, DataSet contents,
                ProgressMonitor pm) throws DriverException {
                LOG.trace("Saving source to " + sourceName);
                ExtendedSource extendedSource = getExtendedSource(sourceName);
                if (extendedSource == null) {
                        throw new IllegalArgumentException(
                                "There is no source with the specified name: " + sourceName);
                } else {
                        DataSourceDefinition dsd = extendedSource.getDataSourceDefinition();
                        dsd.createDataSource(contents, pm);
                }
        }

        @Override
        public File getSourceInfoDirectory() {
                return new File(baseDir);
        }

        @Override
        public void setSourceInfoDirectory(String newDir) throws DriverException {
                saveStatus();
                File newDirectory = new File(newDir);
                if (!newDirectory.exists()) {
                        newDirectory.mkdirs();
                }
                File[] childs = getSourceInfoDirectory().listFiles();
                for (File file : childs) {
                        if (file.getName().startsWith(".")) {
                                continue;
                        }
                        try {
                                FileUtils.copy(file, new File(newDirectory, file.getName()));
                        } catch (IOException e) {
                                throw new DriverException(e);
                        }
                }
                this.baseDir = newDir;
        }

        @Override
        public String getMemento() throws IOException {
                StringBuilder ret = new StringBuilder();
                Iterator<String> it = nameSource.keySet().iterator();
                while (it.hasNext()) {
                        String sourceName = it.next();
                        org.gdms.source.Source source = nameSource.get(sourceName);
                        ret.append(sourceName).append("(");
                        List<String> aliases = getNamesFor(sourceName);
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
                                        source.getFilePropertyContentsAsString(propertyName)).append(")");
                        }
                        ret.append(")");
                }

                return ret.toString();
        }

        @Override
        public void removeName(String secondName) {
                if (nameMapping.containsKey(secondName)) {
                        nameMapping.remove(secondName);
                }
        }

        @Override
        public void changeSourceInfoDirectory(String newSourceInfoDir)
                throws IOException {
                this.baseDir = newSourceInfoDir;
                nameSource = new HashMap<String, ExtendedSource>();
                nameMapping = new HashMap<String, String>();

                schema = new RootSchema();

                File file = getDirectoryFile();
                createFile(file);
                StringBuilder b = new StringBuilder();
                b.append(contextPaths.get(0));
                for (int i = 1; i < contextPaths.size(); i++) {
                        b.append(':').append(contextPaths.get(i));
                }
                try {
                        jc = JAXBContext.newInstance(b.toString(), this.getClass().getClassLoader());
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
                }

                loadSystemTables();
        }

        /**
         * A method used to register some table system TODO : Must be change with
         * GDMS 2.0
         */
        @Override
        public void loadSystemTables() {

                // create spatial_ref_sys source
                if (!exists(SPATIAL_REF_SYSTEM)) {
                        InputStream in = this.getClass().getResourceAsStream(
                                SPATIAL_REF_TABLE_SYSTEM_PATH);
                        String tempPath = getSourceInfoDirectory().getAbsolutePath()
                                + File.separator + SPATIAL_REF_TABLE_SYSTEM_PATH;
                        try {
                                FileUtils.copy(in, new File(tempPath));
                                register(SPATIAL_REF_SYSTEM, new SystemSourceDefinition(
                                        new SystemSource(new File(tempPath))));
                        } catch (IOException e) {
                                dsf.getWarningListener().throwWarning(
                                        "Cannot load the spatial reference system. The coordinate"
                                        + "tranformations won't be available.");
                        }

                }

        }

        @Override
        public String[] getSourceNames() {
                return nameSource.keySet().toArray(new String[nameSource.size()]);
        }

        @Override
        public void addCommitListener(CommitListener listener) {
                commitListeners.add(listener);
        }

        @Override
        public void removeCommitListener(CommitListener listener) {
                commitListeners.remove(listener);
        }

        @Override
        public void fireIsCommiting(String name, Object source)
                throws DriverException {
                CommitListener[] listenerCopy = commitListeners.toArray(new CommitListener[commitListeners.size()]);
                for (CommitListener listener : listenerCopy) {
                        if (listener.getName().equals(name)) {
                                listener.isCommiting(name, source);
                        }
                }
        }

        @Override
        public void fireCommitDone(String name) {
                CommitListener[] listenerCopy = commitListeners.toArray(new CommitListener[commitListeners.size()]);
                for (CommitListener listener : listenerCopy) {
                        if (listener.getName().equals(name)) {
                                try {
                                        listener.commitDone(name);
                                } catch (DriverException e) {
                                        LOG.error("Cannot refresh commit listener: " + name, e);
                                }
                        }
                }
                ExtendedSource src = getExtendedSource(name);
                src.getDataSourceDefinition().refresh();
                String[] referencing = src.getReferencingSources();
                for (String referencingSource : referencing) {
                        fireCommitDone(referencingSource);
                }
        }

        @Override
        public String[] getAllNames(String sourceName) throws NoSuchTableException {
                final List<String> namesFor = getNamesFor(getMainNameFor(sourceName));
                return namesFor.toArray(new String[namesFor.size()]);
        }

        @Override
        public void addSourceContextPath(String path) {
                if (path == null) {
                        throw new IllegalArgumentException("path cannot be null");
                }
                contextPaths.add(path);
        }

        @Override
        public boolean removeSourceContextPath(String path) {
                return contextPaths.remove(path);
        }

        @Override
        public boolean containsSourceContextPath(String path) {
                return contextPaths.contains(path);
        }

        @Override
        public Schema getSchema() {
                return schema;
        }

        @Override
        public boolean removeSchema(String schemaName, boolean purge) {
                if (schemaName.isEmpty()) {
                        throw new IllegalArgumentException("Entpy schema name!");
                }

                String[] s = DOT.split(schemaName);
                int i = 0;

                // jump over root schema name
                if (s[i].equals(schema.getName())) {
                        i++;
                }

                Schema ss = schema;

                // find subs-schemas
                while (i < s.length) {
                        ss = ss.getSubSchemaByName(s[i]);
                        if (ss == null) {
                                return false;
                        }
                        i++;
                }

                return removeSchema(ss, purge);
        }

        private boolean removeSchema(Schema s, boolean purge) {
                boolean done = false;

                // remove all sub-schemas
                String[] sub = s.getSubSchemaNames();
                for (int i = 0; i < sub.length; i++) {
                        done |= removeSchema(s.getSubSchemaByName(sub[i]), purge);
                }

                // remove all tables
                String[] t = s.getTableNames();
                String fullname = s.getFullyQualifiedName();
                for (int j = 0; j < t.length; j++) {
                        done |= remove(fullname + "." + t[j], purge);
                }

                // disconnect from parent
                // parent can be null iff
                // - is the root schema
                // - the remove() above disconnected the last table of the schema (no empty schema is ever kept)
                Schema parent = s.getParentSchema();
                if (parent != null) {
                        parent.removeSubSchema(s.getName());
                }

                return done;
        }

        @Override
        public boolean schemaExists(String name) {
                if (name.isEmpty()) {
                        throw new IllegalArgumentException("Empty schema name!");
                }

                String[] s = DOT.split(name);
                int i = 0;

                // jump over root schema name
                if (s[i].equals(schema.getName())) {
                        i++;
                }

                Schema ss = schema;

                // find subs-schemas
                while (i < s.length) {
                        ss = ss.getSubSchemaByName(s[i]);
                        if (ss == null) {
                                return false;
                        }
                        i++;
                }

                return true;
        }

        @Override
        public void importFrom(String name, File file) throws DriverException {
                importFrom(name, new FileImportDefinition(file));
        }

        @Override
        public void importFrom(String name, ImportSourceDefinition def) throws DriverException {
                def.setDataSourceFactory(dsf);
                String[] tableNames = def.getSchema().getTableNames();

                if (tableNames.length == 1) {
                        DataSourceDefinition dsd = def.importSource(DriverManager.DEFAULT_SINGLE_TABLE_NAME);
                        register(name, dsd);
                } else {
                        DataSourceDefinition[] dsds = def.importAllSources();

                        for (int i = 0; i < dsds.length; i++) {
                                register(name + "." + tableNames[i], dsds[i]);
                        }
                }
        }

        @Override
        public void exportTo(String name, File file) throws DriverException, NoSuchTableException, DataSourceCreationException {
                exportTo(name, new FileExportDefinition(file));
        }

        @Override
        public void exportTo(String name, ExportSourceDefinition def) throws DriverException, NoSuchTableException, DataSourceCreationException {
                // differentiate between schema names and table names
                if (exists(name)) {
                        DataSource d = getDataSource(name, new NullProgressMonitor());

                        def.setDataSourceFactory(dsf);
                        if (def.getSchema().getTableCount() > 1) {
                                throw new DriverException("This export definition expects a schema with "
                                        + def.getSchema().getTableCount() + " table, not a single table.");
                        }
                        final Metadata met = def.getSchema().getTableByName(DriverManager.DEFAULT_SINGLE_TABLE_NAME);
                        d.open();

                        // checks metadata are compatible
                        String error = MetadataUtilities.check(met, d.getMetadata());
                        if (error != null) {
                                throw new DriverException("Cannot export '" + name + "': "
                                        + error);
                        }
                        def.export(d, DriverManager.DEFAULT_SINGLE_TABLE_NAME);
                        d.close();
                } else if (schemaExists(name)) {
                        String[] names = getSourceNames();
                        for (int i = 0; i < names.length; i++) {
                                if (names[i].startsWith(name + ".")) {
                                        // table is in the schema
                                        DataSource d = getDataSource(names[i], new NullProgressMonitor());

                                        def.setDataSourceFactory(dsf);
                                        String intName = names[i].substring(names[i].lastIndexOf('.') + 1);
                                        final Metadata met = def.getSchema().getTableByName(intName);

                                        // checks the table is allowed
                                        if (met == null) {
                                                throw new DriverException("This export definition does not expect"
                                                        + " a table named '" + intName + "'. Expected tables are: "
                                                        + Arrays.toString(def.getSchema().getTableNames()));
                                        }

                                        d.open();
                                        // checks metadata are compatible
                                        String error = MetadataUtilities.check(met, d.getMetadata());
                                        if (error != null) {
                                                throw new DriverException("Cannot export '" + names[i] + "': "
                                                        + error);
                                        }

                                        def.export(d, intName);
                                        d.close();
                                }
                        }
                } else {
                        throw new NoSuchTableException(name);
                }
        }

        @Override
        public boolean exists(URI uri) {
                for (ExtendedSource e : nameSource.values()) {
                        try {
                                if (uri.equals(e.getURI())) {
                                        return true;
                                }
                        } catch (DriverException ex) {
                                LOG.warn("Problem while retriving URI for '" + e.getName()
                                        + "'. Ignoring.", ex);
                        }
                }

                return false;
        }

        @Override
        public String getNameFor(URI uri) throws NoSuchTableException {
                for (ExtendedSource e : nameSource.values()) {
                        try {
                                if (uri.equals(e.getURI())) {
                                        return e.getName();
                                }
                        } catch (DriverException ex) {
                                LOG.warn("Problem while retriving URI for '" + e.getName()
                                        + "'. Ignoring.", ex);
                        }
                }

                throw new NoSuchTableException("for '" + uri.toString() + "'");
        }
}

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
package org.gdms.data;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.jproj.CRSFactory;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.I18N;

import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.edition.EditionDecorator;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.indexes.BTreeIndex;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.indexes.RTreeIndex;
import org.gdms.data.memory.MemorySourceDefinition;
import org.gdms.data.schema.Schema;
import org.gdms.data.sql.SQLSourceDefinition;
import org.gdms.data.stream.StreamSource;
import org.gdms.data.stream.StreamSourceDefinition;
import org.gdms.data.system.SystemSource;
import org.gdms.data.system.SystemSourceDefinition;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.plugins.PlugInManager;
import org.gdms.source.DefaultSourceManager;
import org.gdms.source.SourceManager;
import org.gdms.sql.engine.Engine;
import org.gdms.sql.engine.ParseException;
import org.gdms.sql.engine.SQLStatement;
import org.gdms.sql.engine.SemanticException;
import org.gdms.sql.function.FunctionManager;

/**
 * Main Factory and entry point for Gdms.
 *
 * An instance of this class represents an instance of Gdms. It can receive paths to
 * folders that will store internal data, temporary files and data sources.
 *
 * All features of Gdms can be accessed through this instance or one of the topic-related
 * instances below:
 * <ul>
 * <li>{@link SourceManager}: manages all the data sources registered with this Gdms instance.</li>
 * <li>{@link org.gdms.driver.driverManager.DriverManager}: manages all drivers that handle the I/O of
 * every source type. Can be obtained through the <tt>SourceManager</tt></li>
 * <li>{@link PlugInManager}: loads/unloads Gdms plug-ins.</li>
 * <li>{@link IndexManager}: creates, deletes, updates and queries indexes on registered sources.</li>
 * <li>{@link CRSFactory}: creates Coordinate Reference System objects for reprojection of geospatial
 * data.</li>
 * <li>{@link FunctionManager}: manages all functions available for use via SQL.</li>
 * </ul>.
 *
 * The <tt>DataSourceFactory</tt> itself gives shorthands to:
 * <ul>
 * <li>register data sources and get a {@link DataSource} object from it.
 * For example, see {@link #getDataSource(java.io.File)}.</li>
 * <li>get a data source from an already registered source. See {@link #getDataSource(java.lang.String)}.
 * </li>
 * <li>get a data source from an SQL query. See {@link #getDataSourceFromSQL(java.lang.String)}.</li>
 * <li>execute an SQL script against any loaded source. See {@link #executeSQL(java.lang.String)}.</li>
 * </ul>
 *
 * After using the <tt>DataSourceFactory</tt>, the method {@link #freeResources()} MUST be called in order
 * to properly save the state of Gdms and free any associated resources (file handles, for example).
 *
 */
public final class DataSourceFactory {

        /**
         * No editing capabilities, no status check
         */
        public static final int NORMAL = 0;
        /**
         * Checks that the source is opened before accessing it
         */
        public static final int STATUS_CHECK = 1;
        /**
         * Editing capabilities
         */
        public static final int EDITABLE = 2;
        /**
         * EDITABLE | STATUS_CHECK
         */
        public static final int DEFAULT = EDITABLE | STATUS_CHECK;
        private String i18NLocale = "";
        private File tempDir = new File(".");
        private DefaultSourceManager sourceManager;
        private IndexManager indexManager;
        private File resultDir;
        private PlugInManager plugInManager;
        private CRSFactory crsFactory;
        private static final Logger LOG = Logger.getLogger(DataSourceFactory.class);
        private FunctionManager functionManager = new FunctionManager();
        private GdmsProperties properties = new GdmsProperties(defaultProperties);
        private static final GdmsProperties defaultProperties;

        static {
                defaultProperties = new GdmsProperties();
                try {
                        final InputStream flags = DataSourceFactory.class.getResourceAsStream("flags.properties");
                        defaultProperties.load(flags);
                        flags.close();
                } catch (IOException ex) {
                        LOG.warn("Failed to load the default config flags, falling back to the internal"
                                + " default values (not good).", ex);
                }
        }

        /**
         * Creates a new {@code DataSourceFactory} with a <tt>sourceInfoDir</tt>
         * set to a sub-folder '.gdms' in the user's home.
         */
        public DataSourceFactory() {
                initialize(System.getProperty("user.home") + File.separator + ".gdms",
                        ".", null);
        }

        /**
         * Creates a new {@code DataSourceFactory}.
         *
         * @param sourceInfoDir the directory where the sources are stored
         */
        public DataSourceFactory(String sourceInfoDir) {
                initialize(sourceInfoDir, ".", null);
        }

        /**
         * Creates a new {@code DataSourceFactory}.
         *
         * @param sourceInfoDir the directory where the sources are stored
         * @param tempDir the directory where temporary sources are stored
         */
        public DataSourceFactory(String sourceInfoDir, String tempDir) {
                initialize(sourceInfoDir, tempDir, null);
        }

        /**
         * Creates a new {@code DataSourceFactory}.
         *
         * @param sourceInfoDir the directory where the sources are stored
         * @param tempDir the directory where temporary sources are stored
         * @param plugInDir the directory where plugIn jar files are stored
         */
        public DataSourceFactory(String sourceInfoDir, String tempDir, String plugInDir) {
                initialize(sourceInfoDir, tempDir, plugInDir);
        }

        /**
         * Creates a data source defined by the DataSourceCreation object
         *
         * @param dsc
         *
         * @return the DataSourceDefinition of this created source
         * @throws DriverException
         * if the source creation fails
         */
        public DataSourceDefinition createDataSource(DataSourceCreation dsc)
                throws DriverException {
                return sourceManager.createDataSource(dsc, DriverManager.DEFAULT_SINGLE_TABLE_NAME);
        }

        /**
         * Saves the specified contents into the source specified by the tableName
         * parameter. A source must be registered with that name before
         *
         * @param tableName the name of the table to save to
         * @param contents the DataSource whose content has to be saved
         * @param pm a progress monitor to report progression to
         * @throws DriverException
         */
        public void saveContents(String tableName, DataSet contents,
                ProgressMonitor pm) throws DriverException {
                boolean doOpenClose = contents instanceof DataSource;

                if (doOpenClose) {
                        ((DataSource) contents).open();
                }
                sourceManager.saveContents(tableName, contents, pm);
                if (doOpenClose) {
                        ((DataSource) contents).close();
                }
        }

        /**
         * Saves the specified contents into the source specified by the tableName
         * parameter. A source must be registered with that name before
         *
         *
         * @param tableName the name of the table to save to
         * @param contents the DataSource whose content has to be saved
         * @throws DriverException
         */
        public void saveContents(String tableName, DataSet contents)
                throws DriverException {
                saveContents(tableName, contents, new NullProgressMonitor());
        }

        /**
         * Constructs the stack of DataSources to achieve the functionality
         * specified in the mode parameter
         *
         * @param ds
         * DataSource
         * @param mode
         * opening mode
         * @return DataSource
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
         * Gets a DataSource instance to access the specified MemoryDriver
         *
         * @param object the MemoryDriver to load
         * @param tableName
         * @return a DataSource for this Driver
         *
         * @throws DriverLoadException
         * If there isn't a suitable driver for such a file
         * @throws DriverException
         */
        public DataSource getDataSource(MemoryDriver object, String tableName) throws DriverException {
                return getDataSource(object, tableName, DEFAULT);
        }

        /**
         * Gets a DataSource instance to access the specified MemoryDriver
         *
         * @param object the MemoryDriver to load
         * @param tableName
         * @param mode
         * To enable undo/redo operations UNDOABLE. NORMAL otherwise
         * @return a DataSource for this Driver
         * @throws DriverLoadException
         * If there isn't a suitable driver for such a file
         * @throws DriverException
         */
        public DataSource getDataSource(MemoryDriver object, String tableName, int mode)
                throws DriverException {
                try {
                        return getDataSource(new MemorySourceDefinition(object, tableName), mode,
                                new NullProgressMonitor());
                } catch (DataSourceCreationException e) {
                        throw new DriverException(e);
                }
        }

        /**
         * Gets a DataSource instance to access the MAIN table of the file
         *
         * @param file
         * file to access
         *
         * @return
         *
         * @throws DriverLoadException
         * If there isn't a suitable driver for such a file
         * @throws DataSourceCreationException
         * If the instance creation fails
         * @throws DriverException
         */
        public DataSource getDataSource(File file) throws DataSourceCreationException, DriverException {
                return getDataSource(file, DEFAULT);
        }

        /**
         * Gets a DataSource instance to access the MAIN table of the file
         *
         * @param file
         * file to access
         * @param mode
         * To enable undo/redo operations UNDOABLE. NORMAL otherwise
         * @return
         *
         * @throws DriverLoadException
         * If there isn't a suitable driver for such a file
         * @throws DataSourceCreationException
         * If the instance creation fails
         * @throws DriverException
         */
        public DataSource getDataSource(File file, int mode)
                throws DataSourceCreationException,
                DriverException {
                return getDataSource(new FileSourceDefinition(file, DriverManager.DEFAULT_SINGLE_TABLE_NAME), mode,
                        new NullProgressMonitor());
        }

        /**
         * Gets a <tt>DataSource</tt> instance to access a specific table of a multiple table file.
         *
         * @param file the file to access
         * @param tableName the name of the table to load
         * @return a data source
         * @throws DataSourceCreationException if the instance creation fail
         * @throws DriverException
         */
        public DataSource getDataSource(File file, String tableName) throws DataSourceCreationException, DriverException {
                return getDataSource(new FileSourceDefinition(file, tableName), DEFAULT,
                        new NullProgressMonitor());
        }

        /**
         * Gets a <tt>DataSource</tt> instance to access a specific table of a multiple table file.
         *
         * @param file file to access
         * @param tableName the name of the table to load
         * @param mode the opening mode of the table.
         * @return a data source
         * @throws DataSourceCreationException if the instance creation fail
         * @throws DriverException
         */
        public DataSource getDataSource(File file, String tableName, int mode) throws DataSourceCreationException, DriverException {
                return getDataSource(new FileSourceDefinition(file, tableName), mode,
                        new NullProgressMonitor());
        }

        private DataSource getDataSource(DataSourceDefinition def, int mode, ProgressMonitor pm) throws DataSourceCreationException {
                try {
                        String name = sourceManager.nameAndRegister(def);
                        return getDataSource(name, mode, pm);
                } catch (NoSuchTableException e) {
                        throw new DataSourceCreationException(e);
                } catch (SourceAlreadyExistsException e) {
                        throw new DataSourceCreationException(e);
                }
        }

        /**
         * Gets a DataSource instance to access the database source
         *
         * @param dbSource
         * source to access
         *
         * @return
         *
         * @throws DriverLoadException
         * If there isn't a suitable driver for such a file
         * @throws DataSourceCreationException
         * If the instance creation fails
         * @throws DriverException
         */
        public DataSource getDataSource(DBSource dbSource)
                throws DataSourceCreationException,
                DriverException {
                return getDataSource(dbSource, DEFAULT);
        }

        /**
         * Gets a DataSource instance to access the database source
         *
         * @param dbSource
         * source to access
         * @param mode
         * To enable undo/redo operations UNDOABLE. NORMAL otherwise
         * @return
         *
         * @throws DriverLoadException
         * If there isn't a suitable driver for such a file
         * @throws DataSourceCreationException
         * If the instance creation fails
         * @throws DriverException
         */
        public DataSource getDataSource(DBSource dbSource, int mode)
                throws DataSourceCreationException, DriverException {
                return getDataSource(new DBTableSourceDefinition(dbSource), mode,
                        new NullProgressMonitor());
        }

         /**
         * Gets a DataSource instance to access the stream source.
         *
         * @param streamSource
         *            source to access
         * @param mode
         * To enable undo/redo operations UNDOABLE. NORMAL otherwise
         * @return
         *
         * @throws DataSourceCreationException
         * If the instance creation fails
         * @throws DriverException
         */
        public DataSource getDataSource(StreamSource streamSource, int mode)
                throws DataSourceCreationException, DriverException {
                return getDataSource(new StreamSourceDefinition(streamSource), mode,
                        new NullProgressMonitor());
        }

        /**
         * Gets a DataSource instance to access the system table source with the
         * {@link #DEFAULT} mode
         *
         * @param systemSource
         * source to access
         * @return
         *
         * @throws DataSourceCreationException
         * If the instance creation fails
         * @throws DriverException
         */
        public DataSource getDataSource(SystemSource systemSource)
                throws DataSourceCreationException, DriverException {
                return getDataSource(new SystemSourceDefinition(systemSource), DEFAULT,
                        new NullProgressMonitor());
        }

        /**
         * Gets a DataSource instance to access the system table source
         *
         * @param systemSource
         * source to access
         * @param mode
         * To enable undo/redo operations UNDOABLE. NORMAL otherwise
         * @return
         *
         * @throws DataSourceCreationException
         * If the instance creation fails
         * @throws DriverException
         */
        public DataSource getDataSource(SystemSource systemSource, int mode)
                throws DataSourceCreationException, DriverException {
                return getDataSource(new SystemSourceDefinition(systemSource), mode,
                        new NullProgressMonitor());
        }

        /**
         * Gets a DataSource instance to access the stream source with the
         * {@link #DEFAULT} mode.
         *
         * @param streamSource source to access
         * @return a DataSource
         * @throws DataSourceCreationException if the instance creation fails
         * @throws DriverException
         */
        public DataSource getDataSource(StreamSource streamSource)
                throws DataSourceCreationException, DriverException {
                return getDataSource(new StreamSourceDefinition(streamSource), DEFAULT,
                        new NullProgressMonitor());
        }

        /**
         * Returns a DataSource to access the source associated to the specified
         * name
         *
         * @param tableName
         * source name
         *
         * @return DataSource
         *
         * @throws DriverLoadException
         * If the driver loading fails
         * @throws NoSuchTableException
         * If the 'tableName' data source does not exists
         * @throws DataSourceCreationException
         * If the DataSource could not be created
         */
        public DataSource getDataSource(String tableName)
                throws NoSuchTableException, DataSourceCreationException {
                return getDataSource(tableName, DEFAULT);
        }

        /**
         * Returns a DataSource to access the source associated to the specified
         * name
         *
         * @param tableName
         * source name
         * @param mode
         * Any combination of DEFAULT, EDITABLE, NORMAL, STATUS_CHECK
         *
         * @return DataSource
         *
         * @throws DriverLoadException
         * If the driver loading fails
         * @throws NoSuchTableException
         * If the 'tableName' data source does not exists
         * @throws DataSourceCreationException
         * If the DataSource could not be created
         */
        public DataSource getDataSource(String tableName, int mode)
                throws NoSuchTableException, DataSourceCreationException {
                return getDataSource(tableName, mode, new NullProgressMonitor());
        }

        private DataSource getDataSource(String tableName, int mode, ProgressMonitor pm) throws NoSuchTableException,
                DataSourceCreationException {
                LOG.trace("Getting datasource " + tableName + " in mode " + mode);
                if (pm == null) {
                        pm = new NullProgressMonitor();
                }
                DataSource ds = sourceManager.getDataSource(tableName, pm);
                if (pm.isCancelled()) {
                        ds = null;
                } else {
                        // we check if the actual DS is actually editable
                        // this allows for a DS to force itself NOT to be open
                        // in edition, even if it is asked for.
                        // This is currently used for Streams.
                        if ((mode & EDITABLE) == EDITABLE && !ds.isEditable()) {
                                // bitwise NOT
                                // i.e. the current mode *minus* EDITABLE
                                ds = getModedDataSource(ds, mode & ~EDITABLE);
                        } else {
                                ds = getModedDataSource(ds, mode);
                        }
                }

                return ds;
        }

        /**
         * Frees all resources used during execution
         *
         * @throws DataSourceFinalizationException
         * If cannot free resources
         */
        public void freeResources() throws DataSourceFinalizationException {

                if (plugInManager != null) {
                        plugInManager.unload();
                }

                sourceManager.shutdown();

                File[] tempFiles = tempDir.listFiles(new GdmsFileFilter());

                boolean success = true;
                for (int i = 0; i < tempFiles.length; i++) {
                        success &= tempFiles[i].delete();
                }
                if (!success) {
                        LOG.warn("Error deleting files: not all resources were freed.");
                }
        }

        /**
         * Executes a SQL statement
         *
         * @param sql
         * @throws ParseException
         * @throws DriverException
         * @throws SemanticException if something wrong happens during query validation
         */
        public void executeSQL(String sql) throws ParseException, DriverException {
                executeSQL(sql, new NullProgressMonitor(), DEFAULT);
        }

        /**
         * Executes a SQL statement
         *
         * @param sql
         * @param pm
         * @throws ParseException
         * @throws DriverException
         */
        public void executeSQL(String sql, ProgressMonitor pm) throws ParseException, DriverException {
                executeSQL(sql, pm, DEFAULT);
        }

        /**
         * Executes a SQL statement
         *
         * @param sql
         * sql statement
         * @param pm
         *
         * @param mode
         * @throws ParseException
         * If the sql is not well formed
         * @throws DriverException
         * If there is a problem accessing the sources
         */
        public void executeSQL(String sql, ProgressMonitor pm, int mode) throws ParseException, DriverException {
                LOG.trace("Execute SQL Statement" + '\n' + sql);

                Engine.executeScript(sql, this, properties);
        }

        /**
         * Gets a DataSource instance to access the result of the instruction
         *
         * @param instruction
         * Instruction to evaluate.
         * @param mode
         * The DataSource mode {@link #EDITABLE} {@link #STATUS_CHECK}
         *            {@link #NORMAL} {@link #DEFAULT}
         * @param pm
         * To monitor progress and cancel
         *
         * @return
         * @throws DataSourceCreationException
         */
        public DataSource getDataSource(SQLStatement instruction, int mode, ProgressMonitor pm) throws DataSourceCreationException {
                return getDataSource(new SQLSourceDefinition(instruction), mode, pm);
        }

        /**
         * Gets a DataSource instance to access the result of a query
         *
         * @param sql the SQL query to execute
         * @return a DataSource mapped to the result of the query
         *
         * @throws DriverLoadException
         * If there isn't a suitable driver for such a file
         * @throws DataSourceCreationException
         * If the instance creation fails
         * @throws DriverException
         * @throws ParseException
         * @throws NoSuchTableException
         */
        public DataSource getDataSourceFromSQL(String sql) throws DataSourceCreationException, DriverException, ParseException, NoSuchTableException {
                return getDataSourceFromSQL(sql, DEFAULT, new NullProgressMonitor());
        }

        /**
         * Gets a DataSource instance to access the file with the default mode
         *
         * @param sql the SQL query to execute
         * @param pm
         * Instance that monitors the process. Can be null
         * @return
         *
         * @throws DriverLoadException
         * If there isn't a suitable driver for such a file
         * @throws DataSourceCreationException
         * If the instance creation fails
         * @throws DriverException
         * @throws ParseException
         */
        public DataSource getDataSourceFromSQL(String sql, ProgressMonitor pm) throws DataSourceCreationException, DriverException, ParseException {
                return getDataSourceFromSQL(sql, DEFAULT, pm);
        }

        /**
         * Gets a DataSource instance to access the result of a query
         *
         * @param sql the SQL query to execute
         * @return a DataSource mapped to the result of the query
         * @param mode
         * To enable undo/redo operations UNDOABLE. NORMAL otherwise
         * @throws DriverLoadException
         * If there isn't a suitable driver for such a file
         * @throws DataSourceCreationException
         * If the instance creation fails
         * @throws DriverException
         * @throws ParseException
         */
        public DataSource getDataSourceFromSQL(String sql, int mode) throws DataSourceCreationException, DriverException, ParseException {
                return getDataSourceFromSQL(sql, mode, new NullProgressMonitor());
        }

        /**
         * Gets a DataSource instance to access the result of the SQL
         *
         * @param sql the SQL query to execute
         * @param mode
         * To enable undo/redo operations UNDOABLE. NORMAL otherwise
         * @param pm
         * Instance that monitors the process. Can be null
         * @return The result of the instruction or null if the execution was
         * canceled
         *
         * @throws DriverLoadException
         * If there isn't a suitable driver for such a file
         * @throws DataSourceCreationException
         * If the instance creation fails
         * @throws DriverException
         * @throws ParseException
         */
        public DataSource getDataSourceFromSQL(String sql, int mode, ProgressMonitor pm) throws DataSourceCreationException, DriverException, ParseException {
                LOG.trace("Getting datasource from SQL :\n" + sql);
                if (pm == null) {
                        pm = new NullProgressMonitor();
                }

                SQLStatement s = Engine.parse(sql, properties);
                
                return getDataSource(s, mode, pm);
        }

        /**
         * Gets the {@link FunctionManager} for this instance.
         * @return 
         */
        public FunctionManager getFunctionManager() {
                return functionManager;
        }

        /**
         * Names and registers an SQL view from the query <tt>sql</tt>.
         * @param sql a SELECT query
         * @return the name of the registered view
         * @throws DriverException
         * @throws ParseException
         * @deprecated use {@link SourceManager#nameAndRegister(java.lang.String) }
         *   instead of this method.
         */
        @Deprecated
        public String nameAndRegister(String sql) throws ParseException, DriverException {
                return sourceManager.nameAndRegister(sql);
        }

        /**
         * Registers an SQL view from the query <tt>sql</tt> under some name.
         * @param name the name of the registered view
         * @param sql a SELECT query
         * @throws DriverException
         * @throws SourceAlreadyExistsException
         * @throws ParseException
         * @deprecated use {@link SourceManager#register(java.lang.String, java.lang.String) }
         *   instead of this method.
         */
        @Deprecated
        public void register(String name, String sql) throws ParseException, DriverException {
                sourceManager.register(name, sql);
        }

        private static class GdmsFileFilter implements FileFilter {

                @Override
                public boolean accept(File pathname) {
                        return pathname.getName().toLowerCase().startsWith("gdms");
                }
        }

        /**
         * Gets the I18N local string for this instance.
         * @return 
         */
        public String getI18nLocale() {
                return i18NLocale;
        }

        /**
         * Sets the I18N local string for this instance.
         * @param locale 
         */
        public void setI18nLocale(String locale) {
                this.i18NLocale = locale;
        }

        /**
         * Sets the directory of the plug-in directory.
         *
         * This must be called *before* loadPlugIns().
         *
         * @param plugInDir a valid path to the plug-in directory
         */
        public void setPlugInDirectory(String plugInDir) {
                plugInManager = new PlugInManager(new File(plugInDir), this);
        }

        /**
         * Initializes the system
         *
         * @param sourceInfoDir
         * @param tempDir
         * temporary directory to write data
         * @throws InitializationException
         * If the initialization fails
         */
        private void initialize(String sourceInfoDir, String tempDir, String pluginDir) {
                LOG.trace("DataSourceFactory initializing");

                I18N.addI18n(i18NLocale, "gdms", this.getClass());
                indexManager = new IndexManager(this);
                indexManager.addIndex(IndexManager.RTREE_SPATIAL_INDEX,
                        RTreeIndex.class);
                indexManager.addIndex(IndexManager.BTREE_ALPHANUMERIC_INDEX,
                        BTreeIndex.class);

                setTempDir(tempDir);
                setResultDir(new File(tempDir));

                try {
                        Class.forName("org.hsqldb.jdbcDriver");

                } catch (ClassNotFoundException e) {
                        throw new InitializationException(e);
                }

                crsFactory = new CRSFactory();

                sourceManager = new DefaultSourceManager(this, sourceInfoDir);
                try {
                        sourceManager.init();

                } catch (IOException e) {
                        throw new InitializationException(e);
                }

                if (pluginDir != null) {
                        plugInManager = new PlugInManager(new File(pluginDir), this);
                }

        }

        /**
         * Loads all plug-ins (if a plug-in directory has been specified).
         */
        public void loadPlugins() {
                if (plugInManager != null) {
                        plugInManager.load();
                }
        }

        /**
         * Sets the temporary directory used by this DataSourceFactory to store files
         *
         * @param tempDir
         */
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
         * @param extension the extension of the file
         * @return the absolute path of the file
         */
        public String getTempFile(String extension) {
                String path;
                do {
                        path = tempDir.getAbsolutePath() + File.separator + getUID() + "."
                                + extension;
                } while (new File(path).exists());

                return path;
        }

        /**
         * Gets a unique valid identifier for a source.
         *
         * @return a unique String identifier
         */
        public String getUID() {
                return sourceManager.getUID();
        }

        /**
         * Gets the SourceManager associated with this DataSourceFactory
         *
         * @return
         */
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

        /**
         * Gets the Temp directory
         *
         * @return a File object for the temp directory
         */
        public File getTempDir() {
                return tempDir;
        }

        /**
         * Gets the IndexManager associated with this DataSourceFactory
         *
         * @return
         */
        public IndexManager getIndexManager() {
                return indexManager;
        }

        /**
         * Registers on the source manager associated to this factory the specified
         * DataSourceDefinition with the specified name
         *
         * @param sourceName the name of the new source
         * @param def the definition of the source
         * @throws SourceAlreadyExistsException if a source already exists with this name
         */
        public void registerDataSource(String sourceName, DataSourceDefinition def) {
                sourceManager.register(sourceName, def);
        }

        /**
         * Checks if a source exists
         *
         * @param sourceName the name of the source
         * @return true if the source is found, false otherwise
         */
        public boolean exists(String sourceName) {
                return sourceManager.exists(sourceName);
        }

        /**
         * Removes a source
         *
         * @param sourceName the name of the source
         */
        public void remove(String sourceName) {
                sourceManager.remove(sourceName);
        }

        /**
         * Gets the Schema of every data loaded in this DataSourceFactory.
         *
         * This Schema does not contains any {@code Metadata}, but has a sub-schema for
         * each set of Source that belongs to the same driver.
         *
         * @return the global GDMS schema for this DataSourceFactory
         */
        public Schema getSchema() {
                return sourceManager.getSchema();
        }

        /**
         * Gets the plugin manager for this DataSourceFactory.
         *
         * @return the plugin manager
         */
        public PlugInManager getPlugInManager() {
                return plugInManager;
        }

        /**
         * Gets the CRS Factory for this instance of Gdms.
         *
         * @return the CRS factory
         */
        public CRSFactory getCrsFactory() {
                return crsFactory;
        }

        /**
         * Gets the properties associated with this instance.
         * 
         * Properties control various internal behaviors of Gdms.
         * 
         * Note that these properties inherit from the default properties available at
         * {@link DataSourceFactory#getDefaultProperties() }.
         * 
         * @return 
         */
        public GdmsProperties getProperties() {
                return properties;
        }

        /**
         * Gets the default properties of Gdms.
         * 
         * Changes to these apply to all running <tt>DataSourceFactory</tt> instances inside the same
         * ClassLoader. Use with caution.
         * 
         * @return 
         */
        public static GdmsProperties getDefaultProperties() {
                return defaultProperties;
        }
}

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
import java.io.IOException;
import java.net.URI;

import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFinalizationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.db.DBSource;
import org.gdms.data.exporter.ExportSourceDefinition;
import org.gdms.data.importer.ImportSourceDefinition;
import org.gdms.data.schema.Schema;
import org.gdms.data.stream.StreamSource;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.sql.engine.ParseException;

/**
 * Manages all registered sources.<br />
 *
 * <p>An instance of this class represents the internal catalog of sources registered with Gdms.
 * </p><p>
 * New sources can be registered with the <tt>register(...)</tt> and <tt>nameAndRegister(...)</tt>
 * methods and removed with {@link #remove(java.lang.String) } and {@link #delete(java.lang.String) }.
 * </p>
 * 
 * @author Antoine Gourlay
 */
public interface SourceManager {

        /**
         * The source has no known type.
         */
        int UNKNOWN = 0;
        /**
         * The source type can contain raster fields.
         */
        int RASTER = 1;
        /**
         * The source type can contain geometric fields.
         */
        int VECTORIAL = 2;
        /**
         * The source is stored in a file.
         */
        int FILE = 4;
        /**
         * The source is stored in a database.
         */
        int DB = 8;
        /**
         * The source is stored in memory.
         */
        int MEMORY = 16;
        /**
         * The source is the result of a SQL query.
         */
        int SQL = 32;
        /**
         * The source contains the parameters of a WMS connection.
         */
        int WMS = 64;
        /**
         * The source is a system table.
         */
        int SYSTEM_TABLE = 128;
        /**
         * The source is a live view of some data.
         */
        int LIVE = 256;

        /**
         * Sets the driver manager used to load the drivers of the sources.
         *
         * @param dm a driver manager
         */
        void setDriverManager(DriverManager dm);

        /**
         * Adds a listener to the events in this class.
         *
         * @param e a listener
         * @return always true
         */
        boolean addSourceListener(SourceListener e);

        /**
         * Removes a listener to the events in this class.
         *
         * @param o a listener
         * @return true if the listener was registered before
         */
        boolean removeSourceListener(SourceListener o);

        /**
         * Removes all sources.
         *
         * This essentially disconnects Gdms from all previously registered sources. It does not
         * hard-delete them.
         * 
         * Note that this does not remove system tables.
         *
         * @throws IOException
         */
        void removeAll() throws IOException;

        /**
         * Removes the source with the specified name or alias.
         *
         * This essentially disconnects Gdms from the source. It does not hard-delete it.
         *
         * @param name a source name or alias
         * @return true if the source was found and removed, false otherwise
         * @throws IllegalStateException if some source depends on the source being removed
         */
        boolean remove(String name);

        /**
         * Deletes on disk or in database the source with the specified name or alias.
         *
         * @param name a source name or alias
         * @return true if the source was found and removed, false otherwise
         * @throws IllegalStateException if some source depends on the source being removed
         */
        boolean delete(String name);
        
        /**
         * Registers a source from a URI with the specified name.
         * 
         * This method is 
         *
         * @param name name to register with
         * @param uri URI to the resource to register
         * @throws org.gdms.data.SourceAlreadyExistsException
         */
        void register(String name, URI uri);

        /**
         * Registers a file with the specified name.
         *
         * @param name name to register with
         * @param file file to register
         * @throws org.gdms.data.SourceAlreadyExistsException
         */
        void register(String name, File file);
        
        /**
         * Registers a SQL view with the specified name.
         *
         * @param name name to register with
         * @param sql SQL query to register
         * @throws ParseException 
         * @throws org.gdms.data.SourceAlreadyExistsException
         */
        void register(String name, String sql) throws ParseException;

        /**
         * Registers a database table with the specified name.
         *
         * @param name name to register
         * @param dbTable source to register
         * @throws org.gdms.data.SourceAlreadyExistsException
         */
        void register(String name, DBSource dbTable);
        
        /**
         * Registers a stream source with the specified name.
         *
         * @param name name to register
         * @param wmsSource source to register
         * @throws org.gdms.data.SourceAlreadyExistsException
         */
        void register(String name, StreamSource wmsSource);

        /**
         * Registers a memory object with the specified name.
         *
         * @param name name to register with
         * @param driver object to register
         * @throws org.gdms.data.SourceAlreadyExistsException
         */
        void register(String name, MemoryDriver driver);

        /**
         * Registers the specified DataSourceDefinition with the specified name.
         *
         * @param name name to register with
         * @param def definition of the source
         * @throws org.gdms.data.SourceAlreadyExistsException
         */
        void register(String name, DataSourceDefinition def);

        /**
         * Registers all available tables in the given DataSourceCreation.
         *
         * @param name name of the schema to register
         * @param cr creation object
         */
        void register(String name, DataSourceCreation cr);

        /**
         * Gets a unique id.
         *
         * @return a unique id
         */
        String getUID();

        /**
         * Returns a name that hasn't been used for any registration so far with the given prefix.
         *
         * @param base a prefix
         * @return a new name
         */
        String getUniqueName(String base);
        
        /**
         * Registers a source from an URI generating the name automatically.
         *
         * @param uri an URI
         * @return the name of the registered source
         */
        String nameAndRegister(URI uri);

        /**
         * Registers generating the name automatically.
         *
         * @param file a file
         * @return the name of the registered source
         */
        String nameAndRegister(File file);
        
        /**
         * Registers a view generating the name automatically.
         *
         * @param sql a SQL query
         * @return the name of the registered source
         * @throws ParseException  
         */
        String nameAndRegister(String sql) throws ParseException;

        /**
         * Registers generating the name automatically.
         *
         * @param dbTable some DB connection information
         * @return the name of the registered source
         */
        String nameAndRegister(DBSource dbTable);

        /**
         * Registers generating the name automatically.
         *
         * @param streamSource
         * @return the name of the registered source
         */
        String nameAndRegister(StreamSource streamSource);

        /**
         * Registers generating the name automatically.
         *
         * @param driver a memory object
         * @param tableName the name of the the table from the object to register
         * @return the name of the registered source
         */
        String nameAndRegister(MemoryDriver driver, String tableName);

        /**
         * Registers generating the name automatically.
         *
         * @param def a source definition
         * @return the name of the registered source
         */
        String nameAndRegister(DataSourceDefinition def);

        /**
         * Registers generating the name automatically (for multiple-table sources).
         *
         * @param dsc a source creation
         * @return the name of the registered source
         */
        String nameAndRegister(DataSourceCreation dsc);

        /**
         * Adds an alias to the specified data source name.
         *
         * The main name of the data source will not change but the new name can be used to refer to the
         * source in the same way as the main one.
         *
         * @param dsName current name
         * @param newName new alias
         * @throws NoSuchTableException if the source is not found
         * @throws org.gdms.data.SourceAlreadyExistsException if there already is a source with the new name/alias
         */
        void addName(String dsName, String newName)
                throws NoSuchTableException;

        /**
         * Modifies the name of the specified source.
         *
         * If modifies either the main name if it is the main name of a source, or the alias if
         * it is an alias.
         *
         * @param dsName current name/alias
         * @param newName new name/alias
         * @throws org.gdms.data.SourceAlreadyExistsException
         */
        void rename(String dsName, String newName);

        /**
         * Checks if there is a source with a specific name.
         *
         * @param sourceName a name/alias
         * @return true if there is a source with the specified name, false otherwise
         */
        boolean exists(String sourceName);
        
        /**
         * Checks if a source with the specified URI is already registered.
         *
         * @param uri a valid URI
         * @return true if there is a source with the specified URI, false otherwise
         */
        boolean exists(URI uri);
        
        /**
         * Gets the main name of the source.
         *
         * @param uri a valid URI
         * @return the (main) name of the source with the specified URI
         * @throws NoSuchTableException if there is no source with the specified URI
         */
        String getNameFor(URI uri) throws NoSuchTableException;

        /**
         * Gets the main name of the source.
         *
         * @param dsName a name or alias
         * @return the main name of the source
         * @throws NoSuchTableException if there is no source with the specified name
         */
        String getMainNameFor(String dsName) throws NoSuchTableException;

        /**
         * frees resources used by this SourceManager.
         *
         * @throws DataSourceFinalizationException
         */
        void shutdown() throws DataSourceFinalizationException;

        /**
         * @return true if there is no source in the manager and false otherwise;
         *   system tables are taken into account by this method.
         */
        boolean isEmpty();
        
        /**
         * @param ignoreSystem true if system tables must be ignored
         * @return true if there is no source in the manager and false otherwise
         */
        boolean isEmpty(boolean ignoreSystem);

        /**
         * Creates a source and returns a definition of the source that can be used
         * to register it. This method does not register the created source.
         *
         * @param dsc creation object
         * @param tableName name of the table of the creation object to use
         * @return a new definition for the specified table
         * @throws DriverException
         */
        DataSourceDefinition createDataSource(DataSourceCreation dsc, String tableName)
                throws DriverException;

        /**
         * Creates a source with a default table name and returns a definition of the source
         * that can be used to register it. This method does not register the created source.
         *
         * This is strictly equivalent to calling:
         * <code>createDataSource(dsc, DriverManager.DEFAULT_SINGLE_TABLE_NAME)</code>
         *
         * @param dsc creation object
         * @return a new definition for the default table
         * @throws DriverException
         */
        DataSourceDefinition createDataSource(DataSourceCreation dsc)
                throws DriverException;

        /**
         * Gets the source with the specified name.
         *
         * @param name a source name
         * @return null if there is no source with that name
         */
        Source getSource(String name);

        /**
         * Sets the directory where the registry of sources and its properties are
         * stored. Each call to saveStatus will serialize the content of this
         * manager to the specified directory.
         *
         * @param newDir new location for storing the information
         * @throws DriverException
         */
        void setSourceInfoDirectory(String newDir)
                throws DriverException;

        /**
         * Sets the directory where the registry of sources and its properties are
         * stored. Each call to saveStatus will serialize the content of this
         * manager to the specified directory. The specified directory is read and
         * the status of this source manager is replaced by the one in the directory
         *
         * @param newSourceInfoDir new directory to load sources from
         * @throws IOException
         */
        void changeSourceInfoDirectory(String newSourceInfoDir)
                throws IOException;

        /**
         * Method for debugging purposes that obtains a snapshot of the catalog of sources.
         *
         * @return a debugging statement.
         * @throws IOException
         */
        String getMemento() throws IOException;

        /**
         * Saves all information about the sources in the directory specified by
         * the last call to setSourceInfoDirectory.
         *
         * @throws DriverException
         */
        void saveStatus() throws DriverException;

        /**
         * Gets the directory where information on sources is stored.
         *
         * @return the source info directory
         */
        File getSourceInfoDirectory();

        /**
         * @return the current driver manager
         */
        DriverManager getDriverManager();

        /**
         * Removes the specified alias.
         *
         * @param secondName an alias
         */
        void removeName(String secondName);

        /**
         * Gets the name of the source that accesses the specified source
         * definition.
         *
         * @param dataSourceDefinition a source definition
         * @return the name, or null it no source was found
         */
        String getSourceName(DataSourceDefinition dataSourceDefinition);

        /**
         * Gets the list of the names of all the sources in the manager.
         *
         * @return all the source names
         */
        String[] getSourceNames();

        /**
         * Get all the aliases for the specified source.
         *
         * @param sourceName a name/alias for a source
         * @return all associated aliases
         * @throws NoSuchTableException if there is no source with the specified name/alias
         */
        String[] getAllNames(String sourceName)
                throws NoSuchTableException;

        /**
         * Loads system tables.
         */
        void loadSystemTables();

        /**
         * Adds a CommitListener to this SourceManager.
         *
         * @param listener a listener
         */
        void addCommitListener(CommitListener listener);

        /**
         * Removes a CommitListener to this SourceManager.
         *
         * @param listener a listener
         */
        void removeCommitListener(CommitListener listener);

        /**
         * Fires to listeners that a commit has been done on a table.
         *
         * @param name a table name
         */
        void fireCommitDone(String name);

        /**
         * Fires to listeners that a commit has started on a table.
         *
         * @param name a table name
         * @param source the source of the commit
         * @throws DriverException if something goes wrong in the listeners
         */
        void fireIsCommiting(String name, Object source) throws DriverException;

        /**
         * Adds a path for source loading using JAXB.
         *
         * @param path a valid class path
         */
        void addSourceContextPath(String path);

        /**
         * Gets if a given path for source loading is already registered.
         *
         * @param path a valid class path
         * @return true if it is already registered
         */
        boolean containsSourceContextPath(String path);

        /**
         * Removes a path for source loading using JAXB.
         *
         * @param path a valid class path
         * @return true if an element was really removed
         */
        boolean removeSourceContextPath(String path);

        /**
         * Initializes this SourceManager.
         *
         * Any call to this method while there are sources associated with the SourceManager
         * will cause an InitializationException. This method can be called to reload the SourceManager,
         * after a call to shutdown().
         *
         * Note that this method does not reload the DriverManager associated with this SourceManager.
         *
         * @throws IOException
         * @throws org.gdms.data.InitializationException
         */
        void init() throws IOException;

        /**
         * Gets the Schema of every data loaded in this SourceManager.
         *
         * @return the global Gdms schema for this SourceManager
         */
        Schema getSchema();

        /**
         * Removes the specified schema (and sub-schemas) from this SourceManager.
         *
         * @param schemaName a fully-qualified schema name
         * @param purge true if the sources in the schema must be purged
         * @return true if any schema and/or source was actually removed
         */
        boolean removeSchema(String schemaName, boolean purge);

        /**
         * Gets if the specified schema exists in this SourceManager.
         *
         * @param name a fully-qualified schema name
         * @return true if found
         */
        boolean schemaExists(String name);
        
        /**
         * Imports and register with the given name the content of the file.
         * 
         * If the file contains several tables, they are all imported and
         * registered under the schema <tt>name</tt>. If it contains a single
         * table, it is directly registered as <tt>name</tt>.
         * 
         * @param name a new name
         * @param file a file to import
         * @throws DriverException 
         */
        void importFrom(String name, File file) throws DriverException;
        
        /**
         * Imports and register with the given name the content of an import source definition.
         * 
         * 
         * If the file contains several tables, they are all imported and
         * registered under the schema <tt>name</tt>. If it contains a single
         * table, it is directly registered as <tt>name</tt>.
         * 
         * @param name a new name
         * @param def a source to import
         * @throws DriverException 
         */
        void importFrom(String name, ImportSourceDefinition def) throws DriverException;
        
        /**
         * Exports the content of a table to the specified file.
         * @param name an existing table name
         * @param file a new file to export to
         * @throws DriverException
         * @throws NoSuchTableException
         * @throws DataSourceCreationException 
         */
        void exportTo(String name, File file) throws DriverException, NoSuchTableException
                , DataSourceCreationException;
        
        /**
         * Exports the content of a table to the specified export source definition.
         * @param name an existing table name
         * @param def a source to export to
         * @throws DriverException
         * @throws NoSuchTableException
         * @throws DataSourceCreationException 
         */
        void exportTo(String name, ExportSourceDefinition def) throws DriverException, NoSuchTableException
                , DataSourceCreationException;
}

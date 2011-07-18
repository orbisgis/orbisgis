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

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFinalizationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.db.DBSource;
import org.gdms.data.schema.Schema;
import org.gdms.data.wms.WMSSource;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverManager;

public interface SourceManager {

        /**
         * The source has no known type
         */
        int UNKNOWN = 0;
        /**
         * The source type can contain raster fields
         */
        int RASTER = 1;
        /**
         * The source type can contain geometric fields
         */
        int VECTORIAL = 2;
        /**
         * The source is stored in a file
         */
        int FILE = 4;
        /**
         * The source is stored in a database
         */
        int DB = 8;
        /**
         * The source is stored in memory
         */
        int MEMORY = 16;
        /**
         * The source is the result of a SQL query
         */
        int SQL = 32;
        /**
         * The source contains the parameters of a WMS connection
         */
        int WMS = 64;
        int SYSTEM_TABLE = 128;

        /**
         * Sets the driver manager used to load the drivers of the sources
         *
         * @param dm
         */
        void setDriverManager(DriverManager dm);

        /**
         * Adds a listener to the events in this class
         *
         * @param e
         * @return
         */
        boolean addSourceListener(SourceListener e);

        /**
         * Removes a listener to the events in this class
         *
         * @param o
         * @return
         */
        boolean removeSourceListener(SourceListener o);

        /**
         * Removes all the information about the sources
         *
         * @throws IOException
         */
        void removeAll() throws IOException;

        /**
         * Try to remove the information about the source with the name or alias
         * specified
         *
         * @param name
         * @return true if the source was found and removed and false if the source
         *         was not found
         * @throws IllegalStateException
         *             If some source depends on the source being removed
         */
        boolean remove(String name);

        /**
         * Try to delete on disk or on database the source with the name or alias
         * specified
         *
         * @param name
         * @return true if the source was found and removed and false if the source
         *         was not found
         * @throws IllegalStateException
         *             If some source depends on the source being removed
         */
        boolean delete(String name);

        /**
         * Registers a file with the specified name
         *
         * @param name
         *            Name to register with
         * @param file
         *            file to register
         * @throws SourceAlreadyExistsException
         */
        void register(String name, File file);

        /**
         * Registers a database table with the specified name
         *
         * @param name
         *            Name to register
         * @param dbTable
         *            source to register
         * @throws SourceAlreadyExistsException
         */
        void register(String name, DBSource dbTable);

        /**
         * Registers a wms source with the specified name
         *
         * @param name
         *            Name to register
         * @param wmsSource
         *            source to register
         * @throws SourceAlreadyExistsException
         */
        void register(String name, WMSSource wmsSource);

        /**
         * Registers a object with the specified name
         *
         * @param name
         *            Name to register with
         * @param driver
         *            object to register
         * @throws SourceAlreadyExistsException 
         */
        void register(String name, ObjectDriver driver);

        /**
         * Registers the specified DataSourceDefinition with the specified name
         *
         * @param name
         *            Name to register with
         * @param def
         *            definition of the source
         * @throws SourceAlreadyExistsException
         */
        void register(String name, DataSourceDefinition def);
        
        /**
         * Registers all table available in the given DataSourceCreation.
         * @param name name of the schema to register
         * @param cr
         */
        void register(String name, DataSourceCreation cr);

        /**
         * Gets a unique id
         *
         * @return unique id
         */
        String getUID();

        /**
         * Returns a name that hasn't been used for any registration so far and
         * based in the specified name
         *
         * @param base
         *            Base to obtain the unique name
         * @return
         */
        String getUniqueName(String base);

        /**
         * Registers generating the name automatically
         *
         * @param file
         * @return the name of the registered source
         */
        String nameAndRegister(File file);

        /**
         * Registers generating the name automatically
         *
         * @param dbTable
         * @return the name of the registered source
         */
        String nameAndRegister(DBSource dbTable);

        /**
         * Registers generating the name automatically
         *
         * @param wmsSource
         * @return the name of the registered source
         */
        String nameAndRegister(WMSSource wmsSource);

        /**
         * Registers generating the name automatically
         *
         * @param driver
         * @param tableName 
         * @return the name of the registered source
         */
        String nameAndRegister(ObjectDriver driver, String tableName);

        /**
         * Registers generating the name automatically
         *
         * @param def
         * @return the name of the registered source
         */
        String nameAndRegister(DataSourceDefinition def);
        
        /**
         * Registers generating the name automatically
         * 
         * @param dsc
         * @return the name of the registered source
         */
        String nameAndRegister(DataSourceCreation dsc);

        /**
         * Adds a new name to the specified data source name. The main name of the
         * data source will not change but the new name can be used to refer to the
         * source in the same way as the main one
         *
         * @param dsName
         * @param newName
         * @throws NoSuchTableException
         * @throws SourceAlreadyExistsException
         */
        void addName(String dsName, String newName)
                throws NoSuchTableException;

        /**
         * Modifies the name of the specified source. If modifies either the main
         * name either the aliases
         *
         * @param dsName
         * @param newName
         * @throws SourceAlreadyExistsException
         */
        void rename(String dsName, String newName);

        /**
         * @param sourceName
         * @return true if there is a source with the specified name, false
         *         otherwise
         */
        boolean exists(String sourceName);

        /**
         * Gets the main name of the source
         *
         * @param dsName
         * @return
         * @throws NoSuchTableException
         *             If there is no source with the specified name
         */
        String getMainNameFor(String dsName)
                throws NoSuchTableException;

        /**
         * Called to free resources
         *
         * @throws DataSourceFinalizationException
         */
        void shutdown() throws DataSourceFinalizationException;

        /**
         * @return true if there is no source in the manager and false otherwise
         */
        boolean isEmpty();

        /**
         * Creates a source and returns a definition of the source that can be used
         * to register it. This method does not register the created source
         *
         * @param dsc
         *            creation object
         * @param tableName 
         * @return
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
         * @param dsc
         *            creation object
         * @return
         * @throws DriverException
         */
        DataSourceDefinition createDataSource(DataSourceCreation dsc)
                throws DriverException;
        
        /**
         * Gets the source with the specified name
         *
         * @param name
         * @return null if there is no source with that name
         */
        Source getSource(String name);

        /**
         * Sets the directory where the registry of sources and its properties are
         * stored. Each call to saveStatus will serialize the content of this
         * manager to the specified directory. The current status is preserved
         *
         * @param newDir
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
         * @param newSourceInfoDir
         * @throws IOException
         */
        void changeSourceInfoDirectory(String newSourceInfoDir)
                throws IOException;

        /**
         * Method for debugging purposes that obtains a snapshot of the system
         *
         * @return
         * @throws IOException
         */
        String getMemento() throws IOException;

        /**
         * saves all the information about the sources in the directory specified by
         * the last call to setSourceInfoDirectory
         *
         * @throws DriverException
         */
        void saveStatus() throws DriverException;

        /**
         * Gets the directory where the information of sources is stored
         *
         * @return
         */
        File getSourceInfoDirectory();

        /**
         * Gets the driver manager
         *
         * @return
         */
        DriverManager getDriverManager();

        /**
         * Removes the specified secondary name.
         *
         * @param secondName
         */
        void removeName(String secondName);

        /**
         * Gets the name of the source that accesses the specified source
         * definition. If the source definition is not accessed by any source it
         * will return null
         *
         * @param dataSourceDefinition
         * @return
         */
        String getSourceName(
                DataSourceDefinition dataSourceDefinition);

        /**
         * Gets the list of the names of all the sources in the manager
         *
         * @return
         */
        String[] getSourceNames();

        /**
         * Get all the alternative names for the specified source
         *
         * @param sourceName
         * @return
         * @throws NoSuchTableException
         *             If there is no source with the specified name
         */
        String[] getAllNames(String sourceName)
                throws NoSuchTableException;

        /**
         * Loads the system tables.
         */
        void loadSystemTables();

        void addCommitListener(CommitListener listener);

        void removeCommitListener(CommitListener listener);

        void fireCommitDone(String name);

        void fireIsCommiting(String name, Object source) throws DriverException;

        void addSourceContextPath(String path);

        boolean containsSourceContextPath(String path);

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
         * @throws InitializationException
         */
        void init() throws IOException;

        /**
         * Gets the Schema of every data loaded in this SourceManager.
         *
         * This Schema does not contains any {@code Metadata}, but has a sub-schema for
         * each set of Source that belongs to the same driver.
         * @return the global GDMS schema for this SourceManager
         */
        Schema getSchema();
}

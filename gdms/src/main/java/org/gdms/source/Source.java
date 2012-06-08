/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.db.DBSource;
import org.gdms.data.wms.WMSSource;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;

/**
 * Represents a Source registered in a SourceManager.
 *
 * A Source has a name, a type, and a set of associated properties.
 */
public interface Source {

        /**
         * Creates a property which content is stored in a file. If the property
         * already exists it returns the associated File
         *
         * @param propertyName
         * name of the property
         * @return The file to store the content
         * @throws IOException
         * If the file cannot be created
         */
        File createFileProperty(String propertyName) throws IOException;

        /**
         * Gets the contents of the file associated with the property
         *
         * @param propertyName
         * name of the property we want to access
         * @return The bytes stored in the associated file or null if the property
         * does not exist
         * @throws IOException
         */
        byte[] getFilePropertyContents(String propertyName) throws IOException;

        /**
         * The same as getFilePropertyContents but building an string with the byte
         * array
         *
         * @param propertyName
         * @return
         * @throws IOException
         */
        String getFilePropertyContentsAsString(String propertyName)
                throws IOException;

        /**
         * Creates (or modifies if it already exist) a string property.
         *
         * @param propertyName
         * @param value
         */
        void putProperty(String propertyName, String value);

        /**
         * Gets the value of a string property or null if the property does not
         * exist
         *
         * @param propertyName
         * Name of the property which value will be returned
         * @return
         */
        String getProperty(String propertyName);

        /**
         * Returns true if the source has a property, either stored on a file or a
         * string, with the specified name
         *
         * @param propertyName
         * @return
         */
        boolean hasProperty(String propertyName);

        /**
         * Deletes the property. This method is independent of the type of storage
         * of the property
         *
         * @param propertyName
         * @throws IOException
         */
        void deleteProperty(String propertyName) throws IOException;

        /**
         * Gets the file associated with the specified property. if the property
         * content is not stored on a file or the property does not exist this
         * method will return null
         *
         * @param propertyName
         * @return
         */
        File getFileProperty(String propertyName);

        /**
         * Gets the names of all properties with string values
         *
         * @return
         * @throws IOException
         */
        String[] getStringPropertyNames() throws IOException;

        /**
         * Gets the names of all properties with values stored in files
         *
         * @return
         */
        String[] getFilePropertyNames();

        /**
         * Gets the name of the source
         *
         * @return
         */
        String getName();

        /**
         * @return true if the user specified a name when registering it. False if
         * the name was generated automatically
         */
        boolean isWellKnownName();

        /**
         * Indicates if the source has been modified by another entity different
         * from the DataSourceFactory this source belongs to. This call can be quite
         * time consuming depending on the type of the source
         *
         * @return true if the source has not been modified and false otherwise
         * @throws DriverException
         */
        boolean isUpToDate() throws DriverException;

        /**
         * Gets all the sources that depend on this source
         *
         * @return
         */
        String[] getReferencingSources();

        /**
         * Gets all the sources this source depends on
         *
         * @return
         */
        String[] getReferencedSources();

        /**
         * Gets the definition of this source
         *
         * @return
         */
        DataSourceDefinition getDataSourceDefinition();

        /**
         * Get the id of the driver used to access this source
         *
         * @return
         * @throws DriverException
         */
        String getDriverId() throws DriverException;

        /**
         * Get the type of the source as a bit-or of the constants:
         * {@link SourceManager#FILE},{@link SourceManager#DB},
         * {@link SourceManager#SQL},{@link SourceManager#VECTORIAL},
         * {@link SourceManager#MEMORY},{@link SourceManager#RASTER},
         * {@link SourceManager#SQL}
         *
         * @return
         */
        int getType();

        /**
         * Get the name of the source type as a short description
         *
         * @return
         * @throws DriverException
         */
        String getTypeName() throws DriverException;

        /**
         * Gets the file of this source. If this source is not a file it returns
         * null
         *
         * @return
         */
        File getFile();

        /**
         * Gets the definition of the db source. If this source is not a database
         * source it returns null
         *
         * @return
         */
        DBSource getDBSource();

        /**
         * Get the WMSSource with the information of the WMS connection. If this
         * source is not a wms source it returns null
         *
         * @return
         */
        WMSSource getWMSSource();

        /**
         * Gets the source of the object source. If this source is not a object
         * source it returns null
         *
         * @return
         */
        MemoryDriver getObject();
        
        /**
         * Gets an URI representing this source.
         * 
         * The URI depends on the kind of source. If the source is:
         * <ul>
         * <li>based on some file, this is just the usual URI to the file (file://...)</li>
         * <li>a database connection, this is a URI-formatted connection string</li>
         * <li>a remote stream (e.g. WMS), this is the full URL to its entry point</li>
         * <li>a memory-only object (including a SQL view), this returns <tt>null</tt></li>
         * </ul>
         * @return
         * @throws DriverException  
         */
        URI getURI() throws DriverException;

        /**
         * @return true if this source is a file. False otherwise
         */
        boolean isFileSource();

        /**
         * @return true if source is a database table. False otherwise
         */
        boolean isDBSource();

        /**
         * @return true if source is a wms layer. False otherwise
         */
        boolean isWMSSource();

        /**
         * @return true if source is an object. False otherwise
         */
        boolean isObjectSource();

        /**
         * @return true if source is a sql query. False otherwise
         */
        boolean isSQLSource();

        /**
         *
         * @return true if source is a system table. False otherwise
         */
        boolean isSystemTableSource();

        /**
         * @return true if the source is a live source (e.g. a view). False otherwise
         */
        boolean isLiveSource();
}
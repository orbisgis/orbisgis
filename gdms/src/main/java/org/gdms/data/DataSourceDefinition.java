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

import java.net.URI;
import java.util.List;

import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.schema.Schema;
import org.gdms.driver.DataSet;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.source.directory.DefinitionType;

/**
 * Class to be implemented to add new types of sources to the system.
 */
public interface DataSourceDefinition<D extends Driver> {

        /**
         * Creates a DataSource with the information of this object
         *
         * @param tableName name of the DataSource
         * @param pm to indicate progress or being canceled
         * @return DataSource
         * @throws DataSourceCreationException
         */
        DataSource createDataSource(String tableName, ProgressMonitor pm)
                throws DataSourceCreationException;

        /**
         * Creates this source with the content specified in the parameter
         *
         * @param contents
         * @param pm
         * @throws DriverException
         */
        void createDataSource(DataSet contents, ProgressMonitor pm)
                throws DriverException;

        /**
         * if any, frees the resources taken when the DataSource was created
         *
         * @param name dataSource registration name
         * @throws DataSourceFinalizationException if the operation fails
         */
        void freeResources(String name)
                throws DataSourceFinalizationException;

        /**
         * Gives to the DataSourceDefinition a reference of the DataSourceFactory
         * where the DataSourceDefinition is registered
         *
         * @param dsf
         */
        void setDataSourceFactory(DataSourceFactory dsf);

        /**
         * Returns a xml object to save the definition at disk
         *
         * @return
         */
        DefinitionType getDefinition();

        /**
         * Calculates the checksum of the source
         *
         * @param openDS an instance to an open DataSource that accesses the source
         * this object defines or null if there is none
         *
         * @return
         * @throws DriverException
         */
        String calculateChecksum(DataSource openDS) throws DriverException;

        /**
         * Gets the names of the sources this source depends on. Usually it will be
         * an empty array but definitions that consist in an sql instruction may
         * return several values
         *
         * @return
         * @throws DriverException
         */
        List<String> getSourceDependencies() throws DriverException;

        /**
         * Gets the type of the source accessed by this definition
         *
         * @return
         * @throws DriverException
         */
        int getType() throws DriverException;

        /**
         * Get the source type description of the source accessed by this definition
         *
         * @return
         * @throws DriverException
         */
        String getTypeName() throws DriverException;

        /**
         * Method that lets the DataSourceDefinitions perform any kind of
         * initialization
         *
         * @throws DriverException
         * If the source is not valid and cannot be initializated
         */
        void initialize() throws DriverException;

        /**
         * Get the id of the driver used to access this source definition
         *
         * @return the id of the driver
         * @throws DriverException
         */
        String getDriverId() throws DriverException;

        /**
         * Gets the driver associated with this source.
         *
         * @return the driver
         * @throws DriverException
         */
        D getDriver() throws DriverException;

        /**
         * Refreshes all stored data of the definition (e.g. the type).
         */
        void refresh();

        /**
         * Gets the name of the table of the driver this Definition is
         * describing
         *
         * @return the name of the driver
         */
        String getDriverTableName();

        /**
         * Deletes all physical storage associated with this source.
         */
        void delete() throws DriverException;

        /**
         * Gets the schema of underlying source.
         *
         * @return a never-empty schema
         * @throws DriverException
         */
        Schema getSchema() throws DriverException;

        /**
         * Gets an URI representing this definition.
         * @throws DriverException 
         */
        URI getURI() throws DriverException;
}

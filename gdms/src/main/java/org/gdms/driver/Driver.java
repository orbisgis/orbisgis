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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
package org.gdms.driver;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.TypeDefinition;

/**
 * Provides access to data for GDMS.
 * @author Antoine Gourlay, alexis
 */
public interface Driver {

        /**
         * Gets the driver specific Schema.
         *
         * Important: this Schema must have a unique name. It should at least be
         * dependant on the Driver kind and the specific resource being accessed.
         *
         * @return
         * @throws DriverException
         */
        Schema getSchema() throws DriverException;

        /**
         * Get an access to a table returned by this driver
         * @param name the name of the table
         * @return a DataSet object accessing the table
         */
        DataSet getTable(String name);

        /**
         * Method to pass references to the driver
         *
         * @param dsf
         */
        void setDataSourceFactory(DataSourceFactory dsf);
        
        /**
         * Gets the type of the sources this driver can read. It can be one of the
         * constants in {@link SourceManager} or a new value that will be meaningful
         * for the client of this method.</p>
         * <p>This method is different from {@code getType()}, because it is meaningful
         * even if the driver is not associated to any source (to a File for a {@code FileDriver},
         * for instance). 
         * 
         * @return 
         */
        int getSupportedType();

        /**
         * Gets the type of the sources this driver can read. It can be one of the
         * constants in {@link SourceManager} or a new value that will be meaningful
         * for the client of this method.</p>
         * <p>Note that if the driver is not associated to any source, the correct behaviour
         * of this method can't be guaranteed. If you need to know the general capabilities
         * of a driver (to give a list of drivers that have a particular capability, for instance),
         * you should prefer the use of {@code getSupportedType}.
         *
         * @return
         */
        int getType();

        /**
         * Return a short representation of the source types this driver accesses.
         * E.g.: SHP
         *
         * @return
         */
        String getTypeName();

        /**
         * Return a 'long' description of the source type this driver accesses.
         * E.g.: Esri shapefile format
         *
         * @return
         */
        String getTypeDescription();

        /**
         * Get a description of the driver
         * @return
         */
        String getDriverId();

        /**
         * Return true if the driver can write contents to the source
         *
         * @return
         */
        boolean isCommitable();

        /**
         * Gets the definitions of the actual types this Driver can handle
         * @return
         */
        TypeDefinition[] getTypesDefinitions();

        /**
         * Validates the specified metadata checking that it contains all the
         * requirements to be stored with this driver. If it doesn't a String with
         * the problem should be returned
         *
         * @param metadata
         * @return A description of the problem this metadata has or null if it is
         *         suitable of being used with this driver
         * @throws DriverException
         *             If there is a problem reading the metadata
         */
        String validateMetadata(Metadata metadata) throws DriverException;
}

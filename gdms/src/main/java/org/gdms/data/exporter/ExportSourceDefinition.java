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
package org.gdms.data.exporter;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.Schema;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.io.Exporter;

/**
 * A definition for a way to export a data source.
 *
 * @author Antoine Gourlay
 */
public interface ExportSourceDefinition {

        /**
         * Gets the type of the source accessed by this definition.
         *
         * @return
         */
        int getType();

        /**
         * Get the source type description of the source accessed by this definition.
         *
         * @return
         */
        String getTypeName();

        /**
         * Get the id of the exporter used to access this source definition.
         *
         * @return the id of the driver
         */
        String getExporterId();

        /**
         * Gets the exporter associated with this source.
         *
         * @return the driver
         */
        Exporter getExporter();

        /**
         * Gets the schema of underlying source.
         *
         * @return a never-empty schema
         * @throws DriverException
         */
        Schema getSchema() throws DriverException;

        /**
         * Gives to the DataSourceDefinition a reference of the DataSourceFactory
         * where the DataSourceDefinition is registered
         *
         * @param dsf
         */
        void setDataSourceFactory(DataSourceFactory dsf);

        /**
         * Exports the content of the given data set to this source, in the specified table
         * @param dataSet the data set to export
         * @param table the table in the schema of this source
         * @throws DriverException 
         */
        void export(DataSet dataSet, String table) throws DriverException;
}

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
package org.gdms.driver.io;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.Schema;
import org.gdms.driver.DriverException;

/**
 *
 * @author Antoine Gourlay
 */
public interface Importer {

        /**
         * Gets the importer specific Schema.
         *
         * Important: this Schema must have a unique name. It should at least be
         * dependant on the Driver kind and the specific resource being accessed.
         *
         * @return
         * @throws DriverException
         */
        Schema getSchema() throws DriverException;

        /**
         * Sets the DSF for this importer.
         *
         * @param dsf a DSF
         */
        void setDataSourceFactory(DataSourceFactory dsf);

        /**
         * Gets the type of the sources this importer can read. It can be one of the
         * constants in {@link SourceManager} or a new value that will be meaningful
         * for the client of this method.</p>
         * <p>This method is different from {@code getType()}, because it is meaningful
         * even if the importer is not associated to any source.
         *
         * @return
         */
        int getSupportedType();

        /**
         * Gets the type of the sources this importer can read. It can be one of the
         * constants in {@link SourceManager} or a new value that will be meaningful
         * for the client of this method.</p>
         * <p>Note that if the importer is not associated to any source, the correct behaviour
         * of this method can't be guaranteed. If you need to know the general capabilities
         * of a importer (to give a list of importers that have a particular capability, for instance),
         * you should prefer the use of {@code getSupportedType}.
         *
         * @return
         */
        int getType();

        /**
         * Return a short representation of the source types this importer accesses.
         * E.g.: SHP
         *
         * @return
         */
        String getTypeName();

        /**
         * Return a 'long' description of the source type this importer accesses.
         * E.g.: Esri shapefile format
         *
         * @return
         */
        String getTypeDescription();

        /**
         * Get a description of the importer.
         *
         * @return
         */
        String getImporterId();

        /**
         * Opens the importer.
         *
         * @throws DriverException
         */
        void open() throws DriverException;

        /**
         * Closes the source being accessed.
         *
         * @throws DriverException
         */
        void close() throws DriverException;
        
        /**
         * Converts the table with the specified name using the given row writer
         * 
         * @param name a table in the schema of this importer
         * @param v an object to write the content of the table to
         * @throws DriverException 
         */
        void convertTable(String name, RowWriter v) throws DriverException;
}

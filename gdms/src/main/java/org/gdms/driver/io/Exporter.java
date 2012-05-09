/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 * 
 * Team leader : Erwan BOCHER, scientific researcher,
 * 
 * User support leader : Gwendall Petit, geomatic engineer.
 * 
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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

import org.orbisgis.progress.ProgressMonitor;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;

/**
 *
 * @author Antoine Gourlay
 */
public interface Exporter {

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
         * Method to pass references to the driver
         *
         * @param dsf
         */
        void setDataSourceFactory(DataSourceFactory dsf);

        /**
         * Gets the type of the sources this importer can write. It can be one of the
         * constants in {@link SourceManager} or a new value that will be meaningful
         * for the client of this method.</p>
         * <p>This method is different from {@code getType()}, because it is meaningful
         * even if the importer is not associated to any source.
         *
         * @return
         */
        int getSupportedType();

        /**
         * Gets the type of the sources this importer can write. It can be one of the
         * constants in {@link SourceManager} or a new value that will be meaningful
         * for the client of this method.</p>
         * <p>Note that if the importer is not associated to any source, the correct behaviour
         * of this method can't be guaranteed. If you need to know the general capabilities
         * of a exporter (to give a list of exporters that have a particular capability, for instance),
         * you should prefer the use of {@code getSupportedType}.
         *
         * @return
         */
        int getType();

        /**
         * Return a short representation of the source types this exporter accesses.
         * E.g.: SHP
         *
         * @return
         */
        String getTypeName();

        /**
         * Return a 'long' description of the source type this exporter accesses.
         * E.g.: Esri shapefile format
         *
         * @return
         */
        String getTypeDescription();

        /**
         * Get a description of the exporter
         *
         * @return
         */
        String getExporterId();

        /**
         * Opens the exporter.
         *
         * @throws DriverException
         */
        void open() throws DriverException;

        /**
         * Closes the source being accessed
         *
         * @throws DriverException
         */
        void close() throws DriverException;

        /**
         * Writes the content of the DataSet to this exporter's specified table.
         *
         * @param dataSource
         * @param table 
         * @param pm
         * @throws DriverException
         */
        void write(DataSet dataSource, String table, ProgressMonitor pm) throws DriverException;
}

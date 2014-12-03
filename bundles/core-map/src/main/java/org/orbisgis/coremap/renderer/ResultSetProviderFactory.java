/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.coremap.renderer;

import com.vividsolutions.jts.geom.Envelope;
import org.h2gis.utilities.SpatialResultSet;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.commons.progress.ProgressMonitor;

import java.sql.SQLException;

/**
 * In order to split-up Data query and rendering. The renderer use this interface to query the database.
 * @author Nicolas Fortin
 */
public interface ResultSetProviderFactory {

    /**
     * @param layer Layer to be requested
     * @param pm ProgressMonitor allows to display the process and cancel it.
     * @return Object that query the database.
     * @throws java.sql.SQLException
     */
    ResultSetProvider getResultSetProvider(ILayer layer, ProgressMonitor pm) throws SQLException;

    /**
     * @return Localized result set identifier
     */
    String getName();

    /**
     * Object that query the database.
     */
    public interface ResultSetProvider extends AutoCloseable {
        /**
         * The returned result set may preserve the {@link java.sql.ResultSet#getRow()} of the entire table without
         * filtering.
         * @param pm ProgressMonitor allows to display the process and cancel it.
         * @param extent Filter entities by this envelope
         * @return The content of the table
         * @throws java.sql.SQLException
         */
        SpatialResultSet execute(ProgressMonitor pm, Envelope extent) throws SQLException;

        /**
         * @return The primary key column name, empty if there is no such thing.
         */
        String getPkName();

        @Override
        void close() throws SQLException;
    }
}

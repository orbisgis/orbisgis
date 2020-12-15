/*
 * Bundle OSM is part of the OrbisGIS platform
 *
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 *
 * OSM is distributed under LGPL 3 license.
 *
 * Copyright (C) 2019 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * OSM is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OSM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * OSM. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.osm_utils.utils

import org.h2gis.api.EmptyProgressVisitor
import org.h2gis.functions.io.osm.OSMDriverFunction
import org.h2gis.utilities.JDBCUtilities
import org.h2gis.utilities.TableLocation

import java.sql.Connection

import static org.h2gis.utilities.JDBCUtilities.*

/**
 * Utilities for the data gathering.
 *
 * @author Erwan Bocher (CNRS LAB-STICC)
 * @author Elisabeth Le Saux (UBS LAB-STICC)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

/**
 * This process is used to load an OSM file in a database.
 *
 * @param connection   A connection to a database
 * @param tablesPrefix A prefix to identify the 10 OSM tables
 * @param filePath     The path where the OSM file is
 *
 * @return True if the loading has been successfully done.
 */
static boolean load(Connection connection, String tablesPrefix, def filePath) {
    if (!connection) {
        error "Invalid database connection"
        return
    }
    if (!tablesPrefix || !'^[a-zA-Z0-9_]*$'.compileRegex().matcher(tablesPrefix).matches()) {
        error "Please set a valid table prefix (regex : ^[a-zA-Z0-9_]*\$)."
        return false
    }
    if (!filePath) {
        error "Please set a valid data file path."
        return false
    }
    def osmFile = new File(filePath)
    if (!osmFile.exists()) {
        error "The input data file does not exist."
        return false
    }

    info "Load the data file in the database."
    def tableRef = TableLocation.parse(tablesPrefix, isH2DataBase(connection)).toString(isH2DataBase(connection))
    new OSMDriverFunction().importFile(connection, tableRef, osmFile, true, new EmptyProgressVisitor())
    info "The input data file has been loaded in the database."

    return true
}

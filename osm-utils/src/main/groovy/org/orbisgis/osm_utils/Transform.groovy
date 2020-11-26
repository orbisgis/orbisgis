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
package org.orbisgis.osm_utils

import org.orbisgis.osm_utils.utils.TransformUtils

import java.sql.Connection

/**
 * Groovy script containing methods used to extracts points, lines and polygons from OSM tables.
 *
 * @author Erwan Bocher (CNRS LAB-STICC)
 * @author Elisabeth Lesaux (UBS LAB-STICC)
 * @author Sylvain PALOMINOS (UBS Chaire GEOTERA 2020)
 */

/**
 * Extracts all the points from the OSM tables into a single table.
 *
 * @param connection      Connection to the database containing the OSM data. (Mandatory)
 * @param osmTablesPrefix Prefix name for OSM tables. (Mandatory)
 * @param epsgCode        EPSG code used to reproject the geometries. (Optional, default = 4326)
 * @param tags            List of OSM tags used to filter the nodes to keep.
 * @param columnsToKeep   List of columns to keep. The name of a column corresponds to a key name
 *
 * @return outputTableName Name of the result table which contains all the extracted nodes.
 */
def toPoints(Connection connection, String osmTablesPrefix, int epsgCode = 4326, def tags, def columnsToKeep) {
    //Check the mandatory inputs
    if (!connection) {
        error "Invalid database connection."
        return
    }
    if (!osmTablesPrefix) {
        error "Invalid OSM tables name prefix."
        return
    }
    if(epsgCode < 0) {
        error "Invalid EPSG code."
        return
    }

    def outputTableName = "OSM_POINTS".postfix()
    info "Start points transformation"
    info "Indexing osm tables..."
    TransformUtils.buildIndexes(connection, osmTablesPrefix)
    def pointsNodes = OSMTools.Extract.nodesAsPoints(connection, osmTablesPrefix, outputTableName, epsgCode, tags, columnsToKeep)
    if (pointsNodes) {
        info "The points have been built."
    } else {
        warn "Cannot extract any point."
        return
    }
    return outputTableName
}

/**
 * Extracts all the lines from the OSM tables into a single table.
 *
 * @param connection      Connection to the database containing the OSM data. (Mandatory)
 * @param osmTablesPrefix Prefix name for OSM tables. (Mandatory)
 * @param epsgCode        EPSG code used to reproject the geometries. (Optional)
 * @param tags            List of OSM tags used to filter the lines to keep.
 * @param columnsToKeep   List of columns to keep. The name of a column corresponds to a key name.
 *
 * @return outputTableName Name of the result table which contains all the extracted lines.
 */
def toLines(Connection connection, String osmTablesPrefix, int epsgCode = 4326, def tags, def columnsToKeep) {
    return TransformUtils.toPolygonOrLine(TransformUtils.LINES, connection, osmTablesPrefix, epsgCode, tags, columnsToKeep)
}

/**
 * Extracts all the polygons from the OSM tables into a single table.
 *
 * @param connection      Connection to the database containing the OSM data. (Mandatory)
 * @param osmTablesPrefix Prefix name for OSM tables. (Mandatory)
 * @param epsgCode        EPSG code used to reproject the geometries. (Optional)
 * @param tags            List of OSM tags used to filter the polygons to keep.
 * @param columnsToKeep   List of columns to keep. The name of a column corresponds to a key name.
 *
 * @return outputTableName Name of the result table which contains all the extracted polygon.
 */
def toPolygons(Connection connection, String osmTablesPrefix, int epsgCode = 4326, def tags, def columnsToKeep) {
    return TransformUtils.toPolygonOrLine(TransformUtils.POLYGONS, connection, osmTablesPrefix, epsgCode, tags, columnsToKeep)
}
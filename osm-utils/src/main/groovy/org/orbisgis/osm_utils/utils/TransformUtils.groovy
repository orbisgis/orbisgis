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

import groovy.transform.Field
import org.h2gis.utilities.JDBCUtilities
import org.h2gis.utilities.TableLocation
import org.orbisgis.osm_utils.OSMTools

import java.sql.Connection

import static org.h2gis.utilities.JDBCUtilities.*

/**
 * Script containing utility methods for the {@link org.orbisgis.osm_utils.Transform} script to keep only
 * main methods inside the groovy script.
 *
 * @author Erwan Bocher (CNRS LAB-STICC)
 * @author Elisabeth Lesaux (UBS LAB-STICC)
 * @author Sylvain PALOMINOS (UBS Chaire GEOTERA 2020)
 */

@Field public static final String LINES = "LINES"
@Field public static final String POLYGONS = "POLYGONS"

/**
 * Extract all the polygons/lines from the OSM tables
 *
 * @param type            Type of the geometry to extract (may be "LINES" or "POLYGONS"). (Mandatory)
 * @param connection      Connection to the database containing the OSM data. (Mandatory)
 * @param osmTablesPrefix Prefix name for OSM tables. (Mandatory)
 * @param epsgCode        EPSG code used to reproject the geometries. (Optional)
 * @param tags            List of OSM tags used to filter the lines to keep.
 * TODO : Explicit the usage of columnsToKeep and check name
 * @param columnsToKeep   List of columns to keep. The name of a column corresponds to a key name
 *
 * @return Name of the result table which contains all the extracted polygons/lines
 */
static def toPolygonOrLine(String type, Connection connection, osmTablesPrefix, epsgCode = 4326, tags, columnsToKeep) {
    //Check if parameters a good
    if (!type) {
        error "Invalid data type : $type"
        return
    }
    if (!connection) {
        error "Invalid database connection"
        return
    }
    if (!osmTablesPrefix) {
        error "Invalid OSM tables name prefix."
        return
    }
    if(!tags && ! columnsToKeep){
        error "No tags nor columns to keep"
        return
    }

    //Get the extraction methods according to the type
    def waysProcess
    def relationsProcess
    switch (type) {
        case POLYGONS:
            waysProcess = OSMTools.Extract.&waysAsPolygons
            relationsProcess = OSMTools.Extract.&relationsAsPolygons
            break
        case LINES:
            waysProcess = OSMTools.Extract.&waysAsLines
            relationsProcess = OSMTools.Extract.&relationsAsLines
            break
        default:
            error "Wrong type '${type}'."
            return
    }

    //Start the transformation
    def outputTableName = type.prefix("OSM").postfix()
    info "Start ${type.toLowerCase()} transformation"
    info "Indexing osm tables..."
    buildIndexes(connection, osmTablesPrefix)

    def outputWay = waysProcess(connection, osmTablesPrefix, epsgCode, tags, columnsToKeep)
    def outputRelation = relationsProcess(connection, osmTablesPrefix, epsgCode, tags, columnsToKeep)

    if (outputWay && outputRelation) {
        //Merge ways and relations
        def columnsWays = getColumnNames(connection, TableLocation.parse(outputWay, isH2DataBase(connection)))
        def columnsRelations = getColumnNames(connection, TableLocation.parse(outputRelation, isH2DataBase(connection)))
        def allColumns = [columnsWays, columnsRelations].flatten().unique().sort()
        def leftSelect = ""
        def rightSelect = ""
        allColumns.each { column ->
            leftSelect += columnsWays.contains(column) ? "\"$column\"," : "null AS \"$column\","
            rightSelect += columnsRelations.contains(column) ? "\"$column\"," : "null AS \"$column\","
        }
        leftSelect = leftSelect[0..-2]
        rightSelect = rightSelect[0..-2]

        connection.execute """
            DROP TABLE IF EXISTS $outputTableName;
            CREATE TABLE $outputTableName AS 
                SELECT $leftSelect
                FROM $outputWay
                UNION ALL
                SELECT $rightSelect
                FROM $outputRelation;
            DROP TABLE IF EXISTS $outputWay, $outputRelation;
        """
        info "The way and relation $type have been built."
    } else if (outputWay) {
        connection.execute "ALTER TABLE $outputWay RENAME TO $outputTableName"
        info "The way $type have been built."
    } else if (outputRelation) {
        connection.execute "ALTER TABLE $outputRelation RENAME TO $outputTableName"
        info "The relation $type have been built."
    } else {
        warn "Cannot extract any $type."
        return
    }
    outputTableName
}

/**
 * Build the indexes to perform analysis quicker.
 *
 * @param connection      Connection to the database containing the OSM data. (Mandatory)
 * @param osmTablesPrefix Prefix name for OSM tables. (Mandatory)
 *
 * @return True if the index creation has been done, false otherwise.
 */
static def buildIndexes(Connection connection, String osmTablesPrefix){
    if (!connection) {
        error "Invalid database connection"
        return false
    }
    if (!osmTablesPrefix) {
        error "Invalid OSM tables name prefix."
        return false
    }
    connection.execute """
        CREATE INDEX IF NOT EXISTS ${osmTablesPrefix}_node_index                     ON ${osmTablesPrefix}_node(id_node);
        CREATE INDEX IF NOT EXISTS ${osmTablesPrefix}_way_node_id_node_index         ON ${osmTablesPrefix}_way_node(id_node);
        CREATE INDEX IF NOT EXISTS ${osmTablesPrefix}_way_node_order_index           ON ${osmTablesPrefix}_way_node(node_order);
        CREATE INDEX IF NOT EXISTS ${osmTablesPrefix}_way_node_id_way_index          ON ${osmTablesPrefix}_way_node(id_way);
        CREATE INDEX IF NOT EXISTS ${osmTablesPrefix}_way_index                      ON ${osmTablesPrefix}_way(id_way);
        CREATE INDEX IF NOT EXISTS ${osmTablesPrefix}_way_tag_key_tag_index          ON ${osmTablesPrefix}_way_tag(tag_key);
        CREATE INDEX IF NOT EXISTS ${osmTablesPrefix}_way_tag_id_way_index           ON ${osmTablesPrefix}_way_tag(id_way);
        CREATE INDEX IF NOT EXISTS ${osmTablesPrefix}_way_tag_value_index            ON ${osmTablesPrefix}_way_tag(tag_value);
        CREATE INDEX IF NOT EXISTS ${osmTablesPrefix}_relation_id_relation_index     ON ${osmTablesPrefix}_relation(id_relation);
        CREATE INDEX IF NOT EXISTS ${osmTablesPrefix}_relation_tag_key_tag_index     ON ${osmTablesPrefix}_relation_tag(tag_key);
        CREATE INDEX IF NOT EXISTS ${osmTablesPrefix}_relation_tag_id_relation_index ON ${osmTablesPrefix}_relation_tag(id_relation);
        CREATE INDEX IF NOT EXISTS ${osmTablesPrefix}_relation_tag_tag_value_index   ON ${osmTablesPrefix}_relation_tag(tag_value);
        CREATE INDEX IF NOT EXISTS ${osmTablesPrefix}_way_member_id_relation_index   ON ${osmTablesPrefix}_way_member(id_relation);
        CREATE INDEX IF NOT EXISTS ${osmTablesPrefix}_way_id_way                     ON ${osmTablesPrefix}_way(id_way);
    """
    return true
}
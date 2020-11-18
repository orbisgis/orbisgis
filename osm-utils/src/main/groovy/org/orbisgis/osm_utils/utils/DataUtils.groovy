package org.orbisgis.osm_utils.utils

import org.h2gis.api.EmptyProgressVisitor
import org.h2gis.functions.io.osm.OSMDriverFunction
import org.h2gis.utilities.JDBCUtilities
import org.h2gis.utilities.TableLocation

import java.sql.Connection

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
    def tableRef = TableLocation.parse(tablesPrefix, connection.isH2DataBase()).toString(connection.isH2DataBase())
    new OSMDriverFunction().importFile(connection, tableRef, osmFile, true, new EmptyProgressVisitor())
    info "The input data file has been loaded in the database."

    return true
}

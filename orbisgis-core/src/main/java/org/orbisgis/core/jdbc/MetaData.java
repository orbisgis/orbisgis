/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.jdbc;

import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nicolas Fortin
 */
public class MetaData {

    /**
     * Returns a new unique name when registering a {@link javax.sql.DataSource}.
     * @param table Table identifier
     * @param meta JDBC meta data
     * @param baseName Destination table additional name, may be empty
     *
     * @return New unique name
     */
    public static String getNewUniqueName(String table, DatabaseMetaData meta,String baseName) throws SQLException {
        TableLocation uniqueName;
        TableLocation tableName = TableLocation.parse(table);
        int index = 0;
        if(!baseName.isEmpty()) {
            uniqueName = new TableLocation(tableName.getCatalog(),tableName.getSchema(),
                tableName.getTable()+"_"+baseName);
        } else {
            uniqueName = tableName;
        }
        while (tableExists(uniqueName.toString(), meta)) {
            index++;
            if(!baseName.isEmpty()) {
                uniqueName = new TableLocation(tableName.getCatalog(),tableName.getSchema(),
                        tableName.getTable()+"_"+baseName+"_"+index);
            } else {
                uniqueName = new TableLocation(tableName.getCatalog(),tableName.getSchema(),
                        tableName.getTable()+"_"+index);
            }
        }
        return uniqueName.toString();
    }

    /**
     *
     * @param tableName Table identifier
     * @param meta DatabaseMetaData instance
     * @return True if table exists
     * @throws SQLException
     */
    public static boolean tableExists(String tableName, DatabaseMetaData meta) throws SQLException {
        TableLocation location = TableLocation.parse(tableName);
        try(ResultSet rs = meta.getTables(location.getCatalog(), location.getSchema(), location.getTable(), null)) {
            return rs.next();
        }
    }

    /**
     * Compute the map of primary key to row id.
     * @param connection Active connection, not closed by this function
     * @param table Table identifier [[catalog.]schema.]table
     * @param pkColumn Integer primary key column index of the table {@link JDBCUtilities#getIntegerPrimaryKey(java.sql.DatabaseMetaData, String)}
     * @return Map\<primary key, row id\>. Row id is the {@link java.sql.ResultSet#getRow()} of the "select * from table"
     */
    public static Map<Integer,Integer> primaryKeyToRowId(Connection connection, String table, int pkColumn) throws SQLException {
        TableLocation tableLocation = TableLocation.parse(table);
        int rowCount=0;
        try(Statement st = connection.createStatement()) {
            DatabaseMetaData meta = connection.getMetaData();
            try(ResultSet rs = st.executeQuery("SELECT COUNT(*) cpt from "+tableLocation.toString())) {
                if(rs.next()) {
                    rowCount = rs.getInt(1);
                }
            }
            String pkFieldName = JDBCUtilities.getFieldName(meta,table, pkColumn);
            Map<Integer,Integer> rowMap = new HashMap<>(rowCount);
            try(ResultSet rs = st.executeQuery("SELECT "+pkFieldName+" from "+tableLocation.toString())) {
                if(rs.next()) {
                    rowMap.put(rs.getInt(1), rs.getRow());
                }
            }
            return rowMap;
        }
    }
}

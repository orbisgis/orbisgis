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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Class utility to extract information from JDBC Metadata
 * @author Nicolas Fortin
 */
public class MetaData {
    private static final I18n I18N = I18nFactory.getI18n(MetaData.class);
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
     * Return escaped field name for sql request.
     * @param fieldName Field name ex: My field
     * @return quoted string ex: "My field"
     */
    public static String escapeFieldName(String fieldName) {
        return "\""+fieldName.replace("\"","\"\"")+"\"";
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

    /**
     *
     * @param connection Active connection, not closed by this function
     * @param table Table identifier [[catalog.]schema.]table
     * @param fieldName Field name ex: My field
     * @return {@link java.sql.Types} value
     * @throws SQLException If the column/table is not found.
     */
    public static int getFieldType(Connection connection, String table, String fieldName) throws SQLException {
        TableLocation tableLocation = TableLocation.parse(table);
        try(ResultSet rs = connection.getMetaData().getColumns(tableLocation.getCatalog(), tableLocation.getSchema(), tableLocation.getTable(), fieldName)) {
            if(rs.next()) {
                return rs.getInt("DATA_TYPE");
            }
        }
        throw new SQLException("Column or table not found");
    }

    /**
     * This method is used when user type a sql value in a field.
     * @param userInput User field input
     * @param sqlType Database column type {@link java.sql.Types}
     * @return Casted object
     * @throws NumberFormatException
     */
    public static Object castToSQLType(String userInput, int sqlType) throws NumberFormatException {
        switch(sqlType) {
            case Types.CHAR:
            case Types.NCHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.OTHER:
            case Types.JAVA_OBJECT:
                return userInput;
            case Types.NUMERIC:
            case Types.DECIMAL:
            case Types.BIGINT:
                return Long.parseLong(userInput);
            case Types.BIT:
            case Types.BOOLEAN:
                return userInput.equalsIgnoreCase(I18N.tr("true")) || userInput.equalsIgnoreCase(I18N.tr("yes"))
                        || !(userInput.equalsIgnoreCase(I18N.tr("false")) || userInput.equalsIgnoreCase(I18N.tr("no")))
                        || new BigDecimal(userInput).signum() != 0;
            case Types.SMALLINT:
            case Types.TINYINT:
            case Types.INTEGER:
                return Integer.valueOf(userInput);
            case Types.REAL:
            case Types.DOUBLE:
            case Types.FLOAT:
                return Double.valueOf(userInput);
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.BLOB:
            case Types.CLOB:
            case Types.NCLOB:
            case Types.ARRAY:
                return userInput.getBytes();
            case Types.DATE:
                return Date.valueOf(userInput);
            case Types.TIME:
                return Time.valueOf(userInput);
            case Types.TIMESTAMP:
                return Timestamp.valueOf(userInput);
            case Types.NULL:
                return null;
            default:
                throw new IllegalArgumentException(I18N.tr("Column type is not managed"));
        }
    }
}

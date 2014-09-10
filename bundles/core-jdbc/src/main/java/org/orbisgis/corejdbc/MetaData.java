/*
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
package org.orbisgis.corejdbc;

import org.apache.log4j.Logger;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Class utility to extract information from JDBC Metadata
 * @author Nicolas Fortin
 */
public class MetaData {
    private static final I18n I18N = I18nFactory.getI18n(MetaData.class, Locale.getDefault(), I18nFactory.FALLBACK);
    private static final Logger LOGGER = Logger.getLogger(MetaData.class);

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
        while (JDBCUtilities.tableExists(meta.getConnection(), uniqueName.toString())) {
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
     * Compute the map of primary key to row id.
     * @param connection Active connection, not closed by this function
     * @param table Table identifier [[catalog.]schema.]table
     * @param pkFieldName Primary key column of the table {@link org.orbisgis.corejdbc.MetaData#getPkName(java.sql.Connection, String, boolean)}
     * @return Map\<primary key, row id\>. Row id is the {@link java.sql.ResultSet#getRow()} of the "select * from table"
     */
    public static Map<Object,Integer> primaryKeyToRowId(Connection connection, String table, String pkFieldName) throws SQLException {
        TableLocation tableLocation = TableLocation.parse(table);
        int rowCount=0;
        try(Statement st = connection.createStatement()) {
            try(ResultSet rs = st.executeQuery("SELECT COUNT(*) cpt from "+tableLocation.toString())) {
                if(rs.next()) {
                    rowCount = rs.getInt(1);
                }
            }
            Map<Object,Integer> rowMap = new HashMap<>(rowCount);
            try(ResultSet rs = st.executeQuery("SELECT "+pkFieldName+" from "+tableLocation.toString())) {
                while(rs.next()) {
                    rowMap.put(rs.getObject(1), rs.getRow());
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
     * @param sqlType SQL type from {@link java.sql.Types}
     * @return True if the type is numeric
     */
    public static boolean isNumeric(int sqlType) {
        switch(sqlType) {
            case Types.NUMERIC:
            case Types.DECIMAL:
            case Types.BIGINT:
            case Types.SMALLINT:
            case Types.TINYINT:
            case Types.INTEGER:
            case Types.REAL:
            case Types.DOUBLE:
            case Types.FLOAT:
                return true;
            default:
                return false;
        }
    }

    /**
     * @param sqlType SQL type from {@link java.sql.Types}
     * @return True if the type is alphanumeric
     */
    public static boolean isAlphaNumeric(int sqlType) {
        switch(sqlType) {
            case Types.NUMERIC:
            case Types.DECIMAL:
            case Types.BIGINT:
            case Types.SMALLINT:
            case Types.TINYINT:
            case Types.INTEGER:
            case Types.REAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.LONGNVARCHAR:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if two table identifier are equals
     * @param tableIdentifier First table identifier
     * @param otherTableIdentifier Second table identifier
     * @return True if there are equal.
     */
    public static boolean isTableIdentifierEquals(String tableIdentifier, String otherTableIdentifier) {
        TableLocation tableLocation = TableLocation.parse(tableIdentifier);
        TableLocation tableLocation1 = TableLocation.parse(otherTableIdentifier);
        return (tableLocation.getSchema().isEmpty() && tableLocation1.getSchema().equalsIgnoreCase("public") ||
                tableLocation1.getSchema().isEmpty() && tableLocation.getSchema().equalsIgnoreCase("public")  ||
                tableLocation1.getSchema().equals(tableLocation.getSchema()))
                && tableLocation.getTable().equals(tableLocation1.getTable());
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

    /**
     * Find the primary key name of the table.
     *
     * @param connection Connection
     * @param table      Table location
     * @return The primary key name or empty
     * @throws SQLException
     */
    public static String getPkName(Connection connection, String table, boolean systemColumn) throws SQLException {
        TableLocation tableLocation = TableLocation.parse(table);
        String pkName = "";
        try (Statement st = connection.createStatement()) {
            DatabaseMetaData meta = connection.getMetaData();
            if (systemColumn) {
                if(JDBCUtilities.isH2DataBase(meta)) {
                    boolean hasSpatialIndex = false;
                    try (PreparedStatement preparedStatement = SFSUtilities.prepareInformationSchemaStatement(connection,
                            tableLocation.getCatalog(), tableLocation.getSchema(), tableLocation.getTable(),
                            "INFORMATION_SCHEMA.INDEXES", "", "TABLE_CATALOG", "TABLE_SCHEMA", "TABLE_NAME")) {
                        try (ResultSet rs = preparedStatement.executeQuery()) {
                            while (rs.next()) {
                                if ("SPATIAL INDEX".equals(rs.getString("INDEX_TYPE_NAME"))) {
                                    hasSpatialIndex = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!hasSpatialIndex) {
                        try (ResultSet rs = st.executeQuery("select _ROWID_ from " + tableLocation + " LIMIT 0")) {
                            // Issue https://github.com/irstv/orbisgis/issues/662
                            // Cannot use _ROWID_ in conjunction with spatial index
                            // TODO use always _ROWID_ when issue is fixed
                            pkName = rs.getMetaData().getColumnName(1);
                        } catch (SQLException ex) {
                            //Ignore, key does not exists
                        }
                    }
                } else {
                    // Use PostGre system column
                    try (ResultSet rs = st.executeQuery("select ctid from " + tableLocation + " LIMIT 0")) {
                        pkName = rs.getMetaData().getColumnName(1);
                    } catch (SQLException ex) {
                        //Ignore, key does not exists
                    }
                }
            }
            int pkId = JDBCUtilities.getIntegerPrimaryKey(connection, tableLocation.toString());
            if (pkId > 0) {
                // This table has a Primary key, get the field name
                pkName = JDBCUtilities.getFieldName(connection.getMetaData(), tableLocation.toString(), pkId);
            }
        }
        return pkName;
    }

    /**
     * Show known column meta data using JDBC
     * @param meta DatabaseMetaData instance
     * @param tableReference Table identifier
     * @param col Column index [1-n]
     * @return Localised column information
     * @throws SQLException
     */
    public static String getColumnInformations(DatabaseMetaData meta, String tableReference, int col) throws SQLException {
        TableLocation table = TableLocation.parse(tableReference);
        StringBuilder infos = new StringBuilder();
        try(ResultSet rs = meta.getColumns(table.getCatalog(), table.getSchema(), table.getTable(), null)) {
            while (rs.next()) {
                if(rs.getInt("ORDINAL_POSITION") == col) {
                    infos.append(I18N.tr("\nField name :\t{0}\n",rs.getString("COLUMN_NAME")));
                    infos.append(I18N.tr("Field type :\t{0}\n",rs.getString("TYPE_NAME")));
                    String remarks = rs.getString("REMARKS");
                    if(remarks != null && !remarks.isEmpty()) {
                        infos.append(I18N.tr("Field remarks :\t{0}\n",remarks));
                    }
                    int columnSize = rs.getInt("COLUMN_SIZE");
                    if(!rs.wasNull() && Integer.MAX_VALUE > columnSize) {
                        infos.append(I18N.tr("Size :\t{0}\n", columnSize));
                    }
                    int decimalDigits = rs.getInt("DECIMAL_DIGITS");
                    if(!rs.wasNull() && Integer.MAX_VALUE > decimalDigits) {
                        infos.append(I18N.tr("Decimal digits :\t{0}\n", decimalDigits));
                    }
                    int nullable = rs.getInt("NULLABLE");
                    if(!rs.wasNull()) {
                        switch (nullable) {
                            case DatabaseMetaData.columnNoNulls:
                                // JDBC says might not allow <code>NULL</code> values
                                infos.append(I18N.tr("Nullable : {0}\n", rs.getString("IS_NULLABLE")));
                                break;
                            case DatabaseMetaData.columnNullable:
                                infos.append(I18N.tr("Nullable : allows NULL values\n"));
                                break;
                            default:
                                infos.append(I18N.tr("Nullable : Unknown\n"));
                        }
                    }
                    infos.append(I18N.tr("Default value :\t{0}\n", rs.getString("COLUMN_DEF")));
                    infos.append(I18N.tr("Auto increment :\t{0}\n", rs.getString("IS_AUTOINCREMENT")));
                    break;
                }
            }
        }
        infos.append(I18N.tr("Constraints :\n"));
        try(ResultSet rs = meta.getIndexInfo(table.getCatalog(), table.getSchema(), table.getTable(), false, false)) {
            while (rs.next()) {
                if(rs.getInt("ORDINAL_POSITION") == col) {
                    String filter = rs.getString("FILTER_CONDITION");
                    if(filter != null && !filter.isEmpty()) {
                        infos.append(I18N.tr("\t{0} :\t{1}\n",
                                rs.getString("INDEX_NAME"),filter));
                    }
                    short type = rs.getShort("TYPE");
                    switch (type) {
                        case DatabaseMetaData.tableIndexStatistic:
                            infos.append(I18N.tr("\tType :\ttable statistics\n"));
                            break;
                        case DatabaseMetaData.tableIndexClustered:
                            infos.append(I18N.tr("\tType :\tclustered index\n"));
                            break;
                        case DatabaseMetaData.tableIndexHashed:
                            infos.append(I18N.tr("\tType :\thashed index\n"));
                            break;
                        case DatabaseMetaData.tableIndexOther:
                            infos.append(I18N.tr("\tType :\tother index\n"));
                            break;
                    }
                }
            }
        }
        return infos.toString();
    }
    
    /**
     * Retrieves the table type available for a given table.
     * 
     * @param connection Database connection
     * @param tableName The table of the table
     * @return the type of the table
     * @throws SQLException 
     */
    public static TableType getTableType(Connection connection, String tableName) throws SQLException {
        TableLocation tableLoc = TableLocation.parse(tableName);
        try (ResultSet rs = connection.getMetaData().getTables(tableLoc.getCatalog(), tableLoc.getSchema(),
                tableLoc.getTable(), null)) {
            while (rs.next()) {
                String type = rs.getString(4).toUpperCase();                
                switch (type) {
                    case "TABLE":
                        return TableType.TABLE;
                    case "VIEW":
                        return TableType.VIEW;
                    case "SYSTEM TABLE":
                        return TableType.SYSTEM_TABLE;
                    case "GLOBAL TEMPORARY":
                        return TableType.GLOBAL_TEMPORARY;
                    case "LOCAL TEMPORARY":
                        return TableType.LOCAL_TEMPORARY;
                    case "ALIAS":
                        return TableType.ALIAS;
                    case "SYNONYM":
                        return TableType.SYNONYM;
                    case "EXTERNAL":
                        return TableType.EXTERNAL;
                    default:
                        return TableType.TABLE;
                }
            }
        }
        throw new SQLException(I18N.tr("Cannot find the table {0}", tableName));
    }

    /**
     * List of available table types
     */
    public enum TableType {
        EXTERNAL, SYSTEM_TABLE, VIEW, TABLE,
        GLOBAL_TEMPORARY,
        LOCAL_TEMPORARY, ALIAS, SYNONYM
    }
}

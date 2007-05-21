package org.gdms.data.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.gdms.data.edition.Field;
import org.gdms.data.metadata.DefaultDriverMetadata;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.PTTypes;


/**
 * DBDrivers helper class
 */
public class JDBCSupport {
    public static final String CHAR = "CHAR";

    public static final String VARCHAR = "VARCHAR";

    public static final String LONGVARCHAR = "LONGVARCHAR";

    public static final String BIGINT = "BIGINT";

    public static final String BOOLEAN = "BOOLEAN";

    public static final String DATE = "DATE";

    public static final String DECIMAL = "DECIMAL";

    public static final String NUMERIC = "NUMERIC";

    public static final String FLOAT = "FLOAT";

    public static final String DOUBLE = "DOUBLE";

    public static final String INTEGER = "INTEGER";

    public static final String REAL = "REAL";

    public static final String SMALLINT = "SMALLINT";

    public static final String TINYINT = "TINYINT";

    public static final String BINARY = "BINARY";

    public static final String VARBINARY = "VARBINARY";

    public static final String LONGVARBINARY = "LONGVARBINARY";

    public static final String TIMESTAMP = "TIMESTAMP";

    public static final String TIME = "TIME";

    public static final String BIT = "BIT";

    public static final String PRECISION = "PRECISION";

    public static final String LENGTH = "LENGTH";

    public static final String SCALE = "SCALE";

    private static HashMap<Integer, String> typesDescription = new HashMap<Integer, String>();

    private ResultSet resultSet;

    private int rowCount = -1;

    static {
        java.lang.reflect.Field[] fields = Types.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                typesDescription.put((Integer) fields[i].get(null), fields[i]
                        .getName());
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
    }

    /**
     * Creates a new JDBCSupport object.
     *
     * @param r
     *            ResultSet that will be used to return the methods values
     * @param data
     */
    private JDBCSupport(ResultSet r, DatabaseMetaData dbmd) {
        resultSet = r;
    }

    /**
     * @see org.gdms.driver.ReadAccess#getFieldValue(long,
     *      int)
     */
    public Value getFieldValue(long rowIndex, int fieldId)
            throws DriverException {
        Value value = null;

        try {
            fieldId += 1;
            resultSet.absolute((int) rowIndex + 1);

            int type = resultSet.getMetaData().getColumnType(fieldId);

            switch (type) {
            case Types.BIGINT:
                value = ValueFactory.createValue(resultSet.getLong(fieldId));

                break;

            case Types.BIT:
            case Types.BOOLEAN:
                value = ValueFactory.createValue(resultSet.getBoolean(fieldId));

                break;

            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                String auxString = resultSet.getString(fieldId);
                if (auxString != null) {
                    value = ValueFactory.createValue(auxString);
                }

                break;

            case Types.DATE:
                Date auxDate = resultSet.getDate(fieldId);
                if (auxDate != null) {
                    value = ValueFactory.createValue(auxDate);
                }

                break;

            case Types.DECIMAL:
            case Types.NUMERIC:
            case Types.FLOAT:
            case Types.DOUBLE:
                value = ValueFactory.createValue(resultSet.getDouble(fieldId));

                break;

            case Types.INTEGER:
                value = ValueFactory.createValue(resultSet.getInt(fieldId));

                break;

            case Types.REAL:
                value = ValueFactory.createValue(resultSet.getFloat(fieldId));

                break;

            case Types.SMALLINT:
                value = ValueFactory.createValue(resultSet.getShort(fieldId));

                break;

            case Types.TINYINT:
                value = ValueFactory.createValue(resultSet.getByte(fieldId));

                break;

            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                byte[] auxByteArray = resultSet.getBytes(fieldId);
                if (auxByteArray != null) {
                    value = ValueFactory.createValue(auxByteArray);
                }

                break;

            case Types.TIMESTAMP:
                Timestamp auxTimeStamp = resultSet.getTimestamp(fieldId);
                if (auxTimeStamp != null) {
                    value = ValueFactory.createValue(auxTimeStamp);
                }

                break;

            case Types.TIME:
                Time auxTime = resultSet.getTime(fieldId);
                if (auxTime != null) {
                    value = ValueFactory.createValue(auxTime);
                }

                break;

            default:
                auxString = resultSet.getString(fieldId);
                if (auxString != null) {
                    value = ValueFactory.createValue(auxString);
                }

                break;
            }

            if (resultSet.wasNull()) {
                return ValueFactory.createNullValue();
            } else {
                return value;
            }
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    /**
     * @see org.gdms.driver.ReadAccess#getFieldCount()
     */
    public int getFieldCount() throws DriverException {
        try {
            return resultSet.getMetaData().getColumnCount();
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    /**
     * @see org.gdms.driver.ReadAccess#getFieldName(int)
     */
    public String getFieldName(int fieldId) throws DriverException {
        try {
            return resultSet.getMetaData().getColumnName(fieldId + 1);
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    /**
     * @see org.gdms.driver.ReadAccess#getRowCount()
     */
    public long getRowCount() throws DriverException {
        try {
            if (rowCount == -1) {
                resultSet.last();
                rowCount = resultSet.getRow();
            }

            return rowCount;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.gdms.driver.ReadAccess#getFieldType(int)
     */
    public int getFieldType(int i) throws DriverException {
        try {
            return resultSet.getMetaData().getColumnType(i + 1);
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    /**
     * Closes the internal data source
     *
     * @throws SQLException
     *             if the operation fails
     */
    public void close() throws SQLException {
        resultSet.close();
    }

    /**
     * Creates a new JDBCSuuport object with the data retrieved from the
     * connection with the given sql
     *
     * @param con
     *            Connection to the database
     * @param sql
     *            SQL defining the data to use
     *
     * @return JDBCSupport
     *
     * @throws SQLException
     *             If the data cannot be retrieved
     */
    public static JDBCSupport newJDBCSupport(Connection con, String tableName,
            String orderFieldName) throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        if (orderFieldName != null) {
            sql += " ORDER BY " + orderFieldName;
        }

        Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ResultSet res = st.executeQuery(sql);

        return new JDBCSupport(res, con.getMetaData());
    }

    /**
     * Executes a query with the 'con' connection
     *
     * @param con
     *            connection
     * @param sql
     *            instruction to execute
     *
     * @throws SQLException
     *             if execution fails
     */
    public static void execute(Connection con, String sql) throws SQLException {
        Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        st.execute(sql);
    }

    /**
     * @return
     */
    public ResultSet getResultSet() {
        return resultSet;
    }

    private void fillDriverMetadata(Connection c, String tableName,
            DefaultDriverMetadata ret) throws DriverException {
        try {
            ResultSetMetaData metadata = resultSet.getMetaData();
            for (int i = 0; i < getFieldCount(); i++) {
                int type = getFieldType(i);
                String name = getFieldName(i);
                if (type == PTTypes.GEOMETRY) {
                    ret.addField(name,
                            PTTypes.STR_GEOMETRY);
                } else {
                    String driverType = typesDescription.get(type);
                    String[] paramNames = null;
                    String[] paramValues = null;
                    if (driverType.equals(CHAR) || driverType.equals(VARCHAR)
                            || driverType.equals(LONGVARCHAR)) {
                        paramNames = new String[] { LENGTH };
                        paramValues = new String[] { Integer.toString(metadata
                                .getColumnDisplaySize(i + 1)) };
                    } else if (driverType.equals(DECIMAL)
                            || driverType.equals(NUMERIC)) {
                        paramNames = new String[] { SCALE, PRECISION };
                        paramValues = new String[] {
                                Integer.toString(metadata.getScale(i + 1)),
                                Integer.toString(metadata.getPrecision(i + 1)) };
                    } else {
                        paramNames = new String[0];
                        paramValues = new String[0];
                    }
                    ret.addField(name, driverType, paramNames, paramValues);
                }
            }

            ResultSet rs = c.getMetaData().getPrimaryKeys(null, null,
                    tableName);
            ArrayList<String> pks = new ArrayList<String>();

            while (rs.next()) {
                pks.add(rs.getString("COLUMN_NAME"));
            }

            ret.setPrimaryKey(pks.toArray(new String[0]));
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    public DefaultDriverMetadata getDriverMetadata(Connection c,
            String tableName) throws DriverException {
        DefaultDriverMetadata ret = new DefaultDriverMetadata();
        fillDriverMetadata(c, tableName, ret);
        return ret;
    }

    public static int getType(String driverType) {
        if ((CHAR.equals(driverType)) || (VARCHAR.equals(driverType))
                || (LONGVARCHAR.equals(driverType))) {
            return Value.STRING;
        } else if (BIGINT.equals(driverType)) {
            return Value.LONG;
        } else if ((BOOLEAN.equals(driverType)) || (BIT.equals(driverType))) {
            return Value.BOOLEAN;
        } else if (DATE.equals(driverType)) {
            return Value.DATE;
        } else if ((DECIMAL.equals(driverType)) || (NUMERIC.equals(driverType))
                || (FLOAT.equals(driverType)) || (DOUBLE.equals(driverType))) {
            return Value.DOUBLE;
        } else if (INTEGER.equals(driverType)) {
            return Value.INT;
        } else if (REAL.equals(driverType)) {
            return Value.FLOAT;
        } else if (SMALLINT.equals(driverType)) {
            return Value.SHORT;
        } else if (TINYINT.equals(driverType)) {
            return Value.BYTE;
        } else if ((BINARY.equals(driverType))
                || (VARBINARY.equals(driverType))
                || (LONGVARBINARY.equals(driverType))) {
            return Value.BINARY;
        } else if (TIMESTAMP.equals(driverType)) {
            return Value.TIMESTAMP;
        } else if (TIME.equals(driverType)) {
            return Value.TIME;
        }

        throw new RuntimeException("Where this driver type come from? "
                + driverType);
    }

    public static String[] getAvailableTypes(Connection connection)
            throws SQLException {
        HashSet<String> ret = new HashSet<String>();
        ResultSet rs = connection.getMetaData().getTypeInfo();
        rs.first();
        while (!rs.isAfterLast()) {
            String typeDescription = typesDescription.get(rs
                    .getInt("DATA_TYPE"));
            if (typeDescription != null) {
                ret.add(typeDescription);
            }
            rs.next();
        }

        return ret.toArray(new String[0]);
    }

    public static String[] getDefaultSQLTypes() {
        return new String[] { CHAR, VARCHAR, LONGVARCHAR, BIGINT, BOOLEAN, BIT,
                DATE, DECIMAL, NUMERIC, FLOAT, DOUBLE, INTEGER, REAL, SMALLINT,
                TINYINT, BINARY, VARBINARY, LONGVARBINARY, TIMESTAMP, TIME };
    }

    public static String[] getDefaultSQLParameters(String driverType) {
        if (driverType.equals(CHAR) || driverType.equals(VARCHAR)
                || driverType.equals(LONGVARCHAR)) {
            return new String[] { LENGTH };
        } else if (driverType.equals(DECIMAL) || driverType.equals(NUMERIC)) {
            return new String[] { PRECISION, SCALE };
        }

        return new String[0];
    }

    public static String[] getParameters(Connection connection,
            String driverType) throws SQLException {
        ResultSet rs = connection.getMetaData().getTypeInfo();
        rs.first();
        while (!rs.isAfterLast()) {
            String type = typesDescription.get(rs.getInt("DATA_TYPE"));
            if (type == null) {
                rs.next();
                continue;
            }
            if (type.equals(driverType)) {
                String ret = rs.getString("CREATE_PARAMS");
                if (ret == null) {
                    return new String[0];
                } else {
                    return ret.split("\\Q,\\E");
                }
            }
            rs.next();
        }

        throw new RuntimeException("Where does this type come from? "
                + driverType);
    }

    public static String checkStandard(Field f, Value value) {
        if (f.getDriverType().equals(CHAR) || f.getDriverType().equals(VARCHAR)
                || f.getDriverType().equals(LONGVARCHAR)) {
            if (value.toString().length() > Integer.parseInt(f.getParams().get(
                    LENGTH))) {
                return "too long";
            }
        } else if (f.getDriverType().equals(DECIMAL)
                || f.getDriverType().equals(NUMERIC)) {
            int scale = Integer.parseInt(f.getParams().get(SCALE));
            int precision = Integer.parseInt(f.getParams().get(PRECISION));

            NumericValue nv = (NumericValue) value;
            if (scale < nv.getDecimalDigitsCount()) {
                return "too many decimal digits";
            }
            if (nv.getDecimalDigitsCount() > 0) {
                /*
                 * Don't count the decimal point: length() - 1
                 */
                if (Double.toString(nv.doubleValue()).length() - 1 > precision) {
                    return "too long";
                }
            } else {
                if (Long.toString(nv.longValue()).length() > precision) {
                    return "too long";
                }
            }

            return null;
        }
        return null;
    }

    public static String getTypeInAddColumnStatement(String driverType,
            Map<String, String> params) {
        if (driverType.equals(CHAR) || driverType.equals(VARCHAR)
                || driverType.equals(LONGVARCHAR)) {
            if (params.containsKey(LENGTH)) {
                return driverType + "(" + params.get(LENGTH) + ")";
            } else {
                return driverType;
            }
        } else if (driverType.equals(DECIMAL) || driverType.equals(NUMERIC)) {
            if (params.containsKey(PRECISION) && params.containsKey(SCALE)) {
                return driverType + "(" + params.get(PRECISION) + " ,"
                        + params.get(SCALE) + ")";
            } else {
                return driverType;
            }
        } else {
            return driverType;
        }
    }

    public static void createSource(Connection c, String tableName,
            DriverMetadata driverMetadata) throws DriverException {
        String sql = "CREATE TABLE " + tableName + " (";
        int fc = driverMetadata.getFieldCount();
        if (fc > 0) {
            sql += driverMetadata.getFieldName(0)
                    + " "
                    + getTypeInAddColumnStatement(driverMetadata
                            .getFieldType(0), driverMetadata.getFieldParams(0));
        }
        for (int i = 1; i < driverMetadata.getFieldCount(); i++) {
            sql += ", "
                    + driverMetadata.getFieldName(i)
                    + " "
                    + getTypeInAddColumnStatement(driverMetadata
                            .getFieldType(i), driverMetadata.getFieldParams(i));
        }

        String[] pks = driverMetadata.getPrimaryKeys();
        if (pks.length == 0) {
            throw new DriverException("No primary key specified");
        } else {
            sql += ", PRIMARY KEY(" + pks[0];
            for (int i = 1; i < pks.length; i++) {
                sql += ", " + pks[i];
            }
            sql += ")";
        }

        sql += ")";

        try {
            Statement st = c.createStatement();
            st.execute(sql);
            st.close();
        } catch (SQLException e) {
            throw new DriverException(e.getMessage() + ":" + sql, e);
        }
    }

    public boolean isReadOnly(int fieldId) throws DriverException {
        try {
            return resultSet.getMetaData().isReadOnly(fieldId + 1);
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    public static boolean isValidParameter(String driverType, String paramName,
            String paramValue) {
        if (paramName.equals(LENGTH) || paramName.equals(PRECISION)
                || paramName.equals(SCALE)) {
            if (paramValue == null) {
                return true;
            } else {
                try {
                    Integer.parseInt(paramValue);
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        return true;
    }

}

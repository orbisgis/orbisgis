/*
 * Created on 16-oct-2004
 */
package org.gdms.driver.mysql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.JDBCSupport;
import org.gdms.data.edition.Field;
import org.gdms.data.metadata.DefaultDriverMetadata;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueWriter;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.spatial.FID;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.PTTypes;




/**
 * MySQL driver
 *
 * @author Fernando Gonzalez Cortes
 */
public class MySQLDriver implements DBDriver {
    private static Exception driverException;
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat timeFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    private static ValueWriter vWriter = ValueWriter.internalValueWriter;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            driverException = ex;
        }
    }

    private JDBCSupport jdbcSupport;

    /**
     * DOCUMENT ME!
     *
     * @param host DOCUMENT ME!
     * @param port DOCUMENT ME!
     * @param dbName DOCUMENT ME!
     * @param user DOCUMENT ME!
     * @param password DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws SQLException
     * @throws RuntimeException DOCUMENT ME!
     *
     * @see org.gdms.driver.DBDriver#connect(java.lang.String)
     */
    public Connection getConnection(String host, int port, String dbName,
        String user, String password) throws SQLException {
        if (driverException != null) {
            throw new RuntimeException(driverException);
        }

        String connectionString = "jdbc:mysql://" + host;

        if (port != -1) {
            connectionString += (":" + port);
        }

        connectionString += ("/" + dbName);

        if (user != null) {
            connectionString += ("?user=" + user + "&password=" + password);
        }

        return DriverManager.getConnection(connectionString);
    }

    /**
     * @see com.hardcode.driverManager.Driver#getName()
     */
    public String getName() {
        return "mysql";
    }

    /**
     * @see org.gdms.driver.DBDriver#executeSQL(java.sql.Connection)
     */
	public void open(Connection con, String tableName, String orderFieldName) throws DriverException {
        try {
            jdbcSupport = JDBCSupport.newJDBCSupport(con, tableName, orderFieldName);
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws DriverException DOCUMENT ME!
     */
    public int getFieldCount() throws DriverException {
        return jdbcSupport.getFieldCount();
    }

    /**
     * DOCUMENT ME!
     *
     * @param fieldId DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws DriverException DOCUMENT ME!
     */
    public String getFieldName(int fieldId) throws DriverException {
        return jdbcSupport.getFieldName(fieldId);
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws DriverException DOCUMENT ME!
     */
    public int getFieldType(int i) throws DriverException {
        return jdbcSupport.getFieldType(i);
    }

    /**
     * DOCUMENT ME!
     *
     * @param rowIndex DOCUMENT ME!
     * @param fieldId DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws DriverException DOCUMENT ME!
     */
    public Value getFieldValue(long rowIndex, int fieldId)
        throws DriverException {
        return jdbcSupport.getFieldValue(rowIndex, fieldId);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws DriverException DOCUMENT ME!
     */
    public long getRowCount() throws DriverException {
        return jdbcSupport.getRowCount();
    }

    /**
     * @see org.gdms.driver.DBDriver#close(Connection)
     */
    public void close(Connection conn) throws DriverException {
        try {
            jdbcSupport.close();
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    /**
     * @see org.gdms.data.driver.DriverCommons#getDriverProperties()
     */
    public HashMap getDriverProperties() {
        return null;
    }

    /**
     * @see org.gdms.data.driver.DriverCommons#setDataSourceFactory(org.gdms.data.DataSourceFactory)
     */
    public void setDataSourceFactory(DataSourceFactory dsf) {
    }

    /**
     * @see org.gdms.driver.DBDriver#execute(java.sql.Connection,
     *      java.lang.String, org.gdms.data.HasProperties)
     */
    public void execute(Connection con, String sql) throws SQLException {
        JDBCSupport.execute(con, sql);
    }

    /**
     * @see org.gdms.driver.DBDriver#getStatementString(long)
     */
    public String getStatementString(long i) {
        return vWriter.getStatementString(i);
    }

    /**
     * @see org.gdms.driver.DBDriver#getStatementString(int,
     *      int)
     */
    public String getStatementString(int i, int sqlType) {
        return vWriter.getStatementString(i, sqlType);
    }

    /**
     * @see org.gdms.driver.DBDriver#getStatementString(double,
     *      int)
     */
    public String getStatementString(double d, int sqlType) {
        return vWriter.getStatementString(d, sqlType);
    }

    /**
     * @see org.gdms.driver.DBDriver#getStatementString(java.lang.String,
     *      int)
     */
    public String getStatementString(String str, int sqlType) {
        return vWriter.getStatementString(str, sqlType);
    }

    /**
     * @see org.gdms.driver.DBDriver#getStatementString(java.sql.Date)
     */
    public String getStatementString(Date d) {
        return dateFormat.format(d);
    }

    /**
     * @see org.gdms.driver.DBDriver#getStatementString(java.sql.Time)
     */
    public String getStatementString(Time t) {
        return timeFormat.format(t);
    }

    /**
     * @see org.gdms.driver.DBDriver#getStatementString(java.sql.Timestamp)
     */
    public String getStatementString(Timestamp ts) {
        return timeFormat.format(ts);
    }

    /**
     * @see org.gdms.driver.DBDriver#getStatementString(byte[])
     */
    public String getStatementString(byte[] binary) {
        return "x" + vWriter.getStatementString(binary);
    }

    /**
     * @see org.gdms.driver.DBDriver#getStatementString(boolean)
     */
    public String getStatementString(boolean b) {
        return vWriter.getStatementString(b);
    }

    /**
     * @see org.gdms.data.values.ValueWriter#getNullStatementString()
     */
    public String getNullStatementString() {
        return "null";
    }

    /**
     * @see org.gdms.data.values.ValueWriter#getStatementString(GeometryValue)
     */
    public String getStatementString(GeometryValue g) {
        return vWriter.getStatementString(g);
    }

    /**
     * @see org.gdms.driver.ReadOnlyDriver#getDriverMetadata()
     */
    public DriverMetadata getDriverMetadata() throws DriverException {
        DefaultDriverMetadata ret = new DefaultDriverMetadata();
        for (int i = 0; i < getFieldCount(); i++) {
            int type = getFieldType(i);
            ret.addField(getFieldName(i), PTTypes.typesDescription.get(type));
        }

        return ret;
    }

    /**
     * @see org.gdms.driver.ReadOnlyDriver#getType(java.lang.String)
     */
    public int getType(String driverType) {
        return JDBCSupport.getType(driverType);
    }

    public String getTypeInAddColumnStatement(String driverType, HashMap<String, String> params) {
        return driverType;
    }

    public String[] getAvailableTypes() throws DriverException {
        return JDBCSupport.getDefaultSQLTypes();
    }

    public String[] getParameters(String driverType)
            throws DriverException {
        return JDBCSupport.getDefaultSQLParameters(driverType);
    }

    public void createSource(DBSource source, DriverMetadata driverMetadata) throws DriverException {
    }

    public String check(Field field, Value value) throws DriverException {
        return null;
    }

    public boolean isReadOnly(int i) throws DriverException {
        return jdbcSupport.isReadOnly(i);
    }

    public boolean isValidParameter(String driverType, String paramName, String paramValue) {
        return JDBCSupport.isValidParameter(driverType, paramName, paramValue);
    }

	public boolean prefixAccepted(String prefix) {
		return "jdbc:mysql".equals(prefix.toLowerCase());
	}

	public String getReferenceInSQL(String fieldName) {
		return fieldName;
	}

	public Number[] getScope(int dimension, String fieldName) throws DriverException {
		return null;
	}

	public FID getFid(long row) {
		return null;
	}

	public boolean hasFid() {
		return false;
	}
}
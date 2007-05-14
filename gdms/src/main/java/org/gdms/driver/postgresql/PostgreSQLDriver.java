package org.gdms.driver.postgresql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.JDBCSupport;
import org.gdms.data.driver.DBDriver;
import org.gdms.data.driver.DriverException;
import org.gdms.data.edition.Field;
import org.gdms.data.metadata.DefaultDriverMetadata;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.values.Value;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.PTTypes;




/**
 *
 */
public class PostgreSQLDriver implements DBDriver {
    private static Exception driverException;

    static {
        try {
            Class.forName("org.postgresql.Driver").newInstance();
        } catch (Exception ex) {
            driverException = ex;
        }
    }

    protected JDBCSupport jdbcSupport;

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
     * @see org.gdms.data.driver.DBDriver#connect(java.lang.String)
     */
    public Connection getConnection(String host, int port, String dbName,
        String user, String password) throws SQLException {
        if (driverException != null) {
            throw new RuntimeException(driverException);
        }

        String connectionString = "jdbc:postgresql://" + host;

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
        return "postgresql";
    }

    /**
     * @see org.gdms.data.driver.DBDriver#executeSQL(java.sql.Connection)
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
     * @see org.gdms.data.driver.DBDriver#close(Connection)
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
     * @see org.gdms.data.driver.DBDriver#execute(java.sql.Connection,
     *      java.lang.String, org.gdms.data.HasProperties)
     */
    public void execute(Connection con, String sql) throws SQLException {
        JDBCSupport.execute(con, sql);
    }

    /**
     * @see org.gdms.data.driver.DBDriver#getStatementString(long)
     */
    public String getStatementString(long i) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.gdms.data.driver.DBDriver#getStatementString(int,
     *      int)
     */
    public String getStatementString(int i, int sqlType) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.gdms.data.driver.DBDriver#getStatementString(double,
     *      int)
     */
    public String getStatementString(double d, int sqlType) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.gdms.data.driver.DBDriver#getStatementString(java.lang.String,
     *      int)
     */
    public String getStatementString(String str, int sqlType) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.gdms.data.driver.DBDriver#getStatementString(java.sql.Date)
     */
    public String getStatementString(Date d) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.gdms.data.driver.DBDriver#getStatementString(java.sql.Time)
     */
    public String getStatementString(Time t) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.gdms.data.driver.DBDriver#getStatementString(java.sql.Timestamp)
     */
    public String getStatementString(Timestamp ts) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.gdms.data.driver.DBDriver#getStatementString(byte[])
     */
    public String getStatementString(byte[] binary) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.gdms.data.driver.DBDriver#getStatementString(boolean)
     */
    public String getStatementString(boolean b) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.gdms.data.values.ValueWriter#getNullStatementString()
     */
    public String getNullStatementString() {
        return null;
    }

    /**
     * @see org.gdms.data.driver.DBDriver#getMetadata()
     */
    public ResultSetMetaData getMetadata() throws SQLException {
        return jdbcSupport.getResultSet().getMetaData();
    }

    /**
     * @see org.gdms.data.values.ValueWriter#getStatementString(GeometryValue)
     */
    public String getStatementString(GeometryValue g) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.gdms.data.driver.GDBMSDriver#getDriverMetadata()
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
     * @see org.gdms.data.driver.GDBMSDriver#getType(java.lang.String)
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
        throw new UnsupportedOperationException();
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
		return "jdbc:postgresql".equals(prefix.toLowerCase());
	}

	public String getReferenceInSQL(String fieldName) {
		return "\"" + fieldName + "\"";
	}

	public Number[] getScope(int dimension, String fieldName) throws DriverException {
		return null;
	}
}

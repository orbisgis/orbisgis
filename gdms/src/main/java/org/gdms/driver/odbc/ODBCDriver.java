package org.gdms.driver.odbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.JDBCSupport;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueWriter;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.TableDescription;
import org.gdms.spatial.GeometryValue;

/**
 * ODBC driver
 * 
 * @author Fernando Gonzalez Cortes
 */
public class ODBCDriver implements DBDriver {
	private static Exception driverException;

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static DateFormat timeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static ValueWriter vWriter = ValueWriter.internalValueWriter;

	static {
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
		} catch (Exception ex) {
			driverException = ex;
		}
	}

	private JDBCSupport jdbcSupport;

	/**
	 * DOCUMENT ME!
	 * 
	 * @param host
	 *            DOCUMENT ME!
	 * @param port
	 *            DOCUMENT ME!
	 * @param dbName
	 *            DOCUMENT ME!
	 * @param user
	 *            DOCUMENT ME!
	 * @param password
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws SQLException
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 * 
	 * @see org.gdms.driver.DBDriver#connect(java.lang.String)
	 */
	public Connection getConnection(String host, int port, String dbName,
			String user, String password) throws SQLException {
		if (driverException != null) {
			throw new RuntimeException(driverException);
		}

		String connectionString = "jdbc:odbc:" + dbName;

		if (user != null) {
			connectionString += (";UID=" + user + ";PWD=" + password);
		}

		return DriverManager.getConnection(connectionString);
	}

	/**
	 * @see com.hardcode.driverManager.Driver#getName()
	 */
	public String getName() {
		return "odbc";
	}

	/**
	 * @see org.gdms.driver.DBDriver#executeSQL(java.sql.Connection)
	 */
	public void open(Connection con, String tableName) throws DriverException {
		try {
			jdbcSupport = JDBCSupport.newJDBCSupport(con, tableName, tableName);
		} catch (SQLException e) {
			throw new DriverException(e);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws DriverException
	 *             DOCUMENT ME!
	 */
	public int getFieldCount() throws DriverException {
		return jdbcSupport.getFieldCount();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param fieldId
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws DriverException
	 *             DOCUMENT ME!
	 */
	public String getFieldName(int fieldId) throws DriverException {
		return jdbcSupport.getFieldName(fieldId);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param i
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws DriverException
	 *             DOCUMENT ME!
	 */
	public int getFieldType(int i) throws DriverException {
		return jdbcSupport.getFieldType(i);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param rowIndex
	 *            DOCUMENT ME!
	 * @param fieldId
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws DriverException
	 *             DOCUMENT ME!
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
	 * @throws DriverException
	 *             DOCUMENT ME!
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
	 * @see org.gdms.driver.DBDriver#getStatementString(int, int)
	 */
	public String getStatementString(int i, int sqlType) {
		return vWriter.getStatementString(i, sqlType);
	}

	/**
	 * @see org.gdms.driver.DBDriver#getStatementString(double, int)
	 */
	public String getStatementString(double d, int sqlType) {
		return vWriter.getStatementString(d, sqlType);
	}

	/**
	 * @see org.gdms.driver.DBDriver#getStatementString(java.lang.String, int)
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

	public ResultSetMetaData getResultSetMetaData() throws SQLException {
		return jdbcSupport.getResultSet().getMetaData();
	}

	public String getStatementString(GeometryValue g) {
		return vWriter.getStatementString(g);
	}

	/**
	 * @see org.gdms.driver.ReadOnlyDriver#getMetadata()
	 */
	public Metadata getMetadata() throws DriverException {
		DefaultMetadata result = new DefaultMetadata();
		for (int i = 0; i < getFieldCount(); i++) {
			try {
				result.addField(getFieldName(i), getFieldType(i));
			} catch (InvalidTypeException e) {
				throw new DriverException("Bug in the driver");
			}
		}
		return result;
	}

	public boolean prefixAccepted(String prefix) {
		return "jdbc:odbc".equals(prefix.toLowerCase());
	}

	public String getReferenceInSQL(String fieldName) {
		return fieldName;
	}

	public Number[] getScope(int dimension) throws DriverException {
		return null;
	}

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		// TODO Needs to be implemented
		throw new RuntimeException("Needs to be implemented");
	}

	public TableDescription[] getTables(Connection c) throws DriverException {
		return null;
	}
}
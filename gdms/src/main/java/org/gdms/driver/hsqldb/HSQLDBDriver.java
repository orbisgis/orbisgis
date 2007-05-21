package org.gdms.driver.hsqldb;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.JDBCSupport;
import org.gdms.data.edition.Field;
import org.gdms.data.metadata.DefaultDriverMetadata;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueWriter;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;
import org.gdms.spatial.FID;
import org.gdms.spatial.GeometryValue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonzalez Cortes
 */
public class HSQLDBDriver implements DBDriver, DBReadWriteDriver {
	private static Exception driverException;

	static {
		try {
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
		} catch (Exception ex) {
			driverException = ex;
		}
	}

	private ValueWriter vWriter = ValueWriter.internalValueWriter;

	protected JDBCSupport jdbcSupport;

	private DefaultDriverMetadata metadata;

	/**
	 * @see org.gdms.driver.DBDriver#getConnection(java.lang.String, int,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public Connection getConnection(String host, int port, String dbName,
			String user, String password) throws SQLException {
		if (driverException != null) {
			throw new RuntimeException(driverException);
		}

		String connectionString = "jdbc:hsqldb:file:" + dbName;
		Properties p = new Properties();
		// p.put("user", null);
		// p.put("password", null);
		p.put("shutdown", "true");

		return DriverManager.getConnection(connectionString, p);
	}

	/**
	 * @see org.gdms.driver.DBDriver#open(String, int, String, String, String,
	 *      java.lang.String, org.gdms.data.HasProperties)
	 */

	public void open(Connection con, String tableName, String orderFieldName)
			throws DriverException {
		String sql = "SELECT * FROM \"" + tableName + "\"";
		if (orderFieldName != null) {
			sql += " ORDER BY " + orderFieldName;
		}
		try {
			jdbcSupport = JDBCSupport.newJDBCSupport(con,
					getReferenceInSQL(tableName), orderFieldName);

			metadata = jdbcSupport.getDriverMetadata(con, tableName);
		} catch (SQLException e) {
			throw new DriverException(e);
		}
	}

	/**
	 * @see org.gdms.driver.DBDriver#execute(java.sql.Connection,
	 *      java.lang.String, org.gdms.data.HasProperties)
	 */
	public void execute(Connection con, String sql) throws SQLException {
		JDBCSupport.execute(con, sql);
	}

	/**
	 * @see org.gdms.driver.DBDriver#close(Connection)
	 */
	public void close(Connection conn) throws DriverException {
		try {
			jdbcSupport.close();
			// JDBCSupport.execute(conn, "SHUTDOWN");
		} catch (SQLException e) {
			throw new DriverException(e);
		}
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return jdbcSupport.getFieldValue(rowIndex, fieldId);
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldCount()
	 */
	public int getFieldCount() throws DriverException {
		return jdbcSupport.getFieldCount();
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws DriverException {
		return jdbcSupport.getFieldName(fieldId);
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws DriverException {
		return jdbcSupport.getRowCount();
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldType(int)
	 */
	public int getFieldType(int i) throws DriverException {
		return jdbcSupport.getFieldType(i);
	}

	/**
	 * @see com.hardcode.driverManager.Driver#getName()
	 */
	public String getName() {
		return "GDBMS HSQLDB driver";
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
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getNullStatementString() {
		return vWriter.getNullStatementString();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param b
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(boolean b) {
		return vWriter.getStatementString(b);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param binary
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(byte[] binary) {
		return vWriter.getStatementString(binary);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param d
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(Date d) {
		return vWriter.getStatementString(d);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param d
	 *            DOCUMENT ME!
	 * @param sqlType
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(double d, int sqlType) {
		return vWriter.getStatementString(d, sqlType);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param i
	 *            DOCUMENT ME!
	 * @param sqlType
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(int i, int sqlType) {
		return vWriter.getStatementString(i, sqlType);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param i
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(long i) {
		return vWriter.getStatementString(i);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param str
	 *            DOCUMENT ME!
	 * @param sqlType
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(String str, int sqlType) {
		return vWriter.getStatementString(str, sqlType);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param t
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(Time t) {
		return vWriter.getStatementString(t);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param ts
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(Timestamp ts) {
		return vWriter.getStatementString(ts);
	}

	/**
	 * @see org.gdms.driver.DBDriver#getMetadata()
	 */
	public ResultSetMetaData getMetadata() throws SQLException {
		return jdbcSupport.getResultSet().getMetaData();
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
		return metadata;
	}

	public int getType(String driverType) {
		return JDBCSupport.getType(driverType);
	}

	public String[] getAvailableTypes() throws DriverException {
		return JDBCSupport.getDefaultSQLTypes();
	}

	public String[] getParameters(String driverType) throws DriverException {
		return JDBCSupport.getDefaultSQLParameters(driverType);
	}

	public void createSource(DBSource source, DriverMetadata driverMetadata)
			throws DriverException {
		try {
			Connection c = getConnection(source.getHost(), source.getPort(),
					source.getDbName(), source.getUser(), source.getPassword());
			JDBCSupport.createSource(c, source.getTableName(), driverMetadata);
			c.close();
		} catch (SQLException e) {
			throw new DriverException(e);
		}
	}

	public String check(Field f, Value value) throws DriverException {
		return JDBCSupport.checkStandard(f, value);
	}

	public boolean isReadOnly(int i) throws DriverException {
		return jdbcSupport.isReadOnly(i);
	}

	public boolean isValidParameter(String driverType, String paramName,
			String paramValue) {
		return JDBCSupport.isValidParameter(driverType, paramName, paramValue);
	}

	public boolean prefixAccepted(String prefix) {
		return "jdbc:hsqldb:file".equals(prefix.toLowerCase());
	}

	public String getReferenceInSQL(String fieldName) {
		return "\"" + fieldName + "\"";
	}

	public Number[] getScope(int dimension, String fieldName)
			throws DriverException {
		return null;
	}

	public FID getFid(long row) {
		return null;
	}

	public boolean hasFid() {
		return false;
	}

	/**
	 * @see org.gdms.driver.DBTransactionalDriver#beginTrans(Connection)
	 */
	public void beginTrans(Connection con) throws SQLException {
		execute(con, "SET AUTOCOMMIT FALSE");
	}

	/**
	 * @see org.gdms.driver.DBTransactionalDriver#commitTrans(Connection)
	 */
	public void commitTrans(Connection con) throws SQLException {
		execute(con, "COMMIT;SET AUTOCOMMIT TRUE");
	}

	/**
	 * @see org.gdms.driver.DBTransactionalDriver#rollBackTrans(Connection)
	 */
	public void rollBackTrans(Connection con) throws SQLException {
		execute(con, "ROLLBACK;SET AUTOCOMMIT TRUE");
	}

	public String getTypeInAddColumnStatement(String driverType, Map<String, String> params) {
		return JDBCSupport.getTypeInAddColumnStatement(driverType, params);
	}

	public boolean isEditable() {
		return true;
	}

	public CoordinateReferenceSystem getCRS(String fieldName) throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}
}
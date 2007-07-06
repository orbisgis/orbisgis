package org.gdms.driver.hsqldb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.JDBCSupport;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueWriter;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;
import org.gdms.spatial.GeometryValue;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonzalez Cortes
 */
public class HSQLDBDriver implements DBDriver, DBReadWriteDriver {
	private static Exception driverException;
	public static String DRIVER_NAME = "HSQLDB driver";

	static {
		try {
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
		} catch (Exception ex) {
			driverException = ex;
		}
	}

	private ValueWriter valueWriter = ValueWriter.internalValueWriter;
	private SimpleDateFormat timestampFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSSSSSSSS");

	protected JDBCSupport jdbcSupport;

	private Metadata metadata;

	/**
	 * @see org.gdms.driver.DBDriver#getConnection(java.lang.String, int,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public Connection getConnection(String host, int port, String dbName,
			String user, String password) throws SQLException {
		if (driverException != null) {
			throw new RuntimeException(driverException);
		}

		final String connectionString = "jdbc:hsqldb:file:" + dbName;
		final Properties p = new Properties();
		// p.put("user", null);
		// p.put("password", null);
		p.put("shutdown", "true");

		return DriverManager.getConnection(connectionString, p);
	}

	/**
	 * @see org.gdms.driver.DBDriver#open(String, int, String, String, String,
	 *      java.lang.String, org.gdms.data.HasProperties)
	 */

	public void open(Connection con, String tableName)
			throws DriverException {
		try {
			jdbcSupport = JDBCSupport.newJDBCSupport(con,
					getReferenceInSQL(tableName), tableName);

			metadata = jdbcSupport.getMetadata(con, tableName);
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
	 * @see org.gdms.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws DriverException {
		return jdbcSupport.getRowCount();
	}

	/**
	 * @see com.hardcode.driverManager.Driver#getName()
	 */
	public String getName() {
		return DRIVER_NAME;
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
		return valueWriter.getNullStatementString();
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
		return valueWriter.getStatementString(b);
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
		return valueWriter.getStatementString(binary);
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
		return valueWriter.getStatementString(d);
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
		return valueWriter.getStatementString(d, sqlType);
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
		return valueWriter.getStatementString(i, sqlType);
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
		return valueWriter.getStatementString(i);
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
		return valueWriter.getStatementString(str, sqlType);
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
		return valueWriter.getStatementString(t);
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
		return "'" + timestampFormat.format(ts) + "'";
	}

	public ResultSetMetaData getResultSetMetaData() throws SQLException {
		return jdbcSupport.getResultSet().getMetaData();
	}

	/**
	 * @see org.gdms.data.values.ValueWriter#getStatementString(GeometryValue)
	 */
	public String getStatementString(GeometryValue g) {
		return valueWriter.getStatementString(g);
	}

	/**
	 * @see org.gdms.driver.ReadOnlyDriver#getMetadata()
	 */
	public Metadata getMetadata() throws DriverException {
		return metadata;
	}

	public void createSource(DBSource source, Metadata driverMetadata)
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

	public boolean prefixAccepted(String prefix) {
		return "jdbc:hsqldb:file".equals(prefix.toLowerCase());
	}

	public String getReferenceInSQL(String fieldName) {
		return "\"" + fieldName + "\"";
	}

	public Number[] getScope(int dimension)
			throws DriverException {
		return null;
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

	public String getTypeInAddColumnStatement(final Type driverType)
			throws DriverException {
		return JDBCSupport.getTypeInAddColumnStatement(driverType).toString();
	}

	public boolean isCommitable() {
		return true;
	}

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		return jdbcSupport.getTypesDefinitions();
	}

	public String getChangeFieldNameStatement(String tableName, String oldName,
			String newName) {
		return "ALTER TABLE " + getReferenceInSQL(tableName) + "ALTER COLUMN "
				+ getReferenceInSQL(oldName) + " RENAME TO "
				+ getReferenceInSQL(newName);
	}
	
	public ResultSet getTableNames(Connection c) throws DriverException {
		DatabaseMetaData md = null;
		ResultSet rs = null;

		try {
			String[] types = {"TABLE","VIEW"};
            md = c.getMetaData();
            rs = md.getTables(null, null, null, types);  
        } catch (SQLException e) {
        	throw new DriverException(e);
        }
		
		return rs;
	}
	
}
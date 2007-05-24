package org.gdms.data.edition;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

public class FooConnection implements Connection {

	private String pkName;

	public FooConnection(String pkName) {
		this.pkName = pkName;
	}

	public Statement createStatement() throws SQLException {

		return null;
	}

	public PreparedStatement prepareStatement(String arg0) throws SQLException {

		return null;
	}

	public CallableStatement prepareCall(String arg0) throws SQLException {

		return null;
	}

	public String nativeSQL(String arg0) throws SQLException {

		return null;
	}

	public void setAutoCommit(boolean arg0) throws SQLException {

	}

	public boolean getAutoCommit() throws SQLException {

		return false;
	}

	public void commit() throws SQLException {

	}

	public void rollback() throws SQLException {

	}

	public void close() throws SQLException {

	}

	public boolean isClosed() throws SQLException {

		return false;
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return new FooDatabaseMetadata(pkName);
	}

	public void setReadOnly(boolean arg0) throws SQLException {

	}

	public boolean isReadOnly() throws SQLException {

		return false;
	}

	public void setCatalog(String arg0) throws SQLException {

	}

	public String getCatalog() throws SQLException {

		return null;
	}

	public void setTransactionIsolation(int arg0) throws SQLException {

	}

	public int getTransactionIsolation() throws SQLException {

		return 0;
	}

	public SQLWarning getWarnings() throws SQLException {

		return null;
	}

	public void clearWarnings() throws SQLException {

	}

	public Statement createStatement(int arg0, int arg1) throws SQLException {

		return null;
	}

	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2)
			throws SQLException {

		return null;
	}

	public CallableStatement prepareCall(String arg0, int arg1, int arg2)
			throws SQLException {

		return null;
	}

	@SuppressWarnings("unchecked")
	public Map getTypeMap() throws SQLException {

		return null;
	}

	public void setHoldability(int arg0) throws SQLException {

	}

	public int getHoldability() throws SQLException {

		return 0;
	}

	public Savepoint setSavepoint() throws SQLException {

		return null;
	}

	public Savepoint setSavepoint(String arg0) throws SQLException {

		return null;
	}

	public void rollback(Savepoint arg0) throws SQLException {

	}

	public void releaseSavepoint(Savepoint arg0) throws SQLException {

	}

	public Statement createStatement(int arg0, int arg1, int arg2)
			throws SQLException {

		return null;
	}

	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2,
			int arg3) throws SQLException {

		return null;
	}

	public CallableStatement prepareCall(String arg0, int arg1, int arg2,
			int arg3) throws SQLException {

		return null;
	}

	public PreparedStatement prepareStatement(String arg0, int arg1)
			throws SQLException {

		return null;
	}

	public PreparedStatement prepareStatement(String arg0, int[] arg1)
			throws SQLException {

		return null;
	}

	public PreparedStatement prepareStatement(String arg0, String[] arg1)
			throws SQLException {

		return null;
	}

	// FROM JDK-1.5.0_11 TO JDK-1.6.0_01

	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getClientInfo(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isValid(int timeout) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub

	}

	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub

	}

	public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
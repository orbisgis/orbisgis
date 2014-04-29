/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
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
import java.util.concurrent.Executor;

public class FooConnection implements Connection {

	private String pkName;

	public FooConnection(String pkName) {
		this.pkName = pkName;
	}

	public Statement createStatement() throws SQLException {

		return new FooStatement();
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

        @Override
	public Statement createStatement(int arg0, int arg1) throws SQLException {
                return createStatement();
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
                return createStatement();
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
		return null;
	}

	public Blob createBlob() throws SQLException {
		return null;
	}

	public Clob createClob() throws SQLException {
		return null;
	}

	public NClob createNClob() throws SQLException {
		return null;
	}

	public SQLXML createSQLXML() throws SQLException {
		return null;
	}

	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		return null;
	}

	public Properties getClientInfo() throws SQLException {
		return null;
	}

	public String getClientInfo(String name) throws SQLException {
		return null;
	}

	public boolean isValid(int timeout) throws SQLException {
		return false;
	}

	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
	}

	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
	}

	public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

        public void setSchema(String schema) throws SQLException {
        }

        public String getSchema() throws SQLException {
                return null;
        }

        public void abort(Executor executor) throws SQLException {
        }

        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        }

        public int getNetworkTimeout() throws SQLException {
                return 0;
        }
}
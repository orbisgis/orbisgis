/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.driver.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;

import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DefaultDBDriver;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonzalez Cortes
 */
public class HSQLDBDriver extends DefaultDBDriver implements DBReadWriteDriver {
	private static Exception driverException;
	public static String DRIVER_NAME = "HSQLDB driver";

	static {
		try {
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
		} catch (Exception ex) {
			driverException = ex;
		}
	}

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
		p.put("shutdown", "true");

		return DriverManager.getConnection(connectionString, p);
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
	 * @param ts
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(Timestamp ts) {
		return "'" + ts.toString() + "'";
	}

	@Override
	protected String getCreateTableKeyWord() {
		return "CREATE CACHED TABLE";
	}

	public boolean prefixAccepted(String prefix) {
		return "jdbc:hsqldb:file".equals(prefix.toLowerCase());
	}

	public Number[] getScope(int dimension) throws DriverException {
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

	@Override
	protected String getAutoIncrementDefault() {
		return "null";
	}

	/**
	 * @see org.gdms.driver.DefaultDBDriver#getChangeFieldNameSQL(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public String getChangeFieldNameSQL(String tableName, String oldName,
			String newName) {
		return "ALTER TABLE \"" + tableName + "\" ALTER COLUMN \"" + oldName
				+ "\" RENAME TO \"" + newName + "\"";
	}

	@Override
	protected String getSequenceKeyword() {
		return "IDENTITY";
	}

	public int getType() {
		return SourceManager.HSQLDB;
	}

}
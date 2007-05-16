package org.gdms.driver;

import java.sql.Connection;
import java.sql.SQLException;

import org.gdms.data.values.ValueWriter;

/**
 * Interface to implement by the drivers that use jdbc to access data
 *
 * @author Fernando Gonzalez Cortes
 */
public interface DBDriver extends ReadOnlyDriver, ValueWriter {
	/**
	 * Provides connections to the database. Each invocation creates and returns
	 * a new connection. The connection are managed in upper layers
	 *
	 * @param host
	 *
	 * @param port
	 *            Port of the database management system. -1 means default port
	 * @param dbName
	 *
	 * @param user
	 *
	 * @param password
	 *
	 *
	 * @return Connection
	 *
	 * @throws SQLException
	 *             If some error happens
	 */
	Connection getConnection(String host, int port, String dbName, String user,
			String password) throws SQLException;

	/**
	 * Free any resource reserved in the open method
	 *
	 * @param conn
	 *
	 * @throws SQLException
	 *             If the free fails
	 */
	public void close(Connection conn) throws DriverException;

	/**
	 * Returns true if the driver can access a database with the given prefix in
	 * the connection string
	 *
	 * @param prefix
	 * @return
	 */
	boolean prefixAccepted(String prefix);

	/**
	 * Connects to the data source and reads the specified table in the
	 * specified order
	 *
	 * @param host
	 * @param port
	 * @param dbName
	 * @param user
	 * @param password
	 * @param tableName
	 *            Name of the table where the data is in
	 * @param orderFieldName
	 *            Name of the order field. Can be null
	 *
	 *
	 * @throws DriverException
	 */
	public void open(Connection con, String tableName, String orderFieldName)
			throws DriverException;
}
package org.gdms.driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.gdms.data.db.DBSource;
import org.gdms.data.metadata.DriverMetadata;
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
	 * Executes an instruction against the server
	 * 
	 * @param con
	 *            Connection used to execute the instruction
	 * @param sql
	 *            Instruction to execute
	 * @param props
	 *            Properties of the overlaying DataSource layer
	 * 
	 * @throws SQLException
	 *             If the execution fails
	 */
	public void execute(Connection con, String sql) throws SQLException;

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
	 * Gets a statement to create the specified field on the given table
	 * 
	 * @param driverType
	 * @param params
	 * @return
	 */
	String getTypeInAddColumnStatement(String driverType,
			HashMap<String, String> params);

	/**
	 * Returns true if the driver can access a database with the given prefix in
	 * the connection string
	 * 
	 * @param prefix
	 * @return
	 */
	boolean prefixAccepted(String prefix);

	/**
	 * Returns how the specified reference (field or table reference) should
	 * appear in a SQL statement. For exaple, in postgreSQL should appear as
	 * "fieldName" (with the quotes)
	 * 
	 * @param reference
	 * @return
	 */
	String getReferenceInSQL(String reference);

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

	/**
	 * Creates a new table. The source argument provides information about the
	 * name of the table to be created and the host, port and database where the
	 * table has to be created
	 * 
	 * @param source
	 * @param driverMetadata
	 * @throws DriverException
	 */
	public void createSource(DBSource source, DriverMetadata driverMetadata)
			throws DriverException;

	/**
	 * Begins a transaction
	 * 
	 * @param Connection
	 *            to perform the transacion begining
	 * 
	 * @throws SQLException
	 *             If the transaction could not be started
	 */
	public void beginTrans(Connection con) throws SQLException;

	/**
	 * Commits the changes made during the transaction
	 * 
	 * @param Connection
	 *            to perform the transacion commitment
	 * 
	 * @throws SQLException
	 *             If the transaction could not be commited
	 */
	public void commitTrans(Connection con) throws SQLException;

	/**
	 * Cancels the changes made during the transaction
	 * 
	 * @param Connection
	 *            to perform the transacion rollback
	 * 
	 * @throws SQLException
	 *             If the transaction could not be cancelled
	 */
	public void rollBackTrans(Connection con) throws SQLException;
}
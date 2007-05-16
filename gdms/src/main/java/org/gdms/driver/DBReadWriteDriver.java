package org.gdms.driver;

import java.sql.Connection;
import java.sql.SQLException;

import org.gdms.data.db.DBSource;
import org.gdms.data.metadata.DriverMetadata;

/**
 * Interface to be implement by the DB drivers that as also RW capabilities
 * 
 */
public interface DBReadWriteDriver extends DBDriver {
	/**
	 * Return true iff there is a unique field or a primary key in the DB table
	 * 
	 * @return
	 */
	public boolean isEditable();

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
}
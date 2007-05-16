package org.gdms.driver.hsqldbTransactional;

import java.sql.Connection;
import java.sql.SQLException;

import org.gdms.driver.DBDriver;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.hsqldb.HSQLDBDriver;

/**
 * @author Fernando Gonzalez Cortes
 */
public class HSQLDBTransactionalDriver extends HSQLDBDriver implements
		DBDriver, DBReadWriteDriver {

	public String getName() {
		return "GDBMS HSQLDB Transactional driver";
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
}
package org.gdms.driver.hsqldbTransactional;

import java.sql.Connection;
import java.sql.SQLException;

import org.gdms.data.driver.DBDriver;
import org.gdms.data.driver.DBTransactionalDriver;
import org.gdms.driver.hsqldb.HSQLDBDriver;


/**
 * @author Fernando Gonzalez Cortes
 */
public class HSQLDBTransactionalDriver extends HSQLDBDriver implements DBDriver, DBTransactionalDriver{

    public String getName() {
        return "GDBMS HSQLDB Transactional driver";
    }

    /**
     * @see org.gdms.data.driver.DBTransactionalDriver#beginTrans(Connection)
     */
    public void beginTrans(Connection con) throws SQLException {
        execute(con, "SET AUTOCOMMIT FALSE");
    }

    /**
     * @see org.gdms.data.driver.DBTransactionalDriver#commitTrans(Connection)
     */
    public void commitTrans(Connection con) throws SQLException {
        execute(con, "COMMIT;SET AUTOCOMMIT TRUE");
    }

    /**
     * @see org.gdms.data.driver.DBTransactionalDriver#rollBackTrans(Connection)
     */
    public void rollBackTrans(Connection con) throws SQLException {
        execute(con, "ROLLBACK;SET AUTOCOMMIT TRUE");
    }
}

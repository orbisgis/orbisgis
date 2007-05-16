package org.gdms.driver;

import java.sql.Connection;
import java.sql.SQLException;



/**
 * Interface to be implemented by those db drivers whose management
 * system support transactions
 *
 * @author Fernando Gonz�lez Cort�s
 */
public interface DBTransactionalDriver {
    /**
     * Begins a transaction
     * 
     * @param Connection to perform the transacion begining
     *
     * @throws SQLException If the transaction could not be started
     */
    public void beginTrans(Connection con) throws SQLException;

    /**
     * Commits the changes made during the transaction
     * 
     * @param Connection to perform the transacion commitment
     *
     * @throws SQLException If the transaction could not be commited
     */
    public void commitTrans(Connection con) throws SQLException;

    /**
     * Cancels the changes made during the transaction
     * 
     * @param Connection to perform the transacion rollback
     *
     * @throws SQLException If the transaction could not be cancelled
     */
    public void rollBackTrans(Connection con) throws SQLException;

}

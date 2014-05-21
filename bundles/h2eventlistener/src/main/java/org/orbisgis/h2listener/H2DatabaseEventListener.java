package org.orbisgis.h2listener;

import org.h2.api.DatabaseEventListener;
import org.h2.engine.Database;
import org.h2.engine.Session;
import org.h2.jdbc.JdbcConnection;
import org.orbisgis.corejdbc.DataBaseExecutionProgressEventCatcher;
import org.orbisgis.corejdbc.DatabaseProgressionListener;
import org.orbisgis.corejdbc.StateEvent;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Link to H2 database in order to catch database events.
 * @author Nicolas Fortin
 */
@Component
public class H2DatabaseEventListener implements DatabaseEventListener, DataBaseExecutionProgressEventCatcher {
    private DataSource dataSource;
    private int database

    @Override
    public DatabaseProgressionListener.StateInfo getStateInformations(StateEvent.DB_STATES stateId) {
        return null;
    }

    private Database getDatabase(DataSource dataSource) {
        try(Connection connection = dataSource.getConnection()) {
            JdbcConnection h2Connection = null;
            if (connection instanceof JdbcConnection) {
                h2Connection = (JdbcConnection)connection;
            } else if (connection.isWrapperFor(JdbcConnection.class)) {
                h2Connection = dataSource.unwrap(JdbcConnection.class);
            }
            h2Connection.getClass().getClassLoader().loadClass("")
            // Only work with local Session
            if(h2Connection != null && h2Connection.getSession() instanceof Session) {
                return ((Session) h2Connection.getSession()).getDatabase();
            } else {
                return null;
            }
        } catch (SQLException ex) {
            // Ignore
            return null;
        }
    }

    /**
     * @param dataSource H2 DataSource
     */
    @Reference
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        Database database = getDatabase(dataSource);
        // Register ourselves
        database.setEventListener(this);
    }

    public void unsetDataSource(DataSource dataSource) {
        if(dataSource.equals(this.dataSource)) {
            this.dataSource = null;
        }
    }

    @Override
    public void init(String url) {

    }

    @Override
    public void opened() {

    }

    @Override
    public void exceptionThrown(SQLException e, String sql) {

    }

    @Override
    public void setProgress(int state, String name, int x, int max) {

    }

    @Override
    public void closingDatabase() {
        // TODO reconnect in X seconds
    }
}

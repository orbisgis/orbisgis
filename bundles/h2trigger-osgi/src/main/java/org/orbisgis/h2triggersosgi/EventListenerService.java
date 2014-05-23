package org.orbisgis.h2triggersosgi;

import org.apache.log4j.Logger;
import org.h2.api.DatabaseEventListener;
import org.h2.jdbcx.JdbcDataSource;
import org.h2gis.utilities.JDBCUtilities;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.StateEvent;
import org.orbisgis.h2triggers.H2DatabaseEventListener;
import org.orbisgis.h2triggers.H2Trigger;
import org.orbisgis.h2triggers.TriggerListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import javax.sql.DataSource;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Register and listen to H2 Database event system.
 * @author Nicolas Fortin
 */
@Component(immediate = true)
public class EventListenerService implements DatabaseEventListener, TriggerListener {
    private DataManager dataManager;
    private Logger logger = Logger.getLogger(EventListenerService.class);
    private static boolean isLocalH2DataBase(DatabaseMetaData meta) throws SQLException {
        return JDBCUtilities.isH2DataBase(meta)
                && meta.getURL().startsWith("jdbc:h2:")
                && !meta.getURL().startsWith("jdbc:h2:tcp:/");
    }
    @Reference
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        DataSource dataSource = dataManager.getDataSource();
        // Link
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            if(isLocalH2DataBase(connection.getMetaData())) {
                H2DatabaseEventListener.setDelegateDatabaseEventListener(this);
                // Change DATABASE_EVENT_LISTENER for this Database instance
                st.execute("SET DATABASE_EVENT_LISTENER '" + H2DatabaseEventListener.class.getName() + "'");
                // H2 Database properties are not serialised. Then in order to keep the event listener
                // the JDBC url connection have to be changed
                try {
                    if (dataSource instanceof JdbcDataSource || dataSource.isWrapperFor(JdbcDataSource.class)) {
                        JdbcDataSource jdbcDataSource;
                        if (dataSource instanceof JdbcDataSource) {
                            jdbcDataSource = (JdbcDataSource) dataSource;
                        } else {
                            jdbcDataSource = dataSource.unwrap(JdbcDataSource.class);
                        }
                        if (!jdbcDataSource.getURL().toUpperCase().contains("DATABASE_EVENT_LISTENER")) {
                            jdbcDataSource.setURL(jdbcDataSource.getURL() + ";DATABASE_EVENT_LISTENER='" + H2DatabaseEventListener.class.getName() + "'");
                        }
                    }
                } catch (Exception ex) {
                    logger.warn("Cannot change connection URL:\n" + ex.getLocalizedMessage(), ex);
                }
                H2Trigger.setListener(this);
            }
        } catch (SQLException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
    }

    @Deactivate
    public void disable() {
        H2DatabaseEventListener.setDelegateDatabaseEventListener(null);
    }

    public void unsetDataManager(DataManager dataManager) {
        // Unlink
        try(Connection connection = dataManager.getDataSource().getConnection();
            Statement st = connection.createStatement()) {
            if(isLocalH2DataBase(connection.getMetaData())) {
                st.execute("SET DATABASE_EVENT_LISTENER ''");
                H2Trigger.setListener(null);
            }
        } catch (SQLException ex) {
            // Ignore
        }
    }

    @Override
    public void init(String url) {
        // Not used
    }

    @Override
    public void opened() {
        // Not used
    }

    @Override
    public void exceptionThrown(SQLException e, String sql) {
        // Not used
    }

    @Override
    public void setProgress(int state, String name, int x, int max) {
        if(dataManager != null && state < StateEvent.DB_STATES.values().length) {
            StateEvent.DB_STATES stateEnum = StateEvent.DB_STATES.values()[state];
            dataManager.fireDatabaseProgression(new StateEvent(stateEnum, name, x, max));
        }
    }

    @Override
    public void closingDatabase() {
        // Not used
    }

    @Override
    public void fire(String schemaName, String triggerName, String tableName) {
        if(dataManager != null) {
            dataManager.fireUndoableEditHappened(new UndoableEditEvent(tableName ,new TableUpdate()));
        }
    }

    private static class TableUpdate extends AbstractUndoableEdit {
        private TableUpdate() {
        }

        @Override
        public boolean isSignificant() {
            return false; // This event should not be seen as action in Undo/Redo tasks.
        }
    }
}

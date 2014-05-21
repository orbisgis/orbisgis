package org.orbisgis.h2triggersosgi;

import org.apache.log4j.Logger;
import org.h2.api.DatabaseEventListener;
import org.h2.jdbcx.JdbcDataSource;
import org.h2gis.utilities.JDBCUtilities;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.StateEvent;
import org.orbisgis.h2triggers.H2DatabaseEventListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Register and listen to H2 Database event system.
 * @author Nicolas Fortin
 */
@Component(immediate = true)
public class EventListenerService implements DatabaseEventListener {
    private DataManager dataManager;
    private Logger logger = Logger.getLogger("gui."+EventListenerService.class);

    @Reference
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        DataSource dataSource = dataManager.getDataSource();
        // Link
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            if(JDBCUtilities.isH2DataBase(connection.getMetaData())
                    && connection.getMetaData().getURL().startsWith("jdbc:h2:/")) {
                H2DatabaseEventListener.setDelegateDatabaseEventListener(this);
                // Change DATABASE_EVENT_LISTENER for this Database instance
                st.execute("SET DATABASE_EVENT_LISTENER '" + H2DatabaseEventListener.class.getName() + "'");
                // H2 Database properties are not serialised. Then in order to keep the event listener
                // the JDBC url connection have to be changed
                try {
                    if (dataSource instanceof JdbcDataSource || dataSource.isWrapperFor(JdbcDataSource.class)) {
                        JdbcDataSource jdbcDataSource = dataSource.unwrap(JdbcDataSource.class);
                        if(!jdbcDataSource.getURL().toUpperCase().contains("DATABASE_EVENT_LISTENER")) {
                            jdbcDataSource.setURL(jdbcDataSource.getURL()+";DATABASE_EVENT_LISTENER='" + H2DatabaseEventListener.class.getName() + "'");
                        }
                    }
                } catch (Exception ex) {
                    // Cannot change connection URL
                }
            }
        } catch (SQLException ex) {
            // Ignore
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
            st.execute("SET DATABASE_EVENT_LISTENER ''");
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
        logger.info("OPEN DATABASE");
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
            logger.info("H2 Progress "+stateEnum.name()+" "+name+" "+x+"/"+max);
            dataManager.fireDatabaseProgression(new StateEvent(stateEnum, name, x, max));
        }
    }

    @Override
    public void closingDatabase() {
        // Not used
        logger.info("CLOSE DATABASE");
    }
}

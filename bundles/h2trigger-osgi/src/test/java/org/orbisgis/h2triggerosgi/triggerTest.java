package org.orbisgis.h2triggerosgi;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.DatabaseProgressionListener;
import org.orbisgis.corejdbc.StateEvent;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.h2triggersosgi.EventListenerService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Nicolas Fortin
 */
public class triggerTest {

    private static DataSource dataSource;


    @BeforeClass
    public static void tearUp() throws Exception {
        Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
        // Keep a connection alive to not close the DataBase on each unit test
        dataSource = SpatialH2UT.createDataSource(triggerTest.class.getSimpleName(), true);
    }

    @Test
    public void testListener() throws SQLException {
        DataManager dataManager = new DataManagerImpl(dataSource);
        LocalListener local = new LocalListener();
        dataManager.addDatabaseProgressionListener(local, StateEvent.DB_STATES.STATE_STATEMENT_END);
        EventListenerService evtServ = new EventListenerService();
        evtServ.setDataManager(dataManager);
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            String query = "select * from GEOMETRY_COLUMNS";
            st.execute(query);
            assertNotNull(local.getLastState());
            assertEquals(query, local.getLastState().getName());
        }
        dataManager.removeDatabaseProgressionListener(local);
        evtServ.disable();
        evtServ.unsetDataManager(dataManager);
    }

    private static class LocalListener implements DatabaseProgressionListener {
        private StateEvent lastState;

        @Override
        public void progressionUpdate(StateEvent state) {
            lastState = state;
        }

        public StateEvent getLastState() {
            return lastState;
        }
    }
}

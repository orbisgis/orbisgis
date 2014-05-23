package org.orbisgis.h2triggerosgi;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.utilities.SFSUtilities;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.DatabaseProgressionListener;
import org.orbisgis.corejdbc.StateEvent;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.h2triggersosgi.EventListenerService;

import javax.sql.DataSource;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Nicolas Fortin
 */
public class TriggerTest {

    private static DataSource dataSource;


    @BeforeClass
    public static void tearUp() throws Exception {
        Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
        // Keep a connection alive to not close the DataBase on each unit test
        dataSource = SpatialH2UT.createDataSource(TriggerTest.class.getSimpleName(), true);
    }

    @Test
    public void testListener() throws SQLException {
        testListenerInternal(dataSource);
    }

    private void testListenerInternal(DataSource dataSourceParam) throws SQLException {
        DataManager dataManager = new DataManagerImpl(dataSourceParam);
        LocalListener local = new LocalListener();
        dataManager.addDatabaseProgressionListener(local, StateEvent.DB_STATES.STATE_STATEMENT_END);
        EventListenerService evtServ = new EventListenerService();
        evtServ.setDataManager(dataManager);
        try(Connection connection = dataSourceParam.getConnection();
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

    @Test
    public void testTableTrigger() throws SQLException {
        DataManager dataManager = new DataManagerImpl(dataSource);
        LocalListener local = new LocalListener();
        dataManager.addDatabaseProgressionListener(local, StateEvent.DB_STATES.STATE_STATEMENT_END);
        EventListenerService evtServ = new EventListenerService();
        evtServ.setDataManager(dataManager);
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS TEST");
            st.execute("CREATE TABLE TEST(ID serial)");
            // Install listener
            dataManager.addUndoableEditListener("TEST",undoManager);
            assertFalse(undoManager.getUndoPresentationName());
            st.execute("INSERT INTO TEST VALUES (1)");
            assertTrue(undoManager.);
            assertFalse(undoManager.isSignificant());
            undoManager.end();
        }
        evtServ.disable();
        evtServ.unsetDataManager(dataManager);
    }

    @Test
    public void testListenerWithWrapper() throws SQLException {
        testListenerInternal(SFSUtilities.wrapSpatialDataSource(dataSource));
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

    private static class EventStack implements UndoableEditListener {

    }
}

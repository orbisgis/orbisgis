/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.h2triggersosgi;

import org.apache.log4j.Logger;
import org.h2.api.DatabaseEventListener;
import org.h2.api.Trigger;
import org.h2.jdbcx.JdbcDataSource;
import org.h2gis.utilities.JDBCUtilities;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.StateEvent;
import org.orbisgis.h2triggers.H2DatabaseEventListener;
import org.orbisgis.h2triggers.H2Trigger;
import org.orbisgis.h2triggers.TriggerFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import javax.sql.DataSource;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Register and listen to H2 Database event system.
 * @author Nicolas Fortin
 */
@Component(immediate = true)
public class EventListenerService implements DatabaseEventListener, TriggerFactory {
    private DataManager dataManager;
    private Logger logger = Logger.getLogger(EventListenerService.class);
    private Queue<StateEvent> eventStack = new LinkedBlockingQueue<>();
    private AtomicBoolean eventProcessRunning = new AtomicBoolean(false);

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
                H2Trigger.setTriggerFactory(this);
            }
        } catch (SQLException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
    }

    @Deactivate
    public void disable() {
        H2DatabaseEventListener.setDelegateDatabaseEventListener(null);
        H2Trigger.setTriggerFactory(null);
    }

    public void unsetDataManager(DataManager dataManager) {
        // Unlink
        try(Connection connection = dataManager.getDataSource().getConnection();
            Statement st = connection.createStatement()) {
            if(isLocalH2DataBase(connection.getMetaData())) {
                st.execute("SET DATABASE_EVENT_LISTENER ''");
                H2Trigger.setTriggerFactory(null);
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
        if (dataManager != null && state < StateEvent.DB_STATES.values().length) {
            // Do not fire the event in the H2 thread in order to not raise
            // org.h2.jdbc.JdbcSQLException: Timeout trying to lock table XXX
            StateEvent.DB_STATES stateEnum = StateEvent.DB_STATES.values()[state];
            eventStack.add(new StateEvent(stateEnum, name, x, max));
            if (!eventProcessRunning.getAndSet(true)) {
                SwingUtilities.invokeLater(new StateEventProcess(dataManager, eventStack, eventProcessRunning));
            }
        }
    }

    @Override
    public void closingDatabase() {
        // Not used
    }

    @Override
    public Trigger createTrigger(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) {
        if(dataManager != null) {
            TableTrigger trigger = new TableTrigger(dataManager);
            try {
                trigger.init(conn, schemaName, triggerName, tableName, before, type);
                return trigger;
            } catch (SQLException ex) {
                logger.error("Cannot init trigger "+triggerName,ex);
                return null;
            }
        } else {
            return null;
        }
    }

    private static class StateEventProcess implements Runnable {
        private final DataManager dataManager;
        private final Queue<StateEvent> eventStack;
        private final AtomicBoolean stateEventProcessing;

        private StateEventProcess(DataManager dataManager, Queue<StateEvent> eventStack, AtomicBoolean stateEventProcessing) {
            this.dataManager = dataManager;
            this.eventStack = eventStack;
            this.stateEventProcessing = stateEventProcessing;
        }

        @Override
        public void run() {
            while(!eventStack.isEmpty()) {
                dataManager.fireDatabaseProgression(eventStack.remove());
            }
            stateEventProcessing.set(false);
        }
    }
}

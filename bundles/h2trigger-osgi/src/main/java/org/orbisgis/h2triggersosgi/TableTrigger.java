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

import org.h2.api.Trigger;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.StateEvent;
import org.orbisgis.corejdbc.TableEditEvent;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Trigger attached to H2 Database
 * @author Nicolas Fortin
 */
public class TableTrigger implements Trigger {
    private DataManager dataManager;
    private String tableIdentifier;
    private boolean update;
    private final Queue<TableEditEvent> editStack = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean stateEventProcessing = new AtomicBoolean(false);

    public TableTrigger(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) throws SQLException {
        this.update = type == UPDATE;
        this.tableIdentifier = new TableLocation(schemaName, tableName).toString(true);
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        // Do not fire the event in the H2 thread in order to not raise
        // org.h2.jdbc.JdbcSQLException: Timeout trying to lock table XXX
        fireEvent(new TableEditEvent(tableIdentifier));
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public void remove() throws SQLException {
        // Do not fire the event in the H2 thread in order to not raise
        // org.h2.jdbc.JdbcSQLException: Timeout trying to lock table XXX
        fireEvent(new TableEditEvent(tableIdentifier));
    }

    private void fireEvent(TableEditEvent evt) {
        editStack.add(evt);
        if(!stateEventProcessing.getAndSet(true)) {
            SwingUtilities.invokeLater(new TableEditEventProcess(dataManager, editStack, stateEventProcessing));
        }

    }

    private static class TableEditEventProcess implements Runnable {
        private final DataManager dataManager;
        private final Queue<TableEditEvent> editStack;
        private final AtomicBoolean stateEventProcessing;

        private TableEditEventProcess(DataManager dataManager, Queue<TableEditEvent> editStack, AtomicBoolean stateEventProcessing) {
            this.dataManager = dataManager;
            this.editStack = editStack;
            this.stateEventProcessing = stateEventProcessing;
        }

        @Override
        public void run() {
            while(!editStack.isEmpty()) {
                dataManager.fireTableEditHappened(editStack.remove());
            }
            stateEventProcessing.set(false);
        }
    }
}

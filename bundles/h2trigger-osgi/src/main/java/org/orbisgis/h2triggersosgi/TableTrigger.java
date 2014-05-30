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
import org.orbisgis.corejdbc.TableEditEvent;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Trigger attached to H2 Database
 * @author Nicolas Fortin
 */
public class TableTrigger implements Trigger {
    private DataManager dataManager;
    private String tableIdentifier;
    private boolean update;

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
        SwingUtilities.invokeLater(new TableEditEventProcess(dataManager, new TableEditEvent(tableIdentifier)));
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public void remove() throws SQLException {
        // Fire if table has been deleted
        if(!JDBCUtilities.tableExists(dataManager.getDataSource().getConnection(), tableIdentifier)) {
            // Do not fire the event in the H2 thread in order to not raise
            // org.h2.jdbc.JdbcSQLException: Timeout trying to lock table XXX
            SwingUtilities.invokeLater(new TableEditEventProcess(dataManager, new TableEditEvent(tableIdentifier)));
        }
    }

    private static class TableEditEventProcess implements Runnable {
        DataManager dataManager;
        TableEditEvent evt;

        private TableEditEventProcess(DataManager dataManager, TableEditEvent evt) {
            this.dataManager = dataManager;
            this.evt = evt;
        }

        @Override
        public void run() {
            dataManager.fireTableEditHappened(evt);
        }
    }
}

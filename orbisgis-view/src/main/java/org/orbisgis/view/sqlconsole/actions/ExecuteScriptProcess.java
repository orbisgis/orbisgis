/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.sqlconsole.actions;

import org.apache.log4j.Logger;
import org.orbisgis.corejdbc.ReadTable;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sqlparserapi.ScriptSplitter;
import org.orbisgis.sqlparserapi.ScriptSplitterFactory;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.sqlconsole.ui.SQLConsolePanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Execute SQL script in a background process
 */
public class ExecuteScriptProcess implements BackgroundJob {

        private static final Logger LOGGER = Logger.getLogger("gui."+ExecuteScriptProcess.class);
        protected final static I18n I18N = I18nFactory.getI18n(ExecuteScriptProcess.class);
        private static final String[] executeQueryCommands = new String[] {"select", "explain", "call", "show", "script"};

        private SQLConsolePanel panel;
        private DataSource ds;
        private ScriptSplitterFactory splitterFactory;
        private static final int MAX_PRINTED_ROWS = 100;
        private static final int MAX_FIELD_LENGTH = 30;
        /**
         * @param panel Console panel (Can be null)
         * @param ds DataSource to acquire DBMS Connection
         * @param splitterFactory Sql Parser
         */
        public ExecuteScriptProcess(SQLConsolePanel panel, DataSource ds, ScriptSplitterFactory splitterFactory) {
                this.ds = ds;
                this.panel = panel;
                this.splitterFactory = splitterFactory;
        }

        @Override
        public String getTaskName() {
                return I18N.tr("Executing script");
        }
        
        private void showPanelMessage(final String message) {
                if (panel != null) {
                        SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                        panel.setStatusMessage(message);
                                }
                        });
                }
        }

        private void parseAndExecuteScript(ProgressMonitor pm, Statement st) throws SQLException {
            ScriptSplitter splitter = splitterFactory.create(panel.getScriptPanel().getDocument(), true);
            int totalRequests = 0;
            while(splitter.hasNext()) {
                if (!splitter.next().trim().isEmpty()) {
                    totalRequests++;
                }
            }
            splitter = splitterFactory.create(panel.getScriptPanel().getDocument(), true);
            int currentRequest = 0;
            while(splitter.hasNext()) {
                currentRequest++;
                String query = splitter.next().trim();
                if (!query.isEmpty()) {
                    // Some queries need to be shown to the user
                    LOGGER.info(I18N.tr("Execute request {0}/{1}: {2}",
                            currentRequest, totalRequests, query));
                    long debQuery = System.currentTimeMillis();
                    String lowerCaseQuery = query.toLowerCase();
                    if(isExecuteQuery(lowerCaseQuery)) {
                        try {
                            LOGGER.info("\n" + ReadTable.resultSetToString(query, st, MAX_FIELD_LENGTH, MAX_PRINTED_ROWS, true, true));
                        } catch (SQLException ex) {
                            // May not accept executeQuery but simple query
                            if(!lowerCaseQuery.startsWith("select")) {
                                st.execute(query);
                            } else {
                                throw ex;
                            }
                        }
                    } else {
                        st.execute(query);
                    }
                    LOGGER.info(I18N.tr("Done in {0} seconds\n",(System.currentTimeMillis() - debQuery) / 1000.));
                    pm.endTask();
                }
            }
        }

    private boolean isExecuteQuery(String lc_query) {
        boolean doExecuteQuery = false;
        for(String command : executeQueryCommands) {
            if(lc_query.startsWith(command)) {
                return true;
            }
        }
        return doExecuteQuery;
    }

    @Override
        public void run(ProgressMonitor progress) {
                long t1 = System.currentTimeMillis();
                ProgressMonitor pm = progress.startTask(I18N.tr("Execute SQL Request"), panel.getScriptPanel().getLineCount());
                try(Connection connection = ds.getConnection()) {
                    try(Statement st = connection.createStatement()) {
                        // If the user clicks on cancel, cancel the execution
                        pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL ,
                                EventHandler.create(PropertyChangeListener.class, st, "cancel"));
                        if(splitterFactory != null) {
                            parseAndExecuteScript(pm, st);
                        } else {
                            st.execute(panel.getScriptPanel().getText().trim());
                        }
                    }
                } catch (SQLException ex) {
                    LOGGER.error(ex.getLocalizedMessage(), ex);
                }
                long t2 = System.currentTimeMillis();
                double lastExecTime = ((t2 - t1) / 1000.0);
                String message = I18N.tr("OVERALL EXECUTION TIME: {0} seconds",lastExecTime);
                LOGGER.info(message);
                showPanelMessage(message);
        }
}

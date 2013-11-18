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

import javax.sql.DataSource;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sqlparserapi.ScriptSplitter;
import org.orbisgis.sqlparserapi.ScriptSplitterFactory;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.sqlconsole.ui.SQLConsolePanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Execute SQL script in a background process
 */
public class ExecuteScriptProcess implements BackgroundJob {

        private static final Logger LOGGER = Logger.getLogger("gui."+ExecuteScriptProcess.class);
        protected final static I18n I18N = I18nFactory.getI18n(ExecuteScriptProcess.class);
                
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

        public void printSelect(String query, Statement st) throws SQLException {
            // Select generate a ResultSet
            ResultSet rs = st.executeQuery(query);
            // Print headers
            LOGGER.info(query+ ": ");
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            StringBuilder formatStringBuilder = new StringBuilder();
            String[] header = new String[columnCount];
            for(int idColumn = 1; idColumn <= columnCount; idColumn++) {
                header[idColumn-1] = metaData.getColumnLabel(idColumn)+"("+metaData.getColumnTypeName(idColumn)+")";
                formatStringBuilder.append("%-"+MAX_FIELD_LENGTH+"s");
            }
            LOGGER.info(String.format(formatStringBuilder.toString(), header));
            int shownLines = 0;
            StringBuilder lines = new StringBuilder();
            while(rs.next() && shownLines < MAX_PRINTED_ROWS) {
                String[] row = new String[columnCount];
                for(int idColumn = 1; idColumn <= columnCount; idColumn ++) {
                    String value = rs.getString(idColumn);
                    if(value != null) {
                        if(value.length() > MAX_FIELD_LENGTH) {
                            value = value.substring(0, MAX_FIELD_LENGTH-2) + "..";
                        }
                    } else {
                        value = "NULL";
                    }
                    row[idColumn-1] = value;
                }
                shownLines++;
                lines.append(String.format(formatStringBuilder.toString(),row));
                lines.append("\n");
            }
            LOGGER.info(lines.toString());
        }

        @Override
        public void run(ProgressMonitor pm) {
                ScriptSplitter splitter = splitterFactory.create(panel.getScriptPanel().getDocument());
                long t1 = System.currentTimeMillis();
                pm.init(I18N.tr("Execute SQL Request"), panel.getScriptPanel().getLineCount());
                try(Connection connection = ds.getConnection()) {
                    try(Statement st = connection.createStatement()) {
                        while(splitter.hasNext()) {
                            if(!splitter.isInsideRemark()) {
                                String query = splitter.next();
                                // Some query need to be shown to the user
                                String lc_query = query.trim().toLowerCase();
                                String[] executeQueryCommands = new String[] {"select", "explain", "call", "show", "script"};
                                boolean doExecuteQuery = false;
                                for(String command : executeQueryCommands) {
                                    if(lc_query.startsWith(command)) {
                                        doExecuteQuery = true;
                                        break;
                                    }
                                }
                                if(doExecuteQuery) {
                                    try {
                                        printSelect(query, st);
                                    } catch (SQLException ex) {
                                        //May not accept executeQuery but simple query
                                        if(!lc_query.startsWith("select")) {
                                            st.execute(query);
                                        } else {
                                            throw ex;
                                        }
                                    }
                                } else {
                                    st.execute(query);
                                }
                            }
                            pm.progressTo(splitter.getLineIndex());
                        }
                    }
                } catch (SQLException ex) {
                    LOGGER.error(ex.getLocalizedMessage(), ex);
                }
                long t2 = System.currentTimeMillis();
                double lastExecTime = ((t2 - t1) / 1000.0);
                String message = I18N.tr("Execution time: {0}",lastExecTime);
                LOGGER.info(message);
                showPanelMessage(message);
        }
}

/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.sqlconsole.actions;

import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.corejdbc.ReadTable;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.sqlparserapi.ScriptSplitter;
import org.orbisgis.sqlparserapi.ScriptSplitterFactory;
import org.orbisgis.sqlconsole.ui.SQLConsolePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.StringContent;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Execute SQL script in a background process
 */
public class ExecuteScriptProcess extends SwingWorkerPM {

        private static final Logger LOGGER = LoggerFactory.getLogger("gui." + ExecuteScriptProcess.class);
        protected final static I18n I18N = I18nFactory.getI18n(ExecuteScriptProcess.class);

        private SQLConsolePanel panel;
        private DataSource ds;
        private ScriptSplitterFactory splitterFactory;
        private static final int MAX_PRINTED_ROWS = 100;
        private static final int MAX_FIELD_LENGTH = 30;
        private int timeOut =0;
        private final boolean onlySelected;
        
        /**
         * @param panel Console panel (Can be null)
         * @param ds DataSource to acquire DBMS Connection
         * @param splitterFactory Sql Parser
         * @param timeOut in seconds to execute the statement
         */
        public ExecuteScriptProcess(SQLConsolePanel panel, DataSource ds, ScriptSplitterFactory splitterFactory, int timeOut, boolean onlySelected) {
                this.ds = ds; 
                this.panel = panel;
                this.splitterFactory = splitterFactory;
                this.timeOut=timeOut;
                this.onlySelected=onlySelected;
                setTaskName(I18N.tr("Executing script"));
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

        /**
         * Parse the script and execute each sql statements.
         * The script is cloned to avoid user modifications during its execution.
         * 
         * @param pm
         * @param st
         * @throws SQLException 
         */
        private void parseAndExecuteScript(ProgressMonitor pm, Statement st) throws SQLException {
            // Clone SQL script
            AbstractDocument.Content sqlScript = new StringContent(panel.getScriptPanel().getDocument().getLength());
            try {
                if (onlySelected) {                    
                    sqlScript.insertString(0, panel.getScriptPanel().getSelectedText());                    
                }
                else{
                    sqlScript.insertString(0, panel.getScriptPanel().getText());
                }
                
            } catch (BadLocationException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
                return;
            }
            Document sqlDocument = new PlainDocument(sqlScript);
            ScriptSplitter splitter = splitterFactory.create(sqlDocument, true);
            int totalRequests = 0;
            while (splitter.hasNext()) {
                if (!splitter.next().trim().isEmpty()) {
                    totalRequests++;
                }
            }
            splitter = splitterFactory.create(sqlDocument, true);
            int currentRequest = 0;
            while (splitter.hasNext()) {
                currentRequest++;
                String query = splitter.next().trim();
                if (!query.isEmpty()) {
                    // Some queries need to be shown to the user
                    LOGGER.info(I18N.tr("Execute request {0}/{1}: {2}", currentRequest, totalRequests, query));
                    long debQuery = System.currentTimeMillis();
                    if (st.execute(query)) {
                        ResultSet rs = st.getResultSet();
                        LOGGER.info("\n" + ReadTable.resultSetToString(rs, MAX_FIELD_LENGTH, MAX_PRINTED_ROWS, true, true));

                    }
                    LOGGER.info(I18N.tr("Done in {0} seconds\n", (System.currentTimeMillis() - debQuery) / 1000.));
                    pm.endTask();
                }
            }
        }

    @Override
    protected Object doInBackground() throws Exception {
        long t1 = System.currentTimeMillis();
        ProgressMonitor pm = getProgressMonitor().startTask(I18N.tr("Execute SQL Request"), panel.getScriptPanel().getLineCount());
        try(Connection connection = ds.getConnection()) {
            try(Statement st = connection.createStatement()) {
                st.setQueryTimeout(timeOut);
                // If the user clicks on cancel, cancel the execution
                pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL ,
                        EventHandler.create(PropertyChangeListener.class, st, "cancel"));
                if(splitterFactory != null) {
                    parseAndExecuteScript(pm, st);
                } else {
                    if (onlySelected) {   
                        String selectedText = panel.getScriptPanel().getSelectedText();
                        if (selectedText != null) {
                            st.execute(selectedText.trim());
                        }
                    } else {
                        st.execute(panel.getScriptPanel().getText().trim());
                    }
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
        return null;
    }
}

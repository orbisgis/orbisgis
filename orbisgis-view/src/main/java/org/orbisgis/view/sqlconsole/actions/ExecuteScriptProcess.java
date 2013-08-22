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
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.sqlconsole.ui.SQLConsolePanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Execute SQL script in a background process
 */
public class ExecuteScriptProcess implements BackgroundJob {

        private String script;
        private static final Logger LOGGER = Logger.getLogger("gui."+ExecuteScriptProcess.class);
        protected final static I18n I18N = I18nFactory.getI18n(ExecuteScriptProcess.class);
                
        private SQLConsolePanel panel;
        private DataSource ds;

        /**
         * @param script Script to execute
         * @param panel Console panel (Can be null)
         * @param ds DataSource to acquire DBMS Connection
         */
        public ExecuteScriptProcess(String script, SQLConsolePanel panel, DataSource ds) {
                this.script = script;
                this.ds = ds;
                this.panel = panel;
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

        @Override
        public void run(ProgressMonitor pm) {
                long t1 = System.currentTimeMillis();
                try {
                    Connection connection = ds.getConnection();
                    try {
                        connection.createStatement().execute(script);
                    } finally {
                        connection.close();
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

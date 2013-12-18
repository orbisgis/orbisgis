/*
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

package org.orbisgis.view.map.jobs;

import java.awt.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import org.apache.log4j.Logger;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.core.jdbc.CreateTable;
import org.orbisgis.core.jdbc.MetaData;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * A background job to create a source from a selection.
 *
 * @author ebocher
 */
public class CreateSourceFromSelection implements BackgroundJob {

        private static final I18n I18N = I18nFactory.getI18n(CreateSourceFromSelection.class);
        
        private static final Logger GUILOGGER = Logger.getLogger("gui."+CreateSourceFromSelection.class);
        private static final int INSERT_BATCH_SIZE = 30;
      
        private final DataSource dataSource;
        private final String tableName;
        private final Set<Integer> selectedRows;
        private String newName;

        /**
         * Constructor used by the Map Editor.
         *
         * @param dataSource     Original DataSource
         * @param tableName The table identifier [[catalog.]schema.]table
         * @param selectedRows Selected Rows
         */
        public CreateSourceFromSelection(DataSource dataSource,
                Set<Integer> selectedRows, String tableName) {
                this.dataSource = dataSource;
                this.selectedRows = selectedRows;
                this.tableName = tableName;
        }

        /**
         * Constructor used by the TableEditor.
         *
         * @param dataSource     Original DataSource
         * @param selectedRows Value of primary key to copy
         * @param newName      New name to use to register the DataSource
         */
        public CreateSourceFromSelection(DataSource dataSource,
                                         Set<Integer> selectedRows, String tableName,
                                         String newName) {
            this(dataSource, selectedRows, tableName);
            this.newName = newName;
        }

        @Override
        public void run(ProgressMonitor pm) {

                try {

                        // Populate the new source
                        try(Connection connection = dataSource.getConnection();
                            Statement st = connection.createStatement()) {
                            DatabaseMetaData meta = connection.getMetaData();
                            // Find an unique name to register
                            if (newName == null) {
                                newName = MetaData.getNewUniqueName(tableName,meta,"selection");
                            }
                            // Create row id table
                            String tempTableName = CreateTable.createIndexTempTable(connection, pm, selectedRows, INSERT_BATCH_SIZE);
                            PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, st, "cancel");
                            pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,
                                    listener);
                            // Copy content using pk
                            int primaryKeyIndex = JDBCUtilities.getIntegerPrimaryKey(meta, tableName);
                            if(primaryKeyIndex == 0) {
                                // Should never happen because the check is done before the creation of this class
                                throw new SQLException("Cannot create table from table selection that does not contains a primary key");
                            }
                            String primaryKeyName = JDBCUtilities.getFieldName(meta, tableName, primaryKeyIndex);
                            st.execute(String.format("CREATE TABLE %s AS SELECT a.* FROM %s a,%s b " +
                                    "WHERE a.%s = b.ROWID ",TableLocation.parse(newName),
                                    TableLocation.parse(tableName),tempTableName, primaryKeyName));
                            pm.removePropertyChangeListener(listener);
                        }
                } catch (SQLException e) {
                        GUILOGGER.error("The selection cannot be created.", e);
                        if(newName!=null && !newName.isEmpty()) {
                            try(Connection connection = dataSource.getConnection();
                                Statement st = connection.createStatement()) {
                                st.execute("DROP TABLE IF EXISTS "+TableLocation.parse(newName));
                            } catch (SQLException ex) {
                                GUILOGGER.error("Could not revert changes", e);
                            }
                        }
                }
        }

        @Override
        public String getTaskName() {
                return I18N.tr("Create a datasource from the current selection");
        }

        /**
         * Show an input dialog that ask for destination table.
         * @param parent Parent component to attach Dialog
         * @param dataSource JDBC dataSource
         * @param sourceTable Base table name
         * @return Chosen table name
         * @throws SQLException If the table name check failed.
         */
        public static String showNewNameDialog(Component parent,
                                               DataSource dataSource,String sourceTable) throws SQLException {
            String newName = null;
            boolean inputAccepted = false;
            final String newNameMessage = I18n.marktr("New name for the datasource:");
            JLabel message = new JLabel(I18N.tr(newNameMessage));
            try(Connection connection = dataSource.getConnection()) {
                DatabaseMetaData meta = connection.getMetaData();
                while (!inputAccepted) {
                    newName = JOptionPane.showInputDialog(
                            parent,
                            message.getText(),
                            MetaData.getNewUniqueName(sourceTable, meta, I18N.tr("selection")));
                    // Check if the user canceled the operation.
                    if (newName == null) {
                        // Just exit
                        inputAccepted = true;
                    } // The user clicked OK.
                    else {
                        // Check for an empty name.
                        if (newName.isEmpty()) {
                            message.setText(I18N.tr("You must enter a non-empty name.")
                                    + "\n" + I18N.tr(newNameMessage));
                        } // Check for a source that already exists with that name.
                        else if (MetaData.tableExists(newName, meta)) {
                            message.setText(I18N.tr("A datasource with that name already exists.")
                                    + "\n" + I18N.tr(newNameMessage));
                        } // The user entered a non-empty, unique name.
                        else {
                            inputAccepted = true;
                        }
                    }
                }
            }
            return newName;
        }
}

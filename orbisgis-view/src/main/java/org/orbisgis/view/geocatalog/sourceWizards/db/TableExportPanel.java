/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.geocatalog.sourceWizards.db;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.*;
import javax.swing.table.TableColumn;
import org.apache.log4j.Logger;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.gdms.sql.engine.ParseException;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.MIPValidation;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.PasswordType;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.geocatalog.Catalog;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * This {@code JDialog} is used to export the content of one or more {@code
 * DataSource} into an external DB.
 * @author Erwan Bocher
 * @author Alexis Guéganno
 */
public class TableExportPanel extends JDialog {

        private static final I18n I18N = I18nFactory.getI18n(TableExportPanel.class);
        private static final Logger LOGGER = Logger.getLogger(Catalog.class);
        //private final ConnectionPanel firstPanel;
        private final String[] sourceNames;
        private JScrollPane jScrollPane;
        private JTable jtableExporter;
        private JToolBar connectionToolBar;
        private JComboBox cmbDataBaseUri;
        private static final String dbPropertiesFile = "db_connexions.properties";
        Properties dbProperties = new Properties();
        private final SourceManager sourceManager;
        private JButton btnDisconnect;
        private JButton btnConnect;
        private JButton btnAddConnection;
        private JButton btnEditConnection;
        private JButton btnRemoveConnection;
        private JComboBox comboBoxSchemas;
        private String[] schemas = new String[]{"public"};
        boolean isConnected = false;
        private String[] dbParameters = null;
        private String dbpassWord;

        public TableExportPanel(String[] layerNames, SourceManager sourceManager) {
                this.sourceNames = layerNames;
                this.sourceManager = sourceManager;
                initialize();
                setModal(true);
                setSize(400, 300);
        }

        @Override
        public String getTitle() {
                return I18N.tr("Export sources in a database");
        }

        /**
         * A toolbar to manage the connexion with the database
         *
         * @return
         */
        private JToolBar connectionToolBar() {
                if (connectionToolBar == null) {
                        connectionToolBar = new JToolBar();
                        connectionToolBar.setFloatable(false);
                        connectionToolBar.setOpaque(false);
                        loadDBProperties();
                        Object[] dbKeys = dbProperties.keySet().toArray();
                        boolean btnStatus = dbKeys.length > 0;
                        cmbDataBaseUri = new JComboBox(dbKeys);
                        cmbDataBaseUri.setEditable(false);
                        //Connection button
                        btnConnect = new JButton(OrbisGISIcon.getIcon("database_connect"));
                        btnConnect.setToolTipText(I18N.tr("Connect to the database"));
                        btnConnect.setEnabled(btnStatus);
                        btnConnect.addActionListener(EventHandler.create(ActionListener.class, this, "onConnect"));
                        btnConnect.setBorderPainted(false);
                        //Button for disconnecting.
                        btnDisconnect = new JButton(OrbisGISIcon.getIcon("disconnect"));
                        btnDisconnect.setToolTipText(I18N.tr("Disconnect"));
                        btnDisconnect.setEnabled(false);
                        btnDisconnect.addActionListener(EventHandler.create(ActionListener.class, this, "onDisconnect"));
                        btnDisconnect.setBorderPainted(false);
                        //Button to add a conection.
                        btnAddConnection = new JButton(OrbisGISIcon.getIcon("database_add"));
                        btnAddConnection.setToolTipText(I18N.tr("Add a new connection"));
                        btnAddConnection.addActionListener(EventHandler.create(ActionListener.class, this, "onAddConnection"));
                        btnAddConnection.setBorderPainted(false);
                        //button to edit a connection
                        btnEditConnection = new JButton(OrbisGISIcon.getIcon("database_edit"));
                        btnEditConnection.setToolTipText(I18N.tr("Edit the connection"));
                        btnEditConnection.setEnabled(btnStatus);
                        btnEditConnection.addActionListener(EventHandler.create(ActionListener.class, this, "onEditConnection"));
                        btnEditConnection.setBorderPainted(false);
                        //button to remove a connection
                        btnRemoveConnection = new JButton(OrbisGISIcon.getIcon("database_delete"));
                        btnRemoveConnection.setToolTipText(I18N.tr("Remove the connection"));
                        btnRemoveConnection.setEnabled(btnStatus);
                        btnRemoveConnection.addActionListener(EventHandler.create(ActionListener.class, this, "onRemoveConnection"));
                        btnRemoveConnection.setBorderPainted(false);
                        //The toolbar that contains these buttons.
                        connectionToolBar.add(cmbDataBaseUri);
                        connectionToolBar.add(btnConnect);
                        connectionToolBar.add(btnDisconnect);
                        connectionToolBar.add(btnAddConnection);
                        connectionToolBar.add(btnEditConnection);
                        connectionToolBar.add(btnRemoveConnection);
                }
                return connectionToolBar;
        }

        /**
         * Create the main panel
         */
        private void initialize() {
                if (jScrollPane == null) {
                        this.setLayout(new BorderLayout());
                        jtableExporter = new JTable();
                        jtableExporter.setRowHeight(20);
                        DataBaseTableModel dataBaseTableModel = new DataBaseTableModel(sourceManager, sourceNames);
                        jtableExporter.setModel(dataBaseTableModel);
                        //jtableExporter.setDefaultRenderer(Object.class, new org.orbisgis.view.geocatalog.sourceWizards.db.TableCellRenderer());
                        TableColumn schemaColumn = jtableExporter.getColumnModel().getColumn(2);
                        comboBoxSchemas = new JComboBox(getSchemas());
                        schemaColumn.setCellEditor(new DefaultCellEditor(comboBoxSchemas) {
                                @Override
                                public Component getTableCellEditorComponent(JTable pTable, Object pValue, boolean pIsSelected, int pRow,
                                        int pColumn) {
                                        Component tableCellEditorComponent =
                                                super.getTableCellEditorComponent(pTable, pValue, pIsSelected, pRow, pColumn);
                                        if (2 == pColumn) {
                                                JComboBox comboBox = (JComboBox) tableCellEditorComponent;
                                                comboBox.removeAllItems();
                                                for (String schema : getSchemas()) {
                                                        comboBox.addItem(schema);
                                                }
                                        }
                                        return tableCellEditorComponent;
                                }
                        });
                        jScrollPane = new JScrollPane();
                        jScrollPane.setViewportView(jtableExporter);
                        JPanel buttonPanels = new JPanel();
                        JButton okButtons = new JButton(I18N.tr("Export"));
                        okButtons.addActionListener(EventHandler.create(ActionListener.class, this, "onExport"));     
                        okButtons.setBorderPainted(false);

                        JButton cancelButtons = new JButton(I18N.tr("Cancel"));
                        cancelButtons.addActionListener(EventHandler.create(ActionListener.class, this, "onCancel")); 
                        cancelButtons.setBorderPainted(false);


                        buttonPanels.add(okButtons);
                        buttonPanels.add(cancelButtons);

                        this.add(connectionToolBar(), BorderLayout.NORTH);
                        this.add(jScrollPane, BorderLayout.CENTER);
                        this.add(buttonPanels, BorderLayout.SOUTH);
                }
        }

        /**
         * This method is used to export the selected sources in a database
         *
         * @return
         */
        public void onExport() {

                DataBaseTableModel model = (DataBaseTableModel) jtableExporter.getModel();
                if (!isConnected) {
                        JOptionPane.showMessageDialog(this, I18N.tr("Please connect to a database"));

                } else if (!model.isOneRowSelected()) {
                        JOptionPane.showMessageDialog(this, I18N.tr("At least one row must be checked."));
                } else {
                        BackgroundManager backgroundManager = Services.getService(BackgroundManager.class);
                        int count = model.getRowCount();
                        for (int i = 0; i < count; i++) {
                                DataBaseRow row = model.getRow(i);
                                if (row.isExport()) {
                                        StringBuilder sb = new StringBuilder("EXECUTE EXPORT(");
                                        sb.append(row.toSQL());
                                        sb.append(", ");
                                        sb.append("'").append(dbParameters[0]).append("',");
                                        sb.append("'").append(dbParameters[1]).append("',");
                                        sb.append(dbParameters[2]).append(",");
                                        sb.append("'").append(dbParameters[4]).append("',");
                                        sb.append("'").append(dbParameters[5]).append("',");
                                        sb.append("'").append(dbpassWord).append("',");
                                        sb.append("'").append(row.getSchema()).append("',");
                                        sb.append("'").append(row.getOutPutsourceName()).append("');");
                                        backgroundManager.backgroundOperation(new ExportToDatabase(i, sb.toString()));
                                }
                        }
                }
        }

        /**
         * Load connection properties
         */
        private void loadDBProperties() {
                CoreWorkspace ws = (CoreWorkspace) Services.getService(CoreWorkspace.class);
                try {
                        File propertiesFile = new File(ws.getWorkspaceFolder() + File.separator + dbPropertiesFile);

                        if (propertiesFile.exists()) {
                                dbProperties.load(new FileInputStream(propertiesFile));
                        }
                } catch (IOException e) {
                        LOGGER.error(e);
                }
        }

        /**
         * Save connection properties
         */
        private void saveProperties() {
                try {
                        CoreWorkspace ws = (CoreWorkspace) Services.getService(CoreWorkspace.class);
                        dbProperties.store(new FileOutputStream(ws.getWorkspaceFolder() + File.separator + dbPropertiesFile),
                                I18N.tr("Saved with the OrbisGIS database exporter panel"));
                } catch (IOException ex) {
                        LOGGER.error(ex);
                }

        }

        /**
         * Close the connection to the database
         */
        public void onDisconnect() {
                setConnected(false);
                onUserSelectionChange();

        }

        /**
         * This method creates a connection to the database and populate a list
         * with all schema names. A the end the connection is closed.
         *
         * @param parameters
         * @param passWord
         * @return
         * @throws SQLException
         */
        public void populateSchemas(String[] parameters, String passWord) throws SQLException {

                try {
                        dbParameters = parameters;
                        dbpassWord = passWord;
                        String dbType = parameters[0];
                        DriverManager driverManager = sourceManager.getDriverManager();
                        DBDriver dbDriver = (DBDriver) driverManager.getDriver(dbType);
                        String cs = dbDriver.getConnectionString(parameters[1], Integer.valueOf(
                                parameters[2]), Boolean.valueOf(parameters[3]), parameters[4], parameters[5],
                                passWord);
                        Connection connection = dbDriver.getConnection(cs);
                        schemas = dbDriver.getSchemas(connection);
                        connection.close();
                        setConnected(true);
                        onUserSelectionChange();
                } catch (DriverException ex) {
                        LOGGER.error(ex);
                }


        }

        /**
         * Return all schema names from the current database
         *
         * @return
         */
        public String[] getSchemas() {
                return schemas;
        }

        /**
         * Return true if the panel is connected to a database.
         *
         * @return
         */
        public boolean isConnected() {
                return isConnected;
        }

        /**
         * Set if the panel is connected to a database
         *
         * @param isConnected
         */
        public void setConnected(boolean isConnected) {
                this.isConnected = isConnected;
        }

        /**
         * A background job to export the source in a database.
         *
         */
        private class ExportToDatabase implements BackgroundJob {

                private final String sql;
                private final int rowId;

                private ExportToDatabase(int rowId, String sql) {
                        this.sql = sql;
                        this.rowId = rowId;
                }

                @Override
                public void run(org.orbisgis.progress.ProgressMonitor pm) {
                        DataManager dataManager = Services.getService(DataManager.class);
                        try {
                                dataManager.getDataSourceFactory().executeSQL(sql, pm);
                                jtableExporter.remove(rowId);
                        } catch (ParseException ex) {
                                JOptionPane.showMessageDialog(jScrollPane, ex.getMessage());
                        } catch (DriverException ex) {
                                JOptionPane.showMessageDialog(jScrollPane, ex.getMessage());
                        }
                }

                @Override
                public String getTaskName() {
                        return I18N.tr("Exporting the source in a database.");
                }
        }

        /**
         * Connect to the database
         */
        public void onConnect() {
                String dataBaseUri = cmbDataBaseUri.getSelectedItem().toString();
                if (!dataBaseUri.isEmpty()) {
                        MultiInputPanel passwordDialog = new MultiInputPanel(I18N.tr("Please set a password"));
                        passwordDialog.addInput("password", I18N.tr("Password"), "", new PasswordType(10));
                        passwordDialog.addValidation(new MIPValidation() {
                                @Override
                                public String validate(MultiInputPanel mid) {
                                        if (mid.getInput("password").isEmpty()) {
                                                return I18N.tr("The password cannot be null");
                                        }
                                        return null;

                                }
                        });

                        if (UIFactory.showDialog(passwordDialog)) {
                                try {
                                        String passWord = passwordDialog.getInput("password");
                                        String properties = dbProperties.getProperty(dataBaseUri);
                                        populateSchemas(properties.split(","), passWord);

                                } catch (SQLException ex) {
                                        JOptionPane.showMessageDialog(jScrollPane, I18N.tr("Cannot connect the database"));
                                        LOGGER.error(ex);
                                }


                        }
                }
        }

        /**
         * Add a new connection
         */
        public void onAddConnection() {
                MultiInputPanel mip = DBUIFactory.getConnectionPanel();
                if (UIFactory.showDialog(mip)) {
                        String connectionName = mip.getInput(DBUIFactory.CONNAME);
                        if (!dbProperties.containsKey(connectionName)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(mip.getInput(DBUIFactory.DBTYPE));
                                sb.append(",");
                                sb.append(mip.getInput(DBUIFactory.HOST));
                                sb.append(",");
                                sb.append(mip.getInput(DBUIFactory.PORT));
                                sb.append(",");
                                sb.append(mip.getInput(DBUIFactory.SSL));
                                sb.append(",");
                                sb.append(mip.getInput(DBUIFactory.DBNAME));
                                sb.append(",");
                                sb.append(mip.getInput(DBUIFactory.USER));
                                dbProperties.setProperty(connectionName, sb.toString());
                                cmbDataBaseUri.addItem(connectionName);
                                cmbDataBaseUri.setSelectedItem(connectionName);
                                onUserSelectionChange();
                                saveProperties();

                        } else {
                                JOptionPane.showMessageDialog(jScrollPane, I18N.tr("There is already a connection with this name."));
                        }

                }

        }

        /**
         * Edit a connection to change its parameters
         */
        public void onEditConnection() {
                String dataBaseUri = cmbDataBaseUri.getSelectedItem().toString();
                if (!dataBaseUri.isEmpty()) {
                        String property = dbProperties.getProperty(dataBaseUri);
                        MultiInputPanel mip = DBUIFactory.getEditConnectionPanel(dataBaseUri, property);
                        if (UIFactory.showDialog(mip)) {
                                String connectionName = mip.getInput(DBUIFactory.CONNAME);
                                StringBuilder sb = new StringBuilder();
                                sb.append(mip.getInput(DBUIFactory.DBTYPE));
                                sb.append(",");
                                sb.append(mip.getInput(DBUIFactory.HOST));
                                sb.append(",");
                                sb.append(mip.getInput(DBUIFactory.PORT));
                                sb.append(",");
                                sb.append(mip.getInput(DBUIFactory.SSL));
                                sb.append(",");
                                sb.append(mip.getInput(DBUIFactory.DBNAME));
                                sb.append(",");
                                sb.append(mip.getInput(DBUIFactory.USER));
                                dbProperties.setProperty(connectionName, sb.toString());
                                cmbDataBaseUri.addItem(connectionName);
                                cmbDataBaseUri.setSelectedItem(connectionName);
                                saveProperties();
                        }
                }
        }

        /**
         * Remove a connection
         */
        public void onRemoveConnection() {
                String dataBaseUri = cmbDataBaseUri.getSelectedItem().toString();
                if (!dataBaseUri.isEmpty()) {
                        cmbDataBaseUri.removeItem(dataBaseUri);
                        dbProperties.remove(dataBaseUri);
                        saveProperties();
                }
                onUserSelectionChange();
        }
        
        /**
         * Close the connection panel
         */
        public void onCancel(){
                dispose();
        }

        /**
         * Change the status of the components
         */
        private void onUserSelectionChange() {

                boolean isCmbEmpty = cmbDataBaseUri.getItemCount() == 0;

                if (isCmbEmpty) {
                        btnConnect.setEnabled(false);
                        btnDisconnect.setEnabled(false);
                        btnAddConnection.setEnabled(true);
                        btnEditConnection.setEnabled(false);
                        btnRemoveConnection.setEnabled(false);
                        cmbDataBaseUri.setEnabled(true);
                        DataBaseTableModel model = (DataBaseTableModel) jtableExporter.getModel();
                        model.setEditable(false);
                } else {
                        if (isConnected) {
                                btnConnect.setEnabled(false);
                                btnDisconnect.setEnabled(true);
                                btnAddConnection.setEnabled(false);
                                btnEditConnection.setEnabled(false);
                                btnRemoveConnection.setEnabled(false);
                                cmbDataBaseUri.setEnabled(false);
                                DataBaseTableModel model = (DataBaseTableModel) jtableExporter.getModel();
                                model.setEditable(true);
                        } else {
                                btnConnect.setEnabled(true);
                                btnDisconnect.setEnabled(false);
                                btnAddConnection.setEnabled(true);
                                btnEditConnection.setEnabled(true);
                                btnRemoveConnection.setEnabled(true);
                                cmbDataBaseUri.setEnabled(true);
                                DataBaseTableModel model = (DataBaseTableModel) jtableExporter.getModel();
                                model.setEditable(false);
                        }
                }



        }
}

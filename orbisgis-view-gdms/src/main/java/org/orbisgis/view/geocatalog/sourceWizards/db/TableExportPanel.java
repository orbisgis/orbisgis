/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog.sourceWizards.db;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.Logger;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.events.Listener;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.MIPValidation;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.PasswordType;
import org.orbisgis.sif.multiInputPanel.TextBoxType;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.geocatalog.Catalog;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * This {@code JDialog} is used to export the content of one or more {@code
 * DataSource} into an external DB.
 *
 * @author Erwan Bocher
 * @author Alexis Guéganno
 */
public class TableExportPanel extends JDialog {

        private static final I18n I18N = I18nFactory.getI18n(TableExportPanel.class);
        private static final Logger LOGGER = Logger.getLogger(Catalog.class);
        private static final long serialVersionUID = 1L;
        //private final ConnectionPanel firstPanel;
        private final String[] sourceNames;
        private JScrollPane jScrollPane;
        public JTable jtableExporter;
        private ConnectionToolBar connectionToolBar;
        private final SourceManager sourceManager;
        private JComboBox comboBoxSchemas;
        private String[] schemas = new String[]{"public"};
        private String[] dbParameters = null;
        private String dbpassWord, dbLogin;
        public JButton saveButton;
        public JButton closeButton;
        private DataBaseTableModel dataBaseTableModel;

        public TableExportPanel(String[] layerNames, SourceManager sourceManager) {
                this.sourceNames = layerNames;
                this.sourceManager = sourceManager;
                initialize();
                setModal(true);
                setSize(500, 250);
        }

        @Override
        public String getTitle() {
                return I18N.tr("Save sources in a database");
        }

        /**
         * A toolbar to manage the connexion with the database
         *
         * @return
         */
        private ConnectionToolBar connectionToolBar() {
                if (connectionToolBar == null) {
                        connectionToolBar = new ConnectionToolBar();
                        connectionToolBar.addVetoableChangeListener(ConnectionToolBar.PROP_CONNECTED, EventHandler.create(VetoableChangeListener.class, this, "onUserConnect", ""));
                        connectionToolBar.addPropertyChangeListener(ConnectionToolBar.PROP_CONNECTED, EventHandler.create(PropertyChangeListener.class, this, "onUserConnected","newValue"));
                        connectionToolBar.getMessagesEvents().addListener(this, EventHandler.create(Listener.class, this, "onMessagePanel", "message"));
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
                         dataBaseTableModel = new DataBaseTableModel(sourceManager, sourceNames);
                        jtableExporter.setModel(dataBaseTableModel);
                        TableColumnModel columnModel = jtableExporter.getColumnModel();
                        columnModel.getColumn(0).setCellRenderer(new StatusColumnRenderer());
                        TableColumn schemaColumn = columnModel.getColumn(3);
                        comboBoxSchemas = new JComboBox(getSchemas());
                        schemaColumn.setCellEditor(new DefaultCellEditor(comboBoxSchemas) {

                                @Override
                                public Component getTableCellEditorComponent(JTable pTable, Object pValue, boolean pIsSelected, int pRow,
                                        int pColumn) {
                                        Component tableCellEditorComponent =
                                                super.getTableCellEditorComponent(pTable, pValue, pIsSelected, pRow, pColumn);
                                        if (3 == pColumn) {
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
                        saveButton = new JButton(I18N.tr("Save"));
                        saveButton.addActionListener(EventHandler.create(ActionListener.class, this, "onExport"));
                        saveButton.setBorderPainted(false);
                        saveButton.setEnabled(false);

                        closeButton = new JButton(I18N.tr("Close"));
                        closeButton.addActionListener(EventHandler.create(ActionListener.class, this, "onCancel"));
                        closeButton.setBorderPainted(false);
                        
                        buttonPanels.add(saveButton);
                        buttonPanels.add(closeButton);

                        this.add(connectionToolBar(), BorderLayout.NORTH);
                        this.add(jScrollPane, BorderLayout.CENTER);
                        this.add(buttonPanels, BorderLayout.SOUTH);
                }
        }

        /**
         * This method is used to export the selected sources in a database
         *
         */
        public void onExport() {

                DataBaseTableModel model = (DataBaseTableModel) jtableExporter.getModel();
                if (!connectionToolBar.isConnected()) {
                        JOptionPane.showMessageDialog(this, I18N.tr("Please connect to a database"));
                } else if (!model.isOneRowSelected()) {
                        JOptionPane.showMessageDialog(this, I18N.tr("At least one row must be checked."));
                } else {
                        BackgroundManager backgroundManager = Services.getService(BackgroundManager.class);
                        int count = model.getRowCount();
                        List<DataBaseRow> rows = new LinkedList<DataBaseRow>();
                        for (int i = 0; i < count; i++) {
                                DataBaseRow row = model.getRow(i);
                                if (row.isExport()) {                                        
                                        rows.add(row);
                                }
                        } 
                        
                        Map<String, String> userParams = new HashMap<String, String>();
                        userParams.put("vendor", dbParameters[0]);
                        userParams.put("host", dbParameters[1]);
                        int port = Integer.valueOf(dbParameters[2]);
                        userParams.put("dbName", dbParameters[4]);
                        userParams.put("userName", dbLogin);
                        userParams.put("password", dbpassWord);
                        userParams.put("ssl", dbParameters[3]);
                        backgroundManager.nonBlockingBackgroundOperation(new ExportToDatabase(this, rows, userParams, port));   
                }
        }

        /**
         * This method creates a connection to the database and populate a list
         * with all schema names. A the end the connection is closed.
         *
         * @param parameters
         * @param passWord
         * @throws SQLException
         */
        public void populateSchemas(String[] parameters, String login, String passWord) throws SQLException {

                try {
                        dbParameters = parameters;
                        dbpassWord = passWord;
                        dbLogin=login;
                        String dbType = parameters[0];
                        DriverManager driverManager = sourceManager.getDriverManager();
                        DBDriver dbDriver = (DBDriver) driverManager.getDriver(dbType);
                        String cs = dbDriver.getConnectionString(parameters[1], Integer.valueOf(
                                parameters[2]), Boolean.valueOf(parameters[3]), parameters[4], login,
                                passWord);
                        Connection connection = dbDriver.getConnection(cs);
                        schemas = dbDriver.getSchemas(connection);
                        connection.close();
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
         * The connection or disconnection is done
         */
        public void onUserConnected(boolean newConnectionState) {
                onUserSelectionChange();
        }
        /**
         * Connect to the database
         */
        public void onUserConnect(PropertyChangeEvent evt) throws PropertyVetoException {
                if ((Boolean) evt.getNewValue() == true) {
                        if (!onConnect()) {
                                throw new PropertyVetoException(I18N.tr("Cannot connect to the database."), evt);
                        }
                }
        }

        /**
         * Set the login and password and them connect to the database
         * @return 
         */
        public boolean onConnect() {
                String dataBaseUri = connectionToolBar.getCmbDataBaseUri().getSelectedItem().toString();
                if (!dataBaseUri.isEmpty()) {
                        MultiInputPanel passwordDialog = new MultiInputPanel(I18N.tr("Please set a password"));
                        passwordDialog.addInput("login", I18N.tr("Login"), "", new TextBoxType(10));                        
                        passwordDialog.addInput("password", I18N.tr("Password"), "", new PasswordType(10));
                        passwordDialog.addValidation(new MIPValidation() {

                                @Override
                                public String validate(MultiInputPanel mid) {
                                        if (mid.getInput("login").isEmpty()) {
                                                return I18N.tr("The login cannot be empty.");
                                        }
                                        if (mid.getInput("password").isEmpty()) {
                                                return I18N.tr("The password cannot be empty.");
                                        }
                                        return null;

                                }
                        });

                        if (UIFactory.showDialog(passwordDialog)) {
                                try {
                                        String passWord = passwordDialog.getInput("password");
                                        String login = passwordDialog.getInput("login");
                                        String properties = connectionToolBar.getDbProperties().getProperty(dataBaseUri);
                                        populateSchemas(properties.split(","), login, passWord);
                                        return true;

                                } catch (SQLException ex) {
                                        JOptionPane.showMessageDialog(jScrollPane, I18N.tr("Cannot connect the database"));
                                        LOGGER.error(ex);
                                        return false;
                                }


                        }
                }
                return false;
        }

        /**
         * Close the connection panel
         */
        public void onCancel() {
                dispose();
        }

        /**
         * Change the status of the components
         */
        private void onUserSelectionChange() {

                boolean isCmbEmpty = connectionToolBar.getCmbDataBaseUri().getItemCount() == 0;

                if (isCmbEmpty) {
                        DataBaseTableModel model = (DataBaseTableModel) jtableExporter.getModel();
                        model.setEditable(false);                        
                        saveButton.setEnabled(false);
                } else {
                        if (connectionToolBar.isConnected()) {
                                DataBaseTableModel model = (DataBaseTableModel) jtableExporter.getModel();
                                model.setEditable(true);
                                saveButton.setEnabled(true);
                        } else {
                                DataBaseTableModel model = (DataBaseTableModel) jtableExporter.getModel();
                                model.setEditable(false);
                                saveButton.setEnabled(false);
                        }
                }
        }

        /**
         * Show a message
         * @param message 
         */
        public void onMessagePanel(String message) {
                JOptionPane.showMessageDialog(jScrollPane, message);
        }

        /**
         * Return the database table model
         * @return 
         */
        public DataBaseTableModel getTableModel() {
                return dataBaseTableModel;
        }
}

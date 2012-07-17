/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog.sourceWizards.db;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.apache.log4j.Logger;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.TableDescription;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.sif.multiInputPanel.MIPValidation;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.PasswordType;
import org.orbisgis.view.geocatalog.Catalog;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author ebocher
 */
public class TableExportPanel extends JPanel implements UIPanel {

        protected final static I18n I18N = I18nFactory.getI18n(TableExportPanel.class);
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
        private Connection connection;
        private DBDriver dbDriver;
        private JButton btnDisconnect;
        private JButton btnConnect;
        private JButton btnAddConnection;
        private JButton btnEditConnection;
        private JButton btnRemoveConnection;
        private JComboBox comboBoxSchemas;
        private String[] schemas;

        public TableExportPanel(String[] layerNames, SourceManager sourceManager) {
                this.sourceNames = layerNames;
                this.sourceManager = sourceManager;
                initialize();
        }

        @Override
        public URL getIconURL() {
                return null;
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
        public JToolBar connectionToolBar() {
                if (connectionToolBar == null) {
                        connectionToolBar = new JToolBar();
                        connectionToolBar.setFloatable(false);
                        connectionToolBar.setOpaque(false);
                        loadDBProperties();
                        Object[] dbKeys = dbProperties.keySet().toArray();
                        boolean btnStatus = dbKeys.length > 0;
                        cmbDataBaseUri = new JComboBox(dbKeys);
                        cmbDataBaseUri.setEditable(false);

                        btnConnect = new JButton(OrbisGISIcon.getIcon("database_connect"));
                        btnConnect.setToolTipText(I18N.tr("Connect to the database"));
                        btnConnect.setEnabled(btnStatus);
                        btnConnect.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        String dataBaseUri = cmbDataBaseUri.getSelectedItem().toString();
                                        if (!dataBaseUri.isEmpty()) {
                                                MultiInputPanel passwordDialog = new MultiInputPanel(I18N.tr("Please set a password"));
                                                passwordDialog.addInput("password", I18N.tr("Password"), "", new PasswordType());
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
                                                                createConnection(properties.split(","), passWord);
                                                        } catch (SQLException ex) {
                                                                JOptionPane.showMessageDialog(jScrollPane, I18N.tr("Cannot connect the database"));
                                                                LOGGER.error(ex);
                                                        }


                                                }
                                        }

                                }
                        });
                        btnConnect.setBorderPainted(false);

                        btnDisconnect = new JButton(OrbisGISIcon.getIcon("disconnect"));
                        btnDisconnect.setToolTipText(I18N.tr("Disconnect"));
                        btnDisconnect.setEnabled(connection != null);
                        btnDisconnect.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        closeConnection();
                                }
                        });

                        btnDisconnect.setBorderPainted(false);



                        btnAddConnection = new JButton(OrbisGISIcon.getIcon("database_add"));
                        btnAddConnection.setToolTipText(I18N.tr("Add a new connection"));
                        btnAddConnection.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
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
                                                        saveProperties();

                                                }

                                        }

                                }
                        });
                        btnAddConnection.setBorderPainted(false);


                        btnEditConnection = new JButton(OrbisGISIcon.getIcon("database_edit"));
                        btnEditConnection.setToolTipText(I18N.tr("Edit a connection"));
                        btnEditConnection.setEnabled(btnStatus);

                        btnEditConnection.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
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
                        });
                        btnEditConnection.setBorderPainted(false);


                        btnRemoveConnection = new JButton(OrbisGISIcon.getIcon("database_delete"));
                        btnRemoveConnection.setToolTipText(I18N.tr("Remove a connection"));
                        btnRemoveConnection.setEnabled(btnStatus);

                        btnRemoveConnection.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        String dataBaseUri = cmbDataBaseUri.getSelectedItem().toString();
                                        if (!dataBaseUri.isEmpty()) {
                                                cmbDataBaseUri.removeItem(dataBaseUri);
                                                dbProperties.remove(dataBaseUri);
                                                saveProperties();
                                        }

                                }
                        });
                        btnRemoveConnection.setBorderPainted(false);

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
        public void initialize() {
                if (jScrollPane == null) {
                        this.setLayout(new BorderLayout());
                        jtableExporter = new JTable();
                        jtableExporter.setRowHeight(20);
                        DataBaseTableModel dataBaseTableModel = new DataBaseTableModel(sourceManager, sourceNames);
                        jtableExporter.setModel(dataBaseTableModel);
                        jtableExporter.setDefaultRenderer(Object.class, new org.orbisgis.view.geocatalog.sourceWizards.db.TableCellRenderer());
                        TableColumn schemaColumn = jtableExporter.getColumnModel().getColumn(2);
                        comboBoxSchemas = new JComboBox(new String[]{"public"});
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

                        initColumnSizes(jtableExporter);
                        jScrollPane = new JScrollPane(jtableExporter);
                        this.add(connectionToolBar(), BorderLayout.NORTH);
                        this.add(jScrollPane, BorderLayout.CENTER);
                }
        }

        /**
         * The validateInput is override to ensure that all sources can be
         * exported
         *
         * @return
         */
        @Override
        public String validateInput() {
                String validateInput = null;
                DataBaseTableModel model = (DataBaseTableModel) jtableExporter.getModel();
                if (!model.isOneRowSelected()) {
                        validateInput = I18N.tr("At least one row must be checked.");
                }
                int count = model.getRowCount();
                for (int i = 0; i < count; i++) {
                        DataBaseRow row = model.getRow(i);
                        if (row.isExport()) {
                                row.toSQL();

                        }
                }
                return validateInput;
        }

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public String getInfoText() {
                return null;
        }

        /**
         * This method computes the good column size.
         *
         * @param table
         */
        private void initColumnSizes(JTable table) {
                DataBaseTableModel model = (DataBaseTableModel) table.getModel();
                TableColumn column = null;
                Component comp = null;
                int headerWidth = 0;
                int cellWidth = 0;
                Object[] databaseRow = model.getObjects(0);
                TableCellRenderer headerRenderer =
                        table.getTableHeader().getDefaultRenderer();
                int count = model.getColumnCount();
                for (int i = 0; i < count; i++) {
                        column = table.getColumnModel().getColumn(i);

                        comp = headerRenderer.getTableCellRendererComponent(
                                null, column.getHeaderValue(),
                                false, false, 0, 0);
                        headerWidth = comp.getPreferredSize().width;

                        comp = table.getDefaultRenderer(model.getColumnClass(i)).
                                getTableCellRendererComponent(
                                table, databaseRow[i],
                                false, false, 0, i);
                        cellWidth = comp.getPreferredSize().width;

                        column.setPreferredWidth(Math.max(headerWidth, cellWidth));
                }
        }

        /**
         * Check if the table already exists
         *
         * @param table
         * @param schema
         * @return
         * @throws DriverException
         */
        public boolean ifTableExists(String table, String schema) throws DriverException {
                boolean exists = false;
                TableDescription[] tables = dbDriver.getTables(connection);
                for (int i = 0; i < tables.length && !exists; i++) {
                        TableDescription tableDesc = tables[i];
                        if (tableDesc.getSchema().equals(schema) && tableDesc.getName().equals(table)) {
                                exists = true;
                        }
                }
                return exists;
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
                        dbProperties.store(new FileOutputStream(ws.getWorkspaceFolder() + File.separator + dbPropertiesFile), I18N.tr("Saved with the OrbisGIS database exporter panel"));
                } catch (IOException ex) {
                        LOGGER.error(ex);
                }

        }

        /**
         * Close the connection to the database
         */
        public void closeConnection() {
                if (connection != null) {
                        try {
                                connection.close();
                                dbDriver = null;
                                btnConnect.setEnabled(true);
                                btnAddConnection.setEnabled(true);
                                btnEditConnection.setEnabled(true);
                                btnRemoveConnection.setEnabled(true);
                                btnDisconnect.setEnabled(false);
                        } catch (SQLException ex) {
                                LOGGER.error(ex);
                        }
                }
        }

        /**
         * Return a connection to the database
         *
         * @param parameters
         * @param passWord
         * @return
         * @throws SQLException
         */
        public void createConnection(String[] parameters, String passWord) throws SQLException {
                if (connection == null) {
                        try {
                                String dbType = parameters[0];
                                createDBDriver(dbType);
                                String cs = dbDriver.getConnectionString(parameters[1], Integer.valueOf(
                                        parameters[2]), Boolean.valueOf(parameters[3]), parameters[4], parameters[5],
                                        passWord);
                                schemas = dbDriver.getSchemas(connection);
                                connection = dbDriver.getConnection(cs);
                                btnConnect.setEnabled(false);
                                btnAddConnection.setEnabled(false);
                                btnEditConnection.setEnabled(false);
                                btnRemoveConnection.setEnabled(false);
                                btnDisconnect.setEnabled(true);
                                comboBoxSchemas = new JComboBox(dbDriver.getSchemas(connection));
                                jtableExporter.repaint();
                        } catch (DriverException ex) {
                                LOGGER.error(ex);
                        }
                }

        }

        /*
         * Return the dbdriver corresponding to the dataabse type
         */
        public void createDBDriver(String dbType) {
                if (dbDriver == null) {
                        DriverManager driverManager = sourceManager.getDriverManager();
                        dbDriver = (DBDriver) driverManager.getDriver(dbType);
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
}

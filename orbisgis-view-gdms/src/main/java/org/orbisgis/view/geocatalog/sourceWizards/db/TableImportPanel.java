/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog.sourceWizards.db;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.TableDescription;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.events.Listener;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.MIPValidation;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.PasswordType;
import org.orbisgis.sif.multiInputPanel.TextBoxType;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.geocatalog.Catalog;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Erwan Bocher
 */
public class TableImportPanel extends JDialog {

        private static final I18n I18N = I18nFactory.getI18n(TableImportPanel.class);
        private static final Logger LOGGER = Logger.getLogger(Catalog.class);
        private JScrollPane jScrollPane;
        private JTree tableTree;
        private ConnectionToolBar connectionToolBar;
        private final SourceManager sourceManager;
        private DBSource dBSource;

        public TableImportPanel(SourceManager sourceManager) {
                this.sourceManager = sourceManager;
                initialize();
                setModal(true);
                setSize(400, 300);
        }

        @Override
        public String getTitle() {
                return I18N.tr("Open tables or views from a database");
        }

        /**
         * Create the compoments
         */
        private void initialize() {
                if (null == jScrollPane) {
                        this.setLayout(new BorderLayout());
                        jScrollPane = new JScrollPane();

                        JPanel buttonPanels = new JPanel();
                        JButton okButtons = new JButton(I18N.tr("Open"));
                        okButtons.addActionListener(EventHandler.create(ActionListener.class, this, "onImport"));
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
         * A toolbar to manage the connexion with the database
         *
         * @return
         */
        private ConnectionToolBar connectionToolBar() {
                if (connectionToolBar == null) {
                        connectionToolBar = new ConnectionToolBar();
                        connectionToolBar.addVetoableChangeListener(ConnectionToolBar.PROP_CONNECTED, EventHandler.create(VetoableChangeListener.class, this, "onUserConnect", ""));
                        connectionToolBar.getMessagesEvents().addListener(this, EventHandler.create(Listener.class, this, "onMessagePanel", "message"));
                }
                return connectionToolBar;
        }

        /**
         * Get the current dbsource build when the connection is done.
         *
         * @return
         */
        public DBSource getDBSource() {
                return dBSource;
        }

        /**
         * Connect to the database
         */
        public void onUserConnect(PropertyChangeEvent evt) throws PropertyVetoException {
                if ((Boolean) evt.getNewValue() == true) {
                        if (!onConnect()) {
                                throw new PropertyVetoException(I18N.tr("Cannot connect to the database."), evt);
                        }
                } else {
                        onDisconnect();
                }
        }

        /**
         * The user click on the connect button in the main toolbar
         *
         * @return
         */
        public boolean onConnect() {
                String dataBaseUri = connectionToolBar.getCmbDataBaseUri().getSelectedItem().toString();
                if (!dataBaseUri.isEmpty()) {
                        MultiInputPanel passwordDialog = new MultiInputPanel(I18N.tr("Please set the user name and a password "));
                        passwordDialog.addInput("login", I18N.tr("Login"), "",new TextBoxType(10));
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
                                        String passWord = passwordDialog.getInput("password");
                                        String login = passwordDialog.getInput("login");
                                        String properties = connectionToolBar.getDbProperties().getProperty(dataBaseUri);
                                        BackgroundManager backgroundManager = Services.getService(BackgroundManager.class);
                                        backgroundManager.nonBlockingBackgroundOperation(new PopulatingDBTree(properties.split(","),login, passWord));
                                        return true;
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
         * Refresh the jtree
         */
        public void onDisconnect() {
                onUserSelectionChange();
                dBSource = null;
        }

        /**
         * Change the status of the components when the user click on the button
         * disconnect
         */
        private void onUserSelectionChange() {
                boolean isCmbEmpty = connectionToolBar.getCmbDataBaseUri().getItemCount() == 0;
                if (isCmbEmpty) {
                        tableTree = null;
                        jScrollPane.setViewportView(tableTree);
                } else {
                        if (connectionToolBar.isConnected()) {
                                tableTree = null;
                                jScrollPane.setViewportView(tableTree);
                                jScrollPane.repaint();
                        } else {
                        }
                }
        }

        /**
         * The user click on the import button
         */
        public void onImport() {
                if (!connectionToolBar.isConnected()) {
                        JOptionPane.showMessageDialog(this, I18N.tr("Please connect to a database"));
                } else {
                        TreePath[] treePath;
                        treePath = tableTree.getSelectionPaths();
                        if (treePath == null) {
                                JOptionPane.showMessageDialog(this, I18N.tr("The database contains any tables or views."));
                                return;
                        }
                        int selectedNodes = treePath.length;
                        if (selectedNodes < 1) {
                                JOptionPane.showMessageDialog(this, I18N.tr("At least one table or view must be selected."));
                                return;
                        }

                        for (int i = 0; i < selectedNodes; i++) {
                                Object selectedObject = ((DefaultMutableTreeNode) treePath[i].getLastPathComponent()).getUserObject();
                                if (selectedObject instanceof TableNode) {
					DBSource clonedDBSource = dBSource.clone();
					TableNode tableNode = (TableNode) selectedObject;
                                        String tableName = tableNode.getName();
                                        clonedDBSource.setTableName(tableName);
                                        clonedDBSource.setSchemaName(tableNode.getSchema());
                                        if (sourceManager.exists(tableName)) {
                                                tableName = sourceManager.getUniqueName(tableName);
                                        }
                                        sourceManager.register(tableName, new DBTableSourceDefinition(clonedDBSource));
                                }
                        }
                        dispose();
                }

        }
        
        /**
         * This method is used to populate the treemodel with a list of tables 
         * and views available in the database.
         */
        private class PopulatingDBTree implements BackgroundJob {
                private final String[] parameters;
                private final String passWord;
                private final String login;

                
                public PopulatingDBTree(String[] parameters,String login, String passWord) {
                        this.parameters = parameters;
                        this.login=login;
                        this.passWord=passWord;
                }

                @Override
                public void run(org.orbisgis.progress.ProgressMonitor pm) {
                        try {
                                DriverManager driverManager = sourceManager.getDriverManager();
                                String dbType = parameters[0];
                                DBDriver dbDriver = (DBDriver) driverManager.getDriver(dbType);
                                final String cs = dbDriver.getConnectionString(parameters[1], Integer.valueOf(
                                        parameters[2]), Boolean.valueOf(parameters[3]), parameters[4], login,
                                        passWord);
                                Connection connection = dbDriver.getConnection(cs);

                                dBSource = new DBSource(parameters[1], Integer.valueOf(
                                        parameters[2]), parameters[4], login, passWord, dbDriver.getPrefixes()[0], Boolean.valueOf(parameters[3]));

                                final String[] schemas = dbDriver.getSchemas(connection);
                               

                                DefaultMutableTreeNode rootNode =
                                        new DefaultMutableTreeNode(connection.getCatalog());
                                // Add Data to the tree
                                for (String schema : schemas) {

                                        final TableDescription[] tableDescriptions = dbDriver.getTables(connection, null, schema, null,
                                                new String[]{"TABLE"});
                                        final TableDescription[] viewDescriptions = dbDriver.getTables(connection, null, schema, null,
                                                new String[]{"VIEW"});

                                        if (tableDescriptions.length == 0
                                                && viewDescriptions.length == 0) {
                                                continue;
                                        }

                                        // list schemas
                                        DefaultMutableTreeNode schemaNode = new DefaultMutableTreeNode(
                                                new SchemaNode(schema));
                                        rootNode.add(schemaNode);

                                        // list Tables
                                        DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(
                                                I18N.tr("Tables"));

                                        // we send possible loading errors to the Output window
                                        DriverException[] exs = dbDriver.getLastNonBlockingErrors();
                                        if (exs.length != 0) {
                                                for (int i = 0; i < exs.length; i++) {
                                                        LOGGER.error(exs[i].getMessage(), exs[i].getCause());
                                                }
                                        }

                                        if (tableDescriptions.length > 0) {
                                                schemaNode.add(tableNode);
                                                for (TableDescription tableDescription : tableDescriptions) {
                                                        tableNode.add(new DefaultMutableTreeNode(new TableNode(
                                                                tableDescription)));
                                                }
                                        }

                                        // list View
                                        DefaultMutableTreeNode viewNode = new DefaultMutableTreeNode(
                                                I18N.tr("Views"));
                                        if (viewDescriptions.length > 0) {
                                                schemaNode.add(viewNode);
                                                for (TableDescription viewDescription : viewDescriptions) {
                                                        viewNode.add(new DefaultMutableTreeNode(new ViewNode(
                                                                viewDescription)));
                                                }
                                        }
                                }

                                connection.close();
                                // Apply the new model in the swing thread
                                SwingUtilities.invokeLater(new LoadTreeNode(rootNode));
                        } catch (DriverException ex) {
                                LOGGER.error(I18N.tr("Cannot list the tables and views"), ex);
                        } catch (SQLException ex) {
                                LOGGER.error(I18N.tr("Cannot open the connection"), ex);
                        }
                }

                @Override
                public String getTaskName() {
                        return I18N.tr("Populating the database list...");
                }
                
        }
        private class LoadTreeNode implements Runnable {
                DefaultMutableTreeNode rootNode;

                public LoadTreeNode(DefaultMutableTreeNode rootNode) {
                        this.rootNode = rootNode;
                }
                
                @Override
                public void run() {                        
                        tableTree = new JTree(rootNode);
                        tableTree.setRootVisible(true);
                        tableTree.setShowsRootHandles(true);
                        tableTree.setCellRenderer(new TableTreeCellRenderer());  
                        jScrollPane.setViewportView(tableTree);
                }
                
        }

}

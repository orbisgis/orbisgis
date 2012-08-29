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

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core.events.EventException;
import org.orbisgis.core.events.ListenerContainer;
import org.orbisgis.core.events.OG_VetoableChangeSupport;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.view.geocatalog.Catalog;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author ebocher
 */
public class ConnectionToolBar extends JToolBar {

        private static final I18n I18N = I18nFactory.getI18n(ConnectionToolBar.class);
        private static final Logger LOGGER = Logger.getLogger(Catalog.class);
        private static final String DB_PROPERTIES_FILE = "db_connexions.properties";
        private Properties dbProperties = new Properties();
        private JComboBox cmbDataBaseUri;
        private JButton btnConnect;
        private JButton btnDisconnect;
        private JButton btnAddConnection;
        private JButton btnEditConnection;
        private JButton btnRemoveConnection;
        private ListenerContainer<DBMessageEvents> messagesEvents = new ListenerContainer<DBMessageEvents>();
        

        public ConnectionToolBar() {
                init();
        }

        public ListenerContainer<DBMessageEvents> getMessagesEvents() {
                return messagesEvents;
        }        
        

        /**
         * Create all components
         */
        private void init() {
                setFloatable(false);
                setOpaque(false);
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
                add(cmbDataBaseUri);
                add(btnConnect);
                add(btnDisconnect);
                add(btnAddConnection);
                add(btnEditConnection);
                add(btnRemoveConnection);
        }

        /**
         * Load connection properties
         */
        private void loadDBProperties() {
                CoreWorkspace ws = Services.getService(CoreWorkspace.class);
                try {
                        File propertiesFile = new File(ws.getWorkspaceFolder() + File.separator + DB_PROPERTIES_FILE);

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
        public void saveProperties() {
                try {
                        CoreWorkspace ws = Services.getService(CoreWorkspace.class);
                        dbProperties.store(new FileOutputStream(ws.getWorkspaceFolder() + File.separator + DB_PROPERTIES_FILE),
                                I18N.tr("Saved with the OrbisGIS database panel"));
                } catch (IOException ex) {
                        LOGGER.error(ex);
                }

        }

        public JButton getBtnAddConnection() {
                return btnAddConnection;
        }

        public JButton getBtnConnect() {
                return btnConnect;
        }

        public JButton getBtnDisconnect() {
                return btnDisconnect;
        }

        public JButton getBtnEditConnection() {
                return btnEditConnection;
        }

        public JButton getBtnRemoveConnection() {
                return btnRemoveConnection;
        }

        /**
         * Return the combobox component that contains all string connection parameters
         * @return 
         */
        public JComboBox getCmbDataBaseUri() {
                return cmbDataBaseUri;
        }

        /**
         * Return all string connection parameters 
         * @return 
         */
        public Properties getDbProperties() {
                return dbProperties;
        }
        private boolean connected = false;
        public static final String PROP_CONNECTED = "connected";

        /**
         * Get the value of connected
         *
         * @return the value of connected
         */
        public boolean isConnected() {
                return connected;
        }

        /**
         * Set the value of connected
         *
         * @param connected new value of connected
         * @throws java.beans.PropertyVetoException
         */
        public void setConnected(boolean connected) throws java.beans.PropertyVetoException {
                boolean oldConnected = this.connected;
                vetoableChangeSupport.fireVetoableChange(PROP_CONNECTED, oldConnected, connected);
                fireVetoableChange(PROP_CONNECTED, oldConnected, connected);
                this.connected = connected;
                firePropertyChange(PROP_CONNECTED, oldConnected, connected);
        }

        
        private transient final VetoableChangeSupport vetoableChangeSupport = new OG_VetoableChangeSupport(this);

        

        /**
         * Add a VetoableChangeListener for a specific property.
         *
         * @param listener
         */
        public void addVetoableChangeListener(String property, VetoableChangeListener listener) {
                vetoableChangeSupport.addVetoableChangeListener(property, listener);
        }

        /**
         * Connect to the database
         */
        public void onConnect() {
                try {
                        setConnected(true);
                } catch (PropertyVetoException ex) {
                        return;
                }
                onUserSelectionChange();

        }
        
        /**
         * Disconnect from the database and update all buttons
         */
        public void onDisconnect() {
                 try {
                        setConnected(false);
                } catch (PropertyVetoException ex) {
                        return;
                }
                onUserSelectionChange();

        }

        /**
         * Change the status of the components
         */
        private void onUserSelectionChange() {
                boolean isCmbEmpty = getCmbDataBaseUri().getItemCount() == 0;

                if (isCmbEmpty) {
                        getBtnConnect().setEnabled(false);
                        getBtnDisconnect().setEnabled(false);
                        getBtnAddConnection().setEnabled(true);
                        getBtnEditConnection().setEnabled(false);
                        getBtnRemoveConnection().setEnabled(false);
                        getCmbDataBaseUri().setEnabled(true);
                } else {
                        if (isConnected()) {
                                getBtnConnect().setEnabled(false);
                                getBtnDisconnect().setEnabled(true);
                                getBtnAddConnection().setEnabled(false);
                                getBtnEditConnection().setEnabled(false);
                                getBtnRemoveConnection().setEnabled(false);
                                getCmbDataBaseUri().setEnabled(false);
                        } else {
                                getBtnConnect().setEnabled(true);
                                getBtnDisconnect().setEnabled(false);
                                getBtnAddConnection().setEnabled(true);
                                getBtnEditConnection().setEnabled(true);
                                getBtnRemoveConnection().setEnabled(true);
                                getCmbDataBaseUri().setEnabled(true);
                        }
                }



        }

        /**
         * Add a new connection
         */
        public void onAddConnection() throws EventException {
                MultiInputPanel mip = DBUIFactory.getConnectionPanel();
                if (UIFactory.showDialog(mip)) {
                        String connectionName = mip.getInput(DBUIFactory.CONNAME);
                        if (!getDbProperties().containsKey(connectionName)) {
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
                                getDbProperties().setProperty(connectionName, sb.toString());
                                getCmbDataBaseUri().addItem(connectionName);
                                getCmbDataBaseUri().setSelectedItem(connectionName);
                                onUserSelectionChange();
                                saveProperties();

                        } else {
                                messagesEvents.callListeners(new DBMessageEvents(I18N.tr("There is already a connection with this name."), this));
                               
                        }

                }

        }

        /**
         * Edit a connection to change its parameters
         */
        public void onEditConnection() {
                String dataBaseUri = getCmbDataBaseUri().getSelectedItem().toString();
                if (!dataBaseUri.isEmpty()) {
                        String property = getDbProperties().getProperty(dataBaseUri);
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
                                getDbProperties().setProperty(connectionName, sb.toString());
                                getCmbDataBaseUri().addItem(connectionName);
                                getCmbDataBaseUri().setSelectedItem(connectionName);
                                saveProperties();
                        }
                }
        }

        /**
         * Remove a connection
         */
        public void onRemoveConnection() {
                String dataBaseUri = getCmbDataBaseUri().getSelectedItem().toString();
                if (!dataBaseUri.isEmpty()) {
                        getCmbDataBaseUri().removeItem(dataBaseUri);
                        getDbProperties().remove(dataBaseUri);
                        saveProperties();
                }
                onUserSelectionChange();
        }
}

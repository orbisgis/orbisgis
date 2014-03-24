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
package org.orbisgis.mapeditor.map.mapsManager;

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.MutableTreeNode;
import org.apache.log4j.Logger;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.components.fstree.AbstractTreeNodeContainer;
import org.orbisgis.view.components.fstree.PopupTreeNode;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.viewapi.util.MenuCommonFunctions;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * List of remote map catalog servers
 * @author Nicolas Fortin
 */
public class TreeNodeRemoteRoot extends AbstractTreeNodeContainer implements PopupTreeNode {
        private static final I18n I18N = I18nFactory.getI18n(TreeNodeRemoteRoot.class);
        private static final Logger LOGGER = Logger.getLogger(TreeNodeRemoteRoot.class);
        // This list must be updated to the current state of shown servers
        private List<String> serverList;
        private MapsManagerPersistence mapsManagerPersistence;
        private AtomicBoolean onUpdateSerialisation = new AtomicBoolean(false);
        private DataManager dataManager;
        private File mapsFolder;

        /**
         * Default constructor
         */
        public TreeNodeRemoteRoot(DataManager dataManager,File mapsFolder) {
                setLabel(I18N.tr("Remote"));
                setEditable(false);
                this.dataManager = dataManager;
                this.mapsFolder = mapsFolder;
        }

        /**
         * The server list will keep this instance updated
         * @param mapsManagerPersistence
         */
        public void setMapsManagerPersistence(MapsManagerPersistence mapsManagerPersistence) {
            this.mapsManagerPersistence = mapsManagerPersistence;
            // Install listeners
            mapsManagerPersistence.addPropertyChangeListener(MapsManagerPersistence.PROP_SERVER_URI_LIST, EventHandler.create(PropertyChangeListener.class, this, "onMapServerListUpdate"));
            onMapServerListUpdate();
        }
        private void updateServerList() {
            if(mapsManagerPersistence!=null && serverList!=null) {
                onUpdateSerialisation.set(true);
                try {
                    mapsManagerPersistence.setMapCatalogUrlList(serverList);
                } finally {
                    onUpdateSerialisation.set(false);
                }
            }
        }
        public void onMapServerListUpdate() {
            if(!onUpdateSerialisation.get()) {
                    setServerList(mapsManagerPersistence.getMapCatalogUrlList());
            }
        }
        @Override
        public void insert(MutableTreeNode mtn, int i) {
                super.insert(mtn, i);                        
                if(mtn instanceof TreeNodeMapCatalogServer) {
                        TreeNodeMapCatalogServer newChild = (TreeNodeMapCatalogServer)mtn;
                        if(serverList!=null) {
                                serverList.add(i,newChild.getServerUrl().toExternalForm());
                                updateServerList();
                        }
                }
        }

        /**
         * Set the server list
         * Keep updated to the current state of shown servers
         * @param serverList New server list
         */
        public void setServerList(List<String> serverList) {                
                for (String serverAddress : serverList) {
                        try {
                                URL serverUrl = new URL(serverAddress);
                                model.insertNodeInto(new TreeNodeMapCatalogServer(serverUrl, dataManager, mapsFolder), this, getChildCount());
                        } catch (MalformedURLException ex) {
                                LOGGER.error(I18N.tr("Cannot load map catalog server {0}", serverAddress), ex);
                        }
                }
                this.serverList = new ArrayList<String>(serverList);
        }

        /**
         * @return Server list
         */
        public List<String> getServerList() {
            return Collections.unmodifiableList(serverList);
        }
        @Override
        public void remove(int i) {
                super.remove(i);
                if(serverList!=null) {
                        serverList.remove(i);
                        updateServerList();
                }
        }

        @Override
        public void remove(MutableTreeNode mtn) {
                remove(getIndex(mtn));                
        }

        @Override
        public void setUserObject(Object o) {
                // Read only
        }

        /**
         * Prompt the user to type the server URL
         */
        public void onAddServer() {
                String serverURLString = JOptionPane.showInputDialog(UIFactory.getMainFrame(), I18N.tr("Enter the server URL"), "");
                if(serverURLString!=null && !serverURLString.isEmpty()) {
                        try {
                                URL serverURL = new URL(serverURLString);
                                model.insertNodeInto(new TreeNodeMapCatalogServer(serverURL, dataManager, mapsFolder), this, getChildCount());
                        } catch( MalformedURLException ex) {
                                LOGGER.error(I18N.tr("You type an incorrect URL {0}",serverURLString),ex);
                        }
                }
        }
        
        @Override
        public void feedPopupMenu(JPopupMenu menu) {
                JMenuItem addServer = new JMenuItem(I18N.tr("Add Map Catalog server"),
                        OrbisGISIcon.getIcon("world_add"));
                addServer.setToolTipText(I18N.tr("Add a new remote map catalog"));
                addServer.setActionCommand("TreeNodeRemoteRoot:addServer");
                addServer.addActionListener(
                        EventHandler.create(ActionListener.class,
                        this, "onAddServer"));
                MenuCommonFunctions.updateOrInsertMenuItem(menu, addServer);
        }
}

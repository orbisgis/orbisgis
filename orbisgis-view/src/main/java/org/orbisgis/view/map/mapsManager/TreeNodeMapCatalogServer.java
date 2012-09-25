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
package org.orbisgis.view.map.mapsManager;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.MutableTreeNode;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.mapcatalog.Workspace;
import org.orbisgis.sif.common.MenuCommonFunctions;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.components.fstree.AbstractTreeNodeContainer;
import org.orbisgis.view.components.fstree.PopupTreeNode;
import org.orbisgis.view.components.fstree.TreeNodeCustomIcon;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.mapsManager.jobs.DownloadWorkspaces;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Map Catalog server node.
 * This node children is retrieved from the Internet only when the user
 * expand this node.
 * @author Nicolas Fortin
 */
public class TreeNodeMapCatalogServer extends AbstractTreeNodeContainer implements TreeNodeCustomIcon,PopupTreeNode {

        public static enum SERVER_STATUS { DISCONNECTED, CONNECTED, UNREACHABLE };
        URL serverUrl;
        private static final Logger LOGGER = Logger.getLogger(TreeNodeMapCatalogServer.class);
        AtomicBoolean downloaded = new AtomicBoolean(false);
        private SERVER_STATUS serverStatus = SERVER_STATUS.DISCONNECTED;
        private static final I18n I18N = I18nFactory.getI18n(TreeNodeMapCatalogServer.class);
                
        public TreeNodeMapCatalogServer(URL serverUrl) {
                setServerUrl(serverUrl);
        }
        
        private void setServerUrl(URL serverUrl) {
                this.serverUrl = serverUrl;
                setLabel(serverUrl.toExternalForm());                
        }

        /**
         * @return The shown server status
         */
        public SERVER_STATUS getServerStatus() {
                return serverStatus;
        }

        /**
         * Set the node status to show a visual hint to the user
         * @param serverStatus 
         */
        public void setServerStatus(SERVER_STATUS serverStatus) {
                this.serverStatus = serverStatus;
                model.nodeChanged(this);
        }
        
        public void addWorkspace(Workspace newWorkspace) {
                model.insertNodeInto(new TreeNodeWorkspace(newWorkspace), this, getChildCount());
        }

        @Override
        public void setUserObject(Object o) {
                String userUri = o.toString();
                try {
                        setServerUrl(new URL(userUri));
                        update();
                } catch(MalformedURLException ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                }
        }
        /**
         * Refresh the map catalog content
         */
        public void update() {
                // Clear all childrens
                List<MutableTreeNode> childrenToRemove = new ArrayList<MutableTreeNode>(children);
                for(MutableTreeNode child : childrenToRemove) {
                        model.removeNodeFromParent(child);
                }
                // Insert busy Node
                TreeNodeBusy busyNode = new TreeNodeBusy();
                model.insertNodeInto(busyNode, this, 0);
                // Launch the download job
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.nonBlockingBackgroundOperation(new DownloadWorkspaces(this,busyNode));
                
        }
        @Override
        public int getChildCount() {
                if(!downloaded.getAndSet(true)) {
                        update();
                }
                return super.getChildCount();
        }

        private ImageIcon getServerIcon() {
                switch(serverStatus) {
                        case DISCONNECTED:
                                return OrbisGISIcon.getIcon("worldmap");
                        case CONNECTED:
                                return OrbisGISIcon.getIcon("worldmap");
                        default:
                                return OrbisGISIcon.getIcon("worldmap");
                }
        }
        /**
         * Return the url associated with this server
         * @return 
         */
        public URL getServerUrl() {
                return serverUrl;
        }
        
        @Override
        public ImageIcon getLeafIcon() {
                throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ImageIcon getClosedIcon() {
                return getServerIcon();
        }

        @Override
        public ImageIcon getOpenIcon() {
                return getServerIcon();
        }        
        /**
         * Remove this node
         */
        public void onDeleteServer() {
                model.removeNodeFromParent(this);
        }
        @Override
        public void feedPopupMenu(JPopupMenu menu) {     
                // Find the Rename menu and change the label
                for(Component component : menu.getComponents()) {
                        if(component instanceof JMenuItem) {
                                JMenuItem item = (JMenuItem) component;
                                if(item.getActionCommand().equals("rename")) {
                                        item.setText(I18N.tr("Edit the URL"));
                                }
                        }
                }           
                JMenuItem folderRemove = new JMenuItem(I18N.tr("Delete"),
                        OrbisGISIcon.getIcon("world_delete"));
                folderRemove.setToolTipText(I18N.tr("Remove this server"));
                folderRemove.setActionCommand("delete");
                folderRemove.addActionListener(
                EventHandler.create(ActionListener.class,
                this, "onDeleteServer"));
                MenuCommonFunctions.updateOrInsertMenuItem(menu,folderRemove);
                if(downloaded.get()) {
                        JMenuItem updateMenu = new JMenuItem(I18N.tr("Update"),
                                OrbisGISIcon.getIcon("arrow_refresh"));
                        updateMenu.setToolTipText(I18N.tr("Download the server content"));
                        updateMenu.setActionCommand("Update");
                        updateMenu.addActionListener(
                                EventHandler.create(ActionListener.class,this,"update"));
                        MenuCommonFunctions.updateOrInsertMenuItem(menu, updateMenu);
                }
        }
}

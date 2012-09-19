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

import java.net.URL;
import javax.swing.tree.MutableTreeNode;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.components.fstree.AbstractTreeNodeContainer;
import org.orbisgis.view.map.mapsManager.jobs.DownloadWorkspaces;

/**
 * Map Catalog server node.
 * This node children is retrieved from the Internet only when the user
 * expand this node.
 * @author Nicolas Fortin
 */
public class TreeNodeMapCatalogServer extends AbstractTreeNodeContainer {
        // Download again the workspaces if the expiration is reach
        // and this node is expanded
        private static final long CACHE_EXPIRATION = 60000;
        URL serverUrl;
        private static final Logger LOGGER = Logger.getLogger(TreeNodeMapCatalogServer.class);
        private long lastWorkspacesDownload = 0;

        public TreeNodeMapCatalogServer(URL serverUrl) {
                this.serverUrl = serverUrl;
                setLabel(serverUrl.toExternalForm());
                setEditable(false);
        }

        @Override
        public void setUserObject(Object o) {
                // Change server adress ?
        }

        @Override
        public int getChildCount() {
                long curTime = System.currentTimeMillis();
                if(lastWorkspacesDownload + CACHE_EXPIRATION < curTime) {
                        LOGGER.debug("TreeNodeMapCatalogServer::DownloadWorkspaces");
                        lastWorkspacesDownload = curTime;
                        // Clear all childrens
                        for(MutableTreeNode child : children) {
                                model.removeNodeFromParent(child);
                        }
                        // Insert busy Node
                        TreeNodeBusy busyNode = new TreeNodeBusy();
                        model.insertNodeInto(busyNode, this, 0);
                        // Launch the download job
                        BackgroundManager bm = Services.getService(BackgroundManager.class);
                        bm.nonBlockingBackgroundOperation(new DownloadWorkspaces(this,busyNode));
                }
                return super.getChildCount();
        }

        /**
         * Return the url associated with this server
         * @return 
         */
        public URL getServerUrl() {
                return serverUrl;
        }
}

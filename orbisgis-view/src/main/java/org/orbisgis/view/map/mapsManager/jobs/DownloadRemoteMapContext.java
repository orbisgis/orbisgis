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
package org.orbisgis.view.map.mapsManager.jobs;

import java.io.IOException;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeNode;
import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.mapcatalog.RemoteMapContext;
import org.orbisgis.core.layerModel.mapcatalog.Workspace;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.map.mapsManager.TreeNodeBusy;
import org.orbisgis.view.map.mapsManager.TreeNodeMapCatalogServer;
import org.orbisgis.view.map.mapsManager.TreeNodeWorkspace;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Nicolas Fortin
 */
public class DownloadRemoteMapContext implements BackgroundJob {
        private static final I18n I18N = I18nFactory.getI18n(ReadStoredMap.class);
        private TreeNodeWorkspace workspaceNode;
        private TreeNodeBusy treeNodeBusyHint;
        private static final Logger LOGGER = Logger.getLogger(DownloadWorkspaces.class);
        
        public DownloadRemoteMapContext(TreeNodeWorkspace workspace, TreeNodeBusy treeNodeBusyHint) {
                this.workspaceNode = workspace;
                this.treeNodeBusyHint = treeNodeBusyHint;
        }
        
        @Override
        public void run(ProgressMonitor pm) {
                //
                Workspace workspace = workspaceNode.getWorkspace();
                try {
                        treeNodeBusyHint.setDoAnimation(true);
                        List<RemoteMapContext> contexts = workspace.getMapContextList();
                        SwingUtilities.invokeLater(new FeedWorkspaceNode(workspaceNode, contexts));
                } catch(IOException ex) {
                        // Download fail, inform the user
                        // By logging and by the server icon
                        LOGGER.error(I18N.tr("Cannot download the server's contexts of {0}",workspace.getWorkspaceName()),ex);
                        SwingUtilities.invokeLater(new FeedWorkspaceNode(workspaceNode, null));
                } finally {
                        // Stop animation
                        treeNodeBusyHint.setDoAnimation(false);
                }
        }

        @Override
        public String getTaskName() {
                return I18N.tr("Download the context of {0}",workspaceNode);
        }
        
        private static class FeedWorkspaceNode implements Runnable {
                TreeNodeWorkspace workspaceNode;
                List<RemoteMapContext> contexts;

                public FeedWorkspaceNode(TreeNodeWorkspace workspaceNode, List<RemoteMapContext> contexts) {
                        this.workspaceNode = workspaceNode;
                        this.contexts = contexts;
                }
                @Override
                public void run() {
                        if(contexts!=null) {
                                for(RemoteMapContext context : contexts) {
                                        workspaceNode.addContext(context);
                                }
                        } else {
                                TreeNode parent = workspaceNode.getParent();
                                if(!(parent instanceof TreeNodeMapCatalogServer)) {
                                        return;
                                }
                                TreeNodeMapCatalogServer server = (TreeNodeMapCatalogServer)parent;
                                server.setServerStatus(TreeNodeMapCatalogServer.SERVER_STATUS.UNREACHABLE);                                
                        }
                }                
        }
}

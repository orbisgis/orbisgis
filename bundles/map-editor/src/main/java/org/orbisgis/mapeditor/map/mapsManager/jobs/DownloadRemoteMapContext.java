/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.mapeditor.map.mapsManager.jobs;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.tree.TreeNode;

import org.orbisgis.sif.components.fstree.TreeNodeBusy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.coremap.layerModel.mapcatalog.RemoteMapContext;
import org.orbisgis.coremap.layerModel.mapcatalog.Workspace;
import org.orbisgis.mapeditor.map.mapsManager.TreeNodeMapCatalogServer;
import org.orbisgis.mapeditor.map.mapsManager.TreeNodeWorkspace;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Download the remote map context and update the tree
 * @author Nicolas Fortin
 */
public class DownloadRemoteMapContext extends SwingWorkerPM<List<RemoteMapContext>, List<RemoteMapContext>> {
        private static final I18n I18N = I18nFactory.getI18n(ReadStoredMap.class);
        private TreeNodeWorkspace workspaceNode;
        private TreeNodeBusy treeNodeBusyHint;
        private static final Logger LOGGER = LoggerFactory.getLogger(DownloadWorkspaces.class);
        
        public DownloadRemoteMapContext(TreeNodeWorkspace workspace, TreeNodeBusy treeNodeBusyHint) {
                this.workspaceNode = workspace;
                this.treeNodeBusyHint = treeNodeBusyHint;
                setTaskName(I18N.tr("Download the context of {0}",workspaceNode));
        }

        @Override
        protected List<RemoteMapContext> doInBackground() throws IOException {
            Workspace workspace = workspaceNode.getWorkspace();
            try {
                treeNodeBusyHint.setDoAnimation(true);
                return workspace.getMapContextList();
            } finally {
                // Stop animation
                treeNodeBusyHint.setDoAnimation(false);
            }
        }

        @Override
        protected void done() {
            try {
                feedWorkspaceNode(get());
            } catch (InterruptedException| ExecutionException ex) {
                feedWorkspaceNode(null);
                Workspace workspace = workspaceNode.getWorkspace();
                LOGGER.error(I18N.tr("Cannot download the server's contexts of {0}",workspace.getWorkspaceName()),ex);
            }

        }
        public void feedWorkspaceNode(List<RemoteMapContext> contexts) {
            if(contexts != null) {
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

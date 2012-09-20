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

import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.MutableTreeNode;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.mapcatalog.RemoteMapContext;
import org.orbisgis.core.layerModel.mapcatalog.Workspace;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.components.fstree.AbstractTreeNodeContainer;
import org.orbisgis.view.map.mapsManager.jobs.DownloadRemoteMapContext;
import org.orbisgis.view.map.mapsManager.jobs.DownloadWorkspaces;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Nicolas Fortin
 */
public class TreeNodeWorkspace extends AbstractTreeNodeContainer {
        private static final long CACHE_EXPIRATION = 60000;
        private long lastContextDownload = 0;
        private static final I18n I18N = I18nFactory.getI18n(TreeNodeWorkspace.class);
        
        Workspace workspace;
        /**
         * Constructor
         * @param workspace Workspace structure
         */
        public TreeNodeWorkspace(Workspace workspace) {
                this.workspace = workspace;
                setLabel(workspace.getWorkspaceName());
                setEditable(false);
        }
        
        /**
         * Add this Remote map context
         * @param context 
         */
        public void addContext(RemoteMapContext context) {
                model.insertNodeInto(new TreeNodeRemoteMap(context), this, getChildCount());
        }
        
        

        @Override
        public int getChildCount() {
                long curTime = System.currentTimeMillis();
                if(lastContextDownload + CACHE_EXPIRATION < curTime) {
                        lastContextDownload = curTime;
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
                        bm.nonBlockingBackgroundOperation(new DownloadRemoteMapContext(this,busyNode));
                }
                return super.getChildCount();
        }
        
        /**
         * 
         * @return Workspace structure
         */
        public Workspace getWorkspace() {
                return workspace;
        }
        
        @Override
        public void setUserObject(Object o) {
                //Rename workspace ?
        }        
}

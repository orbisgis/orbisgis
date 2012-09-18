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
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.orbisgis.view.components.fstree.AbstractTreeNode;
import org.orbisgis.view.components.resourceTree.EnumIterator;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Nicolas Fortin
 */
public class TreeNodeRemoteRoot extends AbstractTreeNode {
        private static final I18n I18N = I18nFactory.getI18n(TreeNodeRemoteRoot.class);
        private List<TreeNodeMapCatalogServer> children = new ArrayList<TreeNodeMapCatalogServer>();
        // This list must be updated to the current state of shown servers
        private List<String> serverList;

        public TreeNodeRemoteRoot() {
                setLabel(I18N.tr("Remote"));
                setEditable(false);
        }
        
        @Override
        public void insert(MutableTreeNode mtn, int i) {
                if(mtn instanceof TreeNodeMapCatalogServer) {
                        TreeNodeMapCatalogServer newChild = (TreeNodeMapCatalogServer)mtn;
                        children.add(i, newChild);
                        if(serverList!=null) {
                                serverList.add(i,newChild.getServerUrl().toExternalForm());
                        }
                }
        }

        /**
         * Set the server list, without reading it.
         * Keep updated to the current state of shown servers
         * @param serverList 
         */
        public void setServerList(List<String> serverList) {
                this.serverList = serverList;
        }        
        
        @Override
        public void remove(int i) {
                children.remove(i);
                if(serverList!=null) {
                        serverList.remove(i);
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

        @Override
        public TreeNode getChildAt(int i) {
                return children.get(i);
        }

        @Override
        public int getChildCount() {
                return children.size();
        }

        @Override
        public int getIndex(TreeNode tn) {
                return children.indexOf(tn);
        }

        @Override
        public boolean getAllowsChildren() {
                return true;
        }

        @Override
        public boolean isLeaf() {
                return false;
        }

        @Override
        public Enumeration<? extends Object> children() {
                return new EnumIterator<TreeNodeMapCatalogServer>(children.iterator());
        }
}

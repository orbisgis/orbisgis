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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.apache.log4j.Logger;
import org.orbisgis.view.components.fstree.AbstractTreeNode;
import org.orbisgis.view.components.resourceTree.EnumIterator;

/**
 *
 * @author Nicolas Fortin
 */
public class TreeNodeMapCatalogServer extends AbstractTreeNode {
        URL serverUrl;
        List<TreeNodeWorkspace> children = new ArrayList<TreeNodeWorkspace>();
        private static final Logger LOGGER = Logger.getLogger(TreeNodeMapCatalogServer.class);

        public TreeNodeMapCatalogServer(URL serverUrl) {
                this.serverUrl = serverUrl;
                setLabel(serverUrl.toExternalForm());
                setEditable(false);
        }

        @Override
        public void insert(MutableTreeNode mtn, int i) {
                if(mtn instanceof TreeNodeWorkspace) {
                        children.add(i,(TreeNodeWorkspace) mtn);
                }
        }

        @Override
        public void remove(int i) {
                children.remove(i);
        }

        @Override
        public void remove(MutableTreeNode mtn) {
                children.remove((TreeNodeWorkspace)mtn);
        }

        @Override
        public void setUserObject(Object o) {
                // Change server adress ?
        }

        @Override
        public TreeNode getChildAt(int i) {
                LOGGER.debug("TreeNodeMapCatalogServer::getChildAt");
                return children.get(i);
        }

        @Override
        public int getChildCount() {
                LOGGER.debug("TreeNodeMapCatalogServer::getChildCount");
                return children.size();
        }

        @Override
        public int getIndex(TreeNode tn) {
                LOGGER.debug("TreeNodeMapCatalogServer::getIndex");
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
        /**
         * Return the url associated with this server
         * @return 
         */
        public URL getServerUrl() {
                return serverUrl;
        }
        
        @Override
        public Enumeration<? extends Object> children() {
                LOGGER.debug("TreeNodeMapCatalogServer::children");
                return new EnumIterator<TreeNodeWorkspace>(children.iterator());
        }
}

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
package org.orbisgis.view.components.fstree;

import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

/**
 * Transfer of Nodes inside/outside the Tree
 * @author Nicolas Fortin
 */
public class FileTreeTransferHandler extends TransferHandler {
        private static final long serialVersionUID = 1L;

        @Override
        public int getSourceActions(JComponent jc) {
                return COPY_OR_MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent jc) {
                if(!(jc instanceof JTree)) {
                        return null;
                }
                JTree tree = (JTree)jc;
                // Retrieve selected Transferable nodes
                TreePath[] paths = tree.getSelectionPaths();
                if(paths==null) {
                        return null;
                }
                TransferableList nodeTransferable = new TransferableList();
                for(TreePath nodePath : paths) {
                        Object nodeComp = nodePath.getLastPathComponent();
                        if(nodeComp instanceof DragTreeNode) {
                                DragTreeNode dragNode = (DragTreeNode)nodeComp;
                                dragNode.completeTransferable(nodeTransferable);
                        }
                }
                return nodeTransferable;
        }
        
        

        @Override
        public boolean canImport(TransferSupport ts) {
                DropLocation dl = ts.getDropLocation();
                if(!(dl instanceof JTree.DropLocation)) {
                        return false;
                }
                JTree.DropLocation dropLocation = (JTree.DropLocation) dl;
                TreePath dropPath = dropLocation.getPath();
                if(dropPath==null) {
                        return false;
                }
                Object nodeComp = dropPath.getLastPathComponent();
                if(!(nodeComp instanceof DropDestinationTreeNode)) {
                        return false;
                }
                DropDestinationTreeNode destNode = (DropDestinationTreeNode)nodeComp;
                //Test if the node accept the transfered Node
                return destNode.canImport(ts);
                
        }

        @Override
        public boolean importData(TransferSupport ts) {
                DropLocation dl = ts.getDropLocation();
                if(!(dl instanceof JTree.DropLocation)) {
                        return false;
                }
                JTree.DropLocation dropLocation = (JTree.DropLocation) dl;
                TreePath dropPath = dropLocation.getPath();
                if(dropPath==null) {
                        return false;
                }
                Object nodeComp = dropPath.getLastPathComponent();
                if(!(nodeComp instanceof DropDestinationTreeNode)) {
                        return false;
                }
                DropDestinationTreeNode destNode = (DropDestinationTreeNode)nodeComp;
                //Test if the node accept the transfered Node
                return destNode.importData(ts);
        }
        
        
}

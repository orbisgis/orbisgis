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
package org.orbisgis.sif.components.fstree;

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
                return COPY;
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

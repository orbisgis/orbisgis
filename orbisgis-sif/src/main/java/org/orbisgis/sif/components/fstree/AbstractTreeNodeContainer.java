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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.orbisgis.sif.components.resourceTree.EnumIterator;

/**
 * A node that can contain other nodes
 * @author Nicolas Fortin
 */
public abstract class AbstractTreeNodeContainer extends AbstractTreeNode {
        protected List<MutableTreeNode> children = new ArrayList<MutableTreeNode>();
        
        final protected void internalInsert(AbstractTreeNode mtn, int i) {
                children.add(i, mtn);
                mtn.setParent(this);
        }

        @Override
        public void insert(MutableTreeNode mtn, int i) {
                internalInsert((AbstractTreeNode)mtn,i);
        }

        @Override
        public void remove(int i) {
                children.remove(i);
        }

        @Override
        public void remove(MutableTreeNode mtn) {
                int childIndex = getIndex(mtn);
                remove(childIndex);
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
        public Enumeration<MutableTreeNode> children() {
                return new EnumIterator<MutableTreeNode>(children.iterator());
        }
        
}

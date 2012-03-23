/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer,
Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.indexes.btree;

import java.io.IOException;
import org.gdms.data.indexes.tree.TreeNode;

import org.gdms.data.values.Value;

/**
 * Realizations of this interface represent a node in a BTree.
 */
public interface BTreeNode extends TreeNode<Value, BTreeNode> {

        /**
         * Gets the smallest value in the subtree represented by this node and its
         * children that is not present in the subtree represented by the 'treeNode'
         * node and its children
         *
         * @param treeNode
         * @return
         * @throws IOException
         */
        Value getSmallestValueNotIn(BTreeNode treeNode) throws IOException;

        /**
         * Get the leaf under this node that contains the smallest values
         *
         * @return
         * @throws IOException
         */
        BTreeLeaf getFirstLeaf() throws IOException;

        Value getSmallestValue() throws IOException;

        boolean contains(Value value) throws IOException;

        int[] getIndex(RangeComparator minComparator,
			RangeComparator maxComparator) throws IOException;
}

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
package org.orbisgis.sif.components.resourceTree;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Iterator;

/**
 * Easy way to iterate over selected TreeNode.
 * @author Nicolas Fortin
 */
public class TreeSelectionIterable<Node extends TreeNode> implements Iterable<Node> {
    private TreePath[] selected;
    private Class<Node> nodeClass;
    /**
     * Constructor
     * @param selected tree.getSelectedPaths()
     * @param nodeClass Node.class
     */
    public TreeSelectionIterable(final TreePath[] selected, Class<Node> nodeClass) {
        if(selected==null) {
            this.selected = new TreePath[0];
        } else {
            this.selected = selected.clone();
        }
        this.nodeClass = nodeClass;
    }

    @Override
    public Iterator<Node> iterator() {
        return new TreeIterator<Node>(selected,nodeClass);
    }

    private static class TreeIterator<Node extends TreeNode> implements Iterator<Node> {
        private TreePath[] selected;
        private int index=-1;
        private Class<Node> nodeClass;

        private TreeIterator(final TreePath[] selected, Class<Node> nodeClass) {
            this.selected = selected;
            this.nodeClass = nodeClass;
        }

        @Override
        public boolean hasNext() {
            final int oldIndex = index;
            boolean hasNext = next()!=null;
            index = oldIndex;
            return hasNext;
        }

        @Override
        public Node next() {
            index++;
            if(index >= selected.length) {
                return null;
            }
            Object node = selected[index].getLastPathComponent();
            while(node!=null && !(nodeClass.isInstance(node))) {
                index++;
                if(index < selected.length) {
                    node = selected[index].getLastPathComponent();
                } else {
                    node = null;
                }
            }
            if(node != null) {
                return nodeClass.cast(node);
            } else {
                return null;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported");
        }

    }
}

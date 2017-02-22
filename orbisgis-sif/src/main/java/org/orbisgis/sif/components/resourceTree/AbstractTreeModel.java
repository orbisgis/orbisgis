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

import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public abstract class AbstractTreeModel implements TreeModel {


	public static boolean contains(TreePath[] selectionPaths, TreePath path) {
		for (TreePath treePath : selectionPaths) {
			boolean equals = true;
			Object[] objectPath = treePath.getPath();
			Object[] testPath = path.getPath();
			if (objectPath.length != testPath.length) {
				equals = false;
			} else {
				for (int i = 0; i < testPath.length; i++) {
					if (!(testPath[i].equals(objectPath[i]))) {
						equals = false;
					}
				}
			}
			if (equals) {
				return true;
			}
		}

		return false;
	}

	private ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();

	private JTree tree;

	public AbstractTreeModel(JTree tree) {
		this.tree = tree;
	}

        @Override
	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.add(l);
	}

        protected void fireStructureChanged(TreeModelEvent e){
		TreePath[] selection = tree.getSelectionPaths();
		for (TreeModelListener tml : treeModelListeners){
			tml.treeStructureChanged(e);
		}
		if (selection != null) {
			tree.addSelectionPaths(selection);
		}
        }

        protected void fireNodeInserted(TreeModelEvent e){
		TreePath[] selection = tree.getSelectionPaths();
		for (TreeModelListener tml : treeModelListeners){
			tml.treeNodesInserted(e);
		}
		if (selection != null) {
			tree.addSelectionPaths(selection);
		}
        }

        protected void fireNodeRemoved(TreeModelEvent e){
		TreePath[] selection = tree.getSelectionPaths();
		for (TreeModelListener tml : treeModelListeners){
			tml.treeNodesRemoved(e);
		}
		if (selection != null) {
			tree.addSelectionPaths(selection);
		}
        }

	protected void fireEvent() {
		TreePath root = new TreePath(getRoot());
		Enumeration<TreePath> paths = tree.getExpandedDescendants(root);
		TreePath[] selection = tree.getSelectionPaths();
		TreeModelEvent e = new TreeModelEvent(this, root);
		for (TreeModelListener tml : treeModelListeners){
			tml.treeStructureChanged(e);
		}
		if (paths != null) {
			while (paths.hasMoreElements()) {
				tree.expandPath(paths.nextElement());
			}
		}
		if (selection != null) {
			tree.addSelectionPaths(selection);
		}
	}

        @Override
	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.remove(l);
	}
}

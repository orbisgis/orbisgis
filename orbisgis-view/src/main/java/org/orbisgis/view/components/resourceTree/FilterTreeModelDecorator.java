/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */

package org.orbisgis.view.components.resourceTree;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * This class is used to refresh a treemodel according the name of a leaf .
 * 
 * @author Erwan Bocher
 */
public class FilterTreeModelDecorator extends AbstractTreeModel {

	private TreeModel model;
	private String filterText = "";
	protected JTree tree;

	public FilterTreeModelDecorator(TreeModel model, JTree tree) {
		super(tree);
		this.model = model;
		this.tree = tree;
	}

	@Override
	public Object getChild(Object parent, int index) {
		int count = 0;
		for (int i = 0; i < model.getChildCount(parent); i++) {
			Object child = model.getChild(parent, i);
			if (isFiltered(child)) {
				if (count == index) {
					return child;
				}
				count++;
			}
		}

		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		int count = 0;
		for (int i = 0; i < model.getChildCount(parent); i++) {
			if (isFiltered(model.getChild(parent, i))) {
				count++;
			}
		}

		return count;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		int index = 0;
		for (int i = 0; i < model.getChildCount(parent); i++) {
			Object nodeChild = model.getChild(parent, i);
			if (isFiltered(nodeChild)) {
				if (nodeChild == child) {
					return index;
				}
				index++;
			}
		}
		return -1;
	}

	@Override
	public Object getRoot() {
		return model.getRoot();
	}

	@Override
	public boolean isLeaf(Object node) {
		return model.isLeaf(node);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		model.valueForPathChanged(path, newValue);
	}

        /**
         * Return true if the element leaf has been filtered.
         * @param element
         * @return 
         */
	protected boolean isFiltered(Object element) {
		if (isFiltered()) {
			if (!isLeaf(element)) {
				return getChildCount(element) > 0;
			} else {
				if (element.toString().toLowerCase().indexOf(filterText) == -1) {
					return false;
				} else {
					return true;
				}
			}
		} else {
			return true;
		}
	}

        /**
         * This method refresh the tree based on the names of the leaf.
         * @param text 
         */
	public void filter(String text) {
		this.filterText = text.toLowerCase();

		TreePath[] selPaths = tree.getSelectionPaths();
		if (selPaths != null) {
			for (int i = 0; i < selPaths.length; i++) {
				if (!isFiltered(selPaths[i].getLastPathComponent())) {
					tree.removeSelectionPath(selPaths[i]);
				}
			}
		}

		fireEvent();

		if (!isFiltered()) {
			for (int i = 0; i < tree.getRowCount(); i++) {
				tree.collapseRow(i);
			}
		} else {
			for (int i = 0; i < tree.getRowCount(); i++) {
				tree.expandRow(i);
			}
		}
	}

        /**
         * Return true if the treemodel has been filtered.
         * @return 
         */      
	protected boolean isFiltered() {
		return !filterText.trim().isEmpty();
	}
}

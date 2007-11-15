package org.orbisgis.core.resourceTree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public abstract class AbstractTreeModel implements TreeModel {

	private ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();

	private JTree tree;

	public AbstractTreeModel(JTree tree) {
		this.tree = tree;
	}

	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.add(l);
	}

	protected void fireEvent(TreePath treePath) {
		TreePath root = new TreePath(getRoot());
		Enumeration<TreePath> paths = tree.getExpandedDescendants(root);
		for (Iterator<TreeModelListener> iterator = treeModelListeners
				.iterator(); iterator.hasNext();) {
			iterator.next()
					.treeStructureChanged(new TreeModelEvent(this, root));
		}
		if (paths != null) {
			while (paths.hasMoreElements()) {
				tree.expandPath(paths.nextElement());
			}
		}
	}

	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.remove(l);
	}
}

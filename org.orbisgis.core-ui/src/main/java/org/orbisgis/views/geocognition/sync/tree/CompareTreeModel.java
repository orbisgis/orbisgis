package org.orbisgis.views.geocognition.sync.tree;

import java.util.ArrayList;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.orbisgis.Services;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.views.geocognition.sync.SyncManager;

public class CompareTreeModel implements TreeModel {
	private SyncManager syncManager;
	private ArrayList<TreeModelListener> listenerList = new ArrayList<TreeModelListener>();

	/**
	 * Sets the synchronization manager
	 * 
	 * @param syncManager
	 *            the synchronization manager to set
	 */
	void setSyncManager(SyncManager syncManager) {
		this.syncManager = syncManager;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(l);
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent instanceof TreeElement) {
			TreeElement n = (TreeElement) parent;
			return n.getElement(index);
		} else {
			return null;
		}
	}

	@Override
	public int getChildCount(Object node) {
		if (node instanceof TreeElement) {
			TreeElement n = (TreeElement) node;
			return n.getElementCount();
		} else {
			return 0;
		}
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent instanceof TreeElement) {
			TreeElement parentNode = (TreeElement) parent;
			TreeElement childNode = (TreeElement) child;

			for (int i = 0; i < parentNode.getElementCount(); i++) {
				String childId = parentNode.getElement(i).getId();
				if (childId.equalsIgnoreCase(childNode.getId())) {
					return i;
				}
			}

			return -1;
		} else {
			return -1;
		}
	}

	@Override
	public Object getRoot() {
		TreeElement diff = syncManager.getDifferenceTree();
		Geocognition gc = Services.getService(Geocognition.class);
		String comparingNode = syncManager.getLocalRoot().getIdPath();
		if (comparingNode.equals(gc.getRoot().getIdPath())) {
			comparingNode = "the geocognition";
		} else {
			comparingNode = "'" + comparingNode + "'";
		}
		return (diff != null) ? diff : "No changes in " + comparingNode;
	}

	@Override
	public boolean isLeaf(Object node) {
		if (node instanceof TreeElement) {
			TreeElement n = (TreeElement) node;

			return !n.isFolder() || (n.isFolder() && n.getElementCount() == 0);
		} else {
			return true;
		}
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(l);
	}

	@Override
	public void valueForPathChanged(TreePath treePath, Object newValue) {
		// do nothing
	}

	/**
	 * Calls the <code>treeStructureChanged</code> method in all tree model
	 * listeners
	 */
	void fireTreeStructureChanged() {
		for (TreeModelListener listener : listenerList) {
			listener.treeStructureChanged(new TreeModelEvent(this,
					new TreePath(getRoot())));
		}
	}

}
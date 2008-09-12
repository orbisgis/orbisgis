package org.orbisgis.views.geocognition.sync.tree;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.orbisgis.Services;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.views.geocognition.sync.IdPath;
import org.orbisgis.views.geocognition.sync.SyncListener;
import org.orbisgis.views.geocognition.sync.SyncManager;

public class CompareTreeModel implements TreeModel {

	private SyncManager syncManager;
	private SyncListener syncListener;
	private ArrayList<TreeModelListener> listenerList = new ArrayList<TreeModelListener>();
	private JTree tree;

	/**
	 * Creates a new ComparerTreeModel
	 * 
	 * @param nc
	 *            the comparer between two trees
	 */
	CompareTreeModel() {
		syncListener = new SyncListener() {
			@Override
			public void syncDone() {
				refresh();
			}
		};
	}

	void setModel(SyncManager sm, JTree t) {
		if (syncManager != null) {
			syncManager.removeSyncListener(syncListener);
		}

		if (sm == null) {
			Services.getErrorManager().error(
					"bug!",
					new IllegalArgumentException(
							"The synchronization manager cannot be null"));
		}

		syncManager = sm;
		syncManager.addSyncListener(syncListener);
		tree = t;
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
			return !n.isFolder();
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
	}

	/**
	 * Refreshes the tree
	 */
	private void refresh() {
		// Preserve tree expansions
		Enumeration<TreePath> expanded = null;
		expanded = tree.getExpandedDescendants(tree.getPathForRow(0));

		for (TreeModelListener listener : listenerList) {
			listener.treeStructureChanged(new TreeModelEvent(this,
					new TreePath(getRoot())));
		}

		// Recover tree expansion
		if (expanded != null && syncManager.getDifferenceTree() != null) {
			while (expanded.hasMoreElements()) {
				TreePath path = expanded.nextElement();
				IdPath idPath = new IdPath();
				ArrayList<TreeElement> treePath = new ArrayList<TreeElement>();
				for (int i = 0; i < path.getPathCount(); i++) {
					TreeElement element = (TreeElement) path.getPath()[i];
					idPath.addLast(element.getId());
					treePath.add(syncManager.getDifferenceTree().find(idPath));
				}

				tree.expandPath(new TreePath(treePath.toArray()));
			}
		}
	}
}
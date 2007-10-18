//SAM : COMPLETE, needs some testing (see TODO's)
package org.orbisgis.geocatalog;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.orbisgis.geocatalog.resources.IResource;

public class CatalogModel implements TreeModel {

	private IResource rootNode;

	private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

	public CatalogModel(IResource rootNode) {
		this.rootNode = rootNode;
	}

	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.addElement(l);
	}

	public boolean existNode(IResource node) {
		// TODO : this function needs to be tested...
		// maybe we should use getIndexOfChild()...
		boolean ok = false;
		for (IResource myNode : rootNode.depthChildList()) {
			if (myNode == node) {
				ok = true;
				break;
			}
		}
		return ok;
	}

	public Object getChild(Object parent, int index) {
		IResource p = (IResource) parent;
		return p.getChildAt(index);
	}

	public int getChildCount(Object parent) {
		IResource p = (IResource) parent;
		return p.getChildCount();
	}

	public int getIndexOfChild(Object parent, Object child) {
		IResource p = (IResource) parent;
		return p.getIndexOfChild((IResource) child);
	}

	public IResource getRoot() {
		return rootNode;
	}

	public void insertNodeInto(IResource child, IResource parent, int index) {
		parent.addChild(child, index);
	}

	public boolean isLeaf(Object node) {
		IResource p = (IResource) node;
		return p.getChildCount() == 0;
	}

	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.removeElement(l);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO
	}

	public void removeNodeFromParent(IResource toDelete,
			boolean fireTreeNodesRemoved) {
		IResource father = toDelete.getParent();
		if (fireTreeNodesRemoved) {
			fireTreeNodesRemoved(toDelete);
		}
		father.removeChild(toDelete);
	}

	public void removeAllNodes() {
		while (rootNode.getChildCount() != 0) {
			removeNodeFromParent(rootNode.getChildAt(0), true);
		}
	}

	/**
	 * This will return a tree event containing *ALL* nodes removed, including
	 * subNodes (of a folder for example)
	 *
	 * @param removedNode
	 */
	private void fireTreeNodesRemoved(IResource removedNode) {
		// TODO : implement handling of childIndices[] (not really useful to me)
		IResource parent = removedNode.getParent();
		ArrayList<IResource> childList = removedNode.depthChildList();

		// Don't remove root node !
		if (removedNode != rootNode) {
			childList.add(removedNode);
		}

		int[] childIndices = new int[1];
		childIndices[0] = 0;
		/*
		 * This is a try to implement childIndices[]. SAM : It raises an
		 * exception i can't solve int index = childList.size(); for (int i = 0;
		 * i < index; i++) { MyNode child = childList.get(i); MyNode father =
		 * child.getParent(); int indice = father.getIndexOfChild(child);
		 * childIndices[i] = indice; System.out.println(father+" father of
		 * "+child+" at position "+indice); }
		 */
		TreeModelEvent e = new TreeModelEvent(this, parent.getPath(),
				childIndices, childList.toArray(new IResource[0]));

		for (TreeModelListener tml : treeModelListeners) {
			tml.treeNodesRemoved(e);
		}
	}

	public void setRootNode(IResource root) {
		rootNode = root;
	}

}

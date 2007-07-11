package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class CatalogModel implements TreeModel {
	private MyNode rootNode;

	private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

	public CatalogModel(MyNode rootNode) {
		this.rootNode = rootNode;
	}

	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.addElement(l);
	}

	public boolean existNode(MyNode node) {
		boolean ok = false;
		for (MyNode myNode : rootNode.depthChildList()) {
			if (myNode.equals(node)) {
				ok = true;
				break;
			}
		}
		return ok;
	}

	public Object getChild(Object parent, int index) {
		MyNode p = (MyNode) parent;
		return p.getChildAt(index);
	}

	public int getChildCount(Object parent) {
		MyNode p = (MyNode) parent;
		return p.getChildCount();
	}

	public int getIndexOfChild(Object parent, Object child) {
		MyNode p = (MyNode) parent;
		return p.getIndexOfChild((MyNode) child);
	}

	public MyNode getRoot() {
		return rootNode;
	}

	public void insertNodeInto(MyNode child, MyNode parent, int index) {
		parent.add(child, index);
	}

	public boolean isLeaf(Object node) {
		MyNode p = (MyNode) node;
		return p.getChildCount() == 0;
	}

	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.removeElement(l);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO
	}

	public void removeNodeFromParent(MyNode toDelete,
			boolean fireTreeNodesRemoved) {
		MyNode father = toDelete.getParent();
		if (fireTreeNodesRemoved) {
			fireTreeNodesRemoved(toDelete);
		}
		father.remove(toDelete);
	}

	public void removeAllNodes() {
		while (rootNode.haveChildren()) {
			removeNodeFromParent(rootNode.getChildAt(0), true);
		}
	}

	/**
	 * This will return a tree event containing *ALL* nodes removed, including
	 * subNodes (of a folder for example)
	 * 
	 * @param removedNode
	 */
	private void fireTreeNodesRemoved(MyNode removedNode) {
		// TODO : implement handling of childIndices[] (not really useful to me)
		MyNode parent = removedNode.getParent();
		ArrayList<MyNode> childList = removedNode.depthChildList();

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
				childIndices, childList.toArray(new MyNode[0]));

		for (TreeModelListener tml : treeModelListeners) {
			tml.treeNodesRemoved(e);
		}
	}

}

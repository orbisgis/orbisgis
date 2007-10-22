package org.orbisgis.geocatalog;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.orbisgis.geocatalog.resources.IResource;

public class CatalogModel implements TreeModel {

	private IResource rootNode;

	private ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();

	public CatalogModel(IResource rootNode) {
		this.rootNode = rootNode;
	}

	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.add(l);
	}

	public boolean existNode(IResource node) {
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

	public void insertNodeInto(IResource child, IResource parent) {
		parent.addChild(child);
		fireEvent();
	}

	public void insertNode(IResource child) {
		insertNodeInto(child, rootNode);
	}

	private void fireEvent() {
		for (Iterator<TreeModelListener> iterator = treeModelListeners
				.iterator(); iterator.hasNext();) {
			iterator.next().treeStructureChanged(
					new TreeModelEvent(this, new TreePath(rootNode)));
		}
	}

	public boolean isLeaf(Object node) {
		IResource p = (IResource) node;
		return p.getChildCount() == 0;
	}

	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.remove(l);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	public void removeNode(IResource resource) {
		this.removeNode(resource, true);
	}

	public void removeNode(IResource toDelete,
			boolean fireTreeNodesRemoved) {
		IResource father = toDelete.getParent();
		if (fireTreeNodesRemoved) {
			fireEvent();
		}
		father.removeChild(toDelete);
		fireEvent();
	}

	public void removeAllNodes() {
		while (rootNode.getChildCount() != 0) {
			removeNode(rootNode.getChildAt(0), true);
		}

		fireEvent();
	}

	public IResource[] getNodes(NodeFilter nodeFilter) {
		return getNodes(nodeFilter, rootNode).toArray(new IResource[0]);
	}

	private ArrayList<IResource> getNodes(NodeFilter nodeFilter, IResource node) {
		ArrayList<IResource> ret = new ArrayList<IResource>();
		IResource[] childs = node.getChildren();
		for (int i = 0; i < childs.length; i++) {
			if (nodeFilter.accept(childs[i])) {
				ret.add(childs[i]);
			}
			ret.addAll(getNodes(nodeFilter, childs[i]));
		}

		return ret;
	}
}

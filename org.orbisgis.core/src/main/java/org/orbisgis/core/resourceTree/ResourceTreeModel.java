package org.orbisgis.core.resourceTree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class ResourceTreeModel implements TreeModel {

	private IResource rootNode;

	private ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();

	private JTree tree;

	public ResourceTreeModel(JTree tree, IResource rootNode) {
		this.rootNode = rootNode;
		this.tree = tree;
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
		child.addTo(parent);
		fireEvent(getPath(parent));
	}

	private TreePath getPath(IResource child) {
		ArrayList<IResource> reversePath = new ArrayList<IResource>();
		while (child != null) {
			reversePath.add(child);
			child = child.getParent();
		}

		IResource[] path = new IResource[reversePath.size()];
		for (int i = 0; i < path.length; i++) {
			path[i] = reversePath.get(path.length - i - 1);
		}
		return new TreePath(path);
	}

	public void insertNode(IResource child) {
		insertNodeInto(child, rootNode);
	}

	private void fireEvent(TreePath treePath) {
		TreePath root = new TreePath(getRoot());
		Enumeration<TreePath> paths = tree.getExpandedDescendants(root);
		for (Iterator<TreeModelListener> iterator = treeModelListeners
				.iterator(); iterator.hasNext();) {
			iterator.next().treeStructureChanged(
					new TreeModelEvent(this, root));
		}
		if (paths != null) {
			while (paths.hasMoreElements()) {
				tree.expandPath(paths.nextElement());
			}
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

	public void removeNode(IResource toDelete, boolean fireTreeNodesRemoved) {
		IResource parent = toDelete.getParent();
		toDelete.removeFrom(parent);
		if (fireTreeNodesRemoved) {
			fireEvent(getPath(parent));
		}
	}

	public void removeAllNodes() {
		while (rootNode.getChildCount() != 0) {
			removeNode(rootNode.getChildAt(0), false);
		}

		fireEvent(getPath(rootNode));
	}

	public IResource[] getNodes(NodeFilter nodeFilter) {
		return getNodes(nodeFilter, rootNode).toArray(new IResource[0]);
	}

	private ArrayList<IResource> getNodes(NodeFilter nodeFilter, IResource node) {
		ArrayList<IResource> ret = new ArrayList<IResource>();
		if (nodeFilter.accept(node)) {
			ret.add(node);
		}
		IResource[] childs = node.getChildren();
		for (int i = 0; i < childs.length; i++) {
			ret.addAll(getNodes(nodeFilter, childs[i]));
		}

		return ret;
	}

	public void setRootNode(IResource rootNode) {
		this.rootNode = rootNode;
		fireEvent(new TreePath(rootNode));
	}

	public void refresh(IResource resource) {
		fireEvent(new TreePath(resource.getPath()));
	}

	public void move(IResource resource, IResource dropNode) {
		IResource parentNode = resource.getParent();
		resource.move(dropNode);
		fireEvent(getPath(parentNode));
		fireEvent(getPath(dropNode));
	}
}

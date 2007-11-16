package org.orbisgis.core.resourceTree;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class ResourceTreeModel extends AbstractTreeModel implements TreeModel {

	private IResource rootNode;

	public ResourceTreeModel(JTree tree) {
		super(tree);
		init();
	}

	private void init() {
		rootNode = ResourceFactory.createResource("Root", new Folder(), this);
	}

	public ResourceTreeModel(ResourceTree rt) {
		super(rt.tree);
		init();
	}

	public boolean existNode(IResource node) {
		boolean ok = false;
		for (IResource myNode : rootNode.getResourcesRecursively()) {
			if (myNode == node) {
				ok = true;
				break;
			}
		}
		return ok;
	}

	public void clearCatalog() {
		IResource rootNode = getRoot();
		IResource[] rootChilds = rootNode.getResources();
		for (IResource treeResource : rootChilds) {
			try {
				rootNode.removeResource(treeResource);
			} catch (ResourceTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Object getChild(Object parent, int index) {
		IResource p = (IResource) parent;
		return p.getResourceAt(index);
	}

	public int getChildCount(Object parent) {
		IResource p = (IResource) parent;
		return p.getChildCount();
	}

	public int getIndexOfChild(Object parent, Object child) {
		IResource p = (IResource) parent;
		return p.getIndex((IResource) child);
	}

	public IResource getRoot() {
		return rootNode;
	}


	public boolean isLeaf(Object node) {
		IResource p = (IResource) node;
		return p.getChildCount() == 0;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	public IResource[] getNodes(NodeFilter nodeFilter) {
		return getNodes(nodeFilter, rootNode).toArray(new IResource[0]);
	}

	private ArrayList<IResource> getNodes(NodeFilter nodeFilter,
			IResource node) {
		ArrayList<IResource> ret = new ArrayList<IResource>();
		if (nodeFilter.accept(node)) {
			ret.add(node);
		}
		IResource[] childs = node.getResources();
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
		fireEvent(new TreePath(getRoot()));
	}

	public void resourceTypeProcess(boolean b) {
	}

}

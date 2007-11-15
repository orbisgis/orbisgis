package org.orbisgis.geoview.toc;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.orbisgis.core.resourceTree.AbstractTreeModel;
import org.orbisgis.geoview.layerModel.ILayer;

public class TocTreeModel extends AbstractTreeModel {

	private final ILayer root;

	public TocTreeModel(ILayer root, JTree tree) {
		super(tree);
		this.root = root;
	}

	public void refresh() {
		fireEvent(new TreePath(root));
	}

	public Object getChild(Object parent, int index) {
		ILayer l = (ILayer) parent;
		return l.getChildren()[index];
	}

	public int getChildCount(Object parent) {
		return ((ILayer) parent).getChildren().length;
	}

	public int getIndexOfChild(Object parent, Object child) {
		return ((ILayer) parent).getIndex((ILayer) child);
	}

	public Object getRoot() {
		return root;
	}

	public boolean isLeaf(Object node) {
		return ((ILayer) node).getChildren().length == 0;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {

	}

}

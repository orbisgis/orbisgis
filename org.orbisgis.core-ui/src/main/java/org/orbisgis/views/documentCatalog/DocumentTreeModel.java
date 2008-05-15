package org.orbisgis.views.documentCatalog;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.orbisgis.ui.resourceTree.AbstractTreeModel;

public class DocumentTreeModel extends AbstractTreeModel {

	private IDocument root;

	public DocumentTreeModel(JTree tree, IDocument root) {
		super(tree);
		this.root = root;
	}

	public Object getChild(Object parent, int index) {
		return ((IDocument) parent).getDocument(index);
	}

	public int getChildCount(Object parent) {
		return ((IDocument) parent).getDocumentCount();
	}

	public int getIndexOfChild(Object parent, Object child) {
		IDocument parentDoc = (IDocument) parent;
		for (int i = 0; i < parentDoc.getDocumentCount(); i++) {
			if (parentDoc.getDocument(i) == child) {
				return i;
			}
		}

		return -1;
	}

	public Object getRoot() {
		return root;
	}

	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {

	}

	public void refresh() {
		fireEvent(new TreePath(getRoot()));
	}
}

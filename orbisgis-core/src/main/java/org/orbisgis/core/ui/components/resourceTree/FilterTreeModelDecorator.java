package org.orbisgis.core.ui.components.resourceTree;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class FilterTreeModelDecorator extends AbstractTreeModel {

	private TreeModel model;
	private String filterText = "";
	protected JTree tree;

	public FilterTreeModelDecorator(TreeModel model, JTree tree) {
		super(tree);
		this.model = model;
		this.tree = tree;
	}

	@Override
	public Object getChild(Object parent, int index) {
		int count = 0;
		for (int i = 0; i < model.getChildCount(parent); i++) {
			Object child = model.getChild(parent, i);
			if (isFiltered(child)) {
				if (count == index) {
					return child;
				}
				count++;
			}
		}

		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		int count = 0;
		for (int i = 0; i < model.getChildCount(parent); i++) {
			if (isFiltered(model.getChild(parent, i))) {
				count++;
			}
		}

		return count;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		int index = 0;
		for (int i = 0; i < model.getChildCount(parent); i++) {
			Object nodeChild = model.getChild(parent, i);
			if (isFiltered(nodeChild)) {
				if (nodeChild == child) {
					return index;
				}
				index++;
			}
		}
		return -1;
	}

	@Override
	public Object getRoot() {
		return model.getRoot();
	}

	@Override
	public boolean isLeaf(Object node) {
		return model.isLeaf(node);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		model.valueForPathChanged(path, newValue);
	}

	protected boolean isFiltered(Object element) {
		if (isFiltered()) {
			if (!isLeaf(element)) {
				return getChildCount(element) > 0;
			} else {
				if (element.toString().toLowerCase().indexOf(filterText) == -1) {
					return false;
				} else {
					return true;
				}
			}
		} else {
			return true;
		}
	}

	public void filter(String text) {
		this.filterText = text.toLowerCase();

		TreePath[] selPaths = tree.getSelectionPaths();
		if (selPaths != null) {
			for (int i = 0; i < selPaths.length; i++) {
				if (!isFiltered(selPaths[i].getLastPathComponent())) {
					tree.removeSelectionPath(selPaths[i]);
				}
			}
		}

		fireEvent(null);

		if (!isFiltered()) {
			for (int i = 0; i < tree.getRowCount(); i++) {
				tree.collapseRow(i);
			}
		} else {
			for (int i = 0; i < tree.getRowCount(); i++) {
				tree.expandRow(i);
			}
		}
	}

	protected boolean isFiltered() {
		return !filterText.trim().equals("");
	}
}

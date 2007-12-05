package org.orbisgis.geoview.fromXmlToSQLTree;

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.xml.bind.JAXBException;

import org.orbisgis.core.resourceTree.AbstractTreeModel;
import org.orbisgis.persistence.Menu;
import org.orbisgis.persistence.MenuItem;

public class ToolsMenuPanelTreeModel extends AbstractTreeModel {
	private final Menu rootMenu;

	public ToolsMenuPanelTreeModel(final Menu rootMenu, final JTree tree)
			throws JAXBException {
		super(tree);
		this.rootMenu = rootMenu;
	}

	public Object getChild(Object parent, int index) {
		final Menu parentMenu = (Menu) parent;
		return parentMenu.getMenuOrMenuItem().get(index);
	}

	public int getChildCount(Object parent) {
		final Menu parentMenu = (Menu) parent;
		return parentMenu.getMenuOrMenuItem().size();
	}

	public int getIndexOfChild(Object parent, Object child) {
		final Menu parentMenu = (Menu) parent;
		return parentMenu.getMenuOrMenuItem().indexOf(child);
	}

	public Object getRoot() {
		return rootMenu;
	}

	public boolean isLeaf(Object node) {
		return node instanceof MenuItem;
	}

	public void refresh() {
		fireEvent(new TreePath(rootMenu));
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}
}
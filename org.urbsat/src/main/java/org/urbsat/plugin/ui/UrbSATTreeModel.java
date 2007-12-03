package org.urbsat.plugin.ui;

import java.net.URL;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.orbisgis.geoview.sqlConsole.ui.jaxb.Menu;
import org.orbisgis.geoview.sqlConsole.ui.jaxb.MenuItem;

public class UrbSATTreeModel implements TreeModel {
	private Menu rootMenu;

	public UrbSATTreeModel(final URL xmlFileUrl) throws JAXBException {
		rootMenu = (Menu) JAXBContext.newInstance(
				"org.orbisgis.geoview.sqlConsole.ui.jaxb",
				this.getClass().getClassLoader()).createUnmarshaller()
				.unmarshal(xmlFileUrl);
	}

	public void addTreeModelListener(TreeModelListener l) {
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

	public void removeTreeModelListener(TreeModelListener l) {
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}
}
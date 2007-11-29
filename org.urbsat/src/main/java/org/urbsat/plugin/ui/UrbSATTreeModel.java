package org.urbsat.plugin.ui;

import java.net.URL;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.urbsat.plugin.ui.jaxb.Menu;
import org.urbsat.plugin.ui.jaxb.MenuItem;

public class UrbSATTreeModel implements TreeModel {
	private Menu rootMenu;

	public UrbSATTreeModel(final URL xmlFileUrl) throws JAXBException {
		rootMenu = (Menu) JAXBContext.newInstance("org.urbsat.plugin.ui.jaxb",
				this.getClass().getClassLoader()).createUnmarshaller()
				.unmarshal(xmlFileUrl);

		for (Object item : rootMenu.getMenuOrMenuItem()) {
			System.out.println(((Menu) item).getLabel());
		}
	}

	public void addTreeModelListener(TreeModelListener l) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub

	}

	// public static void main(String[] args) throws JAXBException {
	// new UrbSATTreeModel(UrbSATTreeModel.class.getResource("urbsat.xml"));
	// }
}
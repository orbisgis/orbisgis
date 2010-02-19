package org.orbisgis.core.ui.pluginSystem.menu;

import java.util.ArrayList;

import javax.swing.JComponent;

public class MenuTree {

	private ArrayList<IMenu> unlinkedMenus = new ArrayList<IMenu>();

	private Menu root = new Menu(null, null, null, null, null, null,false);

	public void addMenu(Menu menu) {
		addMenu(menu, true);
	}

	private void addMenu(IMenu menu, boolean addUnlinked) {
		if (menu.getParent() == null) {
			root.addChild(menu);
		} else {
			if (menu.getParent().equals(menu.getId())) {
				throw new RuntimeException("Parent cannot be equal to id: "
						+ menu.getId());
			}
			IMenu parent = getNode(root, menu.getParent());
			if (parent == null) {
				if (addUnlinked) {
					unlinkedMenus.add(menu);
				}
			} else {
				unlinkedMenus.remove(menu);
				parent.addChild(menu);
			}
		}
	}

	private IMenu getNode(IMenu node, String parent) {
		if (parent.equals(node.getId())) {
			return node;
		} else {
			for (int i = 0; i < node.getChildren().length; i++) {
				IMenu ret = getNode(node.getChildren()[i], parent);
				if (ret != null) {
					return ret;
				}
			}

			return null;
		}
	}

	public JComponent[] getJMenus() {
		linkAllMenus();
		if (unlinkedMenus.isEmpty()) {
			root.groupMenus();
			IMenu[] childs = root.getChildren();
			ArrayList<JComponent> ret = new ArrayList<JComponent>();
			for (int i = 0; i < childs.length; i++) {
				JComponent menuItem = childs[i].getJMenuItem();
				if (menuItem != null) {
					ret.add(menuItem);
				}
			}

			return ret.toArray(new JComponent[0]);
		} else {
			throw new IllegalStateException("There are unlinked menus:"
					+ unlinkedMenus.get(0).getId() + ". Parent not found: "
					+ unlinkedMenus.get(0).getParent());
		}
	}
	
	public JComponent[] getJMenus(boolean selected) {
		linkAllMenus();
		if (unlinkedMenus.isEmpty()) {
			root.groupMenus();
			IMenu[] childs = root.getChildren();
			ArrayList<JComponent> ret = new ArrayList<JComponent>();
			for (int i = 0; i < childs.length; i++) {
				JComponent menuItem = childs[i].getJMenuItem();
				if (menuItem != null) {
					ret.add(menuItem);
				}
			}

			return ret.toArray(new JComponent[0]);
		} else {
			throw new IllegalStateException("There are unlinked menus:"
					+ unlinkedMenus.get(0).getId() + ". Parent not found: "
					+ unlinkedMenus.get(0).getParent());
		}
	}

	private void linkAllMenus() {
		int lastSize;
		do {
			lastSize = unlinkedMenus.size();
			for (int i = unlinkedMenus.size() - 1; i >= 0; i--) {
				addMenu(unlinkedMenus.get(i), false);
			}
		} while (lastSize != unlinkedMenus.size());
	}

	public void removeEmptyMenus() {
		linkAllMenus();
		removeEmptyMenus(root);
		root.groupMenus();
	}

	private void removeEmptyMenus(IMenu menu) {
		IMenu[] children = menu.getChildren();
		ArrayList<IMenu> toDelete = new ArrayList<IMenu>();
		for (IMenu childMenu : children) {
			removeEmptyMenus(childMenu);
			if ((childMenu.getChildren().length == 0)) {
				toDelete.add(childMenu);
			}
		}
		for (IMenu menuToDelete : toDelete) {
			menu.remove(menuToDelete);
		}
	}

	public IMenu getRoot() {
		linkAllMenus();
		return root;
	}
}

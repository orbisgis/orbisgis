package org.orbisgis.core;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;

public class MenuTree {
	private ArrayList<Menu> unlinkedMenus = new ArrayList<Menu>();

	private Menu root = new Menu(null, null, null, null);

	private ActionListener al;

	public MenuTree(ActionListener acl) {
		this.al = acl;
	}

	public void addMenu(Menu menu) {
		addMenu(menu, true);
	}

	private void addMenu(Menu menu, boolean addUnlinked) {
		if (menu.getParent() == null) {
			root.addChild(menu);
		} else {
			if (menu.getParent().equals(menu.getId())) {
				throw new RuntimeException("Parent cannot be equal to id: "
						+ menu.getId());
			}
			Menu parent = getNode(root, menu.getParent());
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

	private Menu getNode(Menu node, String parent) {
		if (parent.equals(node.getId())) {
			return node;
		} else {
			for (int i = 0; i < node.getChilds().length; i++) {
				Menu ret = getNode(node.getChilds()[i], parent);
				if (ret != null) {
					return ret;
				}
			}

			return null;
		}
	}

	public JMenuItem[] getJMenus() {
		int lastSize;
		do {
			lastSize = unlinkedMenus.size();
			for (int i = unlinkedMenus.size() - 1; i >= 0; i--) {
				addMenu(unlinkedMenus.get(i), false);
			}
		} while (lastSize != unlinkedMenus.size());
		if (unlinkedMenus.isEmpty()) {
			Menu[] childs = root.getChilds();
			JMenuItem[] ret = new JMenuItem[childs.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = childs[i].getJMenuItem(al);
			}

			return ret;
		} else {
			throw new IllegalStateException("There are unlinked menus:"
					+ unlinkedMenus.get(0).getId());
		}
	}
}

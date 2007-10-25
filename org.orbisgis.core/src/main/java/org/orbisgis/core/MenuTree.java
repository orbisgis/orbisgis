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
		if (menu.getParent() == null) {
			root.addChild(menu);
		} else {
			if (menu.getParent().equals(menu.getId())) {
				throw new RuntimeException("Parent cannot be equal to id: "
						+ menu.getId());
			}
			Menu parent = getNode(root, menu.getParent());
			if (parent == null) {
				unlinkedMenus.add(menu);
			} else {
				parent.addChild(menu);
			}
		}

		ArrayList<Menu> previousUnlinkedMenus = unlinkedMenus;
		unlinkedMenus = new ArrayList<Menu>();

		for (int i = 0; i < previousUnlinkedMenus.size(); i++) {
			addMenu(previousUnlinkedMenus.get(i));
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
		Menu[] childs = root.getChilds();
		JMenuItem[] ret = new JMenuItem[childs.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = childs[i].getJMenuItem(al);
		}

		return ret;
	}
}

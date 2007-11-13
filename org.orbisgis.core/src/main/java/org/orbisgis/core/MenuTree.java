package org.orbisgis.core;

import java.util.ArrayList;

import javax.swing.JComponent;

public class MenuTree {
	private ArrayList<IMenu> unlinkedMenus = new ArrayList<IMenu>();

	private Menu root = new Menu(null, null, null, null, null, null);

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
			for (int i = 0; i < node.getChilds().length; i++) {
				IMenu ret = getNode(node.getChilds()[i], parent);
				if (ret != null) {
					return ret;
				}
			}

			return null;
		}
	}

	public JComponent[] getJMenus() {
		int lastSize;
		do {
			lastSize = unlinkedMenus.size();
			for (int i = unlinkedMenus.size() - 1; i >= 0; i--) {
				addMenu(unlinkedMenus.get(i), false);
			}
		} while (lastSize != unlinkedMenus.size());
		if (unlinkedMenus.isEmpty()) {
			root.groupMenus();
			IMenu[] childs = root.getChilds();
			JComponent[] ret = new JComponent[childs.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = childs[i].getJMenuItem();
			}

			return ret;
		} else {
			throw new IllegalStateException("There are unlinked menus:"
					+ unlinkedMenus.get(0).getId());
		}
	}
}

/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
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

			return ret.toArray(new JComponent[ret.size()]);
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

			return ret.toArray(new JComponent[ret.size()]);
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

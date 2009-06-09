/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

public class Menu implements IMenu {

	private static final String DEFAULT_MENU_GROUP = "_default";

	private String parent;

	private String id;

	private String text;

	private ArrayList<IMenu> children = new ArrayList<IMenu>();

	private String icon;

	private HashMap<String, ArrayList<IMenu>> groups = new HashMap<String, ArrayList<IMenu>>();

	private String group;

	private IActionAdapter action;

	private boolean selectable;

	public Menu(String parent, String id, String group, String text,
			String icon, boolean selectable, IActionAdapter action) {
		super();
		this.parent = parent;
		this.id = id;
		this.text = text;
		this.icon = icon;
		this.group = group;
		this.action = action;
		this.selectable = selectable;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @param parentMenu
	 * @return
	 */
	public JComponent getJMenuItem() {
		JMenuItem ret;
		if (children.size() > 0) {
			ret = new JActionMenu(id, text);
			for (int i = 0; i < children.size(); i++) {
				ret.add(children.get(i).getJMenuItem());
			}
		} else if (action != null) {
			if (selectable) {
				ret = new JActionCheckBoxButtonMenuItem(text, group, id,
						(ISelectableActionAdapter) action);
			} else {
				ret = new JActionMenuItem(text, group, id, action);
			}
		} else {
			return null;
		}
		if (icon != null) {
			ret.setIcon(new ImageIcon(getClass().getResource(icon)));
		}

		ret.setName(id);
		return ret;
	}

	public void addChild(IMenu menu) {
		children.add(menu);

		String menuGroup = menu.getGroup();
		if (menuGroup == null) {
			menuGroup = DEFAULT_MENU_GROUP;
		}
		ArrayList<IMenu> menusInGroup = groups.get(menuGroup);
		if (menusInGroup == null) {
			menusInGroup = new ArrayList<IMenu>();
		}
		menusInGroup.add(menu);
		groups.put(menuGroup, menusInGroup);
	}

	public String getGroup() {
		return group;
	}

	public IMenu[] getChildren() {
		return children.toArray(new IMenu[0]);
	}

	public void groupMenus() {
		ArrayList<IMenu> newChilds = new ArrayList<IMenu>();
		Iterator<String> it = groups.keySet().iterator();
		boolean separator = false;
		while (it.hasNext()) {
			String group = it.next();
			ArrayList<IMenu> menusInGroup = groups.get(group);
			if (menusInGroup.size() > 0) {
				if (separator) {
					newChilds.add(new MenuSeparator());
				}
				separator = true;
			}
			for (IMenu menu : menusInGroup) {
				newChilds.add(menu);
				menu.groupMenus();
			}
		}

		children = newChilds;
	}

	public void remove(IMenu menuToDelete) {
		children.remove(menuToDelete);
		String menuGroup = menuToDelete.getGroup();
		if (menuGroup == null) {
			menuGroup = DEFAULT_MENU_GROUP;
		}
		groups.get(menuGroup).remove(menuToDelete);
	}

	public boolean hasAction() {
		return action != null;
	}

	@Override
	public String toString() {
		return getText();
	}
}

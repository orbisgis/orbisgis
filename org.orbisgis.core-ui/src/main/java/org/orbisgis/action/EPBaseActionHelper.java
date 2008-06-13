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
package org.orbisgis.action;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;
import org.orbisgis.pluginManager.Configuration;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;

public class EPBaseActionHelper {

	private static final Logger logger = Logger.getLogger(EPBaseActionHelper.class);

	/**
	 * Reads the configuration for any extension to any of the specified
	 * extension points and populates the menu and tool bar structures with it.
	 * All of the specified extension points must have an associated schema
	 * similar to the one in action.xsd. This method doesn't populate the leaves
	 * in the menu tree nor the buttons in the tool bar. It only creates the
	 * needed parents
	 *
	 * @param extensionPointIDs
	 * @param menuTree
	 * @param toolBarArray
	 */
	public static void configureParentMenusAndToolBars(
			String[] extensionPointIDs, MenuTree menuTree,
			ToolBarArray toolBarArray) {

		IExtensionRegistry reg = RegistryFactory.getRegistry();
		for (String extensionPointID : extensionPointIDs) {
			Extension[] exts = reg.getExtensions(extensionPointID);
			for (int j = 0; j < exts.length; j++) {
				Configuration c = exts[j].getConfiguration();
				int n = c.evalInt("count(/extension/menu)");
				for (int i = 0; i < n; i++) {
					String base = "/extension/menu[" + (i + 1) + "]";
					String parent = c.getAttribute(base, "parent");
					String id = c.getAttribute(base, "id");
					String group = c.getAttribute(base, "menu-group");
					String text = c.getAttribute(base, "text");
					String icon = c.getAttribute(base, "icon");
					Menu m = new Menu(parent, id, group, text, icon, false,
							null);
					menuTree.addMenu(m);
				}
			}
			for (int j = 0; j < exts.length; j++) {
				Configuration c = exts[j].getConfiguration();
				int n = c.evalInt("count(/extension/toolbar)");
				for (int i = 0; i < n; i++) {
					String base = "/extension/toolbar[" + (i + 1) + "]";
					String id = c.getAttribute(base, "id");
					String text = c.getAttribute(base, "text");
					toolBarArray.put(id, new JToolBar(text));
				}
			}
		}
	}

	/**
	 * Populates the specified menu and tool bar structures with the actions
	 * that will fire the extension code.
	 *
	 * @param extensionPointID
	 *            Extension point which extensions will be processed to add the
	 *            actions
	 * @param actionFactory
	 *            Factory that instantiates an action that will manage the menu
	 *            leave or button in the tool bar
	 * @param menuTree
	 * @param toolBarArray
	 */
	public static void configureMenuAndToolBar(String extensionPointID,
			String actionName, IActionFactory actionFactory, MenuTree menuTree,
			ToolBarArray toolBarArray) {
		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] exts = reg.getExtensions(extensionPointID);

		for (int j = 0; j < exts.length; j++) {
			Configuration c = exts[j].getConfiguration();
			int n = c.evalInt("count(/extension/" + actionName + ")");
			for (int i = 0; i < n; i++) {
				String base = "/extension/" + actionName + "[" + (i + 1) + "]";
				String id = c.getAttribute(base, "id");
				logger.debug("processing action: " + id);
				String menuId = c.getAttribute(base, "menu-id");
				String group = c.getAttribute(base, "menu-group");
				String text = c.getAttribute(base, "text");
				String icon = c.getAttribute(base, "icon");
				boolean selectable = c.getBooleanAttribute(base, "selectable");
				Object actionObject = c.instantiateFromAttribute(base, "class");
				IActionAdapter action;
				if (selectable) {
					action = actionFactory.getSelectableAction(actionObject, c
							.getAttributes(base));
				} else {
					action = actionFactory.getAction(actionObject, c
							.getAttributes(base));
				}
				Menu menu = new Menu(menuId, id, group, text, icon, selectable,
						action);
				if (menuId != null) {
					menuTree.addMenu(menu);
				}

				if (toolBarArray != null) {
					if (icon != null) {
						String toolBarId = c.getAttribute(base, "toolbar-id");
						if (toolBarId != null) {
							JToolBar toolBar = toolBarArray.get(toolBarId);
							if (toolBar == null) {
								throw new RuntimeException(
										"Cannot find toolbar: " + toolBarId
												+ ". Extension: "
												+ exts[j].getId());
							}
							AbstractButton btn;
							if (selectable) {
								btn = new JActionToggleButton(
										new ImageIcon(EPBaseActionHelper.class
												.getResource(icon)), false,
										(ISelectableActionAdapter) action);
							} else {
								btn = new JActionButton(
										new ImageIcon(EPBaseActionHelper.class
												.getResource(icon)), action);
							}
							toolBar.add(btn);
						}
					}
				}
			}
		}
	}

	/**
	 * Reads the configuration for any extension to the specified extension
	 * point and populates the menu and tool bar structures with it. The
	 * extension point associated schema must be similar to the one in
	 * action.xsd. This method doesn't populate the leaves in the menu tree nor
	 * the buttons in the tool bar. It only creates the needed parents
	 *
	 * @param epid
	 *            Extension point id
	 * @param menuTree
	 * @param toolBarArray
	 */
	public static void configureParentMenusAndToolBars(String epid,
			MenuTree menuTree, ToolBarArray toolBarArray) {
		configureParentMenusAndToolBars(new String[] { epid }, menuTree,
				toolBarArray);
	}
}

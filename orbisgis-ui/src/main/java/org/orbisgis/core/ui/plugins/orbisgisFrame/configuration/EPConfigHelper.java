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
package org.orbisgis.core.ui.plugins.orbisgisFrame.configuration;

import java.util.ArrayList;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.menu.IMenu;
import org.orbisgis.core.ui.pluginSystem.menu.Menu;
import org.orbisgis.core.ui.pluginSystem.menu.MenuTree;
import org.orbisgis.utils.I18N;

public class EPConfigHelper {
	private static ArrayList<ConfigurationDecorator> configs;

	/**
	 * Gets the installed configurations in the plugin.xml file
	 * 
	 * @return the installed configurations
	 */
	public static ArrayList<ConfigurationDecorator> getConfigurations() {
		if (configs == null) {
			configs = new ArrayList<ConfigurationDecorator>();
			configs.add(new ConfigurationDecorator(new ProxyConfiguration(),
					"org.orbisgis.core.ui.configurations.ProxyConfiguration",
					I18N.getString("orbisgis.org.orbisgis.configuration.proxy"),
					"org.orbisgis.core.ui.Updates"));
			configs.add(new ConfigurationDecorator(
					new RenderingConfiguration(),
					"org.orbisgis.core.ui.RenderingConfiguration",
					I18N.getString("orbisgis.org.orbisgis.configuration.rules"),
					"org.orbisgis.core.ui.RenderingConfiguration"));
			configs.add(new ConfigurationDecorator(
					new WorkspaceConfiguration(),
					"org.orbisgis.core.ui.WorkspaceConfiguration", "Workspace",
					"org.orbisgis.core.ui.WorkspaceConfiguration"));
		}

		return configs;
	}

	/**
	 * Gets the installed configuration menu
	 * 
	 * @return the installed configuration menu
	 */
	public static IMenu getConfigurationMenu() {
		MenuTree menuTree = new MenuTree();
		Menu m1 = new Menu(null, "org.orbisgis.core.ui.Updates", null, "WWW",
				null, null, false);
		menuTree.addMenu(m1);
		Menu m2 = new Menu(null, "org.orbisgis.core.ui.RenderingConfiguration",
				null,
				I18N.getString("orbisgis.org.orbisgis.configuration.rendering"),
				null, null, false);
		menuTree.addMenu(m2);
		Menu m3 = new Menu(null, "org.orbisgis.core.ui.WorkspaceConfiguration",
				null, "Workspace", null, null, false);
		menuTree.addMenu(m3);
		IMenu root = menuTree.getRoot();
		return root;
	}

	/**
	 * Determines if the given menu or any of his successors have the specified
	 * id
	 * 
	 * @param menu
	 *            the menu to scan
	 * @param id
	 *            the id to find
	 * @return true if the menu or its successors have the given id, false
	 *         otherwise
	 */
	private static boolean scanMenu(IMenu menu, String id) {
		String menuId = menu.getId();
		if (menuId != null && menuId.equals(id)) {
			return true;
		} else {
			IMenu[] children = menu.getChildren();
			for (int i = 0; i < children.length; i++) {
				if (scanMenu(children[i], id)) {
					return true;
				}
			}

			return false;
		}
	}

	/**
	 * Determines if the configurations are linked to a correct configuration
	 * group, loads and applies them
	 */
	public static void loadAndApplyConfigurations() {
		IMenu root = getConfigurationMenu();

		for (ConfigurationDecorator config : getConfigurations()) {
			if (!scanMenu(root, config.getParentId())) {
				Services
						.getErrorManager()
						.error(
								I18N
										.getString("orbisgis.org.orbisgis.configuration.group")
										+ " "
										+ config.getParentId()
										+ I18N
												.getString("orbisgis.org.orbisgis.for")
										+ " "
										+ config.getId()
										+ I18N
												.getString("orbisgis.org.orbisgis.doesNotExist"));
			} else {
				config.loadAndApply();
			}
		}
	}

	/**
	 * Saves the applied configurations
	 */
	public static void saveAppliedConfigurations() {
		for (ConfigurationDecorator config : getConfigurations()) {
			config.saveApplied();
		}
	}
}

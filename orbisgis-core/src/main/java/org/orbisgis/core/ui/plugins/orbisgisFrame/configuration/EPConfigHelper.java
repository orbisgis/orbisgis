package org.orbisgis.core.ui.plugins.orbisgisFrame.configuration;

import java.util.ArrayList;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.menu.IMenu;
import org.orbisgis.core.ui.pluginSystem.menu.Menu;
import org.orbisgis.core.ui.pluginSystem.menu.MenuTree;

public class EPConfigHelper {
	private static ArrayList<ConfigurationDecorator> configs;

	/**
	 * Gets the installed configurations in the plugin.xml file
	 * 
	 * @return the installed configurations
	 */
	// TODO (pyf) : mettre sous forme de plugin
	public static ArrayList<ConfigurationDecorator> getConfigurations() {
		if (configs == null) {
			configs = new ArrayList<ConfigurationDecorator>();
			configs
					.add(new ConfigurationDecorator(
							new ProxyConfiguration(),
							"org.orbisgis.core.ui.configurations.ProxyConfiguration",
							"Proxy", "org.orbisgis.core.ui.Updates"));
			configs.add(new ConfigurationDecorator(
					new RenderingConfiguration(),
					"org.orbisgis.core.ui.RenderingConfiguration",
					"Rules",
					"org.orbisgis.core.ui.RenderingConfiguration"));
			configs.add(new ConfigurationDecorator(
					new WorkspaceConfiguration(),
					"org.orbisgis.core.ui.WorkspaceConfiguration",
					"Workspace",
					"org.orbisgis.core.ui.WorkspaceConfiguration"));
		}

		return configs;
	}

	/**
	 * Gets the installed configuration menu
	 * 
	 * @return the installed configuration menu
	 */
	// TODO (pyf): mettre sous forme de plugin
	public static IMenu getConfigurationMenu() {
		MenuTree menuTree = new MenuTree();
		Menu m1 = new Menu(null, "org.orbisgis.core.ui.Updates", null,
				"WWW", null, null,false);
		menuTree.addMenu(m1);
		Menu m2 = new Menu(null,
				"org.orbisgis.core.ui.RenderingConfiguration", null,
				"Rendering", null, null,false);
		menuTree.addMenu(m2);
		Menu m3 = new Menu(null,
				"org.orbisgis.core.ui.WorkspaceConfiguration", null,
				"Workspace", null, null,false);
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
				Services.getErrorManager().error(
						"The configuration group " + config.getParentId()
								+ " for the configuration " + config.getId()
								+ " does not exist.\n");
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

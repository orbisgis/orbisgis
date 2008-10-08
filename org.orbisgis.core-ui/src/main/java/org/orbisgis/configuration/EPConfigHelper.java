package org.orbisgis.configuration;

import java.util.ArrayList;

import org.orbisgis.Services;
import org.orbisgis.action.EPBaseActionHelper;
import org.orbisgis.action.IMenu;
import org.orbisgis.action.MenuTree;
import org.orbisgis.action.ToolBarArray;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;

public class EPConfigHelper {
	private static ArrayList<ConfigurationDecorator> configs;

	/**
	 * Gets the installed configurations in the plugin.xml file
	 * 
	 * @return the installed configurations
	 */
	public static ArrayList<ConfigurationDecorator> getConfigurations() {
		if (configs == null) {
			ExtensionPointManager<IConfiguration> epm = new ExtensionPointManager<IConfiguration>(
					"org.orbisgis.Configuration");
			ArrayList<ItemAttributes<IConfiguration>> configItemAttributes;
			configItemAttributes = epm
					.getItemAttributes("/extension/configuration");
			configs = new ArrayList<ConfigurationDecorator>();
			for (ItemAttributes<IConfiguration> itemAttributes : configItemAttributes) {
				String id = itemAttributes.getAttribute("id");
				String text = itemAttributes.getAttribute("text");
				String menuParent = itemAttributes.getAttribute("group-id");
				IConfiguration config = itemAttributes.getInstance("class");
				configs.add(new ConfigurationDecorator(config, id, text,
						menuParent));
			}
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
		ToolBarArray foo = new ToolBarArray();
		EPBaseActionHelper.configureParentMenusAndToolBars(
				new String[] { "org.orbisgis.Configuration" }, "group",
				menuTree, foo);
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
		for (ConfigurationDecorator config : configs) {
			config.saveApplied();
		}
	}
}

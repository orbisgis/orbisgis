package org.orbisgis.core.resourceTree;

import javax.swing.tree.TreePath;

import org.orbisgis.core.actions.IAction;
import org.orbisgis.core.actions.IActionFactory;
import org.orbisgis.core.actions.Menu;
import org.orbisgis.core.actions.MenuTree;
import org.orbisgis.pluginManager.Configuration;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;

public class ResourceTreeActionExtensionPointHelper {

	public static void createPopup(MenuTree menuTree, IActionFactory factory,
			ResourceTree rt, String extensionPointID) {

		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] exts = reg.getExtensions(extensionPointID);
		for (int j = 0; j < exts.length; j++) {
			Configuration c = exts[j].getConfiguration();
			int n = c.evalInt("count(/extension/menu)");
			for (int i = 0; i < n; i++) {
				String base = "/extension/menu[" + (i + 1) + "]";
				String parent = c.getAttribute(base, "parent");
				String id = c.getAttribute(base, "id");
				String group = c.getAttribute(base, "menuGroup");
				String text = c.getAttribute(base, "text");
				String icon = c.getAttribute(base, "icon");
				Menu m = new Menu(parent, id, group, text, icon, null);
				menuTree.addMenu(m);
			}
		}
		for (int j = 0; j < exts.length; j++) {
			Configuration c = exts[j].getConfiguration();
			int n = c.evalInt("count(/extension/action)");
			for (int i = 0; i < n; i++) {
				String base = "/extension/action[" + (i + 1) + "]";
				String menuId = c.getAttribute(base, "menuId");
				String id = c.getAttribute(base, "id");
				String group = c.getAttribute(base, "menuGroup");
				String text = c.getAttribute(base, "text");
				String icon = c.getAttribute(base, "icon");
				Object action = c.instantiateFromAttribute(base, "class");
				IAction iAction = factory.getAction(action);
				TreePath[] res = rt.getSelection();
				if (res == null) {
					res = new TreePath[0];
				}
				if (iAction.isVisible()) {
					Menu menu = new Menu(menuId, id, group, text, icon, iAction);
					menuTree.addMenu(menu);
				}
			}
		}

	}
}

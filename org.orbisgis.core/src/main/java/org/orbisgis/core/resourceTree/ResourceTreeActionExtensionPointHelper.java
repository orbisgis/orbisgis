package org.orbisgis.core.resourceTree;

import java.awt.event.ActionListener;

import javax.swing.tree.TreePath;

import org.orbisgis.core.Menu;
import org.orbisgis.core.MenuTree;
import org.orbisgis.pluginManager.Configuration;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;

public class ResourceTreeActionExtensionPointHelper {

	public static void createPopup(MenuTree menuTree, ActionListener acl,
			ResourceTree rt, String extensionPointID,
			ResourceActionValidator val) {

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
				Menu m = new Menu(parent, id, group, text, icon, acl);
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

				TreePath[] res = rt.getSelection();
				if (res == null) {
					res = new TreePath[0];
				}
				if (val.acceptsSelection(action, res)) {
					Menu menu = new Menu(menuId, id, group, text, icon, acl);
					menuTree.addMenu(menu);
				}
			}
		}

	}
}

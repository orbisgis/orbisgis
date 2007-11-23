package org.orbisgis.core.actions;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;

import org.orbisgis.pluginManager.Configuration;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;

public class EPActionHelper {

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
					String group = c.getAttribute(base, "menuGroup");
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

	public static void configureMenuAndToolBar(String extensionPointID,
			IActionFactory actionFactory, MenuTree menuTree,
			ToolBarArray toolBarArray) {
		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] exts = reg.getExtensions(extensionPointID);

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
				boolean selectable = c.getBooleanAttribute(base, "selectable");
				Object actionObject = c.instantiateFromAttribute(base, "class");
				IAction action;
				if (selectable) {
					action = actionFactory.getSelectableAction(actionObject);
				} else {
					action = actionFactory.getAction(actionObject);
				}
				Menu menu = new Menu(menuId, id, group, text, icon, selectable,
						action);
				if (menuId != null) {
					menuTree.addMenu(menu);
				}

				if (toolBarArray != null) {
					if (icon != null) {
						String toolBarId = c.getAttribute(base, "toolbarId");
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
										new ImageIcon(EPActionHelper.class
												.getResource(icon)), false,
										(ISelectableAction) action);
							} else {
								btn = new JActionButton(
										new ImageIcon(EPActionHelper.class
												.getResource(icon)), action);
							}
							toolBar.add(btn);
						}
					}
				}
			}
		}
	}

	public static void configureParentMenusAndToolBars(String epid,
			MenuTree menuTree, ToolBarArray toolBarArray) {
		configureParentMenusAndToolBars(new String[] { epid }, menuTree,
				toolBarArray);
	}
}

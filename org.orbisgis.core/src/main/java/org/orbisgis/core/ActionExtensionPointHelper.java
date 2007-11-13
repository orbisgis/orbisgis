package org.orbisgis.core;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.orbisgis.pluginManager.Configuration;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;

public class ActionExtensionPointHelper {

	public static void configureMenuAndToolBar(String extensionPointID,
			ActionListener al, MenuTree menuTree, JToolBar parentToolBar) {

		HashMap<String, JToolBar> idToolBar = new HashMap<String, JToolBar>();
		ArrayList<String> orderedToolBarIds = new ArrayList<String>();

		HashMap<String, ButtonGroup> exclusiveGroups = new HashMap<String, ButtonGroup>();

		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] exts = reg.getExtensions(extensionPointID);
		for (int j = 0; j < exts.length; j++) {
			Configuration c = exts[j].getConfiguration();
			int n = c.evalInt("count(/extension/menu)");
			for (int i = 0; i < n; i++) {
				String base = "/extension/menu[" + (i + 1) + "]";
				String parent = c.getAttribute(base, "parent");
				String id = c.getAttribute(base, "id");
				String group = c.getAttribute(base, "group");
				String text = c.getAttribute(base, "text");
				String icon = c.getAttribute(base, "icon");
				Menu m = new Menu(parent, id, group, text, icon, al);
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
				idToolBar.put(id, new JToolBar(text));
				orderedToolBarIds.add(id);
			}
		}
		for (int j = 0; j < exts.length; j++) {
			Configuration c = exts[j].getConfiguration();
			int n = c.evalInt("count(/extension/action)");
			for (int i = 0; i < n; i++) {
				String base = "/extension/action[" + (i + 1) + "]";
				String menuId = c.getAttribute(base, "menuId");
				String id = c.getAttribute(base, "id");
				String group = c.getAttribute(base, "group");
				String text = c.getAttribute(base, "text");
				String icon = c.getAttribute(base, "icon");
				Menu menu = new Menu(menuId, id, group, text, icon, al);
				if (menuId != null) {
					menuTree.addMenu(menu);
				}

				if (icon != null) {
					String toolBarId = c.getAttribute(base, "toolbarId");
					if (toolBarId != null) {
						JToolBar toolBar = idToolBar.get(toolBarId);
						if (toolBar == null) {
							throw new RuntimeException("Cannot find toolbar: "
									+ toolBarId + ". Extension: "
									+ exts[j].getId());
						}
						AbstractButton btn;
						String exclusiveGroup = c.getAttribute(base,
								"exclusiveGroup");
						if (exclusiveGroup != null) {
							ButtonGroup bg = exclusiveGroups
									.get(exclusiveGroup);
							if (bg == null) {
								bg = new ButtonGroup();
								exclusiveGroups.put(exclusiveGroup, bg);
							}
							btn = new JToggleButton(new ImageIcon(
									ActionExtensionPointHelper.class
											.getResource(icon)), false);
							menu.setRelatedToggleButton((JToggleButton) btn);
							bg.add(btn);
						} else {
							btn = new JButton(new ImageIcon(
									ActionExtensionPointHelper.class
											.getResource(icon)));
						}
						btn.setActionCommand(id);
						btn.addActionListener(al);
						toolBar.add(btn);
					}
				}
			}
		}
		for (String toolBarId : orderedToolBarIds) {
			JToolBar toolbar = idToolBar.get(toolBarId);
			parentToolBar.add(toolbar);
		}
	}
}

package org.orbisgis.core.resourceTree;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.orbisgis.pluginManager.Configuration;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;

public class ResourceTreeActionExtensionPointHelper {

	public static JPopupMenu getPopup(ActionListener acl, ResourceTree rt, String epId, ResourceActionValidator val) {
		JPopupMenu popup = new JPopupMenu();

		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] exts = reg
				.getExtensions(epId);
		HashMap<String, ArrayList<JMenuItem>> groups = new HashMap<String, ArrayList<JMenuItem>>();
		ArrayList<String> orderedGroups = new ArrayList<String>();
		for (int i = 0; i < exts.length; i++) {
			Configuration c = exts[i].getConfiguration();

			int n = c.evalInt("count(/extension/action)");
			for (int j = 0; j < n; j++) {
				String base = "/extension/action[" + (j + 1) + "]";
				Object action =  c
						.instantiateFromAttribute(base, "class");

				IResource[] res = rt.getSelectedResources();
//				boolean acceptsAllResources = true;
//				if (action.acceptsSelectionCount(res.length)) {
//					for (IResource resource : res) {
//						if (!action.accepts(resource)) {
//							acceptsAllResources = false;
//							break;
//						}
//					}
//				}
				if (val.acceptsSelection(action, res)) {
					JMenuItem actionMenu = getMenuFrom(c, base, groups,
							orderedGroups, acl);
					popup.add(actionMenu);
				}
			}
		}

		for (int i = 0; i < orderedGroups.size(); i++) {
			ArrayList<JMenuItem> pops = groups.get(orderedGroups.get(i));
			for (int j = 0; j < pops.size(); j++) {
				popup.add(pops.get(j));
			}
			if (i != orderedGroups.size() - 1) {
				popup.addSeparator();
			}
		}

		return popup;
	}

	private static JMenuItem getMenuFrom(Configuration c, String baseXPath,
			HashMap<String, ArrayList<JMenuItem>> groups,
			ArrayList<String> orderedGroups, ActionListener acl) {
		String text = c.getAttribute(baseXPath, "text");
		String id = c.getAttribute(baseXPath, "id");
		String icon = c.getAttribute(baseXPath, "icon");
		JMenuItem menu = getMenu(text, id, icon, acl);

		String group = c.getAttribute(baseXPath, "group");
		ArrayList<JMenuItem> pops = groups.get(group);
		if (pops == null) {
			pops = new ArrayList<JMenuItem>();
		}
		pops.add(menu);
		groups.put(group, pops);
		if (!orderedGroups.contains(group)) {
			orderedGroups.add(group);
		}

		return menu;
	}

	private static JMenuItem getMenu(String text, String actionCommand,
			String iconURL, ActionListener acl) {
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.addActionListener(acl);
		menuItem.setActionCommand(actionCommand);

		if (iconURL != null) {
			Icon icon = new ImageIcon(
					ResourceTreeActionExtensionPointHelper.class
							.getResource(iconURL));
			menuItem.setIcon(icon);
		}

		return menuItem;
	}

}

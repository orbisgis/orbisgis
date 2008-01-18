/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
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
				Menu m = new Menu(parent, id, group, text, icon, false, null);
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
					Menu menu = new Menu(menuId, id, group, text, icon, false, iAction);
					menuTree.addMenu(menu);
				}
			}
		}

		menuTree.removeEmptyMenus();

	}
}

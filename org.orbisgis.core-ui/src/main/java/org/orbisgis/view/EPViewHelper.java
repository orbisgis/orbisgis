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
package org.orbisgis.view;

import java.util.ArrayList;
import java.util.HashMap;

import net.infonode.docking.RootWindow;

import org.orbisgis.action.IActionFactory;
import org.orbisgis.action.Menu;
import org.orbisgis.action.MenuTree;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;

public class EPViewHelper {
	private static String parentId = "org.orbisgis.views.View";

	public static ArrayList<ViewDecorator> getViewsInfo(
			String extensionPointId, String viewTag) {
		return getViewsInfo(extensionPointId, null, viewTag);
	}

	private static ArrayList<ViewDecorator> getViewsInfo(
			String extensionPointId, String askedId, String viewTag) {
		ExtensionPointManager<IView> epm = new ExtensionPointManager<IView>(
				extensionPointId);
		ArrayList<ItemAttributes<IView>> views;
		String baseTag = "/extension/" + viewTag;
		if (askedId != null) {
			views = epm.getItemAttributes(baseTag + "[@id='" + askedId + "']");
		} else {
			views = epm.getItemAttributes(baseTag);
		}
		ArrayList<ViewDecorator> ret = new ArrayList<ViewDecorator>();
		for (ItemAttributes<IView> itemAttributes : views) {
			String id = itemAttributes.getAttribute("id");
			String iconStr = itemAttributes.getAttribute("icon");
			String title = itemAttributes.getAttribute("title");
			String editor = itemAttributes.getAttribute("isEditor");
			IView view = itemAttributes.getInstance("class");
			boolean isEditor = (editor == null) ? false : Boolean
					.parseBoolean(editor);
			ArrayList<ItemAttributes<IView>> linkedEditors = epm
					.getItemAttributes(baseTag + "[@id='" + id + "']"
							+ "/editor");
			String[] editors = new String[linkedEditors.size()];
			for (int i = 0; i < linkedEditors.size(); i++) {
				editors[i] = linkedEditors.get(i).getAttribute("id");
			}

			ret.add(new ViewDecorator(view, id, title, iconStr, isEditor,
					editors));
		}

		return ret;
	}

	public static void addViewMenu(MenuTree menuTree, final RootWindow root,
			IActionFactory actionFactory, ArrayList<ViewDecorator> viewsInfo) {
		Menu menu = new Menu(null, parentId, null, "View",
				"/org/orbisgis/images/application.png", false, null);
		menuTree.addMenu(menu);
		for (final ViewDecorator info : viewsInfo) {

			menu = new Menu(parentId, info.getId(), null, info.getTitle(), info
					.getIcon(), true, actionFactory.getSelectableAction(info
					.getId(), new HashMap<String, String>()));
			menuTree.addMenu(menu);

		}
	}
}
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
package org.orbisgis.geoview;

import java.util.ArrayList;
import java.util.HashMap;

import net.infonode.docking.RootWindow;

import org.orbisgis.core.actions.IActionFactory;
import org.orbisgis.core.actions.Menu;
import org.orbisgis.core.actions.MenuTree;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;

public class EPViewHelper {
	private static String parentId = "org.orbisgis.geoview.Views";

	public static ArrayList<ViewDecorator> getViewsInfo(GeoView2D geoview) {
		return getViewsInfo(geoview, null);
	}

	public static ViewDecorator getViewInfo(GeoView2D geoview, String id) {
		return getViewsInfo(geoview, id).get(0);
	}

	private static ArrayList<ViewDecorator> getViewsInfo(GeoView2D geoview,
			String askedId) {
		ExtensionPointManager<IView> epm = new ExtensionPointManager<IView>(
				"org.orbisgis.geoview.View");
		ArrayList<ItemAttributes<IView>> views;
		if (askedId != null) {
			views = epm.getItemAttributes("/extension/view[@id='" + askedId
					+ "']");
		} else {
			views = epm.getItemAttributes("/extension/view");
		}
		ArrayList<ViewDecorator> ret = new ArrayList<ViewDecorator>();
		for (ItemAttributes<IView> itemAttributes : views) {
			String id = itemAttributes.getAttribute("id");
			String iconStr = itemAttributes.getAttribute("icon");
			String title = itemAttributes.getAttribute("title");
			IView view = itemAttributes.getInstance("class");

			ret.add(new ViewDecorator(view, id, title, iconStr, geoview));
		}

		return ret;
	}

	public static void addViewMenu(MenuTree menuTree, final RootWindow root,
			IActionFactory actionFactory, ArrayList<ViewDecorator> viewsInfo) {
		Menu menu = new Menu(null, parentId, null, "View", null, false, null);
		menuTree.addMenu(menu);
		for (final ViewDecorator info : viewsInfo) {

			menu = new Menu(parentId, info.getId(), null, info.getTitle(), info
					.getIcon(), true, actionFactory.getSelectableAction(info
					.getId(), new HashMap<String, String>()));
			menuTree.addMenu(menu);

		}
	}
}
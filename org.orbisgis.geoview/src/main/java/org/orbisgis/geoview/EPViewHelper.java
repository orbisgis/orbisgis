package org.orbisgis.geoview;

import java.util.ArrayList;

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
					.getId()));
			menuTree.addMenu(menu);

		}
	}
}
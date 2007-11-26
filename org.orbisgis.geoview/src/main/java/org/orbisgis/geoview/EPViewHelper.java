package org.orbisgis.geoview;

import java.util.ArrayList;

import net.infonode.docking.RootWindow;

import org.orbisgis.core.actions.ISelectableAction;
import org.orbisgis.core.actions.Menu;
import org.orbisgis.core.actions.MenuTree;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;

public class EPViewHelper {
	private static String parentId = "org.orbisgis.geoview.Views";

	public static ViewDecorator[] getViewsInfo(GeoView2D geoview) {
		ExtensionPointManager<IView> epm = new ExtensionPointManager<IView>(
				"org.orbisgis.geoview.View");
		ArrayList<ItemAttributes<IView>> views = epm
				.getItemAttributes("/extension/view");
		ArrayList<ViewDecorator> ret = new ArrayList<ViewDecorator>();
		for (ItemAttributes<IView> itemAttributes : views) {
			String id = itemAttributes.getAttribute("id");
			String iconStr = itemAttributes.getAttribute("icon");
			String title = itemAttributes.getAttribute("title");
			IView view = itemAttributes.getInstance("class");

			ret.add(new ViewDecorator(view, id, title, iconStr, geoview));
		}

		return ret.toArray(new ViewDecorator[0]);
	}

	public static void addViewMenu(MenuTree menuTree, final RootWindow root,
			GeoView2D geoview, ViewDecorator[] viewsInfo) {
		Menu menu = new Menu(null, parentId, null, "View", null, false, null);
		menuTree.addMenu(menu);
		for (final ViewDecorator info : viewsInfo) {

			menu = new Menu(parentId, info.getId(), null, info.getTitle(), info
					.getIcon(), true, new ISelectableAction() {

				public boolean isVisible() {
					return true;
				}

				public boolean isEnabled() {
					return true;
				}

				public void actionPerformed() {
					if (info.isOpen()) {
						info.close();
					} else {
						info.open(root);
					}
				}

				public boolean isSelected() {
					return info.isOpen();
				}
			});
			menuTree.addMenu(menu);

		}
	}
}
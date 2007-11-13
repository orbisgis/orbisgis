package org.orbisgis.core;

import java.util.ArrayList;
import java.util.HashMap;

import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;

public class EPWindowHelper {

	private static final String BASE_CONF = "/extension/window";
	private static final String EXTENSION_ID = "org.orbisgis.Window";
	private static HashMap<String, ArrayList<IWindow>> windowsById = new HashMap<String, ArrayList<IWindow>>();

	public static void showInitial() {
		ExtensionPointManager<IWindow> epm = new ExtensionPointManager<IWindow>(
				EXTENSION_ID);
		ArrayList<ItemAttributes<IWindow>> itemAttributes = epm
				.getItemAttributes(BASE_CONF);
		for (ItemAttributes<IWindow> attrs : itemAttributes) {
			String id = attrs.getAttribute("id");
			String newOnStartup = attrs.getAttribute("newOnStartup");
			if (newOnStartup.equals("true")) {
				newWindow(id);
			}
		}
	}

	public static void newWindow(String id) {
		ExtensionPointManager<IWindow> epm = new ExtensionPointManager<IWindow>(
				EXTENSION_ID);
		ArrayList<ItemAttributes<IWindow>> itemAttributes = epm
				.getItemAttributes(BASE_CONF +
						"[@id='" + id + "']");
		IWindow wnd = itemAttributes.get(0).getInstance("class");
		wnd.newWindow();

		ArrayList<IWindow> wndLlist = windowsById.get(id);
		if (wndLlist  == null) {
			wndLlist = new ArrayList<IWindow>();
		}
		wndLlist.add(wnd);
		windowsById.put(id, wndLlist);
	}

	public static IWindow[] getWindows(String id) {
		ArrayList<IWindow> ret = windowsById.get(id);
		if (ret == null) {
			return new IWindow[0];
		} else {
			return ret.toArray(new IWindow[0]);
		}
	}

}

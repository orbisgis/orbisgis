package org.orbisgis.geoview;

import org.orbisgis.core.actions.EPActionHelper;
import org.orbisgis.pluginManager.ExtensionPointManager;

public class EPGeoviewActionHelper extends EPActionHelper {

	public static void executeAction(GeoView2D geoview, String id) {
		ExtensionPointManager<IGeoviewAction> epm = new ExtensionPointManager<IGeoviewAction>(
				"org.orbisgis.geoview.Action");
		IGeoviewAction action = epm.instantiateFrom("/extension/action[@id='"
				+ id + "']", "class");
		action.actionPerformed(geoview);
	}
}

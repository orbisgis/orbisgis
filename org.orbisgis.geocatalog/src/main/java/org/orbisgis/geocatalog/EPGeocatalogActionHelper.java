package org.orbisgis.geocatalog;

import org.orbisgis.core.ActionExtensionPointHelper;
import org.orbisgis.pluginManager.ExtensionPointManager;

public class EPGeocatalogActionHelper extends ActionExtensionPointHelper {

	public static void executeAction(Catalog catalog, String id) {
		ExtensionPointManager<IGeocatalogAction> epm = new ExtensionPointManager<IGeocatalogAction>(
				"org.orbisgis.geocatalog.Action");
		IGeocatalogAction action = epm.instantiateFrom(
				"/extension/action[@id='" + id + "']", "class");
		action.actionPerformed(catalog);
	}

}

package org.orbisgis.geocatalog;

import org.orbisgis.core.windows.EPWindowHelper;
import org.orbisgis.pluginManager.PluginManager;

public class SaveWorkspaceAction implements IGeocatalogAction {

	public void actionPerformed(Catalog catalog) {
		EPWindowHelper.saveStatus(PluginManager.getWorkspace());
	}

	public boolean isEnabled(GeoCatalog geoCatalog) {
		return true;
	}

	public boolean isVisible(GeoCatalog geoCatalog) {
		return true;
	}

}

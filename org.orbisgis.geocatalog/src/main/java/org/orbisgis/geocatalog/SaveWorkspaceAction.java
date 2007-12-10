package org.orbisgis.geocatalog;

import org.orbisgis.core.OrbisgisCore;

public class SaveWorkspaceAction implements IGeocatalogAction {

	public void actionPerformed(Catalog catalog) {
		OrbisgisCore.saveStatus();
	}

	public boolean isEnabled(GeoCatalog geoCatalog) {
		return true;
	}

	public boolean isVisible(GeoCatalog geoCatalog) {
		return true;
	}

}

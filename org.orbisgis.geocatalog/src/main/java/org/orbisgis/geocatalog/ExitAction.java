package org.orbisgis.geocatalog;

import org.orbisgis.pluginManager.RegistryFactory;

public class ExitAction implements IGeocatalogAction {

	public void actionPerformed(Catalog catalog) {
		RegistryFactory.shutdown();
	}

}

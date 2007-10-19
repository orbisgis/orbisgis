package org.orbisgis.geocatalog.resources.ui;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.ICatalogExtension;

/**
 * You may want to use this class if you want to add a plugin to "Catalog extension"
 * @author cerma
 *
 */
public class CatalogExtension implements ICatalogExtension {

	@SuppressWarnings("unused")
	public Catalog myCatalog = null;

	public boolean isInitialized = false;

	public CatalogExtension() {

	}

	public void initialize() {

	}

	public void setCatalog(Catalog catalog) {
		myCatalog = catalog;
		if (!isInitialized) {
			initialize();
			isInitialized = true;
		}
	}

}

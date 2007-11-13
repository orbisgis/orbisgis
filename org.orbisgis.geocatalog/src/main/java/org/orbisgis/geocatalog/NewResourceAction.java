package org.orbisgis.geocatalog;

import org.orbisgis.geocatalog.resources.EPResourceWizardHelper;

public class NewResourceAction implements IGeocatalogAction {

	public void actionPerformed(Catalog catalog) {
		EPResourceWizardHelper.openWizard(catalog, null);
	}

}

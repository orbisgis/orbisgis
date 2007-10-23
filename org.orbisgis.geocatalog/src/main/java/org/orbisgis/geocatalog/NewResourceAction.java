package org.orbisgis.geocatalog;

import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.ResourceWizardEP;

public class NewResourceAction implements GeocatalogAction {

	public void actionPerformed(Catalog catalog) {
		IResource[] resources = ResourceWizardEP.openWizard(catalog);
		for (IResource resource : resources) {
			catalog.getCatalogModel().insertNode(resource);
		}
	}

}

package org.orbisgis.geocatalog;

import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.geocatalog.resources.ResourceWizardEP;

public class NewResourceAction implements IGeocatalogAction {

	public void actionPerformed(Catalog catalog) {
		IResource[] resources = ResourceWizardEP.openWizard(catalog);
		for (IResource resource : resources) {
			catalog.getTreeModel().insertNode(resource);
		}
	}

}

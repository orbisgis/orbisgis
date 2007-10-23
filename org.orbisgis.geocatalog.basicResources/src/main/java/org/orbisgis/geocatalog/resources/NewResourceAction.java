package org.orbisgis.geocatalog.resources;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.CatalogModel;
import org.orbisgis.geocatalog.IResourceAction;

public class NewResourceAction implements IResourceAction {

	public boolean accepts(IResource selectedNode) {
		return true;
	}

	public boolean acceptsEmptySelection() {
		return true;
	}

	public void execute(Catalog catalog, IResource currentNode) {
		CatalogModel catalogModel = catalog.getCatalogModel();
		IResource parent = catalogModel.getRoot();
		if (currentNode != null) {
			parent = currentNode;
		}
		IResource[] resources = ResourceWizardEP.openWizard(catalog);
		for (IResource resource : resources) {
			catalogModel.insertNodeInto(resource, parent);
		}

	}

}

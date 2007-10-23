package org.orbisgis.geocatalog.resources;

import org.orbisgis.geocatalog.CatalogModel;
import org.orbisgis.geocatalog.IResourceAction;

public class ClearCatalogAction implements IResourceAction {

	public boolean accepts(IResource currentNode) {
		return true;
	}

	public void execute(CatalogModel catalogModel, IResource currentNode) {
		catalogModel.removeAllNodes();
	}

	public boolean acceptsEmptySelection() {
		return true;
	}

}

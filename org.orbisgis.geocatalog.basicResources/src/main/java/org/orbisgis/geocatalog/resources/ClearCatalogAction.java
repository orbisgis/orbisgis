package org.orbisgis.geocatalog.resources;

import org.orbisgis.geocatalog.CatalogModel;
import org.orbisgis.geocatalog.IResourceAction;

public class ClearCatalogAction implements IResourceAction {

	public boolean accepts(IResource currentNode) {
		return currentNode == null;
	}

	public void execute(CatalogModel catalogModel, IResource currentNode) {
		catalogModel.removeAllNodes();
	}

}

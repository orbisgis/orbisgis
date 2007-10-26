package org.orbisgis.geocatalog.resources;

import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.IResourceAction;

public class DeleteResource implements IResourceAction {

	public boolean accepts(IResource currentNode) {
		return currentNode != null;
	}

	public void execute(Catalog catalog, IResource currentNode) {
		catalog.getCatalogModel().removeNode(currentNode);
	}

	public boolean acceptsEmptySelection() {
		return false;
	}

}

package org.orbisgis.geocatalog.resources;

import org.orbisgis.geocatalog.CatalogModel;
import org.orbisgis.geocatalog.IResourceAction;

public class DeleteResource implements IResourceAction {

	public boolean accepts(IResource currentNode) {
		return currentNode != null;
	}

	public void execute(CatalogModel model, IResource currentNode) {
		model.removeNode(currentNode);
	}

	public boolean acceptsEmptySelection() {
		return false;
	}

}

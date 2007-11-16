package org.orbisgis.geocatalog.resources;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.IResourceAction;

public class ClearCatalogAction implements IResourceAction {

	public boolean accepts(IResource currentNode) {
		return true;
	}

	public void execute(Catalog catalog, IResource currentNode) {
		IResource[] children = catalog.getTreeModel().getRoot().getResources();
		for (IResource resource : children) {
			try {
				currentNode.removeResource(resource);
			} catch (ResourceTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return true;
	}

}

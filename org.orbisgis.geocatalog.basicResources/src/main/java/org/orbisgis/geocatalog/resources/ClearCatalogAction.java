package org.orbisgis.geocatalog.resources;

import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.IResourceAction;

public class ClearCatalogAction implements IResourceAction {

	public boolean accepts(IResource currentNode) {
		return true;
	}

	public void execute(Catalog catalog, IResource currentNode) {
		catalog.getTreeModel().removeAllNodes();
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return true;
	}

}

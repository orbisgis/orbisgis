package org.orbisgis.geocatalog.resources;

import org.orbisgis.core.resourceTree.Folder;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.IResourceAction;

public class ClearFolderAction implements IResourceAction {

	public boolean accepts(IResource selectedNode) {
		return selectedNode instanceof Folder;
	}

	public void execute(Catalog catalog, IResource currentNode) {
		IResource[] children = currentNode.getChildren();
		for (IResource resource : children) {
			catalog.getCatalogModel().removeNode(resource);
		}
	}

	public boolean acceptsEmptySelection() {
		return false;
	}

}

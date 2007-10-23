package org.orbisgis.geocatalog.resources;

import org.orbisgis.geocatalog.CatalogModel;
import org.orbisgis.geocatalog.IResourceAction;

public class ClearFolderAction implements IResourceAction {

	public boolean accepts(IResource selectedNode) {
		return selectedNode instanceof Folder;
	}

	public void execute(CatalogModel catalogModel, IResource currentNode) {
		IResource[] children = currentNode.getChildren();
		for (IResource resource : children) {
			catalogModel.removeNode(resource);
		}
	}

	public boolean acceptsEmptySelection() {
		return false;
	}

}

package org.orbisgis.geocatalog.resources;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.IResourceAction;

public class ClearFolderAction implements IResourceAction {

	public boolean accepts(IResource selectedNode) {
		return selectedNode.getResourceType() instanceof Folder;
	}

	public void execute(Catalog catalog, IResource currentNode) {
		IResource[] children = currentNode.getResources();
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
		return selectionCount > 0;
	}
}

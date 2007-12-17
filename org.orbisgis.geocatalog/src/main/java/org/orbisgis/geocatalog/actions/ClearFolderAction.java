package org.orbisgis.geocatalog.actions;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.IResourceAction;
import org.orbisgis.geocatalog.resources.Folder;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.ResourceTypeException;
import org.orbisgis.pluginManager.PluginManager;

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
				PluginManager.error("Cannot remove the resource: "
						+ resource.getName(), e);
			}
		}
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount > 0;
	}
}

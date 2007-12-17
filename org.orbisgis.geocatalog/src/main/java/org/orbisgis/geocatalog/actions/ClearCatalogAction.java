package org.orbisgis.geocatalog.actions;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.IResourceAction;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.ResourceTypeException;
import org.orbisgis.pluginManager.PluginManager;

public class ClearCatalogAction implements IResourceAction {

	public boolean accepts(IResource currentNode) {
		return true;
	}

	public void execute(Catalog catalog, IResource currentNode) {
		IResource root = catalog.getTreeModel().getRoot();
		IResource[] children = root.getResources();
		for (IResource resource : children) {
			try {
				root.removeResource(resource);
			} catch (ResourceTypeException e) {
				PluginManager.error("Cannot remove the resource: "
						+ resource.getName(), e);
			}
		}
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return true;
	}

}

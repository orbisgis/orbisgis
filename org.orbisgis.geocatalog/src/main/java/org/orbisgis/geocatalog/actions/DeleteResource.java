package org.orbisgis.geocatalog.actions;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.IResourceAction;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.ResourceTypeException;
import org.orbisgis.pluginManager.PluginManager;

public class DeleteResource implements IResourceAction {

	public boolean accepts(IResource currentNode) {
		return currentNode != null;
	}

	public void execute(Catalog catalog, IResource currentNode) {
		try {
			currentNode.getParentResource().removeResource(currentNode);
		} catch (ResourceTypeException e) {
			PluginManager.error("Cannot remove the resource: "
					+ currentNode.getName(), e);
		}
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount > 0;
	}

}

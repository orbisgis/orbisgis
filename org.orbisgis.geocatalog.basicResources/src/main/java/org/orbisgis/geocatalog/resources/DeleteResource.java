package org.orbisgis.geocatalog.resources;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.IResourceAction;

public class DeleteResource implements IResourceAction {

	public boolean accepts(IResource currentNode) {
		return currentNode != null;
	}

	public void execute(Catalog catalog, IResource currentNode) {
		try {
			currentNode.getParentResource().removeResource(currentNode);
		} catch (ResourceTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount > 0;
	}

}

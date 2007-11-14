package org.orbisgis.geocatalog;

import org.orbisgis.core.resourceTree.Folder;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.core.resourceTree.IResourceDnD;
import org.orbisgis.core.resourceTree.ResourceTreeModel;

public class BasicDnD implements IResourceDnD {

	public boolean drop(ResourceTreeModel model, IResource[] draggedNodes,
			IResource dropNode) {
		// If we dropped on a folder, move the resource
		if (dropNode instanceof Folder) {
			for (IResource resource : draggedNodes) {
				model.move(resource, dropNode);
			}
			return true;
		} else {
			return false;
		}
	}

}

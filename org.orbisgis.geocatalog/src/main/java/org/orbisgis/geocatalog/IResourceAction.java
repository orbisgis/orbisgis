package org.orbisgis.geocatalog;

import org.orbisgis.geocatalog.resources.IResource;

public interface IResourceAction {

	boolean accepts(IResource resource);

	boolean acceptsSelectionCount(int selectionCount);

	void execute(Catalog catalog, IResource selectedNode);

}

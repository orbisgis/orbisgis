package org.orbisgis.geocatalog;

import org.orbisgis.core.resourceTree.IResource;

public interface IResourceAction {

	boolean accepts(IResource resource);

	boolean acceptsSelectionCount(int selectionCount);

	void execute(Catalog catalog, IResource selectedNode);

}

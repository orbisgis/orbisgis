package org.orbisgis.geocatalog.resources;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.IResourceAction;

public class NewResourceAction implements IResourceAction {

	public boolean accepts(IResource selectedNode) {
		return true;
	}

	public boolean acceptsEmptySelection() {
		return true;
	}

	public void execute(Catalog catalog, IResource currentNode) {
		ResourceTreeModel catalogModel = catalog.getTreeModel();
		IResource parent = catalogModel.getRoot();
		if (currentNode != null) {
			parent = currentNode;
		}
		EPResourceWizardHelper.openWizard(catalog, parent);
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount <= 1;
	}

}

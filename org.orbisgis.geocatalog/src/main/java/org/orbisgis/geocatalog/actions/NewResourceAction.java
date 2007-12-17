package org.orbisgis.geocatalog.actions;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.IResourceAction;
import org.orbisgis.geocatalog.resources.EPResourceWizardHelper;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.ResourceTreeModel;

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

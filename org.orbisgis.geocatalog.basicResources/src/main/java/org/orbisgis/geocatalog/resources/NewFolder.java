package org.orbisgis.geocatalog.resources;

import org.orbisgis.core.resourceTree.Folder;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.core.resourceTree.ResourceFactory;
import org.orbisgis.geocatalog.AskValue;
import org.orbisgis.geocatalog.INewResource;
import org.sif.UIPanel;

public class NewFolder implements INewResource {

	private AskValue askValue;

	public String getName() {
		return "Folder";
	}

	public IResource[] getResources() {
		return new IResource[] { ResourceFactory.createResource(askValue
				.getValue(), new Folder()) };
	}

	public UIPanel[] getWizardPanels() {
		askValue = new AskValue("Enter folder name", "txt is not null",
				"A name must be specified");
		return new UIPanel[] { askValue };
	}

}

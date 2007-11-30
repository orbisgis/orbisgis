package org.orbisgis.geocatalog.resources;

import org.orbisgis.geocatalog.AskValue;
import org.orbisgis.geocatalog.INewResource;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class NewFolder implements INewResource {

	private AskValue askValue;

	public String getName() {
		return "Folder";
	}

	public IResource[] getResources() {
		askValue = new AskValue("Enter folder name", "txt is not null",
				"A name must be specified");
		if (UIFactory.showDialog(new UIPanel[] { askValue })) {
			return new IResource[] { ResourceFactory.createResource(askValue
					.getValue(), new Folder()) };
		} else {
			return new IResource[0];
		}
	}

}

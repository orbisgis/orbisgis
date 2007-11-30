package org.orbisgis.geocatalog.resources;

import java.io.File;
import java.util.ArrayList;

import org.gdms.data.file.FileSourceDefinition;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.wizards.OpenGdmsFilePanel;
import org.orbisgis.geocatalog.INewResource;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class NewFileResource implements INewResource {

	public String getName() {
		return "Add a file";
	}

	public IResource[] getResources() {
		OpenGdmsFilePanel filePanel = new OpenGdmsFilePanel(
				"Select the file to add");
		ArrayList<IResource> resources = new ArrayList<IResource>();
		if (UIFactory.showDialog(new UIPanel[] { filePanel })) {

			File[] files = filePanel.getSelectedFiles();
			for (File file : files) {
				String name = OrbisgisCore.registerInDSF(file.getName(),
						new FileSourceDefinition(file));
				resources.add(ResourceFactory.createResource(name,
						new AbstractGdmsSource()));
			}
		}
		return resources.toArray(new IResource[0]);
	}
}

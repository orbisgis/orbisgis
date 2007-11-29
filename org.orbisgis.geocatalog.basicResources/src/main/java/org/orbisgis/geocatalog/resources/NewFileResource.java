package org.orbisgis.geocatalog.resources;

import java.io.File;
import java.util.ArrayList;

import org.gdms.data.file.FileSourceDefinition;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.INewResource;
import org.orbisgis.pluginManager.ui.FileWizard;

public class NewFileResource extends FileWizard implements INewResource {

	public String getName() {
		return "Add a file";
	}

	public IResource[] getResources() {
		File[] files = getSelectedFiles();
		ArrayList<IResource> resources = new ArrayList<IResource>();
		for (File file : files) {
			String name = OrbisgisCore.registerInDSF(file.getName(),
					new FileSourceDefinition(file));
			resources.add(ResourceFactory.createResource(name,
					new AbstractGdmsSource()));
		}

		return resources.toArray(new IResource[0]);
	}
}

package org.orbisgis.geocatalog.resources;

import java.io.File;
import java.util.ArrayList;

import org.gdms.data.file.FileSourceDefinition;
import org.orbisgis.core.FileWizard;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.geocatalog.INewResource;

public class NewFileResource extends FileWizard implements INewResource {

	public String getName() {
		return "New file";
	}

	public IResource[] getResources() {
		File[] files = getSelectedFiles();
		ArrayList<IResource> resources = new ArrayList<IResource>();
		for (File file : files) {
			String registerName = OrbisgisCore.registerInDSF(file.getName(),
					new FileSourceDefinition(file));

			resources.add(new GdmsSource(registerName));
		}

		return resources.toArray(new IResource[0]);
	}
}

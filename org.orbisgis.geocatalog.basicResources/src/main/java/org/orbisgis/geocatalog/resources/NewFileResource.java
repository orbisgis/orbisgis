package org.orbisgis.geocatalog.resources;

import java.io.File;
import java.util.ArrayList;

import org.orbisgis.core.FileWizard;
import org.orbisgis.geocatalog.INewResource;

public class NewFileResource extends FileWizard implements INewResource {

	public String getName() {
		return "New file";
	}

	public IResource[] getResources() {
		File[] files = getSelectedFiles();
		ArrayList<IResource> resources = new ArrayList<IResource>();
		for (File file : files) {
			resources.add(ResourceFactory.createResource("",
					new FileGdmsSource(file)));
		}

		return resources.toArray(new IResource[0]);
	}
}

package org.orbisgis.geocatalog.resources;

import java.io.File;

import org.gdms.data.file.FileSourceDefinition;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.resourceTree.IResource;

public class FileGdmsSource extends AbstractGdmsSource {

	private File file;

	public FileGdmsSource(File file) {
		super("");
		this.file = file;
	}

	@Override
	public void addTo(IResource parent) {
		String name = OrbisgisCore.registerInDSF(file.getName(),
				new FileSourceDefinition(file));

		setName(name);
		super.addTo(parent);
	}

}

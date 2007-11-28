package org.orbisgis.geocatalog.resources;

import java.io.File;

import org.gdms.data.file.FileSourceDefinition;
import org.orbisgis.core.OrbisgisCore;

public class FileGdmsSource extends AbstractGdmsSource {

	private File file;
//
//	public FileGdmsSource(File file) {
//		this.file = file;
//	}
//
	public void addToTree(INode parent, INode toAdd)
			throws ResourceTypeException {
		super.addToTree(parent, toAdd);
		String name = OrbisgisCore.registerInDSF(file.getName(),
				new FileSourceDefinition(file));

		toAdd.setName(name);
	}

}

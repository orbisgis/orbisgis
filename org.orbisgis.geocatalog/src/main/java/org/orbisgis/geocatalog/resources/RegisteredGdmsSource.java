package org.orbisgis.geocatalog.resources;

import org.gdms.data.DataSourceDefinition;
import org.orbisgis.core.OrbisgisCore;

public class RegisteredGdmsSource extends AbstractGdmsSource {

	private DataSourceDefinition def;
//
//	public RegisteredGdmsSource(String name) {
//		def = OrbisgisCore.getDSF().getSourceManager().getSource(name)
//				.getDataSourceDefinition();
//	}

	public void addToTree(INode parent, INode toAdd)
			throws ResourceTypeException {
		super.addToTree(parent, toAdd);
		if (!OrbisgisCore.getDSF().getSourceManager().exists(toAdd.getName())) {
			OrbisgisCore.getDSF().getSourceManager().register(toAdd.getName(),
					def);
		}
	}

}

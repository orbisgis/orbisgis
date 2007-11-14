package org.orbisgis.geocatalog.resources;

import org.gdms.data.DataSourceDefinition;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.resourceTree.IResource;

public class RegisteredGdmsSource extends AbstractGdmsSource {

	private DataSourceDefinition def;

	public RegisteredGdmsSource(String name) {
		super(name);
		def = OrbisgisCore.getDSF().getSourceManager().getSource(name)
				.getDataSourceDefinition();
	}

	@Override
	public void addTo(IResource parent) {
		if (!OrbisgisCore.getDSF().getSourceManager().exists(getName())) {
			OrbisgisCore.getDSF().getSourceManager().register(getName(), def);
		}
		super.addTo(parent);
	}

}

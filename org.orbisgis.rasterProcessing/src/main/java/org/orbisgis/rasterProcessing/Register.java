package org.orbisgis.rasterProcessing;

import org.gdms.sql.customQuery.QueryManager;
import org.orbisgis.pluginManager.PluginActivator;
import org.orbisgis.rasterProcessing.tin.Generate2DMesh;

public class Register implements PluginActivator {
	public void start() throws Exception {
		QueryManager.registerQuery(new Generate2DMesh());

	}

	public void stop() throws Exception {
	}

	public boolean allowStop() {
		return true;
	}
}
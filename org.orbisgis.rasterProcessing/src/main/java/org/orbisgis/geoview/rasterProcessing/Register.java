package org.orbisgis.geoview.rasterProcessing;

import org.gdms.sql.customQuery.QueryManager;
import org.orbisgis.geoview.rasterProcessing.tin.Generate2DMesh;
import org.orbisgis.pluginManager.PluginActivator;

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
package org.orbisgis.rasterProcessing;

import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.spatial.raster.utilities.CropRaster;
import org.orbisgis.pluginManager.PluginActivator;
import org.orbisgis.rasterProcessing.tin.Generate2DMesh;

public class Register implements PluginActivator {
	public void start() throws Exception {
		QueryManager.registerQuery(new Generate2DMesh());
		FunctionManager.addFunction(CropRaster.class);
	}

	public void stop() throws Exception {
	}

	public boolean allowStop() {
		return true;
	}
}
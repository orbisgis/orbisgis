package org.orbisgis.graphicModeler;

import java.io.File;

import org.gdms.sql.function.FunctionManager;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.graphicModeler.functions.CutLineFunction;
import org.orbisgis.graphicModeler.functions.LineFunction;
import org.orbisgis.pluginManager.PluginActivator;

public class Activator implements PluginActivator {

	@Override
	public boolean allowStop() {
		return true;
	}

	@Override
	public void start() throws Exception {
		FunctionManager.addFunction(LineFunction.class);
		FunctionManager.addFunction(CutLineFunction.class);
		DataManager dm = Services.getService(DataManager.class);

		File nodes = new File(Activator.class.getResource(
				"/org/orbisgis/graphicModeler/nodes.shp").getFile());
		File edges = new File(Activator.class.getResource(
				"/org/orbisgis/graphicModeler/edges.gdms").getFile());
		dm.getSourceManager().register("nodes", nodes);
		dm.getSourceManager().register("edges", edges);
	}

	@Override
	public void stop() throws Exception {
		// do nothing
	}
}

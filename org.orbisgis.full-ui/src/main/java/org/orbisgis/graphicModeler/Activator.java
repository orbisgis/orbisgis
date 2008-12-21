package org.orbisgis.graphicModeler;

import org.gdms.sql.function.FunctionManager;
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
		try {
			FunctionManager.addFunction(LineFunction.class);
			FunctionManager.addFunction(CutLineFunction.class);
		} catch (IllegalArgumentException e) {
			// do nothing, the functions has been registered before
		}
	}

	@Override
	public void stop() throws Exception {
		// do nothing
	}
}

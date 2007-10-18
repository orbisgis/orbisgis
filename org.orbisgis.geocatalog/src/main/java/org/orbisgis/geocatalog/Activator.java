package org.orbisgis.geocatalog;

import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.pluginManager.PluginActivator;

public class Activator implements PluginActivator {

	public void start() throws Exception {
		GeoCatalog cat = new GeoCatalog();
		cat.show();
		System.out.println(OrbisgisCore.getDSF().getClass());
	}

	public void stop() throws Exception {
	}

}
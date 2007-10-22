package org.orbisgis.geocatalog;

import org.orbisgis.pluginManager.PluginActivator;

public class Activator implements PluginActivator {

	public void start() throws Exception {
		GeoCatalog cat = new GeoCatalog();
		cat.show();
	}

	public void stop() throws Exception {
	}

}
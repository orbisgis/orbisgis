package org.orbisgis.core;

import org.orbisgis.pluginManager.PluginActivator;

public class Activator implements PluginActivator {

	public void start() {
		EPWindowHelper.showInitial();
	}

	public void stop() {
	}

}
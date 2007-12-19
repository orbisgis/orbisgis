package org.orbisgis.geoview;

import org.gdms.sql.customQuery.QueryManager;
import org.orbisgis.geoview.sql.customQuery.Geomark;
import org.orbisgis.pluginManager.PluginActivator;

public class Activator implements PluginActivator {
	public void start() throws Exception {
		QueryManager.registerQuery(new Geomark());
	}

	public void stop() throws Exception {
	}

	public boolean allowStop() {
		return true;
	}
}
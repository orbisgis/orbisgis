package org.orbisgis.geoview;

import org.orbisgis.pluginManager.PluginActivator;

/**
 */
public class Activator implements PluginActivator {

	public void start() throws Exception {
		final GeoView2D vf = new GeoView2D();

		vf.setLocationRelativeTo(null);
		vf.setSize(800, 700);
		vf.setVisible(true);
	}

	public void stop() throws Exception {
	}
}

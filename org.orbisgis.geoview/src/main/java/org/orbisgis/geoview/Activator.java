package org.orbisgis.geoview;

import org.orbisgis.pluginManager.PluginActivator;

/**
 */
public class Activator implements PluginActivator {

	public void start() throws Exception {
		final GeoView2DFrame vf = new GeoView2DFrame();

		vf.setLocationRelativeTo(null);
		vf.setSize(800, 700);
		vf.setVisible(true);
	}

	public void stop() throws Exception {
	}
}

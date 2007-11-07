package org.orbisgis.pluginManager;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

public class PluginManager {

	private static PluginManager pluginManager = null;

	private ArrayList<Plugin> plugins;

	public PluginManager(ArrayList<Plugin> plugins) {
		this.plugins = plugins;
	}

	public static void createPluginManager(ArrayList<Plugin> plugins) {
		pluginManager = new PluginManager(plugins);
	}

	static void start() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {

			public void run() {
				try {
					pluginManager.startPlugins();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

		});
	}

	private void startPlugins() throws Exception {
		for (int i = 0; i < plugins.size(); i++) {
			plugins.get(i).start();
		}
	}

	public static void stop() {
		pluginManager.stopPlugins();
		System.exit(0);
	}

	private void stopPlugins() {
		for (int i = 0; i < plugins.size(); i++) {
			try {
				plugins.get(i).stop();
			} catch (Exception e) {
				// TODO Notify error manager.
			}
		}
	}

}

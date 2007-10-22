package org.orbisgis.pluginManager;

import java.util.ArrayList;

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
		pluginManager.startPlugins();
	}

	private void startPlugins() throws Exception {
		for (int i = 0; i < plugins.size(); i++) {
			plugins.get(i).start();
		}
	}

	public static void stop() throws Exception {
		pluginManager.stopPlugins();
	}

	private void stopPlugins() throws Exception {
		for (int i = 0; i < plugins.size(); i++) {
			plugins.get(i).stop();
		}
	}

}

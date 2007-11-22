package org.orbisgis.pluginManager;

import java.io.File;

public class PluginClassPathReaderFactory {

	private static PluginClassPathReader[] readers = new PluginClassPathReader[] {
			new EclipseProjectReader(), new DeploymentClassPathReader() };

	public static PluginClassPathReader get(File pluginDir) {
		for (PluginClassPathReader reader : readers) {
			if (reader.accepts(pluginDir)) {
				return reader;
			}
		}

		throw new RuntimeException("Unrecognized plugin type: "
				+ pluginDir.getAbsolutePath());
	}

}

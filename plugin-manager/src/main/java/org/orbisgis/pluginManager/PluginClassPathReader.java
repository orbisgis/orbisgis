package org.orbisgis.pluginManager;

import java.io.File;

public interface PluginClassPathReader {

	boolean accepts(File pluginDir);

	PluginClassLoader getClassLoader(File pluginDir);

}

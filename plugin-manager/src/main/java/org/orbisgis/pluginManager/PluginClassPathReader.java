package org.orbisgis.pluginManager;

import java.io.File;

public interface PluginClassPathReader {

	boolean accepts(File pluginDir);

	File[] getJars(File dir);

	File[] getOutputFolders(File dir);

}

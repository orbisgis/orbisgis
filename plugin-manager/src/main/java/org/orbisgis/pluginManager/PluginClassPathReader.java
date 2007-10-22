package org.orbisgis.pluginManager;

import java.io.File;
import java.net.URL;

public interface PluginClassPathReader {

	boolean accepts(File pluginDir);

	URL[] getJars(File dir);

	File[] getOutputFolders(File dir);

}

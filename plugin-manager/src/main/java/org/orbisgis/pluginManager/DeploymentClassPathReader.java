package org.orbisgis.pluginManager;

import java.io.File;
import java.util.ArrayList;

public class DeploymentClassPathReader implements PluginClassPathReader {

	public boolean accepts(File pluginDir) {
		return true;
	}

	public File[] getJars(File dir) {
		ArrayList<File> ret = new ArrayList<File>();
		File[] subFiles = dir.listFiles();
		for (File file : subFiles) {
			if (file.isDirectory()) {
				File[] jars = getJars(file);
				for (File url : jars) {
					ret.add(url);
				}
			} else if (file.getName().toLowerCase().endsWith(".jar")
					|| file.getName().toLowerCase().endsWith(".zip")) {
				ret.add(file);
			}
		}

		return ret.toArray(new File[0]);
	}

	public File[] getOutputFolders(File dir) {
		return new File[0];
	}

}

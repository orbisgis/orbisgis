package org.orbisgis.pluginManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DeploymentClassPathReader implements PluginClassPathReader {

	public boolean accepts(File pluginDir) {
		return true;
	}

	public URL[] getJars(File dir) {
		ArrayList<URL> ret = new ArrayList<URL>();
		File[] subFiles = dir.listFiles();
		for (File file : subFiles) {
			if (file.isDirectory()) {
				URL[] jars = getJars(file);
				for (URL url : jars) {
					ret.add(url);
				}
			} else if (file.getName().toLowerCase().endsWith(".jar")
					|| file.getName().toLowerCase().endsWith(".zip")) {
				try {
					ret.add(file.toURI().toURL());
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return ret.toArray(new URL[0]);
	}

	public File[] getOutputFolders(File dir) {
		return new File[0];
	}

}

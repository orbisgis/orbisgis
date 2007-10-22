package org.orbisgis.pluginManager;

import java.io.File;

public class Plugin {

	private PluginActivator resolvedActivator;
	private String activator;
	private File baseDir;
	private ClassLoader loader;

	public Plugin(String activator, File baseDir, ClassLoader loader) {
		super();
		this.loader = loader;
		this.activator = activator;
		this.baseDir = baseDir;
	}

	public String getActivator() {
		return activator;
	}

	public File getBaseDir() {
		return baseDir;
	}

	public void setResolvedActivator(PluginActivator resolvedActivator) {
		this.resolvedActivator = resolvedActivator;
	}

	public void setActivator(String activator) {
		this.activator = activator;
	}

	public void start() throws Exception {
		PluginActivator resolvedActivator = getResolvedActivator();
		if (resolvedActivator != null) {
			resolvedActivator.start();
		}
	}

	private PluginActivator getResolvedActivator()
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		if (activator == null) {
			return null;
		} else {
			if (resolvedActivator == null) {
				resolvedActivator = (PluginActivator) loader.loadClass(
						activator).newInstance();
			}

			return resolvedActivator;
		}
	}

	public void stop() throws Exception {
		try {
			PluginActivator resolvedActivator = getResolvedActivator();
			if (resolvedActivator != null) {
				resolvedActivator.stop();
			}
		} catch (InstantiationException e) {
			throw new RuntimeException("start worked but not stop: bug!");
		} catch (IllegalAccessException e) {
			throw new RuntimeException("start worked but not stop: bug!");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("start worked but not stop: bug!");
		}
	}
}

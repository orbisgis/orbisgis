package org.orbisgis.pluginManager;

import org.orbisgis.pluginManager.launcher.CommonClassLoader;

public interface Starter {
	void start(String[] args) throws Exception;

	void setClassLoader(CommonClassLoader commonClassLoader);
}

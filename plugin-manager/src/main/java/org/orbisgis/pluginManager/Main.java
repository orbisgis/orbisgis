package org.orbisgis.pluginManager;


public class Main {

	public static void main(String[] args) throws Exception {
		CommonClassLoader commonClassLoader = new CommonClassLoader();
		commonClassLoader.loadClass("org.orbisgis.pluginManager.StartUp");
		StartUp st = new StartUp(commonClassLoader);
		st.main(args);
	}

}

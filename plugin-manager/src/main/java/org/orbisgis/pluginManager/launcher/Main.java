package org.orbisgis.pluginManager.launcher;

import org.orbisgis.pluginManager.Starter;

public class Main {

	public static void main(String[] args) throws Exception {
		System.out.println(Main.class
				.getResource("/org/orbisgis/pluginManager/log4j.properties"));
		CommonClassLoader commonClassLoader = new CommonClassLoader();
		Starter st = (Starter) commonClassLoader.loadClass(
				"org.orbisgis.pluginManager.StartUp").newInstance();
		st.setClassLoader(commonClassLoader);
		st.start(args);
	}

}

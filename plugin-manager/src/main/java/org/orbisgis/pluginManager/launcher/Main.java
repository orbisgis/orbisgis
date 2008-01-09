package org.orbisgis.pluginManager.launcher;

import org.orbisgis.pluginManager.Starter;

public class Main {

	public static void main(String[] args) throws Exception {
		// TODO System.out.println(Main.class
		// .getResource("/automata/ZoomOut.fsa.xml"));
		CommonClassLoader commonClassLoader = new CommonClassLoader();
		Starter st = (Starter) commonClassLoader.loadClass(
				"org.orbisgis.pluginManager.StartUp").newInstance();
		st.setClassLoader(commonClassLoader);
		st.start(args);
	}

}

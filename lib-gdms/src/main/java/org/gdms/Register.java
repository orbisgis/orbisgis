package org.gdms;

import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.customQuery.spatial.ToLineNoder;
import org.gdms.sql.customQuery.spatial.jgrapht.ShortestPath;
import org.gdms.sql.customQuery.spatial.tin.BuildTIN;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.spatial.extract.ToMultiSegments;
import org.gdms.sql.function.spatial.generalize.Generalize;
import org.orbisgis.pluginManager.PluginActivator;

public class Register implements PluginActivator {
	public void start() throws Exception {

		FunctionManager.addFunction(ToMultiSegments.class);
		FunctionManager.addFunction(Generalize.class);

		QueryManager.registerQuery(new ToLineNoder());
		QueryManager.registerQuery(new ShortestPath());
		QueryManager.registerQuery(new BuildTIN());
	}

	public void stop() throws Exception {
	}

	public boolean allowStop() {
		return true;
	}
}
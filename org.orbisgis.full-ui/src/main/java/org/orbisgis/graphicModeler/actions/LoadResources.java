package org.orbisgis.graphicModeler.actions;

import java.io.File;

import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.action.IAction;
import org.orbisgis.graphicModeler.Activator;

public class LoadResources implements IAction {

	@Override
	public void actionPerformed() {
		DataManager dm = Services.getService(DataManager.class);
		File nodes = new File(Activator.class.getResource(
				"/org/orbisgis/graphicModeler/nodes.shp").getFile());
		File edges = new File(Activator.class.getResource(
				"/org/orbisgis/graphicModeler/edges.gdms").getFile());
		if (!dm.getSourceManager().exists("nodes")) {
			dm.getSourceManager().register("nodes", nodes);
		}
		if (!dm.getSourceManager().exists("edges")) {
			dm.getSourceManager().register("edges", edges);
		}

	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isVisible() {
		return isEnabled();
	}
}

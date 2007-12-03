package org.orbisgis.geoview.table;

import java.awt.Component;

import org.gdms.driver.DriverException;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.pluginManager.PluginManager;

public class ShowInTable implements org.orbisgis.geoview.toc.ILayerAction {

	public boolean accepts(ILayer layer) {
		return layer instanceof VectorLayer;
	}

	public boolean acceptsAll(ILayer[] layer) {
		return true;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount == 1;
	}

	public void execute(GeoView2D view, ILayer resource) {
		view.showView("org.orbisgis.geoview.Table");
		Component comp = view.getView("org.orbisgis.geoview.Table");
		if (comp != null) {
			Table table = (Table) comp;
			try {
				table.setContents(((VectorLayer) resource).getDataSource());
			} catch (DriverException e) {
				PluginManager.error("Cannot show contents in table:"
						+ resource.getName(), e);
			}
		} else {
			PluginManager.error("Cannot find a table to show contents");
		}
	}

	public void executeAll(GeoView2D view, ILayer[] layers) {

	}

}

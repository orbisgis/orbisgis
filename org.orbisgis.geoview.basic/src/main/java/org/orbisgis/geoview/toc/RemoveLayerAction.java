package org.orbisgis.geoview.toc;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.pluginManager.PluginManager;

public class RemoveLayerAction implements ILayerAction {

	public boolean accepts(ILayer layer) {
		return layer != null;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount > 0;
	}

	public void execute(GeoView2D view, ILayer resource) {
		try {
			resource.getParent().remove(resource);
		} catch (LayerException e) {
			PluginManager.error("Cannot delete layer: " + e.getMessage(), e);
		}
	}

	public void executeAll(GeoView2D view, ILayer[] layers) {
	}

	public boolean acceptsAll(ILayer[] layer) {
		return true;
	}

}

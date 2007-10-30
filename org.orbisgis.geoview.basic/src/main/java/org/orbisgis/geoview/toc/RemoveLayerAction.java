package org.orbisgis.geoview.toc;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.ILayer;

public class RemoveLayerAction implements ILayerAction {

	public boolean accepts(ILayer layer) {
		return layer != null;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount > 0;
	}

	public void execute(GeoView2D view, ILayer resource) {
		resource.getParent().remove(resource);
	}

	public void executeAll(GeoView2D view, ILayer[] layers) {
	}

	public boolean acceptsAll(ILayer[] layer) {
		return true;
	}

}

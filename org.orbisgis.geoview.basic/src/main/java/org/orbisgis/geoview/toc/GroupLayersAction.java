package org.orbisgis.geoview.toc;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerCollection;
import org.orbisgis.geoview.layerModel.LayerFactory;

public class GroupLayersAction implements ILayerAction {

	public boolean accepts(ILayer layer) {
		return layer != null;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount >= 1;
	}

	public void execute(GeoView2D view, ILayer resource) {
	}

	public void executeAll(GeoView2D view, ILayer[] layers) {
		LayerCollection col = LayerFactory.createLayerCollection("group"
				+ System.currentTimeMillis());
		ILayer parent = layers[0].getParent();
		try {
			parent.put(col);
			for (ILayer layer : layers) {
				layer.moveTo(col);
			}
		} catch (CRSException e) {
			// They already have the same CRS because they are in the same
			// mapcontext
			throw new RuntimeException("bug!");
		}
	}

	public boolean acceptsAll(ILayer[] layer) {
		for (int i = 0; i < layer.length - 1; i++) {
			if (!layer[i].getParent().equals(layer[i + 1].getParent())) {
				return false;
			}
		}

		return true;
	}

}

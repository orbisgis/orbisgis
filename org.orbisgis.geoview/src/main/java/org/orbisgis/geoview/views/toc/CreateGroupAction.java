package org.orbisgis.geoview.views.toc;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;

public class CreateGroupAction implements ILayerAction {

	public boolean accepts(ILayer layer) {
		return layer.acceptsChilds();
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount <= 1;
	}

	public void execute(GeoView2D view, ILayer resource) {
		ILayer newLayerCollection = LayerFactory
				.createLayerCollection("group" + System.currentTimeMillis());

		if ((resource == null) || (!resource.acceptsChilds())) {
			resource = view.getViewContext().getRootLayer();
		}
		try {
			resource.put(newLayerCollection);
		} catch (CRSException e) {
			// They already have the same CRS because they are in the same
			// mapcontext
			throw new RuntimeException("bug!");
		} catch (LayerException e) {
			throw new RuntimeException("bug!");
		}
	}

	public void executeAll(GeoView2D view, ILayer[] resource) {
	}

	public boolean acceptsAll(ILayer[] layer) {
		return true;
	}

}

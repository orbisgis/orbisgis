package org.orbisgis.geoview.views.toc;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.ILayer;

import com.vividsolutions.jts.geom.Envelope;

public class ZoomToLayerAction implements ILayerAction {

	public boolean accepts(ILayer layer) {
		return layer != null;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount == 1;
	}

	public void execute(GeoView2D view, ILayer resource) {
	}

	public void executeAll(GeoView2D view, ILayer[] layers) {
		Envelope env = new Envelope(layers[0].getEnvelope());
		for (ILayer layer : layers) {
			env.expandToInclude(layer.getEnvelope());
		}
		view.getMap().setExtent(env);
	}

	public boolean acceptsAll(ILayer[] layer) {
		return true;
	}

}

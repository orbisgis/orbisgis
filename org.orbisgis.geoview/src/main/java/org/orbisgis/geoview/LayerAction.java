/**
 *
 */
package org.orbisgis.geoview;

import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.ILayerAction;

import com.vividsolutions.jts.geom.Envelope;

public class LayerAction implements ILayerAction {
	private Envelope globalEnvelope = null;

	public void action(ILayer layer) {
		if (layer.isVisible()) {
			final Envelope env = layer.getEnvelope();

			if (null == globalEnvelope) {
				globalEnvelope = new Envelope(env);
			} else {
				globalEnvelope.expandToInclude(env);
			}
		}
	}

	public Envelope getGlobalEnvelope() {
		return globalEnvelope;
	}
}
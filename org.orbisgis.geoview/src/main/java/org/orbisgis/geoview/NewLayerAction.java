package org.orbisgis.geoview;

import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;

public class NewLayerAction implements IGeoviewAction {

	public void actionPerformed(GeoView2D geoview) {
		ILayer[] layers = LayerWizardEP.openWizard(geoview);
		OGMapControlModel model = geoview.getMapModel();
		ILayer lc = model.getLayers();
		for (ILayer layer : layers) {
			try {
				lc.put(layer);
			} catch (CRSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

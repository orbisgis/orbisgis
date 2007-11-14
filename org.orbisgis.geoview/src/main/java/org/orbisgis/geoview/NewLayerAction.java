package org.orbisgis.geoview;


public class NewLayerAction implements IGeoviewAction {

	public void actionPerformed(GeoView2D geoview) {
		EPLayerWizardHelper.openWizard(geoview);
	}
}

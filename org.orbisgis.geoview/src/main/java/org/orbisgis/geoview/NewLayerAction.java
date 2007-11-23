package org.orbisgis.geoview;

public class NewLayerAction implements IGeoviewAction {

	public void actionPerformed(GeoView2D geoview) {
		EPLayerWizardHelper.openWizard(geoview);
	}

	public boolean isEnabled(GeoView2D geoView2D) {
		return true;
	}

	public boolean isVisible(GeoView2D geoView2D) {
		return true;
	}
}

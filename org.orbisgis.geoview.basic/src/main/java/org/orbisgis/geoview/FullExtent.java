package org.orbisgis.geoview;

public class FullExtent implements IGeoviewAction {

	public void actionPerformed(GeoView2D geoview) {
		MapControl mc = geoview.getMap();
		mc.setExtent(geoview.getViewContext().getRootLayer().getEnvelope());
	}

	public boolean isEnabled(GeoView2D geoView2D) {
		return true;
	}

	public boolean isVisible(GeoView2D geoView2D) {
		return true;
	}

}

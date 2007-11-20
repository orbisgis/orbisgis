package org.orbisgis.geoview;

public class FullExtent implements IGeoviewAction {

	public void actionPerformed(GeoView2D geoview) {
		MapControl mc = geoview.getMap();
		mc.setExtent(geoview.getViewContext().getRootLayer().getEnvelope());
	}

}

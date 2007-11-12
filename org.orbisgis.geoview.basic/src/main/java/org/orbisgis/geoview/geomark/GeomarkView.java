package org.orbisgis.geoview.geomark;

import java.awt.Component;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;

public class GeomarkView implements IView {
	public Component getComponent(GeoView2D geoview) {
		return new GeomarkPanel(geoview);
	}
}
package org.orbisgis.geoview.toc;

import java.awt.Component;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;

public class TocView implements IView {

	public Component getComponent(GeoView2D geoview) {
		return new Toc(geoview);
	}

}

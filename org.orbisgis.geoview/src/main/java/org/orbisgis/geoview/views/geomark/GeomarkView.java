package org.orbisgis.geoview.views.geomark;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;

public class GeomarkView implements IView {
	public Component getComponent(GeoView2D geoview) {
		return new GeomarkPanel(geoview);
	}

	public void loadStatus(InputStream ois) {
	}

	public void saveStatus(OutputStream oos) {
	}

	public void delete() {

	}

	public void initialize(GeoView2D geoView2D) {

	}
}
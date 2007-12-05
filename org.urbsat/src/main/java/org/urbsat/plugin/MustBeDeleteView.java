package org.urbsat.plugin;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;

public class MustBeDeleteView implements IView {
	public Component getComponent(GeoView2D geoview) {
		return null;
	}

	public void loadStatus(InputStream ois) {

	}

	public void saveStatus(OutputStream oos) {

	}

	public void delete() {

	}
}
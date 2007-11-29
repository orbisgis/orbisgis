package org.orbisgis.geoview.toc;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;

public class TocView implements IView {

	public Component getComponent(GeoView2D geoview) {
		return new Toc(geoview);
	}

	public void loadStatus(InputStream ois) {

	}

	public void saveStatus(OutputStream oos) {

	}

}

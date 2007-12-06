package org.orbisgis.geoview.toc;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;

public class TocView implements IView {

	private Toc toc;

	public Component getComponent(GeoView2D geoview) {
		toc = new Toc(geoview);
		return toc;
	}

	public void loadStatus(InputStream ois) {

	}

	public void saveStatus(OutputStream oos) {

	}

	public void delete() {
		toc.delete();
	}

	public void initialize(GeoView2D geoView2D) {

	}
}

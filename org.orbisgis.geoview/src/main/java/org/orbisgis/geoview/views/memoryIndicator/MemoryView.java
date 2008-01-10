package org.orbisgis.geoview.views.memoryIndicator;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;

public class MemoryView implements IView {

	public void delete() {
		
	}

	public Component getComponent(GeoView2D geoview) {
		return new ViewPanel();
	}

	public void initialize(GeoView2D geoView2D) {
		
	}

	public void loadStatus(InputStream ois) {
		
	}

	public void saveStatus(OutputStream oos) {
		
	}

}

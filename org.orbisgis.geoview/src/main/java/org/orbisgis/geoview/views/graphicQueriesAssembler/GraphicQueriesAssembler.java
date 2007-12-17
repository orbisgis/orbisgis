package org.orbisgis.geoview.views.graphicQueriesAssembler;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;

public class GraphicQueriesAssembler implements IView {

	public Component getComponent(GeoView2D geoview) {
		ImageIcon ii = new ImageIcon("/tmp/process.png", "");
		JLabel lbl = new JLabel();
		lbl.setIcon(ii);

		return lbl;
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

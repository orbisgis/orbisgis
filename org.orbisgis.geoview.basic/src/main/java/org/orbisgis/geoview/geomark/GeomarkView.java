package org.orbisgis.geoview.geomark;

import java.awt.Component;

import javax.swing.JLabel;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;

public class GeomarkView implements IView {

	public Component getComponent(GeoView2D geoview) {
//		return new JLabel("ok");
		return new GeomarkPanel(geoview);
	}
}
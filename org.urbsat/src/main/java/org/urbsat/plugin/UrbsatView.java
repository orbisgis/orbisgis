package org.urbsat.plugin;

import java.awt.Component;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;
import org.urbsat.plugin.ui.UrbSATPanel;

public class UrbsatView implements IView {
	public Component getComponent(GeoView2D geoview) {
		return new UrbSATPanel(geoview);
	}
}
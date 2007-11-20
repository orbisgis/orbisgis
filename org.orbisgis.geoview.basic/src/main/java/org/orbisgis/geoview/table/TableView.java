package org.orbisgis.geoview.table;

import java.awt.Component;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;

public class TableView implements IView {

	public Component getComponent(GeoView2D geoview) {
		return new Table();
	}

}

package org.orbisgis.geoview.table;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IGeoviewAction;
import org.orbisgis.tools.TransitionException;

public class InfoToolAction implements IGeoviewAction {

	public void actionPerformed(GeoView2D geoview) {
		try {
			geoview.getMap().setTool(new InfoTool());
		} catch (TransitionException e) {
			throw new RuntimeException(e);
		}
	}
	public boolean isEnabled(GeoView2D geoView2D) {
		return true;
	}

	public boolean isVisible(GeoView2D geoView2D) {
		return true;
	}

}

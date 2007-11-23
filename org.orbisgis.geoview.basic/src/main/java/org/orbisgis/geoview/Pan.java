package org.orbisgis.geoview;

import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.PanTool;

public class Pan implements IGeoviewAction {

	public void actionPerformed(GeoView2D geoview) {
		try {
			geoview.getMap().setTool(new PanTool());
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

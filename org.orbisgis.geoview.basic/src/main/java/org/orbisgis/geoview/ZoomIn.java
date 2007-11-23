package org.orbisgis.geoview;

import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.ZoomInTool;

public class ZoomIn implements IGeoviewAction {

	public void actionPerformed(GeoView2D geoview) {
		try {
			geoview.getMap().setTool(new ZoomInTool());
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

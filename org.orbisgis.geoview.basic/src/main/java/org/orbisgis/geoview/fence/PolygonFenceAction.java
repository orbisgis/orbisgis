package org.orbisgis.geoview.fence;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IGeoviewAction;
import org.orbisgis.tools.TransitionException;

public class PolygonFenceAction implements IGeoviewAction {
	private final static FencePolygonTool fencePolygonTool = new FencePolygonTool();

	public void actionPerformed(GeoView2D geoview) {
		try {
			if (fencePolygonTool != null) {
				geoview.getMap().setTool(fencePolygonTool);
			}

		} catch (TransitionException e) {
			throw new RuntimeException(e);
		}
	}
}
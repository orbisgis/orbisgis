package org.orbisgis.geoview.fence;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IGeoviewAction;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.PolygonTool;

public class PolygonFenceAction implements IGeoviewAction {
	public void actionPerformed(GeoView2D geoview) {
		try {
			geoview.getMap().setTool(new PolygonTool());
		} catch (TransitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
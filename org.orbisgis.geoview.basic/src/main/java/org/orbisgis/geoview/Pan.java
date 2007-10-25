package org.orbisgis.geoview;

import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.PanTool;

public class Pan implements IGeoviewAction {

	public void actionPerformed(GeoView2D geoview) {
		try {
			geoview.getMap().setTool(new PanTool());
		} catch (TransitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

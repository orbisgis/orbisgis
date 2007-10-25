package org.orbisgis.geoview.about;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IGeoviewAction;


public class AboutAction implements IGeoviewAction {

	public void actionPerformed(GeoView2D geoview) {
		About ab = new About();
		ab.setModal(true);
		ab.setVisible(true);
	}

}

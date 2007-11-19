package org.orbisgis.geoview;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public interface MapControlModel {

	void draw(final Graphics2D graphics);

	/**
	 * Gets the map area this model can draw
	 *
	 * @return
	 */
	Rectangle2D getMapArea();

	MapControl getMapControl();
}

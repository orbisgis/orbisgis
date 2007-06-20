package org.orbisgis.plugin.view.ui.workbench;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public interface MapControlModel {

	/**
	 * Gets the exceptions that happened during the last call to
	 * draw
	 *
	 * @return
	 */
	Exception[] getProblems();

	void draw(BufferedImage image, Rectangle2D bbox, int imageWidth, int imageHeight,
			Color backColor);

	/**
	 * Gets the map area this model can draw
	 *
	 * @return
	 */
	Rectangle2D getMapArea();

}

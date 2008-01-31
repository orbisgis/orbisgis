package org.orbisgis.geoview.renderer.legend;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Geometry;

public interface Symbol {

	/**
	 * Draws the symbol in the specified graphics object
	 *
	 * @param g
	 * @throws DriverException
	 */
	void draw(Graphics2D g, Geometry geom, AffineTransform at)
			throws DriverException;

	/**
	 * Returns true if the symbol will draw the specified geometry
	 *
	 * @param geom
	 * @return
	 */
	boolean willDraw(Geometry geom);

}

package org.orbisgis.geoview.renderer.legend;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.gdms.driver.DriverException;
import org.orbisgis.geoview.renderer.RenderPermission;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public interface Symbol {

	/**
	 * Draws the symbol in the specified graphics object
	 *
	 * @param g
	 * @param geom
	 * @param at
	 * @param permission
	 *            manager that can be asked for permission to draw
	 * @return The area used by this draw operation
	 * @throws DriverException
	 */
	Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
			RenderPermission permission) throws DriverException;

	/**
	 * Returns true if the symbol will draw the specified geometry
	 *
	 * @param geom
	 * @return
	 */
	boolean willDraw(Geometry geom);

}

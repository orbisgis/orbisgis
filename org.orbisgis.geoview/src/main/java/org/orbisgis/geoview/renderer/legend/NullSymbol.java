package org.orbisgis.geoview.renderer.legend;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Geometry;

public class NullSymbol implements Symbol {

	public void draw(Graphics2D g, Geometry geom, AffineTransform at)
			throws DriverException {
	}

	public boolean willDraw(Geometry geom) {
		return true;
	}

}

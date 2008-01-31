package org.orbisgis.geoview.renderer.legend;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.gdms.driver.DriverException;
import org.orbisgis.geoview.renderer.RenderPermission;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class NullSymbol implements Symbol {

	public Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
			RenderPermission permission) throws DriverException {
		return null;
	}

	public boolean willDraw(Geometry geom) {
		return true;
	}

}

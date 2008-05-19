package org.orbisgis.renderer.legend;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.gdms.driver.DriverException;
import org.orbisgis.renderer.RenderPermission;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class InteriorCircleSymbol extends CircleSymbol {

	public InteriorCircleSymbol(Color outline, Color fillColor, int size) {
		super(outline, fillColor, size);
	}

	public Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
			RenderPermission permission) throws DriverException {
		Point point = geom.getInteriorPoint();
		Point2D p = new Point2D.Double(point.getX(), point.getY());
		p = at.transform(p, null);
		paintCircle(g, (int) p.getX(), (int) p.getY());

		return null;
	}

	@Override
	public boolean willDraw(Geometry geom) {
		return true;
	}
}

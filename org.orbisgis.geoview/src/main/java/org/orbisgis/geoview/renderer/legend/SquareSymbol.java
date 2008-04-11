package org.orbisgis.geoview.renderer.legend;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import org.gdms.driver.DriverException;
import org.orbisgis.geoview.renderer.RenderPermission;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class SquareSymbol extends AbstractSquareSymbol {

	public SquareSymbol(Color outline, Color fillColor, int size) {
		super(outline, fillColor, size);
	}

	public Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
			RenderPermission permission) throws DriverException {
		LiteShape ls = new LiteShape(geom, at, false);
		PathIterator pi = ls.getPathIterator(null);
		double[] coords = new double[6];

		while (!pi.isDone()) {
			pi.currentSegment(coords);
			paintCircle(g, (int) coords[0], (int) coords[1]);
			pi.next();
		}

		return null;
	}
}

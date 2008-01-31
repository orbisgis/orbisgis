package org.orbisgis.geoview.renderer.legend;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import org.gdms.driver.DriverException;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;

import com.vividsolutions.jts.geom.Geometry;

public class CircleSymbol extends AbstractPointSymbol implements Symbol {

	private int size;
	private Color outline;
	private Color fillColor;

	public CircleSymbol(Color outline, Color fillColor, int size) {
		this.size = size;
		this.outline = outline;
		this.fillColor = fillColor;
	}

	public void draw(Graphics2D g, Geometry geom, AffineTransform at)
			throws DriverException {
		LiteShape ls = new LiteShape(geom, at, false);
		PathIterator pi = ls.getPathIterator(null);
		double[] coords = new double[6];

		while (!pi.isDone()) {
			pi.currentSegment(coords);
			int x = (int) coords[0] - size / 2;
			int y = (int) coords[1] - size / 2;
			if (fillColor != null) {
				g.setPaint(fillColor);
				g.fillOval(x, y, size, size);
			}
			g.setColor(outline);
			g.drawOval(x, y, size, size);
			pi.next();
		}
	}
}

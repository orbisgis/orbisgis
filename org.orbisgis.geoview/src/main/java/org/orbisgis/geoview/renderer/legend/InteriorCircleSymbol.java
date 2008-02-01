package org.orbisgis.geoview.renderer.legend;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.gdms.driver.DriverException;
import org.orbisgis.geoview.renderer.RenderPermission;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class InteriorCircleSymbol extends AbstractPointSymbol implements Symbol {

	private int size;

	private Color outline;

	private Color fillColor;

	public InteriorCircleSymbol(Color outline, Color fillColor, int size) {
		this.size = size;
		this.outline = outline;
		this.fillColor = fillColor;
	}

	public Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
			RenderPermission permission) throws DriverException {
		Point point = geom.getInteriorPoint();
		Point2D p = new Point2D.Double(point.getX(), point.getY());
		p = at.transform(p, null);
		int x = (int) p.getX();
		int y = (int) p.getY();
		if (fillColor != null) {
			g.setPaint(fillColor);
			g.fillOval(x, y, size, size);
		}
		g.setColor(outline);
		g.drawOval(x, y, size, size);

		return null;
	}
}

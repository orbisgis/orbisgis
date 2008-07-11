package org.orbisgis.renderer.symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.gdms.driver.DriverException;
import org.orbisgis.renderer.RenderPermission;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class CirclePointSymbol extends AbstractPointSymbol {

	public CirclePointSymbol(Color outline, int lineWidth, Color fillColor,
			int size) {
		super(outline, lineWidth, fillColor, size);
	}

	public Symbol cloneSymbol() {
		return new CirclePointSymbol(outline, lineWidth, fillColor, size);
	}

	protected void paintCircle(Graphics2D g, int x, int y) {
		x = x - size / 2;
		y = y - size / 2;
		if (fillColor != null) {
			g.setPaint(fillColor);
			g.fillOval(x, y, size, size);
		}
		if (outline != null) {
			g.setStroke(new BasicStroke(lineWidth));
			g.setColor(outline);
			g.drawOval(x, y, size, size);
		}
	}

	public Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
			RenderPermission permission) throws DriverException {
		Point point = geom.getCentroid();
		Point2D p = new Point2D.Double(point.getX(), point.getY());
		p = at.transform(p, null);
		paintCircle(g, (int) p.getX(), (int) p.getY());

		return null;
	}

	public String getClassName() {
		return "Circle in point";
	}

	public String getId() {
		return "org.orbisgis.symbol.point.Circle";
	}

}

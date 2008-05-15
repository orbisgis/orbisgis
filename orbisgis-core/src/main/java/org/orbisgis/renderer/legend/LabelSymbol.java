package org.orbisgis.renderer.legend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.gdms.driver.DriverException;
import org.orbisgis.renderer.RenderPermission;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class LabelSymbol extends AbstractSymbol implements Symbol {

	private int fontSize;
	private String text;

	public LabelSymbol(String text, int fontSize) {
		this.text = text;
		this.fontSize = fontSize;
	}

	public Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
			RenderPermission permission) throws DriverException {
		Font font = g.getFont();
		g.setFont(font.deriveFont(Font.BOLD, fontSize));
		FontMetrics metrics = g.getFontMetrics(font);
		// get the height of a line of text in this font and render context
		int hgt = metrics.getHeight();
		// get the advance of my text in this font and render context
		int adv = metrics.stringWidth(text);
		// calculate the size of a box to hold the text with some padding.
		Dimension size = new Dimension(adv + 2, hgt + 2);
		Point interiorPoint = geom.getInteriorPoint();
		Point2D p = new Point2D.Double(interiorPoint.getX(), interiorPoint
				.getY());
		p = at.transform(p, null);
		double width = size.getWidth();
		double x = p.getX() - width / 2;
		double height = size.getHeight();
		double y = p.getY() + height / 2;
		Envelope area = new Envelope(new Coordinate(x, y), new Coordinate(x
				+ width, y + height));
		if (permission.canDraw(area)) {
			g.setColor(Color.black);
			g.drawString(text, (int) x, (int) y);
			return area;
		} else {
			return null;
		}
	}

	public boolean willDraw(Geometry geom) {
		return true;
	}

}

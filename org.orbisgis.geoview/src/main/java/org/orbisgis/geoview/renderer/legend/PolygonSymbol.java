package org.orbisgis.geoview.renderer.legend;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import org.gdms.driver.DriverException;
import org.orbisgis.geoview.renderer.RenderPermission;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class PolygonSymbol extends AbstractPolygonSymbol {

	private Stroke stroke = new BasicStroke();

	private Color outlineColor = Color.black;

	private Color fillColor = Color.red;

	public Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
			RenderPermission permission) throws DriverException {
		if (geom instanceof Polygon || geom instanceof MultiPolygon) {
			LiteShape ls = new LiteShape(geom, at, true);
			g.setStroke(stroke);
			if (fillColor != null) {
				g.setPaint(fillColor);
				g.fill(ls);
			}
			g.setColor(outlineColor);
			g.draw(ls);
		}

		return null;
	}

	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}

	public Color getOutlineColor() {
		return outlineColor;
	}

	public void setOutlineColor(Color outlineColor) {
		this.outlineColor = outlineColor;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public Stroke getStroke() {
		return stroke;
	}

}

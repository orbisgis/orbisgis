package org.orbisgis.geoview.renderer.legend;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import org.gdms.driver.DriverException;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;

import com.vividsolutions.jts.geom.Geometry;

public class LineSymbol extends AbstractLineSymbol implements Symbol {

	private Color color;
	private Stroke stroke;

	public LineSymbol(Color color, Stroke stroke) {
		this.color = color;
		this.stroke = stroke;
	}

	public void draw(Graphics2D g, Geometry geom, AffineTransform at)
			throws DriverException {
		LiteShape ls = new LiteShape(geom, at, true);
		g.setStroke(stroke);
		g.setColor(color);
		g.setPaint(null);
		g.draw(ls);

	}

}

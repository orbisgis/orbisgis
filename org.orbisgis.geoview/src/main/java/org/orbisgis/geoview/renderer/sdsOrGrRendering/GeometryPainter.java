package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D.Float;

import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;
import org.orbisgis.geoview.renderer.style.Style;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class GeometryPainter {
	public static void paint(final Geometry geometry, final Graphics2D g,
			final Style style, final MapControl mapControl) {
		Shape liteShape;
		Shape shapeToDraw = null;

		if (null != geometry) {
			liteShape = new LiteShape(geometry, mapControl.getTrans(), true);

			if ((geometry instanceof Point) | (geometry instanceof MultiPoint)) {
				PathIterator pi = liteShape.getPathIterator(null);
				while (!pi.isDone()) {
					double[] point = new double[6];
					pi.currentSegment(point);
					float x = (float) point[0];
					float y = (float) point[1];
					
					float r = 50;
					shapeToDraw = new Ellipse2D.Float(x, y, r, r);
					pi.next();
				}

			} else {

				shapeToDraw = liteShape;

			}

			if ((style.getFillColor() != null) && (geometry instanceof Polygon)
					| (geometry instanceof MultiPolygon)) {
				// TODO : we should manage also GeometryCollection...
				g.setPaint(style.getFillColor());
				g.fill(shapeToDraw);
			}

			if (style.getLineColor() != null) {
				g.setColor(style.getLineColor());

			} else {
				g.setColor(style.getDefaultLineColor());
			}

			if (null != style.getStroke()) {
				g.setStroke(style.getStroke());
			}

			g.draw(shapeToDraw);

		}
	}
}
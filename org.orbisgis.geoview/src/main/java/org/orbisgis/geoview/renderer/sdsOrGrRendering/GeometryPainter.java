package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D.Float;
import java.util.Random;

import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;
import org.orbisgis.geoview.renderer.style.BasicStyle;
import org.orbisgis.geoview.renderer.style.PointStyle;
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
				
			
				PointStyle pointStyle = new PointStyle("#FFFF00", "#FFFF00");
				PathIterator pi = liteShape.getPathIterator(null);
				while (!pi.isDone()) {
					double[] point = new double[6];
					pi.currentSegment(point);
					float x = (float) point[0];
					float y = (float) point[1];
					
					
					shapeToDraw = pointStyle.getDefaultShape(x, y, 10);
					g.setPaint(style.getFillColor());
					g.fill(shapeToDraw);
					pi.next();
				}

			} else {

				shapeToDraw = liteShape;

			}

			if ((style.getFillColor() != null) && (geometry instanceof Polygon)
					| (geometry instanceof MultiPolygon)  ) {
				// TODO : we should manage also GeometryCollection...
				g.setPaint(style.getFillColor());
				
				g.fill(shapeToDraw);
			}

			if (style.getLineColor() != null) {
				g.setColor(style.getLineColor());

			} else {
				g.setColor(style.getDefaultLineColor());
			}

			if (null != style.getBasicStroke()) {
				g.setStroke(style.getBasicStroke());
			}			
				
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
			
			g.setComposite(ac);
			g.draw(shapeToDraw);

		}
	}
}
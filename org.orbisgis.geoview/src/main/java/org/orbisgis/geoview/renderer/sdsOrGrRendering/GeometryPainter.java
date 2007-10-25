package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.Graphics2D;

import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;
import org.orbisgis.geoview.renderer.style.Style;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class GeometryPainter {
	public static void paint(final Geometry geometry, final Graphics2D g,
			final Style style, final MapControl mapControl) {
		if (null != geometry) {
			final LiteShape liteShape = new LiteShape(geometry, mapControl
					.getTrans(), true);

			if ((style.getFillColor() != null)
					&& ((geometry instanceof Polygon) || (geometry instanceof MultiPolygon))) {
				// TODO : we should manage also GeometryCollection...
				g.setPaint(style.getFillColor());
				g.fill(liteShape);
			}

			if (style.getLineColor() != null) {
				g.setColor(style.getLineColor());
			} else {
				g.setColor(style.getDefaultLineColor());
			}
			g.draw(liteShape);
		}
	}
}
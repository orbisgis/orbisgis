package org.orbisgis.plugin.renderer.sdsOrGrRendering;

import java.awt.Graphics2D;

import org.orbisgis.plugin.renderer.liteShape.LiteShape;
import org.orbisgis.plugin.renderer.style.Style;
import org.orbisgis.plugin.view.ui.workbench.MapControl;

import com.vividsolutions.jts.geom.Geometry;

public class GeometryPainter {
	public static void paint(final Geometry geometry, Graphics2D g,
			Style style, MapControl mapControl) {
		LiteShape liteShape = null;

		if (null != geometry) {
			liteShape = new LiteShape(geometry, mapControl.getTrans(), true);
			if (style.getFillColor() != null) {
				g.setPaint(style.getFillColor());
				g.fill(liteShape);
			}

			if (style.getLineColor() != null) {
				g.setColor(style.getLineColor());
				g.draw(liteShape);
			} else {
				g.setColor(style.getDefaultLineColor());
				g.draw(liteShape);
			}
		}
	}
}
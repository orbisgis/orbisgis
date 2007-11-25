package org.orbisgis.geoview.renderer.sdsOrGrRendering;


import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.Shape;


import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;
import org.orbisgis.geoview.renderer.style.BasicStyle;
import org.orbisgis.geoview.renderer.style.LineStyle;
import org.orbisgis.geoview.renderer.style.PolygonStyle;

import org.orbisgis.geoview.renderer.style.sld.PolygonSymbolizer;

import com.vividsolutions.jts.geom.Geometry;


public class LinePainter {
	public static void paint(final Geometry geometry, final Graphics2D g,
			final  LineStyle lineStyle, final MapControl mapControl) {
		Shape liteShape;
	
	
		if (null != geometry) {
			liteShape = new LiteShape(geometry, mapControl.getTrans(), true);
			
			if (lineStyle.getLineColor() != null) {
				g.setColor(lineStyle.getLineColor());

			} else {
				g.setColor(lineStyle.getDefaultLineColor());
			}

			if (null != lineStyle.getBasicStroke()) {
				g.setStroke(lineStyle.getBasicStroke());
			}
			
			
			g.draw(liteShape);

		}
	}
}
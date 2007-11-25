package org.orbisgis.geoview.renderer.sdsOrGrRendering;


import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.Shape;


import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;
import org.orbisgis.geoview.renderer.style.BasicStyle;
import org.orbisgis.geoview.renderer.style.PolygonStyle;

import org.orbisgis.geoview.renderer.style.sld.PolygonSymbolizer;

import com.vividsolutions.jts.geom.Geometry;


public class PolygonPainter {
	public static void paint(final Geometry geometry, final Graphics2D g,
			final  PolygonStyle polygonStyle, final MapControl mapControl) {
		Shape liteShape;
		
		
		if (null != geometry) {
			liteShape = new LiteShape(geometry, mapControl.getTrans(), true);
			
			if ((polygonStyle.getFillColor() != null)) {				
				g.setPaint(polygonStyle.getFillColor());
				
				g.fill(liteShape);
			}

			if (polygonStyle.getLineColor() != null) {
				g.setColor(polygonStyle.getLineColor());

			} else {
				g.setColor(polygonStyle.getDefaultLineColor());
			}

			/**
			 * todo : implements stroke
			 */
			/*	if (null != style.getStrokeColor()) {
				g.setStroke(style.getStrokeColor());
			}
			*/
							
			
			//g.setComposite(polygonStyle.getAlphaComposite());
			g.draw(liteShape);

		}
	}
}
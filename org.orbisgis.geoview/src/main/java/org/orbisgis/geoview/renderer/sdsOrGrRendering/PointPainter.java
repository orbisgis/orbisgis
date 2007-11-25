package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.PathIterator;

import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;
import org.orbisgis.geoview.renderer.style.PointStyle;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

public class PointPainter {

	public static void paint(Geometry geometry, Graphics2D g, PointStyle pointStyle, MapControl mapControl) {
	
		Shape liteShape;
		Shape shapeToDraw = null;
		
		if (null != geometry) {
			
			if ((geometry instanceof Point) | (geometry instanceof MultiPoint)) {
				liteShape = new LiteShape(geometry, mapControl.getTrans(), true);
				
				PathIterator pi = liteShape.getPathIterator(null);
				while (!pi.isDone()) {
					double[] point = new double[6];
					pi.currentSegment(point);
					double x =  point[0];
					double y =  point[1];
					
					shapeToDraw = pointStyle.getDefaultShape(x, y, 10);
					g.setPaint(pointStyle.getFillColor());
					g.fill(shapeToDraw);
					pi.next();
				}
				
				
			}
			
			else {
				Geometry geom;
				for (int i = 0; i < geometry.getNumGeometries(); i++) {
					
					geom = geometry.getGeometryN(i);
					liteShape = new LiteShape(geom, mapControl.getTrans(), true);
									
					
					PathIterator pi = liteShape.getPathIterator(null);
					while (!pi.isDone()) {
						double[] point = new double[6];
						pi.currentSegment(point);
						double x =  point[0];
						double y =  point[1];
						
						shapeToDraw = pointStyle.getDefaultShape(x, y, 10);
						g.setPaint(pointStyle.getFillColor());
						g.fill(shapeToDraw);
						pi.next();
					}
					
				}
				
				
			}
						
			g.draw(shapeToDraw);
			
		}
		
		
	}

	
	
}

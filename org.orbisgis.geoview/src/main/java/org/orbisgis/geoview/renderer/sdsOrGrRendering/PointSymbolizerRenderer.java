package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.Graphics2D;
import java.awt.Shape;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;
import org.orbisgis.geoview.renderer.style.PointStyle;
import org.orbisgis.geoview.renderer.style.sld.PointSymbolizer;

import com.vividsolutions.jts.geom.Geometry;

public class PointSymbolizerRenderer {

	
	private MapControl mapControl;

	public PointSymbolizerRenderer(final MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public static void paint(Graphics2D graphics, SpatialDataSourceDecorator sds, PointSymbolizer symbolizer, MapControl mapControl) {
		
		
		PointStyle pointStyle = new PointStyle();
		
		
		try {
			for (int i = 0; i < sds.getRowCount(); i++) {
				
				final Geometry geom =sds.getGeometry(i);
				
				PointPainter.paint(geom, graphics, pointStyle, mapControl);
				
			}
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}

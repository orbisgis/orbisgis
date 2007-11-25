package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.Graphics2D;

import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;

import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.style.LineStyle;
import org.orbisgis.geoview.renderer.style.sld.LineSymbolizer;

import com.vividsolutions.jts.geom.Geometry;
import com.ximpleware.xpath.XPathParseException;

public class LineSymbolizerRenderer {

	
	private MapControl mapControl;

	public LineSymbolizerRenderer(final MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public static void paint(Graphics2D graphics, SpatialDataSourceDecorator sds, LineSymbolizer symbolizer, MapControl mapControl) {
		
		LineStyle lineStyle = null;
		try {
			lineStyle = new LineStyle(symbolizer.getStroke().getStrokeColor(), symbolizer.getStroke().getStrokeWidth());
		} catch (XPathParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			for (int i = 0; i < sds.getRowCount(); i++) {
				
				final Geometry geom =sds.getGeometry(i);
				
				LinePainter.paint(geom, graphics, lineStyle, mapControl);
				
			}
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

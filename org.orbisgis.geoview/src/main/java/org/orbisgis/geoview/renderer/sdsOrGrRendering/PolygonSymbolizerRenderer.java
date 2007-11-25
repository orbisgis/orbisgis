package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.Color;
import java.awt.Graphics2D;

import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.style.BasicStyle;
import org.orbisgis.geoview.renderer.style.PolygonStyle;
import org.orbisgis.geoview.renderer.style.sld.PolygonSymbolizer;
import org.orbisgis.pluginManager.PluginManager;

import com.vividsolutions.jts.geom.Geometry;
import com.ximpleware.xpath.XPathParseException;

public class PolygonSymbolizerRenderer {

	
	private static MapControl mapControl;

	public PolygonSymbolizerRenderer(final MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public static void paint(Graphics2D graphics, SpatialDataSourceDecorator sds, PolygonSymbolizer symbolizer, MapControl mapControl) {
		
		
		PolygonStyle polygonStyle = null;
		try {
			polygonStyle = new PolygonStyle(symbolizer.getFillColor(), symbolizer.getStroke().getStrokeColor());
		} catch (XPathParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		try {
			for (int i = 0; i < sds.getRowCount(); i++) {
				
				final Geometry geom =sds.getGeometry(i);
				
				PolygonPainter.paint(geom, graphics, polygonStyle, mapControl);
				
			}
			
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
}

package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.Color;
import java.awt.Graphics2D;

import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.style.BasicStyle;
import org.orbisgis.geoview.renderer.style.PolygonSymbolizer;
import org.orbisgis.pluginManager.PluginManager;

import com.vividsolutions.jts.geom.Geometry;
import com.ximpleware.xpath.XPathParseException;

public class PolygonSymbolizerRenderer {

	
	private static MapControl mapControl;

	public PolygonSymbolizerRenderer(final MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public static void paint(Graphics2D graphics, SpatialDataSourceDecorator sds, PolygonSymbolizer symbolizer, MapControl mapControl) {
		
		Color fillColor = null;
		Color lineColor = null;
		
		try {
			if (symbolizer.getFillColor().length()>0){
				System.out.println("Fill" + symbolizer.getFillColor());
				fillColor = Color.decode(symbolizer.getFillColor());
				
			}
			if (symbolizer.getStroke().getStroke().length()>0){
				lineColor = Color.decode(symbolizer.getStroke().getStroke());
			}
			
			
		} catch (XPathParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			BasicStyle style = new BasicStyle(lineColor, fillColor, 1);
			for (int i = 0; i < sds.getRowCount(); i++) {
				try {
					final Geometry geometry = sds.getGeometry(i);
					GeometryPainter.paint(geometry, graphics, style,
							mapControl);
				} catch (DriverException e) {
					PluginManager.warning("Cannot access the " + i
							+ "the feature of " + sds.getName(), e);
				}
			}
		} catch (DriverException e) {
			PluginManager.warning("Cannot access data in "
					+ sds.getName(), e);
		}
		
		
	}
}

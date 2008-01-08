package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.Graphics2D;

import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.style.sld.LineSymbolizer;
import org.orbisgis.geoview.renderer.style.sld.PointSymbolizer;
import org.orbisgis.geoview.renderer.style.sld.PolygonSymbolizer;
import org.orbisgis.geoview.renderer.style.sld.Symbolizer;

public class SymbolizerRenderer {

	
	private MapControl mapControl;

	public SymbolizerRenderer(final MapControl mapControl) {
		this.mapControl = mapControl;
	}
	
	public static void paint(Graphics2D graphics, SpatialDataSourceDecorator sds, Symbolizer symbolizer, MapControl mapControl) {
		
		
		String type = symbolizer.getType();
		
		if (type.equalsIgnoreCase("sld:PointSymbolizer")){
			
			PointSymbolizerRenderer.paint(graphics, sds, (PointSymbolizer) symbolizer, mapControl);
		}
		else if (type.equalsIgnoreCase("sld:LineSymbolizer")) {
			LineSymbolizerRenderer.paint(graphics, sds, (LineSymbolizer) symbolizer, mapControl);
		}
		else if (type.equalsIgnoreCase("sld:PolygonSymbolizer")){
			PolygonSymbolizerRenderer.paint(graphics, sds, (PolygonSymbolizer) symbolizer, mapControl);
		}
		
		else {
			
		}
		
		
	}

}

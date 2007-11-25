package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.Graphics2D;

import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.style.LineSymbolizer;

public class LineSymbolizerRenderer {

	
	private MapControl mapControl;

	public LineSymbolizerRenderer(final MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public static void paint(Graphics2D graphics, SpatialDataSourceDecorator sds, LineSymbolizer symbolizer, MapControl mapControl) {
		// TODO Auto-generated method stub
		
	}

}

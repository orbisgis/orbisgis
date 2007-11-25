package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.Graphics2D;

import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.style.sld.PointSymbolizer;

public class PointSymbolizerRenderer {

	
	private MapControl mapControl;

	public PointSymbolizerRenderer(final MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public static void paint(Graphics2D graphics, SpatialDataSourceDecorator sds, PointSymbolizer symbolizer, MapControl mapControl) {
		
	}
}

package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.Graphics2D;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.style.Style;
import org.orbisgis.geoview.renderer.style.sld.FeatureTypeStyle;
import org.orbisgis.geoview.renderer.style.sld.Rule;
import org.orbisgis.pluginManager.PluginManager;

import com.vividsolutions.jts.geom.Geometry;
import com.ximpleware.xpath.XPathParseException;

public class FeatureTypeRenderer {
	private MapControl mapControl;

	public FeatureTypeRenderer(final MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public static void paint(final Graphics2D graphics,
			final SpatialDataSourceDecorator sds, final FeatureTypeStyle featureTypeStyle, final MapControl mapControl) {
		
		 try {
			 
			Rule rule = null;
			for (int i = 0; i < featureTypeStyle.getRuleCount(); i++) {
				
				 rule = featureTypeStyle.getRules().get(i);
				
				RuleRenderer.paint(graphics, sds, rule, mapControl);
				
			}
			
			
		} catch (XPathParseException e) {			
			e.printStackTrace();
		}

	}
}
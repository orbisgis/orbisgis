package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.style.BasicStyle;
import org.orbisgis.geoview.renderer.style.FeatureTypeStyle;
import org.orbisgis.geoview.renderer.style.Style;
import org.orbisgis.pluginManager.PluginManager;

import com.vividsolutions.jts.geom.Geometry;

public class SLDRenderer {
	private MapControl mapControl;

	public SLDRenderer(final MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public void paint(final Graphics2D graphics,
			final SpatialDataSourceDecorator sds,  List<FeatureTypeStyle> style) {
				
		try {
			int  attributeIndex = sds.getFieldIndexByName("BV");
			for (int i = 0; i < sds.getRowCount(); i++) {
				try {
					final Geometry geometry = sds.getGeometry(i);
						
					Value attribute = sds.getFieldValue(i, attributeIndex);
					System.out.println("Valeur " + attribute.toString() + "_");					
					System.out.println("Valeur " + attribute.getType());					
					
						if(attribute.toString().equalsIgnoreCase("Pin Sec")){
							
						}
						else {
							
						}
						
				
					
					
				} catch (DriverException e) {
					PluginManager.warning("Cannot access the " + i
							+ "th feature of " + sds.getName(), e);
				}
			}
		} catch (DriverException e) {
			PluginManager.warning("Cannot access data in " + sds.getName(), e);
		}
	}
}
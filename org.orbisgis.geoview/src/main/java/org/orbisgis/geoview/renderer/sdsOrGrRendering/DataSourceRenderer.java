package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.Graphics2D;

import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.style.Style;
import org.orbisgis.pluginManager.PluginManager;

import com.vividsolutions.jts.geom.Geometry;

public class DataSourceRenderer {
	private MapControl mapControl;

	public DataSourceRenderer(final MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public void paint(final Graphics2D graphics,
			final SpatialDataSourceDecorator sds, final Style style) {
		try {
			sds.open();
			for (int i = 0; i < sds.getRowCount(); i++) {
				try {
					final Geometry geometry = sds.getGeometry(i);
					GeometryPainter
							.paint(geometry, graphics, style, mapControl);
				} catch (DriverException e) {
					PluginManager.warning("Cannot access the " + i
							+ "th feature of " + sds.getName(), e);
				}
			}
			sds.cancel();
		} catch (DriverException e) {
			PluginManager.warning("Cannot open: " + sds.getName(), e);
		}
	}
}
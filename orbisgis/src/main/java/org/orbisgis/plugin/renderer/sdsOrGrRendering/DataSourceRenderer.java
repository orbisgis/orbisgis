package org.orbisgis.plugin.renderer.sdsOrGrRendering;

import java.awt.Graphics2D;

import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.plugin.renderer.style.Style;
import org.orbisgis.plugin.view.ui.workbench.MapControl;

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
				final Geometry geometry = sds.getGeometry(i);
				GeometryPainter.paint(geometry, graphics, style, mapControl);
			}
			sds.cancel();

			// Maybe to refresh the view
			// Blackboard.mapArea.zoomToFullExtent();
			// Blackboard.mapArea.repaint();

		} catch (DriverException e) {
			e.printStackTrace();
		}
	}
}
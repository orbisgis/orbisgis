package org.orbisgis.geoview.table;

import java.awt.Component;
import java.awt.geom.Rectangle2D;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.AbstractRectangleTool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class InfoTool extends AbstractRectangleTool {

	@Override
	protected void rectangleDone(Rectangle2D rect) throws TransitionException {
		ILayer layer = ec.getSelectedLayers()[0];

		DataSource ds = ((VectorLayer) layer).getDataSource();
		try {
			ds.open();
			SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
			ObjectMemoryDriver omd = new ObjectMemoryDriver(ds.getMetadata());
			GeometryFactory gf = ToolManager.toolsGeometryFactory;
			double tolerance = tm.getTolerance();
			double minx = rect.getMinX();
			double miny = rect.getMinY();
			double maxx = rect.getMaxX();
			double maxy = rect.getMaxY();
			if ((maxx - minx < tolerance) && maxy - miny < tolerance) {
				minx = rect.getCenterX() - tolerance;
				miny = rect.getCenterY() - tolerance;
				maxx = rect.getCenterX() + tolerance;
				maxy = rect.getCenterY() + tolerance;
			}
			Coordinate lowerLeft = new Coordinate(minx, miny);
			Coordinate upperRight = new Coordinate(maxx, maxy);
			Envelope envelope = new Envelope(lowerLeft, upperRight);
			LinearRing envelopeShell = gf.createLinearRing(new Coordinate[] {
					lowerLeft, new Coordinate(minx, maxy), upperRight,
					new Coordinate(maxx, miny), lowerLeft, });
			Geometry geomEnvelope = gf.createPolygon(envelopeShell,
					new LinearRing[0]);
			for (int i = 0; i < ds.getRowCount(); i++) {
				Geometry g = sds.getGeometry(i);
				if (envelope.intersects(g.getEnvelopeInternal())) {
					if (g.intersects(geomEnvelope)) {
						omd.addValues(sds.getRow(i));
					}
				}
			}
			Component comp = ec.getView().getView("org.orbisgis.geoview.Table");
			Table table = (Table) comp;
			table.setContents(OrbisgisCore.getDSF().getDataSource(omd));

		} catch (DriverException e) {
			throw new TransitionException(e);
		} catch (DriverLoadException e) {
			throw new RuntimeException(e);
		} catch (DataSourceCreationException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isEnabled() {
		return false;
	}

	public boolean isVisible() {
		return true;
	}

}

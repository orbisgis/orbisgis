package org.orbisgis.geoview.views.table;

import java.awt.Component;
import java.awt.geom.Rectangle2D;

import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.instances.AbstractRectangleTool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.io.WKTWriter;

public class InfoTool extends AbstractRectangleTool {

	@Override
	protected void rectangleDone(Rectangle2D rect, ViewContext vc,
			ToolManager tm) throws TransitionException {
		ILayer layer = vc.getSelectedLayers()[0];

		DataSource ds = ((VectorLayer) layer).getDataSource();
		try {
			SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
			GeometryFactory gf = ToolManager.toolsGeometryFactory;
			double minx = rect.getMinX();
			double miny = rect.getMinY();
			double maxx = rect.getMaxX();
			double maxy = rect.getMaxY();

			Coordinate lowerLeft = new Coordinate(minx, miny);
			Coordinate upperRight = new Coordinate(maxx, maxy);
			LinearRing envelopeShell = gf.createLinearRing(new Coordinate[] {
					lowerLeft, new Coordinate(minx, maxy), upperRight,
					new Coordinate(maxx, miny), lowerLeft, });
			Geometry geomEnvelope = gf.createPolygon(envelopeShell,
					new LinearRing[0]);
			WKTWriter writer = new WKTWriter();
			String sql = "select * from " + layer.getName()
					+ " where intersects(" + sds.getDefaultGeometry()
					+ ", geomfromtext('" + writer.write(geomEnvelope) + "'));";
			Component comp = vc.getView().getView("org.orbisgis.geoview.Table");
			Table table = (Table) comp;
			table.setContents(OrbisgisCore.getDSF().executeSQL(sql));

		} catch (DriverException e) {
			throw new TransitionException(e);
		} catch (DriverLoadException e) {
			throw new RuntimeException(e);
		} catch (SyntaxException e) {
			throw new RuntimeException(e);
		} catch (NoSuchTableException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isEnabled(ViewContext vc, ToolManager tm) {
		if (vc.getSelectedLayers().length == 1) {
			if (vc.getSelectedLayers()[0] instanceof VectorLayer) {
				return true;
			}
		}

		return false;
	}

	public boolean isVisible(ViewContext vc, ToolManager tm) {
		return true;
	}

}

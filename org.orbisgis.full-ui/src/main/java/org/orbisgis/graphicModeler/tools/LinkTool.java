package org.orbisgis.graphicModeler.tools;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.editors.map.tool.FinishedAutomatonException;
import org.orbisgis.editors.map.tool.Rectangle2DDouble;
import org.orbisgis.editors.map.tool.ToolManager;
import org.orbisgis.editors.map.tool.TransitionException;
import org.orbisgis.editors.map.tools.AbstractLineTool;
import org.orbisgis.editors.map.tools.ToolUtilities;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class LinkTool extends AbstractLineTool {

	protected void lineDone(LineString ls, MapContext mc, ToolManager tm)
			throws TransitionException {
		try {
			DataManager dm = Services.getService(DataManager.class);
			DataSource ds = dm.getDSF().getDataSource("edges");
			if (!ds.isOpen()) {
				ds.open();
			}
			SpatialDataSourceDecorator sds = mc.getActiveLayer()
					.getDataSource();

			int ids[] = new int[2];
			for (int i = 0; i < ls.getNumPoints(); i++) {
				for (long j = 0; j < sds.getRowCount(); j++) {
					if (ls.getPointN(i).equalsExact(sds.getGeometry(j))) {
						ids[i] = sds.getInt(j, "id");
						break;
					}
				}
			}

			Value[] row = new Value[ds.getMetadata().getFieldCount()];
			row[ds.getFieldIndexByName("src")] = ValueFactory
					.createValue(ids[0]);
			row[ds.getFieldIndexByName("dest")] = ValueFactory
					.createValue(ids[1]);
			ds.insertFilledRow(row);

			ds.commit();
			ds.close();
		} catch (DriverException e) {
			throw new TransitionException("Cannot insert linestring", e);
		} catch (DriverLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchTableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NonEditableDataSourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void transitionTo_Point(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		// TODO Get a valid tolerance
		double tolerance = tm.getTolerance() * 8;
		Rectangle2DDouble p = new Rectangle2DDouble(tm.getValues()[0]
				- tolerance / 2, tm.getValues()[1] - tolerance / 2, tolerance,
				tolerance);

		Geometry selectionRect = p.getEnvelope();

		ILayer activeLayer = vc.getSelectedLayers()[0];
		SpatialDataSourceDecorator ds = activeLayer.getDataSource();
		try {
			for (long i = 0; i < ds.getRowCount(); i++) {
				Point point = (Point) ds.getGeometry(i);
				Coordinate coordinate = point.getCoordinate();
				if (point.intersects(selectionRect)
						&& !points.contains(coordinate)) {
					points.add(coordinate);
					break;
				}
			}
		} catch (DriverException e) {
			throw new TransitionException(e);
		}

		if (points.size() == 0) {
			setStatus("Standby");
		}

		if (points.size() == 2) {
			toolFinished(vc, tm);
		}
	}

	@Override
	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return ToolUtilities.isActiveLayerEditable(vc)
				&& ToolUtilities.isActiveLayerVisible(vc)
				&& ToolUtilities.geometryTypeIs(vc, GeometryConstraint.POINT);
	}

	@Override
	public boolean isVisible(MapContext vc, ToolManager tm) {
		return isEnabled(vc, tm);
	}
}

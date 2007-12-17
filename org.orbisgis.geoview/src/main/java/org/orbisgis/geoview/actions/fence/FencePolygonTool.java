package org.orbisgis.geoview.actions.fence;

import java.awt.Color;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.GeometryValue;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.renderer.style.BasicStyle;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.instances.AbstractPolygonTool;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class FencePolygonTool extends AbstractPolygonTool {
	private final DataSourceFactory dsf = OrbisgisCore.getDSF();

	private DataSource dsResult;

	private VectorLayer layer;

	private final String fenceLayerName = "fence";

	protected void polygonDone(Polygon g, ViewContext vc, ToolManager tm)
			throws TransitionException {
		try {
			if (null != layer) {
				vc.getRootLayer().remove(layer);
			}
			buildFenceDatasource(g);
			layer = LayerFactory.createVectorialLayer(fenceLayerName, dsResult);
			BasicStyle style = new BasicStyle(Color.orange, 10, null);

			layer.setStyle(style);

			try {
				vc.getRootLayer().put(layer);
			} catch (CRSException e) {
				PluginManager.error("Bug in fence tool", e);
			}
		} catch (LayerException e) {
			PluginManager.error("Cannot use fence tool: " + e.getMessage(), e);
		}
	}

	public boolean isEnabled(ViewContext vc, ToolManager tm) {

		return true;
	}

	public boolean isVisible(ViewContext vc, ToolManager tm) {

		return true;
	}

	private String buildFenceDatasource(Geometry g) {

		ObjectMemoryDriver driver;
		try {
			driver = new ObjectMemoryDriver(new String[] { "the_geom" },
					new Type[] { TypeFactory.createType(Type.GEOMETRY) });

			if (!dsf.getSourceManager().exists(fenceLayerName)) {
				dsf.getSourceManager().register(fenceLayerName, driver);
			}

			dsResult = dsf.getDataSource(fenceLayerName);

			dsResult.open();

			while (dsResult.getRowCount() > 0) {
				dsResult.deleteRow(0);
			}

			if (dsResult.getFieldCount() == 0) {
				dsResult.addField("the_geom", TypeFactory
						.createType(Type.GEOMETRY));
			}

			dsResult.insertFilledRow(new Value[] { new GeometryValue(g) });

			dsResult.commit();

			return dsResult.getName();
		} catch (InvalidTypeException e) {
			throw new RuntimeException(e);
		} catch (DriverLoadException e) {
			throw new RuntimeException(e);
		} catch (DataSourceCreationException e) {
			throw new RuntimeException(e);
		} catch (DriverException e) {
			throw new RuntimeException(e);
		} catch (FreeingResourcesException e) {
			throw new RuntimeException(e);
		} catch (NonEditableDataSourceException e) {
			throw new RuntimeException(e);
		} catch (NoSuchTableException e) {
			throw new RuntimeException(e);
		}

	}

}

package org.orbisgis.plugin.renderer.sdsOrGrRendering;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.grap.model.GeoRaster;
import org.grap.processing.operation.Crop;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.renderer.utilities.EnvelopeUtil;
import org.orbisgis.plugin.view.layerModel.BasicLayer;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;
import org.orbisgis.plugin.view.ui.workbench.MapControl;
import org.orbisgis.plugin.view.ui.workbench.OGMapControlModel;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Polygon;

public class LayerRenderer {
	private static Logger logger = Logger.getLogger(OGMapControlModel.class
			.getName());

	private Envelope geographicPaintArea;

	private List<Exception> problems = new ArrayList<Exception>();

	public LayerRenderer(final MapControl mapControl) {
		geographicPaintArea = mapControl.getAdjustedExtentEnvelope();
		problems.clear();
	}

	public Object prepareRenderer(final BasicLayer basicLayer)
			throws DriverException, SyntaxException, DriverLoadException,
			NoSuchTableException, ExecutionException {
		if (basicLayer instanceof VectorLayer) {
			VectorLayer vl = (VectorLayer) basicLayer;
			if (vl.isVisible()) {
				final Envelope layerEnvelope = vl.getEnvelope();
				SpatialDataSourceDecorator sds = vl.getDataSource();
				sds.open();
				if (geographicPaintArea.contains(layerEnvelope)) {
					// all the geometries of the sds are
					// visible
					if (sds.getRowCount() > 0) {
						return sds;
					}
				} else if (geographicPaintArea.intersects(layerEnvelope)) {
					// some of the geometries of the sds are
					// visible
					final DataSourceFactory dsf = TempPluginServices.dsf;
					final String sql = "select * from '" + sds.getName()
							+ "' where Intersects(GeomFromText('POLYGON (( "
							+ geographicPaintArea.getMinX() + " "
							+ geographicPaintArea.getMinY() + ", "
							+ geographicPaintArea.getMaxX() + " "
							+ geographicPaintArea.getMinY() + ", "
							+ geographicPaintArea.getMaxX() + " "
							+ geographicPaintArea.getMaxY() + ", "
							+ geographicPaintArea.getMinX() + " "
							+ geographicPaintArea.getMaxY() + ", "
							+ geographicPaintArea.getMinX() + " "
							+ geographicPaintArea.getMinY() + "))'), "
							+ sds.getDefaultGeometry() + " )";
					final DataSource filtered = dsf.executeSQL(sql,
							DataSourceFactory.NORMAL);
					sds.cancel();
					sds = new SpatialDataSourceDecorator(filtered);
					sds.open();
					if (sds.getRowCount() > 0) {
						logger.info("drawing query:" + sql);
						return sds;
					}
				}
			}
		} else if (basicLayer instanceof RasterLayer) {
			RasterLayer rl = (RasterLayer) basicLayer;
			if (rl.isVisible()) {
				final GeoRaster gr = rl.getGeoRaster();
				gr.open();
				Envelope layerEnvelope = gr.getMetadata().getEnvelope();
				if (geographicPaintArea.contains(layerEnvelope)) {
					// all the GeoRaster is visible
					return gr;
				} else if (geographicPaintArea.intersects(layerEnvelope)) {
					// part of the GeoRaster is visible
					layerEnvelope = geographicPaintArea
							.intersection(layerEnvelope);

					if ((0 < layerEnvelope.getWidth())
							&& (0 < layerEnvelope.getHeight())) {
						return gr.doOperation(new Crop((Polygon) EnvelopeUtil
								.toGeometry(layerEnvelope)));
					}
				}
			}
		}
		return null;
	}
}
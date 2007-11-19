package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.LayerStackEntry;
import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.OGMapControlModel;
import org.orbisgis.geoview.layerModel.BasicLayer;
import org.orbisgis.geoview.layerModel.RasterLayer;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.renderer.utilities.EnvelopeUtil;
import org.orbisgis.pluginManager.PluginManager;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LinearRing;

public class LayerRenderer {
	private static Logger logger = Logger.getLogger(OGMapControlModel.class
			.getName());
	private Envelope geographicPaintArea;
	private MapControl mapControl;
	private BasicLayer basicLayer;
	private Map<Integer, LayerStackEntry> drawingStack;
	private int index;

	// public LayerRenderer(final MapControl mapControl) {
	// this.mapControl = mapControl;
	// geographicPaintArea = mapControl.getAdjustedExtentEnvelope();
	// problems.clear();
	// }

	public LayerRenderer(final MapControl mapControl,
			final Envelope geographicPaintArea, final BasicLayer basicLayer,
			final Map<Integer, LayerStackEntry> drawingStack, final int index) {
		this.mapControl = mapControl;
		this.geographicPaintArea = geographicPaintArea;
		this.basicLayer = basicLayer;
		this.drawingStack = drawingStack;
		this.index = index;
	}

	private LayerStackEntry prepareRenderer() throws DriverException,
			GeoreferencingException, SyntaxException, DriverLoadException,
			NoSuchTableException, ExecutionException, IOException,
			OperationException {

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
						return new LayerStackEntry(sds, vl.getStyle());
					}
				} else if (geographicPaintArea.intersects(layerEnvelope)) {
					// some of the geometries of the sds are
					// visible
					final DataSourceFactory dsf = OrbisgisCore.getDSF();
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
						return new LayerStackEntry(sds, vl.getStyle());
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
					final Envelope mapEnvelope = mapControl
							.fromGeographicToMap(layerEnvelope);
					return new LayerStackEntry(gr, rl.getStyle(), mapEnvelope);
				} else if (geographicPaintArea.intersects(layerEnvelope)) {
					// part of the GeoRaster is visible
					layerEnvelope = geographicPaintArea
							.intersection(layerEnvelope);

					if ((0 < layerEnvelope.getWidth())
							&& (0 < layerEnvelope.getHeight())) {
						final GeoRaster croppedGr = gr
								.crop((LinearRing) EnvelopeUtil
										.toGeometry(layerEnvelope));
						final Envelope mapEnvelope = mapControl
								.fromGeographicToMap(layerEnvelope);
						return new LayerStackEntry(croppedGr, rl.getStyle(),
								mapEnvelope);
					}
				}
			}
		}
		return null;
	}

	public void run() {
		try {
			drawingStack.put(index, prepareRenderer());
		} catch (SyntaxException e) {
			throw new RuntimeException("bug!");
		} catch (DriverLoadException e) {
			throw new RuntimeException("bug!");
		} catch (DriverException e) {
			PluginManager.error("Error while preparing rendering", e);
		} catch (NoSuchTableException e) {
			throw new RuntimeException("bug!");
		} catch (ExecutionException e) {
			throw new RuntimeException("bug!");
		} catch (IOException e) {
			PluginManager.error("Error while preparing rendering", e);
		} catch (GeoreferencingException e) {
			PluginManager.error("Error while preparing rendering", e);
		} catch (OperationException e) {
			PluginManager.error("Error while preparing rendering", e);
		}
	}
}
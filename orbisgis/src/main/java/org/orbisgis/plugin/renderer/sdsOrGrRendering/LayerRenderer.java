package org.orbisgis.plugin.renderer.sdsOrGrRendering;

import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.orbisgis.plugin.view.ui.workbench.LayerStackEntry;
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
		problems.clear();
	}

	private LayerStackEntry prepareRenderer() throws DriverException,
			SyntaxException, DriverLoadException, NoSuchTableException,
			ExecutionException {

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
					final ImageProcessor rescaledImageProcessor = gr
							.getImagePlus().getProcessor().resize(
									(int) mapEnvelope.getWidth(),
									(int) mapEnvelope.getHeight());
					return new LayerStackEntry(rescaledImageProcessor, rl
							.getStyle(), mapEnvelope);
				} else if (geographicPaintArea.intersects(layerEnvelope)) {
					// part of the GeoRaster is visible
					layerEnvelope = geographicPaintArea
							.intersection(layerEnvelope);

					if ((0 < layerEnvelope.getWidth())
							&& (0 < layerEnvelope.getHeight())) {
						final GeoRaster croppedGr = gr.doOperation(new Crop(
								(Polygon) EnvelopeUtil
										.toGeometry(layerEnvelope)));
						final Envelope mapEnvelope = mapControl
								.fromGeographicToMap(layerEnvelope);
						final ImageProcessor rescaledImageProcessor = croppedGr
								.getImagePlus().getProcessor().resize(
										(int) mapEnvelope.getWidth(),
										(int) mapEnvelope.getHeight());
						return new LayerStackEntry(rescaledImageProcessor, rl
								.getStyle(), mapEnvelope);
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
			reportProblem(e);
		} catch (DriverLoadException e) {
			reportProblem(e);
		} catch (DriverException e) {
			reportProblem(e);
		} catch (NoSuchTableException e) {
			reportProblem(e);
		} catch (ExecutionException e) {
			reportProblem(e);
		}
	}

	private void reportProblem(Exception e) {
		problems.add(e);
		throw new RuntimeException(e);
	}
}
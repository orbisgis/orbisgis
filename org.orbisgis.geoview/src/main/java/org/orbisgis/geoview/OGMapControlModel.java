package org.orbisgis.geoview;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gdms.data.SyntaxException;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.geoview.layerModel.BasicLayer;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.ILayerAction;
import org.orbisgis.geoview.layerModel.LayerCollection;
import org.orbisgis.geoview.layerModel.LayerCollectionEvent;
import org.orbisgis.geoview.layerModel.LayerCollectionListener;
import org.orbisgis.geoview.layerModel.LayerListenerEvent;
import org.orbisgis.geoview.renderer.sdsOrGrRendering.DataSourceRenderer;
import org.orbisgis.geoview.renderer.sdsOrGrRendering.GeoRasterRenderer;
import org.orbisgis.geoview.renderer.sdsOrGrRendering.LayerRenderer;

import com.vividsolutions.jts.geom.Envelope;

public class OGMapControlModel implements MapControlModel {
	private static Logger logger = Logger.getLogger(OGMapControlModel.class
			.getName());

	private LayerCollection root;

	private MapControl mapControl;

	private List<Exception> problems = new ArrayList<Exception>();

	private LayerListener layerListener;

	private Map<Integer, LayerStackEntry> drawingStack;

	private DataSourceRenderer dataSourceRenderer;

	private GeoRasterRenderer geoRasterRenderer;

	public OGMapControlModel() {
		this.root = new LayerCollection("root");
		layerListener = new LayerListener();
		listen(root);

		problems.clear();
	}

	public void setMapControl(MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public MapControl getMapControl() {
		return mapControl;
	}

	private void listen(ILayer node) {
		LayerCollection.processLayersNodes(node, new ILayerAction() {
			public void action(ILayer layer) {
				layer.addLayerListener(layerListener);
				if (layer instanceof LayerCollection) {
					((LayerCollection) layer)
							.addCollectionListener(layerListener);
				}
			}
		});
	}

	public void draw(final Graphics2D graphics) {
		drawingStack = new HashMap<Integer, LayerStackEntry>();
		dataSourceRenderer = new DataSourceRenderer(mapControl);
		geoRasterRenderer = new GeoRasterRenderer(mapControl);

		// prepare rendering...
		LayerCollection.processLayersLeaves(root, new ILayerAction() {
			private int index = 0;

			public void action(ILayer layer) {
				BasicLayer basicLayer = (BasicLayer) layer;
				try {
					// sequential version...
					new LayerRenderer(mapControl, mapControl
							.getAdjustedExtentEnvelope(), basicLayer,
							drawingStack, index++).run();
				} catch (SyntaxException e) {
					reportProblem(e);
				} catch (DriverLoadException e) {
					reportProblem(e);
				}
			}
		});

		for (int i = drawingStack.size() - 1; i >= 0; i--) {
			final LayerStackEntry item = drawingStack.get(i);
			if ((null != item) && (null != item.getDataSource())) {
				final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
						item.getDataSource());
				dataSourceRenderer.paint(graphics, sds, item.getStyle());
			} else if ((null != item) && (null != item.getGeoRaster())) {
				try {
					geoRasterRenderer.paint(graphics, item.getGeoRaster(), item
							.getMapEnvelope(), item.getStyle());
				} catch (IOException e) {
					reportProblem(e);
				}
			}
		}
		// for (LayerStackEntry item : drawingStack) {
		// if (null != item.getDataSource()) {
		// try {
		// final SpatialDataSourceDecorator sds = new
		// SpatialDataSourceDecorator(
		// item.getDataSource());
		// dataSourceRenderer.paint(graphics, sds, item.getStyle());
		// } catch (DriverException e) {
		// reportProblem(e);
		// }
		// } else if (null != item.getImageProcessor()) {
		// geoRasterRenderer.paint(graphics, item.getImageProcessor(),
		// item.getMapEnvelope(), item.getStyle());
		// }
		// }
		closeDataSources();
	}

	private void closeDataSources() {
		boolean flag = false;
		for (LayerStackEntry layerStackEntry : drawingStack.values()) {
			if ((null != layerStackEntry)
					&& (null != layerStackEntry.getDataSource())) {
				try {
					layerStackEntry.getDataSource().cancel();
					flag = true;
				} catch (DriverException e) {
					reportProblem(e);
				}
			}
		}
		if (flag) {
			logger.info("closing data sources...");
		}
	}

	private void reportProblem(Exception e) {
		problems.add(e);
		throw new RuntimeException(e);
	}

	// private ReferencedEnvelope getEnvelope(Rectangle2D bbox,
	// CoordinateReferenceSystem crs) {
	// Envelope env = new Envelope(new Coordinate(bbox.getMinX(), bbox
	// .getMinY()), new Coordinate(bbox.getMaxX(), bbox.getMaxY()));
	// return new ReferencedEnvelope(env, crs);
	// }

	public Exception[] getProblems() {
		return problems.toArray(new Exception[0]);
	}

	public Rectangle2D getMapArea() {
		final LayerAction la = new LayerAction();
		LayerCollection.processLayersLeaves(root, la);
		final Envelope globalEnv = la.getGlobalEnvelope();
		return (null == globalEnv) ? null : new Rectangle2D.Double(globalEnv
				.getMinX(), globalEnv.getMinY(), globalEnv.getWidth(),
				globalEnv.getHeight());
	}

	private class LayerListener implements LayerCollectionListener,
			org.orbisgis.geoview.layerModel.LayerListener {

		public void layerAdded(LayerCollectionEvent listener) {
			for (ILayer layer : listener.getAffected()) {
				layer.addLayerListener(this);
				if (mapControl.getAdjustedExtent() == null) {
					final Envelope e = layer.getEnvelope();
					final Rectangle2D.Double newExtent = new Rectangle2D.Double(
							e.getMinX(), e.getMinY(), e.getWidth(), e
									.getHeight());

					mapControl.setExtent(newExtent);
				} else {
					mapControl.drawMap();
				}
			}
		}

		public void layerMoved(LayerCollectionEvent listener) {
			mapControl.drawMap();
		}

		public void layerRemoved(LayerCollectionEvent listener) {
			for (ILayer layer : listener.getAffected()) {
				layer.removeLayerListener(this);
				mapControl.drawMap();
			}
		}

		public void nameChanged(LayerListenerEvent e) {
		}

		public void visibilityChanged(LayerListenerEvent e) {
			mapControl.drawMap();
		}

		public void styleChanged(LayerListenerEvent e) {
			mapControl.drawMap();
		}
	}

	public LayerCollection getLayers() {
		return root;
	}
}
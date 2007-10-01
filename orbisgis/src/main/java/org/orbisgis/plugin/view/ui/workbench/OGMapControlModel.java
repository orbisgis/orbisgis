package org.orbisgis.plugin.view.ui.workbench;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.apache.log4j.Logger;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.plugin.renderer.sdsOrGrRendering.DataSourceRenderer;
import org.orbisgis.plugin.renderer.sdsOrGrRendering.GeoRasterRenderer;
import org.orbisgis.plugin.renderer.sdsOrGrRendering.LayerRenderer;
import org.orbisgis.plugin.view.layerModel.BasicLayer;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.ILayerAction;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import org.orbisgis.plugin.view.layerModel.LayerCollectionEvent;
import org.orbisgis.plugin.view.layerModel.LayerCollectionListener;
import org.orbisgis.plugin.view.layerModel.LayerListenerEvent;

import com.hardcode.driverManager.DriverLoadException;
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

	private CyclicBarrier cyclicBarrier;

	private final static int NUMBER_OF_THREADS = Runtime.getRuntime()
			.availableProcessors();

	public OGMapControlModel(final LayerCollection root) {
		this.root = root;
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
		cyclicBarrier = new CyclicBarrier(NUMBER_OF_THREADS + 1);

		// prepare rendering...
		LayerCollection.processLayersLeaves(root, new ILayerAction() {
			private int index = 0;

			public void action(ILayer layer) {
				BasicLayer basicLayer = (BasicLayer) layer;
				try {
					// sequential version...
					// new LayerRenderer(mapControl, mapControl
					// .getAdjustedExtentEnvelope(), basicLayer,
					// cyclicBarrier, drawingStack, index++).run();
					// multi-threaded version...
					new Thread(new LayerRenderer(mapControl, mapControl
							.getAdjustedExtentEnvelope(), basicLayer,
							cyclicBarrier, drawingStack, index++)).start();
					// synchronization...
					cyclicBarrier.await();
				} catch (SyntaxException e) {
					reportProblem(e);
				} catch (DriverLoadException e) {
					reportProblem(e);
				} catch (InterruptedException e) {
					reportProblem(e);
				} catch (BrokenBarrierException e) {
					reportProblem(e);
				}
			}
		});

		for (int i = drawingStack.size() - 1; i >= 0; i--) {
			final LayerStackEntry item = drawingStack.get(i);
			if ((null != item) && (null != item.getDataSource())) {
				try {
					final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
							item.getDataSource());
					dataSourceRenderer.paint(graphics, sds, item.getStyle());
				} catch (DriverException e) {
					reportProblem(e);
				}
			} else if ((null != item) && (null != item.getImageProcessor())) {
				geoRasterRenderer.paint(graphics, item.getImageProcessor(),
						item.getMapEnvelope(), item.getStyle());
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
		// Envelope e = mc.getAreaOfInterest();
		// return new Rectangle2D.Double(e.getMinX(), e.getMinY(), e.getWidth(),
		// e.getHeight());
	}

	private class LayerListener implements LayerCollectionListener,
			org.orbisgis.plugin.view.layerModel.LayerListener {

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
}
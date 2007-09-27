/**
 *
 */
package org.orbisgis.plugin.view.ui.workbench;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.grap.model.GeoRaster;
import org.orbisgis.plugin.renderer.sdsOrGrRendering.DataSourceRenderer;
import org.orbisgis.plugin.renderer.sdsOrGrRendering.GeoRasterRenderer;
import org.orbisgis.plugin.renderer.sdsOrGrRendering.LayerRenderer;
import org.orbisgis.plugin.renderer.style.Style;
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

	private LinkedList<LayerStackEntry> drawingStack;

	private DataSourceRenderer dataSourceRenderer;

	private GeoRasterRenderer geoRasterRenderer;

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

	private class LayerStackEntry {
		private Object sdsOrGr;

		private Style style;

		public LayerStackEntry(final Object sdsOrGr, final Style style) {
			this.sdsOrGr = sdsOrGr;
			this.style = style;
		}

		public Object getSdsOrGr() {
			return sdsOrGr;
		}

		public Style getStyle() {
			return style;
		}
	}

	public void draw(final Graphics2D graphics) {
		drawingStack = new LinkedList<LayerStackEntry>();
		dataSourceRenderer = new DataSourceRenderer(mapControl);
		geoRasterRenderer = new GeoRasterRenderer(mapControl);

		LayerCollection.processLayersLeaves(root, new ILayerAction() {
			private LayerRenderer layerRenderer = new LayerRenderer(mapControl);

			public void action(ILayer layer) {
				BasicLayer basicLayer = (BasicLayer) layer;
				try {
					drawingStack
							.addFirst(new LayerStackEntry(layerRenderer
									.prepareRenderer(basicLayer), basicLayer
									.getStyle()));
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
		});

		for (LayerStackEntry item : drawingStack) {
			if (item.getSdsOrGr() instanceof DataSource) {
				try {
					final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
							(DataSource) item.getSdsOrGr());
					dataSourceRenderer.paint(graphics, sds, item.getStyle());
				} catch (DriverException e) {
					reportProblem(e);
				}
			} else if (item.getSdsOrGr() instanceof GeoRaster) {
				final GeoRaster geoRaster = (GeoRaster) item.getSdsOrGr();
				geoRasterRenderer.paint(graphics, geoRaster, item.getStyle());
			}
		}
		closeDataSources();
	}

	private void closeDataSources() {
		boolean flag = false;
		for (LayerStackEntry stackEntry : drawingStack) {
			if (stackEntry.getSdsOrGr() instanceof DataSource) {
				try {
					((DataSource) stackEntry.getSdsOrGr()).cancel();
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
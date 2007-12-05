package org.orbisgis.geoview;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;

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
import org.orbisgis.IProgressMonitor;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.ILayerAction;
import org.orbisgis.geoview.layerModel.LayerCollection;
import org.orbisgis.geoview.layerModel.LayerCollectionEvent;
import org.orbisgis.geoview.layerModel.LayerListener;
import org.orbisgis.geoview.layerModel.LayerListenerEvent;
import org.orbisgis.geoview.layerModel.RasterLayer;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.renderer.sdsOrGrRendering.DataSourceRenderer;
import org.orbisgis.geoview.renderer.sdsOrGrRendering.GeoRasterRenderer;
import org.orbisgis.geoview.renderer.utilities.EnvelopeUtil;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.background.LongProcess;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LinearRing;

public class OGMapControlModel implements MapControlModel {
	private static Logger logger = Logger.getLogger(OGMapControlModel.class
			.getName());

	private MapControl mapControl;

	private ModelLayerListener layerListener;

	private ILayer root;

	public OGMapControlModel(ILayer root) {
		this.root = root;
		layerListener = new ModelLayerListener();
		root.addLayerListenerRecursively(layerListener);
	}

	public void setMapControl(MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public MapControl getMapControl() {
		return mapControl;
	}

	public void draw(final Graphics2D graphics) {
		Drawer d = new Drawer(graphics);
		PluginManager.backgroundOperation(d);
	}

	public Rectangle2D getMapArea() {
		final LayerAction la = new LayerAction();
		LayerCollection.processLayersLeaves(root, la);
		final Envelope globalEnv = la.getGlobalEnvelope();
		return (null == globalEnv) ? null : new Rectangle2D.Double(globalEnv
				.getMinX(), globalEnv.getMinY(), globalEnv.getWidth(),
				globalEnv.getHeight());
	}

	private class ModelLayerListener implements LayerListener {
		public void layerAdded(LayerCollectionEvent listener) {
			for (ILayer layer : listener.getAffected()) {
				layer.addLayerListenerRecursively(this);
				if (mapControl.getAdjustedExtent() == null) {
					final Envelope e = layer.getEnvelope();
					if (e != null) {
						final Rectangle2D.Double newExtent = new Rectangle2D.Double(
								e.getMinX(), e.getMinY(), e.getWidth(), e
										.getHeight());

						mapControl.setExtent(newExtent);
					}
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
				layer.removeLayerListenerRecursively(this);
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

	public ILayer getLayers() {
		return root;
	}

	public class Drawer implements LongProcess {

		private Graphics2D graphics;

		public Drawer(Graphics2D graphics) {
			this.graphics = graphics;
		}

		public String getTaskName() {
			return "Drawing";
		}

		public void run(IProgressMonitor pm) {
			try {
				final ArrayList<LayerStackEntry> drawingStack = new ArrayList<LayerStackEntry>();
				Rectangle2D adjustedExtent = mapControl.getAdjustedExtent();
				final Envelope env = new Envelope(new Coordinate(adjustedExtent
						.getMinX(), adjustedExtent.getMinY()), new Coordinate(
						adjustedExtent.getMaxX(), adjustedExtent.getMaxY()));

				DataSourceRenderer dataSourceRenderer = new DataSourceRenderer(
						mapControl);
				GeoRasterRenderer geoRasterRenderer = new GeoRasterRenderer(
						mapControl);

				// build layer stack
				LayerCollection.processLayersLeaves(root, new ILayerAction() {
					public void action(ILayer layer) {
						// sequential version...
						LayerStackEntry entry;
						try {
							entry = getEntry(env, layer);
							if (entry != null) {
								drawingStack.add(entry);
							}
						} catch (DriverException e) {
							PluginManager.error(
									"Won't draw " + layer.getName(), e);
						} catch (ExecutionException e) {
							PluginManager.error(
									"Won't draw " + layer.getName(), e);
						} catch (IOException e) {
							PluginManager.error(
									"Won't draw " + layer.getName(), e);
						} catch (OperationException e) {
							PluginManager.error(
									"Won't draw " + layer.getName(), e);
						}
					}
				});

				for (int i = drawingStack.size() - 1; i >= 0; i--) {
					final LayerStackEntry item = drawingStack.get(i);
					try {
						logger.debug("Drawing " + item.getLayerName());
						if ((null != item) && (null != item.getDataSource())) {
							final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
									item.getDataSource());
							sds.open();
							dataSourceRenderer.paint(graphics, sds, item
									.getStyle());
							sds.cancel();
						} else if ((null != item)
								&& (null != item.getGeoRaster())) {
							logger.debug("raster envelope: "
									+ item.getMapEnvelope());
							geoRasterRenderer.paint(graphics, item
									.getGeoRaster(), item.getMapEnvelope(),
									item.getStyle());
						}
					} catch (IOException e) {
						PluginManager.error("Cannot draw raster:"
								+ item.getLayerName(), e);
					} catch (GeoreferencingException e) {
						PluginManager.error("Cannot draw raster: "
								+ item.getLayerName(), e);
					} catch (DriverException e) {
						PluginManager.error("Cannot draw : "
								+ item.getLayerName(), e);
					}
					pm.progressTo(100 - (100 * i) / drawingStack.size());
				}
			} catch (RuntimeException e) {
				throw e;
			} catch (Error e) {
				throw e;
			} finally {
				getMapControl().drawFinished();
			}
		}

		private LayerStackEntry getEntry(Envelope geographicPaintArea,
				ILayer layer) throws DriverException, ExecutionException,
				IOException, OperationException {

			if (layer instanceof VectorLayer) {
				VectorLayer vl = (VectorLayer) layer;
				if (vl.isVisible()) {
					final Envelope layerEnvelope = vl.getEnvelope();
					SpatialDataSourceDecorator sds = vl.getDataSource();
					if (geographicPaintArea.contains(layerEnvelope)) {
						// all the geometries of the sds are
						// visible
						if (sds.getRowCount() > 0) {
							return new LayerStackEntry(sds, vl.getStyle(), vl
									.getName());
						}
					} else if (geographicPaintArea.intersects(layerEnvelope)) {
						// some of the geometries of the sds are
						// visible
						final DataSourceFactory dsf = OrbisgisCore.getDSF();
						final String sql = "select * from '"
								+ sds.getName()
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
						DataSource filtered;
						try {
							logger.debug("filtering to draw: " + sql);
							filtered = dsf.executeSQL(sql,
									DataSourceFactory.NORMAL);
						} catch (SyntaxException e) {
							throw new RuntimeException("bug");
						} catch (DriverLoadException e) {
							throw new RuntimeException("bug");
						} catch (NoSuchTableException e) {
							throw new RuntimeException("bug");
						}
						sds = new SpatialDataSourceDecorator(filtered);
						sds.open();
						if (sds.getRowCount() > 0) {
							logger.info("drawing query:" + sql);
							return new LayerStackEntry(sds, vl.getStyle(), vl
									.getName());
						}
						sds.cancel();
					}
				}
			} else if (layer instanceof RasterLayer) {
				RasterLayer rl = (RasterLayer) layer;
				if (rl.isVisible()) {
					final GeoRaster gr = rl.getGeoRaster();
					Envelope layerEnvelope = gr.getMetadata().getEnvelope();
					if (geographicPaintArea.contains(layerEnvelope)) {
						// all the GeoRaster is visible
						final Envelope mapEnvelope = mapControl
								.fromGeographicToMap(layerEnvelope);
						return new LayerStackEntry(gr, rl.getStyle(),
								mapEnvelope, rl.getName());
					} else if (geographicPaintArea.intersects(layerEnvelope)) {
						// part of the GeoRaster is visible
						layerEnvelope = geographicPaintArea
								.intersection(layerEnvelope);

						if ((0 < layerEnvelope.getWidth())
								&& (0 < layerEnvelope.getHeight())) {
							GeoRaster croppedGr;
							try {
								croppedGr = gr.crop((LinearRing) EnvelopeUtil
										.toGeometry(layerEnvelope));
							} catch (GeoreferencingException e) {
								throw new RuntimeException("bug");
							}
							final Envelope mapEnvelope = mapControl
									.fromGeographicToMap(layerEnvelope);
							if (((int) mapEnvelope.getWidth() != 0)
									&& ((int) mapEnvelope.getHeight() != 0)) {
								return new LayerStackEntry(croppedGr, rl
										.getStyle(), mapEnvelope, rl.getName());
							}
						}
					}
				}
			}
			return null;
		}

	}
}
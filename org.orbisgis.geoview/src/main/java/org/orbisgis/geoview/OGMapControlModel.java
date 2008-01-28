/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
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
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.SyntaxException;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
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
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.background.LongProcess;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

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
				GeoRasterRenderer geoRasterRenderer = new GeoRasterRenderer();

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
					if (geographicPaintArea.intersects(layerEnvelope)) {
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
							throw new RuntimeException("bug", e);
						} catch (DriverLoadException e) {
							throw new RuntimeException("bug", e);
						} catch (NoSuchTableException e) {
							throw new RuntimeException("bug", e);
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
					if (geographicPaintArea.intersects(layerEnvelope)) {
						// part or all of the GeoRaster is visible
						final Envelope mapEnvelope = mapControl
								.toPixel(layerEnvelope);
						return new LayerStackEntry(gr, rl.getStyle(),
								mapEnvelope, rl.getName());
					}
				}
			}
			return null;
		}

	}
}
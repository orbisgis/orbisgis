/**
 *
 */
package org.orbisgis.plugin.view.ui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.gdms.data.SpatialDataSource;
import org.gdms.data.driver.DriverException;
import org.gdms.geotoolsAdapter.FeatureCollectionAdapter;
import org.gdms.geotoolsAdapter.GeometryAttributeTypeAdapter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.renderer.lite.StreamingRenderer;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.ILayerAction;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import org.orbisgis.plugin.view.layerModel.LayerCollectionEvent;
import org.orbisgis.plugin.view.layerModel.LayerCollectionListener;
import org.orbisgis.plugin.view.layerModel.LayerListenerEvent;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class OGMapControlModel implements MapControlModel {

	private LayerCollection root;

	private MapContext mc;

	private MapControl mapControl;

	private StreamingRenderer streamingRenderer;

	private List<Exception> problems = new ArrayList<Exception>();

	private LayerListener layerListener;

	public OGMapControlModel(LayerCollection root) {
		this.root = root;
		layerListener = new LayerListener();
		listen(root);

		problems.clear();
		prepareStreamingRenderer();
	}

	public void setMapControl(MapControl mapControl) {
		this.mapControl = mapControl;
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

	private void prepareStreamingRenderer() {
		mc = new DefaultMapContext(root.getCoordinateReferenceSystem());
		GeometryAttributeTypeAdapter.currentCRS = root
				.getCoordinateReferenceSystem();

		LayerCollection.processLayersLeaves(root, new ILayerAction() {
			public void action(ILayer layer) {
				if (layer instanceof VectorLayer) {
					try {
						VectorLayer vl = (VectorLayer) layer;
						if (vl.isVisible()) {
							SpatialDataSource sds = vl.getDataSource();
							sds.beginTrans();
							mc.addLayer(new FeatureCollectionAdapter(sds), vl
									.getStyle());
						}
					} catch (DriverException e) {
						problems.add(e);
					}
				} else if (layer instanceof RasterLayer) {
					RasterLayer rl = (RasterLayer) layer;
					if (rl.isVisible()) {
						GridCoverage gc = rl.getGridCoverage(); 
						mc.addLayer(gc, rl.getStyle());
					}
				}
			}
		});

		streamingRenderer = new StreamingRenderer();
		streamingRenderer.setContext(mc);
	}

	public void draw(BufferedImage image, Rectangle2D bbox, int imageWidth,
			int imageHeight, Color backColor) {
		prepareStreamingRenderer();
		streamingRenderer.paint(image.createGraphics(), new Rectangle(0, 0,
				imageWidth, imageHeight), getEnvelope(bbox, root
				.getCoordinateReferenceSystem()));
	}

	private ReferencedEnvelope getEnvelope(Rectangle2D bbox,
			CoordinateReferenceSystem crs) {
		Envelope env = new Envelope(new Coordinate(bbox.getMinX(), bbox
				.getMinY()), new Coordinate(bbox.getMaxX(), bbox.getMaxY()));
		return new ReferencedEnvelope(env, crs);
	}

	public Exception[] getProblems() {
		return problems.toArray(new Exception[0]);
	}

	private class LayerAction implements ILayerAction {
		private Envelope globalEnvelope = null;

		public void action(ILayer layer) {
			if (layer instanceof VectorLayer) {
				VectorLayer vl = (VectorLayer) layer;
				if (null != vl.getDataSource()) {
					try {
						Envelope env = vl.getDataSource().getFullExtent();
						if (null == globalEnvelope) {
							globalEnvelope = env;
						} else {
							globalEnvelope.expandToInclude(env);
						}
					} catch (DriverException e) {
						e.printStackTrace();
					}
				}
			} else if (layer instanceof RasterLayer) {
				RasterLayer rl = (RasterLayer) layer;
				if (null != rl.getGridCoverage()) {
					org.opengis.spatialschema.geometry.Envelope envTmp = rl
							.getGridCoverage().getEnvelope();
					double[] lowerCorner = envTmp.getLowerCorner()
							.getCoordinates();
					double[] upperCorner = envTmp.getUpperCorner()
							.getCoordinates();
					Envelope env = new Envelope(lowerCorner[0], upperCorner[0],
							lowerCorner[1], upperCorner[1]);
					if (null == globalEnvelope) {
						globalEnvelope = env;
					} else {
						globalEnvelope.expandToInclude(env);
					}
				}
			}
		}

		public Envelope getGlobalEnvelope() {
			return globalEnvelope;
		}
	}

	public Rectangle2D getMapArea() {
		LayerAction la = new LayerAction();
		LayerCollection.processLayersLeaves(root, la);
		Envelope globalEnv = la.getGlobalEnvelope();
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
			}
		}

		public void layerMoved(LayerCollectionEvent listener) {
		}

		public void layerRemoved(LayerCollectionEvent listener) {
			for (ILayer layer : listener.getAffected()) {
				layer.removeLayerListener(this);
			}
		}

		public void nameChanged(LayerListenerEvent e) {
		}

		public void visibilityChanged(LayerListenerEvent e) {
			mapControl.drawMap();
		}
	}
}
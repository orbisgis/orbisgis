package org.orbisgis.geoview.renderer;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GrapImagePlus;
import org.orbisgis.IProgressMonitor;
import org.orbisgis.NullProgressMonitor;
import org.orbisgis.geoview.MapTransform;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.RasterLayer;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.renderer.legend.Legend;
import org.orbisgis.geoview.renderer.legend.RenderException;
import org.orbisgis.geoview.renderer.legend.RenderUtils;
import org.orbisgis.geoview.renderer.legend.Symbol;
import org.orbisgis.pluginManager.PluginManager;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class Renderer {

	private static Logger logger = Logger.getLogger(Renderer.class.getName());

	public void draw(Image img, Envelope extent, ILayer layer,
			IProgressMonitor pm) {
		MapTransform mt = new MapTransform();
		mt.resizeImage(img.getWidth(null), img.getHeight(null));
		mt.setExtent(extent);

		ILayer[] layers = layer.getLayersRecursively();

		for (int i = layers.length - 1; i >= 0; i--) {
			if (pm.isCancelled()) {
				break;
			} else {
				layer = layers[i];
				if (layer.isVisible()) {
					try {
						logger.debug("Drawing " + layer.getName());
						long t1 = System.currentTimeMillis();
						if (layer instanceof VectorLayer) {
							drawVectorLayer(mt, layer, img, extent, pm);
						} else if (layer instanceof RasterLayer) {
							drawRasterLayer(mt, layer, img, extent, pm);
						} else {
							logger.warn("Not drawn: " + layer.getName());
						}
						pm.progressTo(100 - (100 * i) / layers.length);
						long t2 = System.currentTimeMillis();
						logger.info("Rendering time:" + (t2 - t1));
					} catch (IOException e) {
						PluginManager.error("Cannot draw raster:"
								+ layer.getName(), e);
					} catch (GeoreferencingException e) {
						PluginManager.error("Cannot draw raster: "
								+ layer.getName(), e);
					} catch (DriverException e) {
						PluginManager.error("Cannot draw : " + layer.getName(),
								e);
					}
				}
			}
		}

	}

	private void drawRasterLayer(MapTransform mt, ILayer layer, Image img,
			Envelope extent, IProgressMonitor pm) throws IOException,
			GeoreferencingException {
		RasterLayer rl = (RasterLayer) layer;
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		logger.debug("raster envelope: " + layer.getEnvelope());
		g2.setComposite(AlphaComposite.SrcOver);
		GeoRaster geoRaster = rl.getGeoRaster();
		Envelope layerEnvelope = geoRaster.getMetadata().getEnvelope();
		Envelope layerPixelEnvelope = null;
		if (extent.intersects(layerEnvelope)) {

			BufferedImage mapControlImage = (BufferedImage) img;
			BufferedImage layerImage = new BufferedImage(mapControlImage
					.getWidth(), mapControlImage.getHeight(),
					BufferedImage.TYPE_INT_ARGB);

			// part or all of the GeoRaster is visible
			layerPixelEnvelope = mt.toPixel(layerEnvelope);
			final GrapImagePlus ip = geoRaster.getGrapImagePlus();
			Graphics2D gLayer = layerImage.createGraphics();
			gLayer.drawImage(ip.getImage(), (int) layerPixelEnvelope.getMinX(),
					(int) layerPixelEnvelope.getMinY(),
					(int) layerPixelEnvelope.getWidth() + 1,
					(int) layerPixelEnvelope.getHeight() + 1, null);
			pm.startTask("Drawing " + layer.getName());
			g2.drawImage(layerImage, 0, 0, null);
			pm.endTask();
		}
	}

	private void drawVectorLayer(MapTransform mt, ILayer layer, Image img,
			Envelope extent, IProgressMonitor pm) throws DriverException {
		VectorLayer vl = (VectorLayer) layer;
		Legend legend = vl.getLegend();
		SpatialDataSourceDecorator sds = vl.getDataSource();
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		try {
			if (sds.getFullExtent().intersects(extent)) {
				DefaultRendererPermission permission = new DefaultRendererPermission();
				for (int legendLayer = 0; legendLayer < legend.getNumLayers(); legendLayer++) {
					legend.setLayer(legendLayer);
					long rowCount = sds.getRowCount();
					pm.startTask("Drawing " + layer.getName());
					for (int i = 0; i < rowCount; i++) {
						if (i / 10000 == i / 10000.0) {
							if (pm.isCancelled()) {
								break;
							} else {
								pm.progressTo((int) (100 * i / rowCount));
							}
						}
						Symbol sym = legend.getSymbol(i);
						Geometry g = sds.getGeometry(i);
						if (g.getEnvelopeInternal().intersects(extent)) {
							Envelope symbolEnvelope;
							if (g.getGeometryType()
									.equals("GeometryCollection")) {
								symbolEnvelope = drawGeometryCollection(mt, g2,
										sym, g, permission);
							} else {
								symbolEnvelope = sym.draw(g2, g, mt
										.getAffineTransform(), permission);
							}
							if (symbolEnvelope != null) {
								permission.addUsedArea(symbolEnvelope);
							}
						}
					}
					pm.endTask();
				}
			}
		} catch (RenderException e) {
			PluginManager.warning("Cannot draw layer: " + vl.getName(), e);
		}
	}

	/**
	 * For geometry collections we need to filter the symbol composite before
	 * drawing
	 *
	 * @param mt
	 * @param g2
	 * @param sym
	 * @param g
	 * @param permission
	 * @throws DriverException
	 */
	private Envelope drawGeometryCollection(MapTransform mt, Graphics2D g2,
			Symbol sym, Geometry g, DefaultRendererPermission permission)
			throws DriverException {
		if (g.getGeometryType().equals("GeometryCollection")) {
			Envelope ret = null;
			for (int j = 0; j < g.getNumGeometries(); j++) {
				Geometry childGeom = g.getGeometryN(j);
				Envelope area = drawGeometryCollection(mt, g2, sym, childGeom,
						permission);
				if (ret == null) {
					ret = area;
				} else {
					ret.expandToInclude(area);
				}
			}

			return ret;
		} else {
			sym = RenderUtils.buildSymbolToDraw(sym, g);
			return sym.draw(g2, g, mt.getAffineTransform(), permission);
		}
	}

	public void draw(Image img, Envelope extent, ILayer layer) {
		draw(img, extent, layer, new NullProgressMonitor());
	}

	private class DefaultRendererPermission implements RenderPermission {

		private Quadtree quadtree;

		public DefaultRendererPermission() {
			quadtree = new Quadtree();
		}

		public void addUsedArea(Envelope area) {
			quadtree.insert(area, area);
		}

		@SuppressWarnings("unchecked")
		public boolean canDraw(Envelope area) {
			List<Envelope> list = quadtree.query(area);
			for (Envelope envelope : list) {
				if ((envelope.intersects(area)) || envelope.contains(area)) {
					return false;
				}
			}

			return true;
		}
	}
}

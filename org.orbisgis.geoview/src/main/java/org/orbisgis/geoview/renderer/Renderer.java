package org.orbisgis.geoview.renderer;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
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
						if (layer instanceof VectorLayer) {
							pm.startTask("drawing " + layer.getName(),
									100 * i / layers.length);
							drawVectorLayer(mt, layer, img, extent, pm);
							pm.endTask();
						} else if (layer instanceof RasterLayer) {
							drawRasterLayer(mt, layer, img, extent);
						} else {
							logger.warn("Not drawn: " + layer.getName());
						}
						pm.progressTo(100 - (100 * i) / layers.length);
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
			Envelope extent) throws IOException, GeoreferencingException {
		RasterLayer rl = (RasterLayer) layer;
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		logger.debug("raster envelope: " + layer.getEnvelope());
		g2.setComposite(AlphaComposite.SrcOver);
		GeoRaster geoRaster = rl.getGeoRaster();
		Envelope layerEnvelope = geoRaster.getMetadata().getEnvelope();
		Envelope layerPixelEnvelope = null;
		if (extent.intersects(layerEnvelope)) {
			// part or all of the GeoRaster is visible
			layerPixelEnvelope = mt.toPixel(layerEnvelope);
		}
		final GrapImagePlus ip = geoRaster.getGrapImagePlus();
		/*
		 * TODO I comment this because this doesn't work. After solving the bug
		 * of transparencies this must be removed // draw NaN values as fully
		 * transparent... final IndexColorModel cm = (IndexColorModel)
		 * ip.getProcessor() .getColorModel(); byte[] reds = new byte[256];
		 * byte[] greens = new byte[256]; byte[] blues = new byte[256]; byte[]
		 * alphas = new byte[256]; cm.getReds(reds); cm.getGreens(greens);
		 * cm.getBlues(blues); cm.getAlphas(alphas); for (int i = 0; i < 256;
		 * i++) { alphas[i] = 1; } alphas[0] = 0;
		 * ip.getProcessor().setColorModel( new IndexColorModel(8, 256, reds,
		 * greens, blues)); ip.updateAndDraw();
		 */
		if (layerPixelEnvelope != null) {
			g2.drawImage(ip.getImage(), (int) layerPixelEnvelope.getMinX(),
					(int) layerPixelEnvelope.getMinY(),
					(int) layerPixelEnvelope.getWidth(),
					(int) layerPixelEnvelope.getHeight(), null);
		}
	}

	private void drawVectorLayer(MapTransform mt, ILayer layer, Image img,
			Envelope extent, IProgressMonitor pm) throws DriverException {
		VectorLayer vl = (VectorLayer) layer;
		Legend legend = vl.getLegend();
		SpatialDataSourceDecorator sds = vl.getDataSource();
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		try {
			long t1 = System.currentTimeMillis();
			DefaultRendererPermission permission = new DefaultRendererPermission();
			for (int legendLayer = 0; legendLayer < legend.getNumLayers(); legendLayer++) {
				legend.setLayer(legendLayer);
				for (int i = 0; i < sds.getRowCount(); i++) {
					if (pm.isCancelled()) {
						break;
					} else {
						Symbol sym = legend.getSymbol(i);
						Geometry g = sds.getGeometry(i);
						Envelope symbolEnvelope;
						if (g.getGeometryType().equals("GeometryCollection")) {
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
			}
			long t2 = System.currentTimeMillis();
			logger.info("Rendering time:" + (t2 - t1));
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

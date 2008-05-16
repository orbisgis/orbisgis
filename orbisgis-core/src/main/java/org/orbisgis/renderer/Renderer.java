package org.orbisgis.renderer;

import ij.ImagePlus;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GrapImagePlus;
import org.orbisgis.Services;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.map.MapTransform;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.RasterLegend;
import org.orbisgis.renderer.legend.RenderException;
import org.orbisgis.renderer.legend.RenderUtils;
import org.orbisgis.renderer.legend.Symbol;

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

		long total1 = System.currentTimeMillis();
		for (int i = layers.length - 1; i >= 0; i--) {
			if (pm.isCancelled()) {
				break;
			} else {
				layer = layers[i];
				if (layer.isVisible()) {
					try {
						logger.debug("Drawing " + layer.getName());
						long t1 = System.currentTimeMillis();
						SpatialDataSourceDecorator sds = layer.getDataSource();
						if (sds != null) {
							if (sds.isDefaultVectorial()) {
								drawVectorLayer(mt, layer, img, extent, pm);
							} else if (sds.isDefaultRaster()) {
								drawRasterLayer(mt, layer, img, extent, pm);
							} else {
								logger.warn("Not drawn: " + layer.getName());
							}
							pm.progressTo(100 - (100 * i) / layers.length);
						}
						long t2 = System.currentTimeMillis();
						logger.info("Rendering time:" + (t2 - t1));
					} catch (IOException e) {
						Services.getErrorManager().error(
								"Cannot draw raster:" + layer.getName(), e);
					} catch (GeoreferencingException e) {
						Services.getErrorManager().error(
								"Cannot draw raster: " + layer.getName(), e);
					} catch (DriverException e) {
						Services.getErrorManager().error(
								"Cannot draw : " + layer.getName(), e);
					}
				}
			}
		}
		long total2 = System.currentTimeMillis();
		logger.info("Total rendering time:" + (total2 - total1));

	}

	private void drawRasterLayer(MapTransform mt, ILayer layer, Image img,
			Envelope extent, IProgressMonitor pm) throws DriverException,
			IOException, GeoreferencingException {
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		logger.debug("raster envelope: " + layer.getEnvelope());
		Legend[] legends = layer.getLegend();
		for (Legend legend : legends) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					((RasterLegend) legend).getOpacity()));
			GeoRaster geoRaster = layer.getDataSource().getRaster(0);
			Envelope layerEnvelope = geoRaster.getMetadata().getEnvelope();
			Envelope layerPixelEnvelope = null;
			if (extent.intersects(layerEnvelope)) {

				BufferedImage mapControlImage = (BufferedImage) img;
				BufferedImage layerImage = new BufferedImage(mapControlImage
						.getWidth(), mapControlImage.getHeight(),
						BufferedImage.TYPE_INT_ARGB);

				// part or all of the GeoRaster is visible
				layerPixelEnvelope = mt.toPixel(layerEnvelope);
				Graphics2D gLayer = layerImage.createGraphics();
				Image dataImage = getImage(geoRaster, (RasterLegend) legend);
				gLayer.drawImage(dataImage, (int) layerPixelEnvelope.getMinX(),
						(int) layerPixelEnvelope.getMinY(),
						(int) layerPixelEnvelope.getWidth() + 1,
						(int) layerPixelEnvelope.getHeight() + 1, null);
				pm.startTask("Drawing " + layer.getName());
				g2.drawImage(layerImage, 0, 0, null);
				pm.endTask();
			}
		}
	}

	/**
	 * This code is from FloatProcessor.getImage() in IJ. The difference is that
	 * IJ creates 255 classes for the values and we create 254. The first class
	 * is reserved for NaN (see first 'if' inside 'for')
	 *
	 * @param pixels
	 * @param width
	 * @param height
	 * @param min
	 * @param max
	 * @return
	 */
	private static Image getImage(float[] pixels, int width, int height,
			float min, float max, ColorModel cm, float noDataValue) {
		// scale from float to 8-bits
		byte[] pixels8;
		int size = width * height;
		pixels8 = new byte[size];
		float value;
		int ivalue;
		float scale = 254f / (max - min);
		for (int i = 0; i < size; i++) {
			if (Float.isNaN(pixels[i])) {
				pixels8[i] = (byte) 0;
			} else {
				value = pixels[i] - min;
				if (value < 0f)
					value = 0f;
				ivalue = (int) (value * scale);
				if (ivalue > 254)
					ivalue = 254;
				pixels8[i] = (byte) (ivalue + 1);
			}
		}
		MemoryImageSource source = new MemoryImageSource(width, height, cm,
				pixels8, 0, width);
		source.setAnimated(true);
		source.setFullBufferUpdates(true);
		return Toolkit.getDefaultToolkit().createImage(source);
	}

	/**
	 * Gets an image of the whole georaster taking into account the raster
	 * styling parameters:transparency, color model, no data value, etc.
	 *
	 * @param gr
	 * @return
	 * @throws IOException
	 * @throws GeoreferencingException
	 */
	private Image getImage(GeoRaster gr, RasterLegend legend)
			throws IOException, GeoreferencingException {
		ColorModel colorModel = legend.getColorModel();
		if ((colorModel != null)
				&& (!Float.isNaN(gr.getMetadata().getNoDataValue()))) {
			colorModel = addTransparency(colorModel);
		}
		GrapImagePlus ip = gr.getGrapImagePlus();
		if (gr.getType() == ImagePlus.GRAY32) {
			float[] pixels = (float[]) ip.getPixels();
			return getImage(pixels, ip.getWidth(), ip.getHeight(), (float) gr
					.getMin(), (float) gr.getMax(), colorModel, gr
					.getMetadata().getNoDataValue());
		} else if (gr.getType() == ImagePlus.GRAY16) {
			short[] pixels = (short[]) ip.getPixels();
			return getImage(pixels, ip.getWidth(), ip.getHeight(), (short) gr
					.getMin(), (short) gr.getMax(), colorModel, (short) gr
					.getMetadata().getNoDataValue());
		} else {
			// ImagePlus.GRAY8, ImagePlus.COLOR_256, ImagePlus.COLOR_RGB
			if (colorModel != null) {
				ip.getProcessor().setColorModel(colorModel);
			}
			return ip.getImage();
		}
	}

	/**
	 * This code is from ShortProcessor.getImage() in IJ. The difference is that
	 * IJ creates 255 classes for the values and we create 254. The first class
	 * is reserved for the no data value (see first 'if' inside 'for')
	 *
	 * @param pixels
	 * @param width
	 * @param height
	 * @param min
	 * @param max
	 * @return
	 */
	private static Image getImage(short[] pixels, int width, int height,
			short min, short max, ColorModel cm, short noDataValue) {
		// scale from float to 8-bits
		byte[] pixels8;
		int size = width * height;
		pixels8 = new byte[size];
		short value;
		int ivalue;
		float scale = 254 / (max - min);
		for (int i = 0; i < size; i++) {
			if (noDataValue == pixels[i]) {
				pixels8[i] = (byte) 0;
			} else {
				value = (short) (pixels[i] - min);
				if (value < 0)
					value = 0;
				ivalue = (int) (value * scale);
				if (ivalue > 254)
					ivalue = 254;
				pixels8[i] = (byte) (ivalue + 1);
			}
		}
		MemoryImageSource source = new MemoryImageSource(width, height, cm,
				pixels8, 0, width);
		source.setAnimated(true);
		source.setFullBufferUpdates(true);
		return Toolkit.getDefaultToolkit().createImage(source);
	}

	/**
	 * Returns a color model equal to the one specified as parameter but making
	 * the class containing the no-data-value pixels (first class) be
	 * transparent
	 *
	 * @param colorModel
	 * @return
	 */
	private static ColorModel addTransparency(ColorModel colorModel) {
		IndexColorModel indexColorModel = (IndexColorModel) colorModel;
		int nbOfColors = indexColorModel.getMapSize();
		byte[] reds = new byte[nbOfColors];
		byte[] greens = new byte[nbOfColors];
		byte[] blues = new byte[nbOfColors];
		byte[] alphas = new byte[nbOfColors];

		indexColorModel.getReds(reds);
		indexColorModel.getGreens(greens);
		indexColorModel.getBlues(blues);
		indexColorModel.getAlphas(alphas);
		// transparency for nodata (NaN) pixels
		alphas[0] = 0;

		return new IndexColorModel(8, nbOfColors, reds, greens, blues, alphas);
	}

	private void drawVectorLayer(MapTransform mt, ILayer layer, Image img,
			Envelope extent, IProgressMonitor pm) throws DriverException {
		Legend[] legends = layer.getLegend();
		SpatialDataSourceDecorator sds = layer.getDataSource();
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		try {
			if (sds.getFullExtent().intersects(extent)) {
				DefaultRendererPermission permission = new DefaultRendererPermission();
				for (Legend legend : legends) {
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
			Services.getErrorManager().warning(
					"Cannot draw layer: " + layer.getName(), e);
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

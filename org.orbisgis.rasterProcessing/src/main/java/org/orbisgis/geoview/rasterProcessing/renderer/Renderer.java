package org.orbisgis.geoview.rasterProcessing.renderer;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.grap.io.GeoreferencingException;
import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

public class Renderer {
	public final static Color BGCOLOR = Color.RED;

	private BufferedImage bufferedImage;
	private int width;
	private int height;
	private int type;
	private Color noDataColor;

	public Renderer(final GeoRaster geoRaster, final int width, final int height)
			throws IOException, GeoreferencingException {
		this.width = width;
		this.height = height;
		this.type = geoRaster.getType();
		this.noDataColor = new Color(0xFFFFFF);

		bufferedImage = new BufferedImage(this.width, this.height,
				BufferedImage.TYPE_INT_ARGB);
		final Graphics graphics = bufferedImage.getGraphics();
		final ImageProcessor ip = geoRaster.getGrapImagePlus().getProcessor()
				.resize(width, height);
		// final ByteProcessor bp = (ByteProcessor) new MedianCut(
		// (ColorProcessor) ip).convertToByte(256);

		graphics.drawImage(ip.createImage(), 0, 0, BGCOLOR, null);
		graphics.dispose();
	}

	public void setTransparency(final Color transparentColor) {
		final int transparentColorCode = transparentColor.getRGB();
		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				final int pixelColor = bufferedImage.getRGB(c, r);
				if (pixelColor == transparentColorCode) {
					System.out.println("ok");
					bufferedImage.setRGB(c, r, 0x00FFFFFF & pixelColor);
				}
			}
		}
	}

	public void setTransparency(final Color[] transparentColors) {
		final Set<Integer> transparentColorsCodes = new HashSet<Integer>(
				transparentColors.length);
		for (Color transparentColor : transparentColors) {
			transparentColorsCodes.add(transparentColor.getRGB());
		}

		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				final int pixelColor = bufferedImage.getRGB(c, r);
				if (transparentColorsCodes.contains(pixelColor)) {
					bufferedImage.setRGB(c, r, 0x00FFFFFF & pixelColor);
				}
			}
		}
	}

	public void setOpacity(final float opacity) {
		final BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TRANSLUCENT);
		final Graphics2D graphics2D = image.createGraphics();
		graphics2D.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, opacity));
		graphics2D.drawImage(bufferedImage, null, 0, 0);

		graphics2D.dispose();
		bufferedImage = image;
	}

	public void setLUT(final ColorModel colorModel)
			throws FileNotFoundException, IOException, GeoreferencingException {
		switch (type) {
		case ImagePlus.GRAY8:
		case ImagePlus.GRAY16:
		case ImagePlus.GRAY32:
			final ImagePlus tmpImagePlus = new ImagePlus("Temp", bufferedImage);
			tmpImagePlus.show();
			final ImageProcessor tmpImageProcessor = tmpImagePlus
					.getProcessor();
			tmpImageProcessor.setColorModel(colorModel);

			bufferedImage = (BufferedImage) tmpImagePlus.getImage();
			tmpImagePlus.show();

			// if (colorModel instanceof IndexColorModel) {
			// ImageProcessor.createProcessor(width, height);
			//				
			//				
			// final WritableRaster writableRaster = bufferedImage.getRaster();
			// final SampleModel sampleModel = writableRaster.getSampleModel();
			// final DataBuffer dataBuffer = writableRaster.getDataBuffer();
			//
			// // final WritableRaster wRaster = Raster
			// // .createPackedRaster(dataBuffer, width, height, 32, null);
			// final WritableRaster wRaster = Raster.createPackedRaster(
			// dataBuffer, width, height, 32, null);
			//
			// // final ImageTypeSpecifier imageTypeSpecifier = new
			// // ImageTypeSpecifier(colorModel,
			// // sampleModel);
			// // final BufferedImage image =
			// // imageTypeSpecifier.createBufferedImage(width, height);
			//
			// // final BufferedImage image = new BufferedImage(width, height,
			// // BufferedImage.TYPE_INT_ARGB);
			//
			// final BufferedImage image = new BufferedImage(colorModel,
			// wRaster, false, null);
			//
			// // final BufferedImage image = new BufferedImage(width, height,
			// // BufferedImage.TYPE_BYTE_INDEXED,
			// // (IndexColorModel) colorModel);
			// final Graphics2D graphics2D = image.createGraphics();
			//
			// graphics2D.drawImage(bufferedImage, null, 0, 0);
			// graphics2D.dispose();
			// bufferedImage = image;
			// } else {
			// throw new UnsupportedOperationException(
			// "setLUT requires an index colored gray image");
			// }
			break;
		case ImagePlus.COLOR_256:
		case ImagePlus.COLOR_RGB:
			throw new UnsupportedOperationException(
					"setLUT requires an index colored gray image");
		default:
			throw new UnsupportedOperationException(
					"setLUT requires an index colored gray image");
		}
	}

	public void setRGBBand() {
		// final BufferedImage image = new BufferedImage(width, height,
		// bufferedImage.getType(), (IndexColorModel) colorModel);
		// final Graphics2D graphics2D = image.createGraphics();
		// graphics2D.drawImage(bufferedImage, null, 0, 0);
		// graphics2D.dispose();
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException, GeoreferencingException {
		// final String src = "../../datas2tests/grid/sample.asc";
		// final String src = "../../datas2tests/geotif/440606.tif";
		// final String src = "../../datas2tests/geotif/LeHavre.tif";
		// final GeoRaster gr = GeoRasterFactory.createGeoRaster(src);
		final GeoRaster gr = foo();
		gr.open();

		final Renderer r = new Renderer(gr, 400, 400);
		// r.setTransparency(Color.BLACK);
		// r.setOpacity(0.75f);
//		r.setLUT(LutGenerator.colorModel("cyan"));

		new ImagePlus("v2", r.getBufferedImage()).show();
	}

	private static GeoRaster foo() {
		final int ncols = 400;
		final int nrows = 400;
		final float pixels[] = new float[nrows * ncols];
		final int quarter = (nrows * ncols) / 4;
		for (int i = 0; i < nrows * ncols; i++) {
			if (i < quarter) {
				pixels[i] = Float.NaN;
			} else if (i < 2 * quarter) {
				pixels[i] = 70;
			} else if (i < 3 * quarter) {
				pixels[i] = 140;
			} else {
				pixels[i] = 210;
			}
		}
		return GeoRasterFactory.createGeoRaster(pixels, ncols, nrows,
				new RasterMetadata(0, 0, 1, -1, ncols, nrows));
	}
}
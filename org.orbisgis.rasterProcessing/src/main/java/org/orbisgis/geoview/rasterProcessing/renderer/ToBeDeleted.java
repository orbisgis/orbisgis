package org.orbisgis.geoview.rasterProcessing.renderer;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;

public class ToBeDeleted {
	private static ImageProcessor foo() {
		final int ncols = 400;
		final int nrows = 400;
		final float pixels[] = new float[nrows * ncols];
		final int quarter = (nrows * ncols) / 4;
		for (int i = 0; i < nrows * ncols; i++) {
			if (i < quarter) {
				pixels[i] = Float.NaN; // 0.1f;
			} else if (i < 2 * quarter) {
				pixels[i] = 210;
			} else if (i < 3 * quarter) {
				pixels[i] = 140;
			} else {
				pixels[i] = 70;
			}
		}
		return new FloatProcessor(ncols, nrows, pixels, null);
	}

	private static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return ((BufferedImage) image);
		} else {
			image = new ImageIcon(image).getImage();
			final BufferedImage bufferedImage = new BufferedImage(image
					.getWidth(null), image.getHeight(null),
					BufferedImage.TYPE_INT_RGB);
			final Graphics g = bufferedImage.createGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();
			return bufferedImage;
		}
	}

	private static BufferedImage toBufferedImage(Image image, final int width,
			final int height) {
		if (image instanceof BufferedImage) {
			return ((BufferedImage) image);
		} else {
			image = new ImageIcon(image).getImage();
			final BufferedImage bufferedImage = new BufferedImage(width,
					height, BufferedImage.TYPE_INT_RGB);
			final Graphics g = bufferedImage.createGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();
			return bufferedImage;
		}
	}

	public static void main(String[] args) {
		final float x = Float.NaN;
		final ImageProcessor fp = new FloatProcessor(4, 1, new float[] { 33,
				70.3f, x, 244.4f }, null);
		// foo();
		fp.setBackgroundValue(x);
		fp.resetMinAndMax();
		fp.autoThreshold();

		for (float item : (float[]) fp.getPixels()) {
			System.out.println("\tpixel value : " + item);
		}

		final ImagePlus imagePlus = new ImagePlus("test", fp);
		final BufferedImage bi = toBufferedImage(imagePlus.getImage());

		final Set<Integer> colorSet = new HashSet<Integer>();
		for (int r = 0; r < bi.getWidth(); r++) {
			for (int c = 0; c < bi.getHeight(); c++) {
				colorSet.add(bi.getRGB(r, c));
			}
		}
		System.out.println("Number of different colors = " + colorSet.size());
		for (int item : colorSet) {
			System.out.println("\tcolor value : " + item);
		}
		// new ImagePlus("test", fp).show();
	}
}

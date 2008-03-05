package org.orbisgis.geoview.rasterProcessing.renderer;

import ij.IJ;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class ToBeDeleted {
	private static ImageProcessor foo() {
		final int ncols = 400;
		final int nrows = 400;
		final float pixels[] = new float[nrows * ncols];
		final int quarter = (nrows * ncols) / 4;
		for (int i = 0; i < nrows * ncols; i++) {
			if (i < quarter) {
				pixels[i] = Float.NaN;
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

	public static void main(String[] args) {
		final ImageProcessor fp = foo();
		final ImagePlus imagePlus = new ImagePlus("test", fp);
		final BufferedImage bi = (BufferedImage) imagePlus.getImage();

		final Set<Integer> colorSet = new HashSet<Integer>();
		for (int r = 0; r < bi.getWidth(); r++) {
			for (int c = 0; c < bi.getHeight(); c++) {
				colorSet.add(bi.getRGB(r, c));
			}
		}
		for (int item : colorSet) {
			System.out.println(item);
		}
		
		// fp.setColor(Color.red);
		fp.setThreshold(0, 210, ImageProcessor.OVER_UNDER_LUT);

		// WindowManager.setTempCurrentImage(imp);
		IJ.run("NaN Background");
		new ImagePlus("test", fp).show();
	}
}

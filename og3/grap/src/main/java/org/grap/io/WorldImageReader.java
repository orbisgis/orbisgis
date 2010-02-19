/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
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
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.grap.io;

import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.Opener;
import ij.io.TiffDecoder;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.grap.model.RasterMetadata;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

public class WorldImageReader implements RasterReader {
	private static Map<String, String[]> worldFileExtensions;

	private String fileName;

	private File worldFile;

	private boolean isTiff = false;
	private boolean isJpg = false;

	private String fileNamePrefix;

	private String fileNameExtension;

	private JPEGImageDecoder jpgDecoder;

	private BufferedImage bufJpg;

	static {
		worldFileExtensions = new HashMap<String, String[]>();
		worldFileExtensions.put("tif", new String[] { "tfw" });
		worldFileExtensions.put("tiff", new String[] { "tfw", "tiffw" });
		worldFileExtensions.put("jpg", new String[] { "jpw", "jgw", "jpgw",
				"jpegw" });
		worldFileExtensions.put("jpeg", new String[] { "jpw", "jgw", "jpgw",
				"jpegw" });
		worldFileExtensions.put("gif", new String[] { "gfw", "gifw" });
		worldFileExtensions.put("bmp", new String[] { "bmw", "bmpw" });
		worldFileExtensions.put("png", new String[] { "pgw", "pngw" });
	}

	// constructor
	public WorldImageReader(final String fileName) {
		this.fileName = fileName;

		final int dotIndex = fileName.lastIndexOf('.');
		fileNamePrefix = fileName.substring(0, dotIndex);
		fileNameExtension = fileName.substring(dotIndex + 1).toLowerCase();

		if (fileNameExtension.equals("tif") || fileNameExtension.equals("tiff")) {
			isTiff = true;
		} else if (fileNameExtension.equals("jpg")
				|| fileNameExtension.equals("jpeg")) {
			isJpg = true;
		}
	}

	// private method
	private boolean isThereAnyWorldFile() throws IOException {
		worldFile = null;

		for (String extension : worldFileExtensions.get(fileNameExtension)) {
			if (new File(fileNamePrefix + "." + extension).exists()) {
				worldFile = new File(fileNamePrefix + "." + extension);
				return true;
			} else if (new File(fileNamePrefix + "." + extension.toUpperCase())
					.exists()) {
				worldFile = new File(fileNamePrefix + "."
						+ extension.toUpperCase());
				return true;
			}
		}
		return false;
	}

	// public methods
	public RasterMetadata readRasterMetadata() throws IOException {
		final File file = new File(fileName);
		InputStream inputStream = new BufferedInputStream(new FileInputStream(
				file));

		// read image's dimensions
		int ncols;
		int nrows;
		if (isTiff) {
			final TiffDecoder tiffDecoder = new TiffDecoder(inputStream,
					fileName);
			final FileInfo[] fileInfo = tiffDecoder.getTiffInfo();
			ncols = fileInfo[0].width;
			nrows = fileInfo[0].height;

		} else if (isJpg) {

			jpgDecoder = JPEGCodec.createJPEGDecoder(inputStream);

			bufJpg = jpgDecoder.decodeAsBufferedImage();
			ncols = bufJpg.getWidth();
			nrows = bufJpg.getHeight();

		}

		else {
			final ImageInfo imageInfo = new ImageInfo();
			imageInfo.setInput(inputStream);
			if (imageInfo.check()) {
				ncols = imageInfo.getWidth();
				nrows = imageInfo.getHeight();
			} else {
				throw new RuntimeException("Unsupported image file format.");
			}
		}

		inputStream.close();

		// read other image's metadata
		if (isThereAnyWorldFile() == true) {
			final WorldFile wf = WorldFile.read(worldFile);

			final double upperLeftX = wf.getXUpperLeft();
			final double upperLeftY = wf.getYUpperLeft();
			final float pixelSize_X = wf.getXSize();
			final float pixelSize_Y = wf.getYSize();
			final float xRotation = wf.getColRotation();
			final float yRotation = wf.getRowRotation();

			return new RasterMetadata(upperLeftX, upperLeftY, pixelSize_X,
					pixelSize_Y, ncols, nrows, xRotation, yRotation);
		} else {
			throw new IOException("Could not find world file for " + fileName);
		}
	}

	public ImagePlus readImagePlus() throws IOException {
		// return new Opener().openImage(fileName);

		ImagePlus imagePlus;
		final ImageProcessor imageProcessor;
		if (isJpg) {

			imagePlus = new ImagePlus("jpg", bufJpg);
			imageProcessor = imagePlus.getProcessor();
		} else {

			imagePlus = new Opener().openImage(fileName);
			imageProcessor = imagePlus.getProcessor();
			imagePlus = null;
		}

		return new ImagePlus("", imageProcessor);
	}
}
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
package org.grap.model;

import ij.ImagePlus;
import ij.io.FileSaver;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.grap.io.EsriGRIDWriter;
import org.grap.io.RasterReader;
import org.grap.io.FileReaderFactory;
import org.grap.io.WorldFile;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;

/**
 * A GeoRaster object is composed of an ImageJ ImagePlus object and some spatial
 * fields such as : a projection system, an envelop, a pixel size...
 */
public class DefaultGeoRaster implements GeoRaster {

	private static Logger logger = Logger.getLogger(DefaultGeoRaster.class
			.getName());

	private RasterMetadata rasterMetadata;
	private RasterReader fileReader;
	private ImagePlus cachedImagePlus;
	private double maxThreshold = Double.NaN;
	private double minThreshold = Double.NaN;
	private float noDataValue = Float.NaN;
	private Integer cachedType = null;
	/**
	 * Minimum valid value (inclusive)
	 */
	private Double cachedMin = null;
	/**
	 * Maximum valid value (inclusive)
	 */
	private Double cachedMax = null;
	private Integer cachedWidth = null;
	private Integer cachedHeight = null;
	private ColorModel cachedColorModel = null;

	// constructors
	DefaultGeoRaster(final String fileName) throws FileNotFoundException,
			IOException {
		this(fileName, GeoProcessorType.FLOAT);
	}

	DefaultGeoRaster(final String fileName,
			final GeoProcessorType geoProcessorType)
			throws FileNotFoundException, IOException {
		fileReader = FileReaderFactory.create(fileName, geoProcessorType);
	}

	DefaultGeoRaster(final String fileName,
			final GeoProcessorType geoProcessorType, float pixelsize)
			throws FileNotFoundException, IOException {
		fileReader = FileReaderFactory.create(fileName, geoProcessorType,
				pixelsize);
	}

	DefaultGeoRaster(final ImagePlus imagePlus, final RasterMetadata metadata) {
		cachedImagePlus = imagePlus;
		this.rasterMetadata = metadata;
		cachedWidth = metadata.getNCols();
		cachedHeight = metadata.getNRows();
	}

	public DefaultGeoRaster(ImagePlus imagePlus, RasterMetadata metadata,
			int imageType, double min, double max) {
		this(imagePlus, metadata);
		cachedMin = min;
		cachedMax = max;
		cachedType = imageType;
	}

	public DefaultGeoRaster(RasterReader fileReader) {
		this.fileReader = fileReader;
	}

	public DefaultGeoRaster(RasterReader fileReader, int imageType, double min,
			double max) {
		cachedMin = min;
		cachedMax = max;
		cachedType = imageType;
		this.fileReader = fileReader;
	}

	// public methods
	public void open() throws IOException {
		if (null != fileReader) {
			rasterMetadata = fileReader.readRasterMetadata();
		} else {
			// Ignore open for results in memory
		}
		noDataValue = getMetadata().getNoDataValue();
	}

	public RasterMetadata getMetadata() {
		return rasterMetadata;
	}

	public void setRangeValues(final double min, final double max)
			throws IOException {
		if (getType() == ImagePlus.COLOR_RGB) {
			throw new UnsupportedOperationException("RGB images doesn't "
					+ "allow no-data-value");
		}
		minThreshold = min;
		maxThreshold = max;
		resetMinAndMax(null);
	}

	public void setNodataValue(final float value) throws IOException {
		if (getType() == ImagePlus.COLOR_RGB) {
			throw new UnsupportedOperationException("RGB images doesn't "
					+ "allow no-data-value");
		}
		noDataValue = value;
		resetMinAndMax(null);
	}

	public Point2D fromPixelToRealWorld(final int xpixel, final int ypixel) {
		return rasterMetadata.toWorld(xpixel, ypixel);
	}

	public Point2D fromRealWorldToPixel(final double mouseX, final double mouseY) {
		return rasterMetadata.toPixel(mouseX, mouseY);
	}

	public void save(final String dest) throws IOException {
		final int dotIndex = dest.lastIndexOf('.');
		final String localFileNamePrefix = dest.substring(0, dotIndex);
		final String localFileNameExtension = dest.substring(dotIndex + 1);
		ImagePlus imagePlus = getImagePlus();
		final FileSaver fileSaver = new FileSaver(imagePlus);

		final String tmp = localFileNameExtension.toLowerCase();
		if (tmp.endsWith("tif") || (tmp.endsWith("tiff"))) {
			fileSaver.saveAsTiff(dest);
			WorldFile.save(localFileNamePrefix + ".tfw", rasterMetadata);
		} else if (tmp.endsWith("png")) {
			fileSaver.saveAsPng(dest);
			WorldFile.save(localFileNamePrefix + ".pgw", rasterMetadata);
		} else if (tmp.endsWith("jpg") || (tmp.endsWith("jpeg"))) {
			fileSaver.saveAsJpeg(dest);
			WorldFile.save(localFileNamePrefix + ".jgw", rasterMetadata);
		} else if (tmp.endsWith("gif")) {
			fileSaver.saveAsGif(dest);
			WorldFile.save(localFileNamePrefix + ".gfw", rasterMetadata);
		} else if (tmp.endsWith("bmp")) {
			fileSaver.saveAsBmp(dest);
			WorldFile.save(localFileNamePrefix + ".bpw", rasterMetadata);
		} else if (tmp.endsWith("asc")) {
			EsriGRIDWriter esriGRIDWriter = new EsriGRIDWriter(
					localFileNamePrefix + ".asc", imagePlus, rasterMetadata);
			esriGRIDWriter.save();
		}

		else {
			throw new RuntimeException("Cannot write in format: "
					+ localFileNameExtension);
		}
	}

	public void show() throws IOException {
		getImagePlus().show();
	}

	public GeoRaster doOperation(final Operation operation)
			throws OperationException {
		return operation.execute(this, new NullProgressMonitor());
	}

	public GeoRaster doOperation(final Operation operation, IProgressMonitor pm)
			throws OperationException {
		return operation.execute(this, pm);
	}

	public int getType() throws IOException {
		if (cachedType == null) {
			updateCachedValues(null);
		}
		return cachedType.intValue();
	}

	private void updateCachedValues(ImagePlus imagePlus) throws IOException {
		if (imagePlus == null) {
			imagePlus = getImagePlus();
		}
		cachedType = imagePlus.getType();
		cachedWidth = imagePlus.getWidth();
		cachedHeight = imagePlus.getHeight();
		cachedColorModel = imagePlus.getProcessor().getColorModel();

		if ((cachedMin == null) || (cachedMax == null)) {
			resetMinAndMax(imagePlus);
		}

	}

	private void resetMinAndMax(ImagePlus imagePlus) throws IOException {
		logger.debug("Recalculating min and max");
		if (imagePlus == null) {
			imagePlus = getImagePlus();
		}
		switch (getType()) {
		case ImagePlus.COLOR_256:
		case ImagePlus.GRAY8:
			resetMinAndMaxByte((byte[]) imagePlus.getProcessor().getPixels());
			break;
		case ImagePlus.GRAY16:
			resetMinAndMaxShort((short[]) imagePlus.getProcessor().getPixels());
			break;
		case ImagePlus.GRAY32:
			resetMinAndMaxFloat((float[]) imagePlus.getProcessor().getPixels());
			break;
		case ImagePlus.COLOR_RGB:
			resetMinAndMaxInt((int[]) imagePlus.getProcessor().getPixels());
			break;
		}
	}

	private boolean noDataSpecified() throws IOException {
		return !Double.isNaN(getNoDataValue()) || !Double.isNaN(minThreshold)
				|| !Double.isNaN(maxThreshold);
	}

	private void resetMinAndMaxFloat(float[] pixels) {
		float min = Float.POSITIVE_INFINITY;
		float max = Float.NEGATIVE_INFINITY;
		for (float pixel : pixels) {
			if (pixel == FLOAT_NO_DATA_VALUE) {
				continue;
			} else {
				if (min > pixel) {
					min = pixel;
				}
				if (max < pixel) {
					max = pixel;
				}
			}
		}
		cachedMin = new Double(min);
		cachedMax = new Double(max);
	}

	private void resetMinAndMaxShort(short[] pixels) {
		short min = Short.MAX_VALUE;
		short max = Short.MIN_VALUE;
		for (short pixel : pixels) {
			if (pixel == SHORT_NO_DATA_VALUE) {
				continue;
			} else {
				if (min > pixel) {
					min = pixel;
				}
				if (max < pixel) {
					max = pixel;
				}
			}
		}
		cachedMin = new Double(min);
		cachedMax = new Double(max);
	}

	private void resetMinAndMaxByte(byte[] pixels) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (byte pixel : pixels) {
			if (pixel == BYTE_NO_DATA_VALUE) {
				continue;
			} else {
				if (min > pixel) {
					min = pixel;
				}
				if (max < pixel) {
					max = pixel;
				}
			}
		}
		cachedMin = new Double(min);
		cachedMax = new Double(max);
	}

	private void resetMinAndMaxInt(int[] pixels) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (int pixel : pixels) {
			if (min > pixel) {
				min = pixel;
			}
			if (max < pixel) {
				max = pixel;
			}
		}
		cachedMin = new Double(min);
		cachedMax = new Double(max);
	}

	public boolean isEmpty() {
		return false;
	}

	public double getMax() throws IOException {
		if (cachedMax == null) {
			updateCachedValues(null);
		}
		return cachedMax.doubleValue();
	}

	public double getMin() throws IOException {
		if (cachedMin == null) {
			updateCachedValues(null);
		}
		return cachedMin.doubleValue();
	}

	public int getHeight() throws IOException {
		if (cachedHeight == null) {
			updateCachedValues(null);
		}
		return cachedHeight.intValue();
	}

	public int getWidth() throws IOException {
		if (cachedWidth == null) {
			updateCachedValues(null);
		}
		return cachedWidth.intValue();
	}

	public ImagePlus getImagePlus() throws IOException {
		logger.debug("Getting ImagePlus");
		final ImagePlus grapImagePlus = (null == cachedImagePlus) ? fileReader
				.readImagePlus() : cachedImagePlus;

		if (!(grapImagePlus.getType() == ImagePlus.COLOR_RGB)) {
			setNDVValues(grapImagePlus);
		} else {
			if (cachedMin == null) {
				updateCachedValues(grapImagePlus);
			}
		}

		return grapImagePlus;
	}

	private void setNDVValues(ImagePlus grapImagePlus) throws IOException {
		if (noDataSpecified()) {
			logger.debug("setting ndv pixels");
			switch (grapImagePlus.getType()) {
			case ImagePlus.COLOR_256:
			case ImagePlus.GRAY8:
				setNDVValuesByte(grapImagePlus);
				break;
			case ImagePlus.GRAY16:
				setNDVValuesShort(grapImagePlus);
				break;
			case ImagePlus.GRAY32:
				setNDVValuesFloat(grapImagePlus);
				break;
			}
		} else {
			logger.debug("No ndv specified");
		}
	}

	private void setNDVValuesFloat(ImagePlus grapImagePlus) throws IOException {
		float ndv = (float) getNoDataValue();
		float min = Float.NEGATIVE_INFINITY;
		if (!Double.isNaN(minThreshold)) {
			min = (float) minThreshold;
		}
		float max = Float.POSITIVE_INFINITY;
		if (!Double.isNaN(maxThreshold)) {
			max = (float) maxThreshold;
		}
		float[] pixels = (float[]) grapImagePlus.getProcessor().getPixels();
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] < min) {
				pixels[i] = FLOAT_NO_DATA_VALUE;
			} else if (pixels[i] > max) {
				pixels[i] = FLOAT_NO_DATA_VALUE;
			} else if (pixels[i] == ndv) {
				pixels[i] = FLOAT_NO_DATA_VALUE;
			}
		}
	}

	private void setNDVValuesShort(ImagePlus grapImagePlus) throws IOException {
		short nan = (short) getNoDataValue();
		short min = Short.MIN_VALUE;
		if (!Double.isNaN(minThreshold)) {
			min = (short) minThreshold;
		}
		short max = Short.MAX_VALUE;
		if (!Double.isNaN(maxThreshold)) {
			max = (short) maxThreshold;
		}
		short[] pixels = (short[]) grapImagePlus.getProcessor().getPixels();
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] < min) {
				pixels[i] = SHORT_NO_DATA_VALUE;
			} else if (pixels[i] > max) {
				pixels[i] = SHORT_NO_DATA_VALUE;
			} else if (pixels[i] == nan) {
				pixels[i] = SHORT_NO_DATA_VALUE;
			}
		}
	}

	private void setNDVValuesByte(ImagePlus grapImagePlus) throws IOException {
		byte nan = (byte) getNoDataValue();
		byte min = Byte.MIN_VALUE;
		if (!Double.isNaN(minThreshold)) {
			min = (byte) minThreshold;
		}
		byte max = Byte.MAX_VALUE;
		if (!Double.isNaN(maxThreshold)) {
			max = (byte) maxThreshold;
		}
		byte[] pixels = (byte[]) grapImagePlus.getProcessor().getPixels();
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] < min) {
				pixels[i] = BYTE_NO_DATA_VALUE;
			} else if (pixels[i] > max) {
				pixels[i] = BYTE_NO_DATA_VALUE;
			} else if (pixels[i] == nan) {
				pixels[i] = BYTE_NO_DATA_VALUE;
			}
		}
	}

	public ColorModel getDefaultColorModel() throws IOException {
		if (cachedColorModel == null) {
			updateCachedValues(null);
		}
		return cachedColorModel;
	}

	public double getNoDataValue() throws IOException {
		return noDataValue;
	}

	public byte[] getBytePixels() throws IOException {
		return (byte[]) getImagePlus().getProcessor().getPixels();
	}

	public float[] getFloatPixels() throws IOException {
		return (float[]) getImagePlus().getProcessor().getPixels();
	}

	public int[] getIntPixels() throws IOException {
		return (int[]) getImagePlus().getProcessor().getPixels();
	}

	public short[] getShortPixels() throws IOException {
		return (short[]) getImagePlus().getProcessor().getPixels();
	}

	/**
	 * This method is used to
	 */
	public Image getImage(ColorModel colorModel) throws IOException {
		if (noDataSpecified()) {
			colorModel = addFirstTransparentClass(colorModel);
			switch (getType()) {
			case ImagePlus.GRAY8:
				logger.debug("getting image with ndv");
				return getByteImage(colorModel);
			case ImagePlus.GRAY16:
				logger.debug("getting image with ndv");
				return getShortImage(colorModel);
			case ImagePlus.GRAY32:
				logger.debug("getting image with ndv");
				return getFloatImage(colorModel);
			case ImagePlus.COLOR_RGB:
				logger.debug("getting image without ndv RGB type");
				return getImagePlus().getImage();
			}

		}
		logger.debug("getting image from imageJ");
		ImagePlus imagePlus = getImagePlus();
		imagePlus.getProcessor().setColorModel(colorModel);
		return imagePlus.getImage();
	}

	/**
	 * This code is from FloatProcessor.getImage() in IJ. The difference is that
	 * IJ creates 255 classes for the values and we create 254. The first class
	 * is reserved for NaN (see first 'if' inside 'for')
	 *
	 * @return
	 * @throws IOException
	 */
	private Image getFloatImage(ColorModel cm) throws IOException {
		// Get the imagej pixels
		final ImagePlus imagePlus = (null == cachedImagePlus) ? fileReader
				.readImagePlus() : cachedImagePlus;
		float[] pixels = (float[]) imagePlus.getProcessor().getPixels();
		// scale from float to 8-bits
		byte[] pixels8;
		int size = getWidth() * getHeight();
		pixels8 = new byte[size];
		float max = (float) getMax();
		float min = (float) getMin();
		float scale = 254f / (max - min);
		for (int i = 0; i < size; i++) {
			if ((pixels[i] == noDataValue) || (pixels[i] < min)
					|| (pixels[i] > max)) {
				pixels8[i] = (byte) 0;
			} else {
				float value = pixels[i] - min;
				if (value < 0f) {
					value = 0f;
				}
				int ivalue = (int) (value * scale);
				if (ivalue > 254) {
					ivalue = 254;
				}
				pixels8[i] = (byte) (ivalue + 1);
			}
		}
		MemoryImageSource source = new MemoryImageSource(getWidth(),
				getHeight(), cm, pixels8, 0, getWidth());
		source.setAnimated(true);
		source.setFullBufferUpdates(true);
		return Toolkit.getDefaultToolkit().createImage(source);
	}

	/**
	 * This code is from ShortProcessor.getImage() in IJ. The difference is that
	 * IJ creates 255 classes for the values and we create 254. The first class
	 * is reserved for NaN (see first 'if' inside 'for')
	 *
	 * @return
	 * @throws IOException
	 */
	private Image getShortImage(ColorModel cm) throws IOException {
		// Get the imagej pixels
		final ImagePlus imagePlus = (null == cachedImagePlus) ? fileReader
				.readImagePlus() : cachedImagePlus;
		short[] pixels = (short[]) imagePlus.getProcessor().getPixels();
		// scale from float to 8-bits
		byte[] pixels8;
		int size = getWidth() * getHeight();
		pixels8 = new byte[size];
		short max = (short) getMax();
		short min = (short) getMin();
		float scale = 254 / (max - min);
		for (int i = 0; i < size; i++) {
			if ((pixels[i] == noDataValue) || (pixels[i] < min)
					|| (pixels[i] > max)) {
				pixels8[i] = (byte) 0;
			} else {
				short value = (short) (pixels[i] - min);
				if (value < 0) {
					value = 0;
				}
				int ivalue = (int) (value * scale);
				if (ivalue > 254) {
					ivalue = 254;
				}
				pixels8[i] = (byte) (ivalue + 1);
			}
		}
		MemoryImageSource source = new MemoryImageSource(getWidth(),
				getHeight(), cm, pixels8, 0, getWidth());
		source.setAnimated(true);
		source.setFullBufferUpdates(true);
		return Toolkit.getDefaultToolkit().createImage(source);
	}

	/**
	 * This code is from ShortProcessor.getImage() in IJ. The difference is that
	 * IJ creates 255 classes for the values and we create 254. The first class
	 * is reserved for NaN (see first 'if' inside 'for')
	 *
	 * @return
	 * @throws IOException
	 */
	private Image getByteImage(ColorModel cm) throws IOException {
		// Get the imagej pixels
		final ImagePlus imagePlus = (null == cachedImagePlus) ? fileReader
				.readImagePlus() : cachedImagePlus;
		byte[] pixels = (byte[]) imagePlus.getProcessor().getPixels();
		// scale from float to 8-bits
		byte[] pixels8;
		int size = getWidth() * getHeight();
		pixels8 = new byte[size];
		byte max = (byte) getMax();
		byte min = (byte) getMin();
		float scale = 254 / (max - min);
		for (int i = 0; i < size; i++) {
			if ((pixels[i] == noDataValue) || (pixels[i] < min)
					|| (pixels[i] > max)) {
				pixels8[i] = (byte) 0;
			} else {
				byte value = (byte) (pixels[i] - min);
				if (value < 0) {
					value = 0;
				}
				int ivalue = (int) (value * scale);
				if (ivalue > 254) {
					ivalue = 254;
				}
				pixels8[i] = (byte) (ivalue + 1);
			}
		}
		MemoryImageSource source = new MemoryImageSource(getWidth(),
				getHeight(), cm, pixels8, 0, getWidth());
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
	private static ColorModel addFirstTransparentClass(ColorModel colorModel) {
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

}
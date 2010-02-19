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
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.grap.model.GeoProcessorType;
import org.grap.model.RasterMetadata;

/**
 *
 * @author Thomas and Erwan
 *
 * This class is written to directly access the ESRI ascii grid format.
 *
 * The ASCII grid data file format comprises a few lines of header data followed
 * by lists of cell values. The header data includes the following keywords and
 * values:
 *
 * ncols : number of columns in the data set.
 *
 * nrows : number of rows in the data set.
 *
 * xllcorner : x-coordinate of the west border of the LowerLeft corner.
 *
 * yllcorner : y-coordinate of the south border of the LowerLeft corner.
 *
 * cellsize : size of the square cell of the data set.
 *
 * NODATA_value : arbitrary value assigned to unknown cells.
 *
 * Grap's meta-data matches the world file specifications as mentioned in the
 * wikipedia web page http://en.wikipedia.org/wiki/World_file
 *
 * Indeed, the first pixel of the pixels grid (with raw and column indexes both
 * equals to zero, at the upper left corner) corresponds, in the real world, to
 * the centroid of the corresponding UpperLeft rectangle.
 *
 * As defined in the ESRI Grid specifications wikipedia web page
 * http://en.wikipedia.org/wiki/ESRI_grid, the given lower left coordinates
 * (those that corresponds to the south-west edge of the LowerLeft rectangle in
 * the real world) have to be converted into the Grap UpperLeft centroid.
 *
 * Concerning the unique cellsize (pixel are square in ESRI Grid format), it is
 * converted into pixelSize_X (without any modification) and pixelSize_Y (using
 * the opposite -cellsize value).
 *
 * For example
 *
 * ncols 466
 *
 * nrows 448
 *
 * xllcorner 634592
 *
 * yllcorner 5588395
 *
 * cellsize 10
 *
 * NODATA_value -9999
 */
public class EsriGRIDReader implements RasterReader {
	private InputStream in;

	private float noDataValue;

	private RasterMetadata rasterMetadata;

	private GeoProcessorType geoProcessorType = GeoProcessorType.FLOAT;

	private String fileName;

	// constructors
	public EsriGRIDReader(final String fileName) {
		this(fileName, GeoProcessorType.FLOAT);
	}

	public EsriGRIDReader(final String fileName,
			final GeoProcessorType geoProcessorType) {
		this.fileName = fileName;
		this.geoProcessorType = geoProcessorType;
	}

	public EsriGRIDReader(final URL src) {
		this(src.getFile());
	}

	// private methods
	private final char readWhiteSpaces() throws IOException {
		int c = in.read();
		// ((' ' == c) || ('\t' == c) || ('\n' == c) || ('\r' == c));
		while ((32 == c) || (9 == c) || (10 == c) || (13 == c)) {
			c = in.read();
		}
		return (char) c;
	}

	private final String readString() throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append(readWhiteSpaces());
		int c = in.read();
		while ((32 != c) && (9 != c) && (10 != c) && (13 != c) && (-1 != c)) {
			sb.append((char) c);
			c = in.read();
		}
		return sb.toString();
	}

	private final String readNumber(final char initial) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append(initial);
		int c = in.read();
		while (((c > 47) && (c < 58)) || (45 == c) || (46 == c)) {
			sb.append((char) c);
			c = in.read();
		}
		return sb.toString();
	}

	private final int readInteger() throws NumberFormatException, IOException {
		return Integer.parseInt(readNumber(readWhiteSpaces()));
	}

	private final float readFloat() throws NumberFormatException, IOException {
		return Float.parseFloat(readNumber(readWhiteSpaces()));
	}

	private final double readDouble() throws NumberFormatException, IOException {
		return Double.parseDouble(readNumber(readWhiteSpaces()));
	}

	private final float[] readFloatBody(final int n) throws IOException {
		final float[] fValues = new float[n];
		for (int i = 0; i < n; i++) {
			fValues[i] = readFloat();
		}
		return fValues;
	}

	private final short[] readShortBody(final int n) throws IOException {
		final short[] sValues = new short[n];
		for (int i = 0; i < n; i++) {
			sValues[i] = (short) readInteger();
		}
		return sValues;
	}

	private final byte[] readByteBody(final int n) throws IOException {
		final byte[] bValues = new byte[n];
		for (int i = 0; i < n; i++) {
			bValues[i] = (byte) readInteger();
		}
		return bValues;
	}

	private final void open() throws IOException {
		InputStream src = new BufferedInputStream(new FileInputStream(fileName));

		if (fileName.toLowerCase().endsWith(".gz")) {
			src = new GZIPInputStream(src);
		} else if (fileName.toLowerCase().endsWith(".zip")) {
			src = new ZipInputStream(src); // needs to be tested
		}

		in = new BufferedInputStream(src);
	}

	private final void close() throws IOException {
		if (null != in) {
			in.close();
		}
	}

	private void readString(final String stringToCompareWith)
			throws IOException {
		if (!readString().equalsIgnoreCase(stringToCompareWith)) {
			throw new IOException("Invalid EsriGRID format ("
					+ stringToCompareWith + ")!");
		}
	}

	private final RasterMetadata readHeaderPart() throws IOException {
		readString("ncols");
		final int ncols = readInteger();
		readString("nrows");
		final int nrows = readInteger();
		readString("xllcorner");
		final double xllcorner = readDouble();
		readString("yllcorner");
		final double yllcorner = readDouble();
		readString("cellsize");
		final float cellsize = readFloat();
		readString("NODATA_value");
		noDataValue = readFloat();

		// the -cellsize in case of pixelSize_Y parameter is due to the world
		// file specifications
		final double upperLeftX = xllcorner + cellsize / 2;
		final double upperLeftY = yllcorner + nrows * cellsize - cellsize / 2;
		final float pixelSize_X = cellsize;
		final float pixelSize_Y = -cellsize;

		rasterMetadata = new RasterMetadata(upperLeftX, upperLeftY,
				pixelSize_X, pixelSize_Y, ncols, nrows, noDataValue);
		return rasterMetadata;
	}

	// public methods
	/**
	 * @return the new GeoRaster metadata.
	 * @throws IOException
	 */
	public synchronized final RasterMetadata readRasterMetadata()
			throws IOException {
		open();
		rasterMetadata = readHeaderPart();
		close();
		return rasterMetadata;
	}

	public synchronized ImagePlus readImagePlus() throws IOException {
		open();
		readHeaderPart();
		ImageProcessor imageProcessor = null;
		switch (geoProcessorType) {
		case BYTE:
			imageProcessor = new ByteProcessor(rasterMetadata.getNCols(),
					rasterMetadata.getNRows(), readByteBody(rasterMetadata
							.getNCols()
							* rasterMetadata.getNRows()), null);
			break;
		case SHORT:
			imageProcessor = new ShortProcessor(rasterMetadata.getNCols(),
					rasterMetadata.getNRows(), readShortBody(rasterMetadata
							.getNCols()
							* rasterMetadata.getNRows()), null);
			break;
		case FLOAT:
			imageProcessor = new FloatProcessor(rasterMetadata.getNCols(),
					rasterMetadata.getNRows(), readFloatBody(rasterMetadata
							.getNCols()
							* rasterMetadata.getNRows()), null);
			break;
		default:
			throw new RuntimeException("Unknown geoProcessorType : "
					+ geoProcessorType);
		}
		close();
		return new ImagePlus("", imageProcessor);
	}
}
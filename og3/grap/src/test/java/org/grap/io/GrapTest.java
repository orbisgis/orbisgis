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
import junit.framework.TestCase;

import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

public class GrapTest extends TestCase {
	public final static String externalData = "../../datas2tests/";
	public final static String internalData = "src/test/resources/";
	public final static String tmpData = "../../datas2tests/tmp/";
	private static int nrows = 10;
	private static int ncols = 10;
	public static GeoRaster sampleRaster;
	public static GeoRaster sampleDEM;
	public static short[] slopesAccumulationForDEM;
	public static short[] allWatershedsForDEM;
	public static short[] otherAllWatershedsForDEM;
	public static float[] slopesDirectionForDEM;
	public static short[] allOutletsForDEM;
	public static short[] watershedFromOutletIndexForDEM;

	static {
		final byte[] values = new byte[nrows * ncols];
		for (int i = 0; i < nrows * ncols; i++) {
			values[i] = (byte) i;
		}

		final RasterMetadata rmd = new RasterMetadata(0, 15, 1, -1, ncols,
				nrows);
		sampleRaster = GeoRasterFactory.createGeoRaster(values, LutGenerator
				.colorModel("fire"), rmd);

		final short[] DEM = new short[] {//
		100, 100, 100, 100, 100, 100, 100, 0, 100, 100,//
				100, 50, 50, 50, 100, 100, 25, 10, 25, 100,//
				100, 25, 25, 25, 100, 100, 25, 11, 25, 100,//
				100, 25, 15, 25, 100, 100, 25, 12, 25, 100,//
				100, 25, 14, 25, 100, 100, 25, 13, 25, 100,//
				100, 25, 13, 25, 100, 100, 25, 14, 25, 100,//
				100, 25, 12, 25, 100, 100, 25, 15, 25, 100,//
				100, 25, 11, 25, 100, 100, 25, 25, 25, 100,//
				100, 25, 10, 25, 100, 100, 50, 50, 50, 100,//
				100, 100, 0, 100, 100, 100, 100, 100, 100, 100,//
		};

		sampleDEM = GeoRasterFactory.createGeoRaster(DEM, LutGenerator
				.colorModel("fire"), rmd);
		float N = Float.NaN;
		slopesDirectionForDEM = new float[] {//
		N, N, N, N, N, N, N, N, N, N,//
				N, 7, 7, 7, 6, 1, 2, 3, 4, N,//
				N, 8, 7, 6, 5, 1, 1, 3, 5, N,//
				N, 1, 7, 5, 5, 1, 1, 3, 5, N,//
				N, 1, 7, 5, 5, 1, 1, 3, 5, N,//
				N, 1, 7, 5, 5, 1, 1, 3, 5, N,//
				N, 1, 7, 5, 5, 1, 1, 3, 5, N,//
				N, 1, 7, 5, 5, 1, 2, 3, 4, N,//
				N, 8, 7, 6, 5, 2, 3, 3, 3, N,//
				N, N, N, N, N, N, N, N, N, N,//
		};

		slopesAccumulationForDEM = new short[] {//
		0, 0, 0, 0, 0, 0, 0, 49, 0, 0,//
				0, 2, 1, 2, 0, 0, 2, 40, 2, 0,//
				0, 5, 2, 5, 0, 0, 1, 39, 1, 0,//
				0, 1, 19, 1, 0, 0, 1, 34, 1, 0,//
				0, 1, 24, 1, 0, 0, 1, 29, 1, 0,//
				0, 1, 29, 1, 0, 0, 1, 24, 1, 0,//
				0, 1, 34, 1, 0, 0, 1, 19, 1, 0,//
				0, 1, 39, 1, 0, 0, 5, 2, 5, 0,//
				0, 2, 40, 2, 0, 0, 2, 1, 2, 0,//
				0, 0, 49, 0, 0, 0, 0, 0, 0, 0,//
		};

		allWatershedsForDEM = new short[] { //
		1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
				1, 1, 1, 1, 1, 2, 2, 2, 2, 2,//
		};

		otherAllWatershedsForDEM = new short[] { //
		2, 2, 2, 2, 2, 1, 1, 1, 1, 1,//
				2, 2, 2, 2, 2, 1, 1, 1, 1, 1,//
				2, 2, 2, 2, 2, 1, 1, 1, 1, 1,//
				2, 2, 2, 2, 2, 1, 1, 1, 1, 1,//
				2, 2, 2, 2, 2, 1, 1, 1, 1, 1,//
				2, 2, 2, 2, 2, 1, 1, 1, 1, 1,//
				2, 2, 2, 2, 2, 1, 1, 1, 1, 1,//
				2, 2, 2, 2, 2, 1, 1, 1, 1, 1,//
				2, 2, 2, 2, 2, 1, 1, 1, 1, 1,//
				2, 2, 2, 2, 2, 1, 1, 1, 1, 1,//
		};

		allOutletsForDEM = new short[] { //
		0, 0, 0, 0, 0, 0, 0, 1, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 1, 0, 0, 0, 0, 0, 0, 0,//
		};

		watershedFromOutletIndexForDEM = new short[] { //
		1, 1, 1, 1, 1, 0, 0, 0, 0, 0,//
				1, 1, 1, 1, 1, 0, 0, 0, 0, 0,//
				1, 1, 1, 1, 1, 0, 0, 0, 0, 0,//
				1, 1, 1, 1, 1, 0, 0, 0, 0, 0,//
				1, 1, 1, 1, 1, 0, 0, 0, 0, 0,//
				1, 1, 1, 1, 1, 0, 0, 0, 0, 0,//
				1, 1, 1, 1, 1, 0, 0, 0, 0, 0,//
				1, 1, 1, 1, 1, 0, 0, 0, 0, 0,//
				1, 1, 1, 1, 1, 0, 0, 0, 0, 0,//
				1, 1, 1, 1, 1, 0, 0, 0, 0, 0,//
		};
	}

	public static void compareGeoRasterAndArray(final GeoRaster geoRaster,
			final short[] sArray) throws Exception {
		assertTrue(geoRaster.getWidth() * geoRaster.getHeight() == sArray.length);
		final ImagePlus grapImagePlus = geoRaster.getImagePlus();
		for (int r = 0; r < geoRaster.getHeight(); r++) {
			for (int c = 0; c < geoRaster.getWidth(); c++) {
				assertTrue((short) grapImagePlus.getProcessor().getPixelValue(
						c, r) == sArray[r * ncols + c]);
			}
		}

	}

	public static void compareGeoRasterAndArray(final GeoRaster geoRaster,
			final float[] sArray) throws Exception {
		assertTrue(geoRaster.getWidth() * geoRaster.getHeight() == sArray.length);
		final ImagePlus grapImagePlus = geoRaster.getImagePlus();
		for (int r = 0; r < geoRaster.getHeight(); r++) {
			for (int c = 0; c < geoRaster.getWidth(); c++) {
				assertTrue((grapImagePlus.getProcessor().getPixelValue(c, r) == sArray[r
						* ncols + c])
						|| (Float.isNaN(grapImagePlus.getProcessor()
								.getPixelValue(c, r)) && Float.isNaN(sArray[r
								* ncols + c])));
			}
		}
	}

	public static void printGeoRasterAndArray(final GeoRaster geoRaster,
			final short[] sArray) throws Exception {
		final ImagePlus grapImagePlus = geoRaster.getImagePlus();
		for (int r = 0; r < geoRaster.getHeight(); r++) {
			System.out.printf("raw %d\t", r);
			for (int c = 0; c < geoRaster.getWidth(); c++) {
				System.out.printf("%4.0f", grapImagePlus.getProcessor()
						.getPixelValue(c, r));
			}
			System.out.printf("\t");
			for (int c = 0; c < geoRaster.getWidth(); c++) {
				System.out.printf("%4d", sArray[r * ncols + c]);
			}
			System.out.println();
		}
	}

	public static void printGeoRasterAndArray(final GeoRaster geoRaster,
			final float[] sArray) throws Exception {
		final ImagePlus grapImagePlus = geoRaster.getImagePlus();
		for (int r = 0; r < geoRaster.getHeight(); r++) {
			System.out.printf("raw %d\t", r);
			for (int c = 0; c < geoRaster.getWidth(); c++) {
				System.out.printf("%4.0f", grapImagePlus.getProcessor()
						.getPixelValue(c, r));
			}
			System.out.printf("\t");
			for (int c = 0; c < geoRaster.getWidth(); c++) {
				System.out.printf("%4.0f", sArray[r * ncols + c]);
			}
			System.out.println();
		}
	}

	protected boolean equals(float[] pixels, float[] tifPixels) {
		if (tifPixels.length != pixels.length) {
			return false;
		} else {
			for (int i = 0; i < tifPixels.length; i++) {
				if (Float.isNaN(tifPixels[i])) {
					if (!Float.isNaN(pixels[i])) {
						return false;
					}
				} else {
					if (tifPixels[i] != pixels[i]) {
						return false;
					}
				}
			}
		}
		return true;
	}
}
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
package org.gdms;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.asc.AscDriver;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

abstract public class AbstractRasterProcessingTest extends TestCase {
	private static final double EPSILON = 1.0E-7;

	public static DataSourceFactory dsf = new DataSourceFactory();

	public static GeoRaster geoRaster;
	public static float[] pixels;
	public static float pixelSize_X;
	public static float pixelSize_Y;
	public static double xUlcorner;
	public static double yUlcorner;

	public final static String geoRasterPath = AbstractRasterProcessingTest.class
			.getResource("4x3.asc").getFile();

	static {
		dsf.getSourceManager().getDriverManager().registerDriver(
				AscDriver.class);
		dsf.getSourceManager().register("georastersource",
				new File(geoRasterPath));

		try {
			geoRaster = GeoRasterFactory.createGeoRaster(geoRasterPath);
			pixels = geoRaster.getFloatPixels();
			geoRaster.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		pixelSize_X = geoRaster.getMetadata().getPixelSize_X();
		pixelSize_Y = geoRaster.getMetadata().getPixelSize_Y();
		xUlcorner = geoRaster.getMetadata().getXulcorner();
		yUlcorner = geoRaster.getMetadata().getYulcorner();
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public boolean floatingPointNumbersEquality(final double a, final double b) {
		if (Double.isNaN(a)) {
			return Double.isNaN(b);
		} else {
			return Math.abs(a - b) < EPSILON;
		}
	}

	public void print(final DataSource ds) throws DriverException {
		ds.open();
		final long rowCount = ds.getRowCount();
		final int fieldCount = ds.getFieldCount();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			for (int fieldId = 0; fieldId < fieldCount; fieldId++) {
				final Value fieldValue = ds.getFieldValue(rowIndex, fieldId);
				System.out.printf("%s = %s ", fieldValue.getClass()
						.getSimpleName(), fieldValue.toString());
			}
			System.out.println();
		}
		ds.close();
	}
}
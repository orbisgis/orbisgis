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
package org.grap.processing.operation.hydrology.archive;

import junit.framework.TestCase;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.operation.hydrology.HydrologyUtilities;

public class PixelUtilitiesTest extends TestCase {
	private static final double EPSILON = 1.0E-5;
	private float[][] arrayOfDEMs;
	private RasterMetadata[] arrayOfRMDs;

	protected void setUp() throws Exception {
		super.setUp();

		arrayOfDEMs = new float[][] { //
		new float[] { 10, 10, 10, 10, Float.NaN, 10, 10, 10, 10 }, //
				new float[] { 10, 10, 10, 10, 10, 10, 10, 10, 10 }, //
				new float[] { 10, 10, 10, 5, 10, 10, 10, 10, 10 }, //
				new float[] { 10, 9, 8, 5, 10, 7, 6, 6, 10 }, //
		};

		arrayOfRMDs = new RasterMetadata[] {//
		new RasterMetadata(0, 15, 1, -1, 3, 3), //
				new RasterMetadata(0, 15, 5, -5, 3, 3), //
		};
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetMaxSlopeDirection() throws Exception {
		for (int i = 0; i < arrayOfRMDs.length; i++) {
			for (int j = 0; j < arrayOfDEMs.length; j++) {
				GeoRaster grDEM = GeoRasterFactory.createGeoRaster(
						arrayOfDEMs[j], arrayOfRMDs[i]);
				HydrologyUtilities hydrologyUtilities = new HydrologyUtilities(
						grDEM);

				switch (j) {
				case 0:
					assertEquals(GeoRaster.FLOAT_NO_DATA_VALUE,
							hydrologyUtilities.getD8Direction(1, 1));
					break;
				case 1:
					assertEquals(HydrologyUtilities.indecisionDirection,
							hydrologyUtilities.getD8Direction(1, 1));
					break;
				case 2:
				case 3:
					assertEquals(16, hydrologyUtilities.getD8Direction(1, 1));
					break;
				default:
					fail();
				}
			}
		}
	}

	public void testGetMaxSlopeAngleInDegrees() throws Exception {
		for (int i = 0; i < arrayOfRMDs.length; i++) {
			for (int j = 0; j < arrayOfDEMs.length; j++) {
				GeoRaster grDEM = GeoRasterFactory.createGeoRaster(
						arrayOfDEMs[j], arrayOfRMDs[i]);
				HydrologyUtilities hydrologyUtilities = new HydrologyUtilities(
						grDEM);

				switch (j) {
				case 0:
					assertTrue(Float.isNaN(hydrologyUtilities
							.getSlopeInDegrees(1, 1)));
					break;
				case 1:
					assertEquals(hydrologyUtilities.getSlopeInDegrees(1, 1), 0f);
					break;
				case 2:
				case 3:
					if (0 == i) {
						assertTrue(Math.abs(hydrologyUtilities
								.getSlopeInDegrees(1, 1)
								- 180 * Math.atan(5) / Math.PI) < EPSILON);
					} else if (1 == i) {
						assertEquals(
								hydrologyUtilities.getSlopeInDegrees(1, 1), 45f);
					}
					break;
				default:
					fail();
				}
			}
		}
	}
}
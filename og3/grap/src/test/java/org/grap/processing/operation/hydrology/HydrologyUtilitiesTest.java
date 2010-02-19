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
package org.grap.processing.operation.hydrology;

import ij.process.ImageProcessor;

import java.util.Set;
import java.util.Stack;

import junit.framework.TestCase;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

public class HydrologyUtilitiesTest extends TestCase {
	private static final double EPSILON = 1.0E-6;

	private static float ND = GeoRaster.FLOAT_NO_DATA_VALUE;

	private int nrows = 10;
	private int ncols = 10;

	private GeoRaster dem;
	private float[] demArray;

	private GeoRaster direction;
	private float[] directionArray;

	private GeoRaster accumulation;
	private float[] accumulationArray;

	protected void setUp() throws Exception {
		super.setUp();

		demArray = new float[] {//
		ND, ND, ND, ND, ND, ND, ND, ND, ND, ND,// 
				ND, 99, ND, ND, ND, ND, ND, ND, ND, ND,// 
				ND, ND, 95, ND, ND, ND, ND, ND, ND, ND,// 
				ND, ND, 90, ND, ND, ND, ND, ND, ND, ND,// 
				ND, ND, 85, ND, ND, ND, ND, ND, ND, ND,// 
				ND, 80, ND, ND, ND, ND, ND, ND, ND, ND,// 
				ND, ND, 75, 70, 65, 60, ND, ND, 45, ND,// 
				ND, ND, ND, ND, ND, ND, 55, 50, ND, ND,// 
				ND, ND, ND, ND, ND, ND, ND, ND, ND, ND,// 
				ND, ND, ND, ND, ND, ND, ND, ND, ND, ND,// 
		};
		RasterMetadata rasterMetadata = new RasterMetadata(0, 15, 1, -1, ncols,
				nrows);
		dem = GeoRasterFactory.createGeoRaster(demArray, rasterMetadata);
		dem.setNodataValue(ND);

		directionArray = new float[] {//
		ND, ND, ND, ND, ND, ND, ND, ND, ND, ND,// 
				ND, 8, ND, ND, ND, ND, ND, ND, ND, ND,// 
				ND, ND, 7, ND, ND, ND, ND, ND, ND, ND,// 
				ND, ND, 7, ND, ND, ND, ND, ND, ND, ND,// 
				ND, ND, 6, ND, ND, ND, ND, ND, ND, ND,// 
				ND, 8, ND, ND, ND, ND, ND, ND, ND, ND,// 
				ND, ND, 1, 1, 1, 8, ND, ND, -1, ND,// 
				ND, ND, ND, ND, ND, ND, 1, 2, ND, ND, // 
				ND, ND, ND, ND, ND, ND, ND, ND, ND, ND,// 
				ND, ND, ND, ND, ND, ND, ND, ND, ND, ND,// 
		};
		direction = dem.doOperation(new D8OpDirection());

		accumulation = direction.doOperation(new D8OpAccumulation());
		accumulationArray = new float[] {//
		ND, ND, ND, ND, ND, ND, ND, ND, ND, ND,// 
				ND, 1, ND, ND, ND, ND, ND, ND, ND, ND,// 
				ND, ND, 2, ND, ND, ND, ND, ND, ND, ND,// 
				ND, ND, 3, ND, ND, ND, ND, ND, ND, ND,// 
				ND, ND, 4, ND, ND, ND, ND, ND, ND, ND,// 
				ND, 5, ND, ND, ND, ND, ND, ND, ND, ND,// 
				ND, ND, 6, 7, 8, 9, ND, ND, 12, ND,// 
				ND, ND, ND, ND, ND, ND, 10, 11, ND, ND, // 
				ND, ND, ND, ND, ND, ND, ND, ND, ND, ND,// 
				ND, ND, ND, ND, ND, ND, ND, ND, ND, ND,// 
		};
	}

	public void compareGeoRasterAndArray(final GeoRaster geoRaster,
			final float[] array) throws Exception {
		assertTrue(geoRaster.getWidth() * geoRaster.getHeight() == array.length);
		ImageProcessor processor = geoRaster.getImagePlus().getProcessor();
		for (int r = 0; r < geoRaster.getHeight(); r++) {
			for (int c = 0; c < geoRaster.getWidth(); c++) {
				assertTrue(processor.getPixelValue(c, r) == array[r * ncols + c]);
			}
		}
	}

	private void printGeoRasterAndArray(final GeoRaster geoRaster,
			final float[] array) throws Exception {
		ImageProcessor processor = geoRaster.getImagePlus().getProcessor();
		for (int r = 0; r < geoRaster.getHeight(); r++) {
			System.out.printf("raw %d\t", r);
			for (int c = 0; c < geoRaster.getWidth(); c++) {
				System.out.printf("%6.0f", processor.getPixelValue(c, r));
			}
			System.out.printf("\t");
			for (int c = 0; c < geoRaster.getWidth(); c++) {
				System.out.printf("%6.0f", array[r * ncols + c]);
			}
			System.out.println();
		}
	}

	public boolean floatingPointNumbersEquality(final double a, final double b) {
		if (Double.isNaN(a)) {
			return Double.isNaN(b);
		} else {
			return Math.abs(a - b) < EPSILON;
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testHydrologyUtilities() {
		// TODO
	}

	public void testIsOnEdge() {
		// TODO
	}

	public void testGetPixelValueInt() {
		// TODO
	}

	public void testGetPixelValueIntInt() {
		// TODO
	}

	public void testGetD8Direction() {
		// TODO
	}

	public void testGetSlope() {
		// TODO
	}

	public void testGetSlopeInRadians() {
		// TODO
	}

	public void testGetSlopeInDegrees() {
		// TODO
	}

	public void testFromCellSlopeDirectionIdxToContributiveArea()
			throws Exception {
		HydrologyUtilities hu = new HydrologyUtilities(direction);

		Set<Integer> result = hu
				.fromCellSlopeDirectionIdxToContributiveArea(68);
		assertEquals(1, result.size());
		assertTrue(result.contains(77));

		result.clear();
		result = hu.fromCellSlopeDirectionIdxToContributiveArea(77);
		assertEquals(1, result.size());
		assertTrue(result.contains(76));
	}

	public void testFromCellSlopeDirectionToNextCellIndexInt() {
		// TODO
	}

	public void testFromCellSlopeDirectionToNextCellIndexIntIntInt() {
		// TODO
	}

	public void testIsARiverStart() {
		// TODO
	}

	public void testHydrologicalPath() throws Exception {
		HydrologyUtilities hu = new HydrologyUtilities(direction);

		Stack<HydroCell> path = new Stack<HydroCell>();
		hu.hydrologicalPath(11, path);
		assertEquals(12, path.size());
		assertTrue(floatingPointNumbersEquality(5 * Math.sqrt(2) + 6 * 1 + 1
				* 0, hu.pathLength(path)));

		printGeoRasterAndArray(dem, directionArray);
		printGeoRasterAndArray(direction, directionArray);
		compareGeoRasterAndArray(direction, directionArray);

		for (HydroCell pathCell : path) {
			System.out.printf("%d %f\n", pathCell.index, pathCell.dist);
		}
	}

	public void testShortHydrologicalPath() throws Exception {
		HydrologyUtilities hu = new HydrologyUtilities(direction);
		Stack<HydroCell> path = new Stack<HydroCell>();

		hu.shortHydrologicalPath(11, path, accumulationArray, 12345);
		assertEquals(12, path.size());
		assertTrue(floatingPointNumbersEquality(5 * Math.sqrt(2) + 6 * 1 + 1
				* 0, hu.pathLength(path)));

		path.clear();
		float threshold = 12f;
		HydroCell top = hu.shortHydrologicalPath(11, path, accumulationArray,
				threshold);
		assertEquals(11, path.size());
		assertTrue(floatingPointNumbersEquality(5 * Math.sqrt(2) + 6 * 1, hu
				.pathLength(path)));
		assertEquals(68, top.index);
		System.err.println(top.dist);
		assertEquals(threshold, top.dist);

		for (HydroCell pathCell : path) {
			System.out.printf("%d %f\n", pathCell.index, pathCell.dist);
		}

		// printGeoRasterAndArray(accumulation, accumulationArray);
		// compareGeoRasterAndArray(accumulation, accumulationArray);
	}
}
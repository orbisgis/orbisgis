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

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.grap.model.GeoRaster;

public class HydrologyUtilities {
	public final static float indecisionDirection = -1;
	public final static float indecisionAngle = 0;
	public float ndv;

	private final static double FACTOR = 180 / Math.PI;

	private ImageProcessor imageProcessor;
	private int ncols;
	private int nrows;

	private float[] invD8Distances;
	private float[] d8Distances;

	private final static short[] neighboursDirection = new short[] { 5, 6, 7,
			8, 1, 2, 3, 4 };

	/**
	 * Implementation of some classical D8 analysis algorithms. D8 stands for
	 * "Deterministic eight neighbour" method by O’Callaghan & Mark (1984)
	 * 
	 * The standard we have decided to implement is the one explained by David
	 * G. Tarboton (Utah State University, May, 2005) in the "Terrain Analysis
	 * Using Digital Elevation Models" (TauDEM) method.
	 * 
	 * 4 | 3 | 2
	 * 
	 * 5 | X | 1
	 * 
	 * 6 | 7 | 8
	 * 
	 * sink and flat areas pixels are equal to -1
	 * 
	 * nodataValue pixels and pixels located on DEM edges are equal to
	 * GeoRaster.FLOAT_NO_DATA_VALUE
	 */

	public HydrologyUtilities(final GeoRaster dem) throws IOException {
		ncols = dem.getMetadata().getNCols();
		nrows = dem.getMetadata().getNRows();
		imageProcessor = dem.getImagePlus().getProcessor();
		ndv = (float) (Double.isNaN(dem.getNoDataValue()) ? GeoRaster.FLOAT_NO_DATA_VALUE
				: dem.getNoDataValue());

		float cellWidth = dem.getMetadata().getPixelSize_X();
		float cellHeight = Math.abs(dem.getMetadata().getPixelSize_Y()); // usefull
		float hypotenuse = (float) Math.sqrt(cellWidth * cellWidth + cellHeight
				* cellHeight);
		float invPixelSize_X = 1 / cellWidth;
		float invPixelSize_Y = 1 / cellHeight;
		float invHypotenuse = (float) (1 / hypotenuse);

		d8Distances = new float[] { cellWidth, hypotenuse, cellHeight,
				hypotenuse, cellWidth, hypotenuse, cellHeight, hypotenuse };
		invD8Distances = new float[] { invPixelSize_X, invHypotenuse,
				invPixelSize_Y, invHypotenuse, invPixelSize_X, invHypotenuse,
				invPixelSize_Y, invHypotenuse };
	}

	public boolean isABorder(final int x, final int y) {
		return (0 == x) || (ncols - 1 == x) || (0 == y) || (nrows - 1 == y);
	}

	public float getPixelValue(final int idx) {
		return getPixelValue(idx % ncols, idx / ncols);
	}

	public float getPixelValue(final int x, final int y) {
		if ((0 > y) || (nrows <= y) || (0 > x) || (ncols <= x)) {
			return Float.NaN;
		} else {
			float pv = imageProcessor.getPixelValue(x, y);
			if (ndv == pv) {
				return Float.NaN;
			} else {
				return pv;
			}
		}
	}

	private static int getIdxForMaxValue(final float[] values) {
		float max = 0;
		int result = -1;
		for (int i = 0; i < values.length; i++) {
			if ((!Float.isNaN(values[i])) && (values[i] > max)) {
				result = i;
				max = values[i];
			}
		}
		return result;
	}

	private float[] getD8DirectionAndD8Slope(final int x, final int y) {
		final float currentElevation = getPixelValue(x, y);

		if (Float.isNaN(currentElevation) || isABorder(x, y)) {
			return new float[] { ndv, ndv };
		} else {
			final float[] ratios = new float[] {
					(currentElevation - getPixelValue(x + 1, y))
							* invD8Distances[0],
					(currentElevation - getPixelValue(x + 1, y - 1))
							* invD8Distances[1],
					(currentElevation - getPixelValue(x, y - 1))
							* invD8Distances[2],
					(currentElevation - getPixelValue(x - 1, y - 1))
							* invD8Distances[3],
					(currentElevation - getPixelValue(x - 1, y))
							* invD8Distances[4],
					(currentElevation - getPixelValue(x - 1, y + 1))
							* invD8Distances[5],
					(currentElevation - getPixelValue(x, y + 1))
							* invD8Distances[6],
					(currentElevation - getPixelValue(x + 1, y + 1))
							* invD8Distances[7] };
			final int tmpIdx = getIdxForMaxValue(ratios);
			if (-1 == tmpIdx) {
				// maybe an outlet or a sink
				return new float[] { indecisionDirection, indecisionAngle };
			} else {
				return new float[] { 1 + tmpIdx, ratios[tmpIdx] };
				// return new float[] { 1 << tmpIdx, ratios[tmpIdx] };
			}
		}
	}

	public float getD8Direction(final int x, final int y) {
		return getD8DirectionAndD8Slope(x, y)[0];
	}

	public float getSlope(final int x, final int y) {
		return getD8DirectionAndD8Slope(x, y)[1];
	}

	public float getSlopeInRadians(final int x, final int y) {
		float slope = getSlope(x, y);
		return (float) ((ndv == slope) ? ndv : Math.atan(slope));
	}

	public float getSlopeInDegrees(final int x, final int y) {
		float slope = getSlope(x, y);
		return (float) ((ndv == slope) ? ndv : FACTOR * Math.atan(slope));
	}

	public Set<Integer> fromCellSlopeDirectionIdxToContributiveArea(
			final int cellIdx) throws IOException {
		final Set<Integer> contributiveArea = new HashSet<Integer>();
		final int[] neighboursIndices = new int[] { 1, -ncols + 1, -ncols,
				-ncols - 1, -1, ncols - 1, ncols, ncols + 1 };

		for (int i = 0; i < 8; i++) {
			Integer tmp = cellIdx + neighboursIndices[i];
			final int yTmp = tmp / ncols;
			final int xTmp = tmp % ncols;
			tmp = getCellIndex(tmp, xTmp, yTmp);
			if ((null != tmp)
					&& (neighboursDirection[i] == getPixelValue(xTmp, yTmp))) {
				contributiveArea.add(tmp);
			}
		}
		return contributiveArea;
	}

	public Integer fromCellSlopeDirectionToNextCellIndex(final int i)
			throws IOException {
		final int y = i / ncols;
		final int x = i % ncols;
		return fromCellSlopeDirectionToNextCellIndex(i, x, y);
	}

	/**
	 * To use following method, take care to have created current
	 * HydrologyUtilities object with a directions grid !
	 * 
	 * @param i
	 * @param x
	 * @param y
	 * @return
	 * @throws IOException
	 */
	public Integer fromCellSlopeDirectionToNextCellIndex(final int i,
			final int x, final int y) throws IOException {
		switch ((short) getPixelValue(x, y)) {
		case 1:
			return getCellIndex(i + 1, x + 1, y);
		case 2:
			return getCellIndex(i - ncols + 1, x + 1, y - 1);
		case 3:
			return getCellIndex(i - ncols, x, y - 1);
		case 4:
			return getCellIndex(i - ncols - 1, x - 1, y - 1);
		case 5:
			return getCellIndex(i - 1, x - 1, y);
		case 6:
			return getCellIndex(i + ncols - 1, x - 1, y + 1);
		case 7:
			return getCellIndex(i + ncols, x, y + 1);
		case 8:
			return getCellIndex(i + ncols + 1, x + 1, y + 1);
		}
		return null;
	}

	private Integer getCellIndex(final int i) {
		final int y = i / ncols;
		final int x = i % ncols;
		return getCellIndex(i, x, y);
	}

	private Integer getCellIndex(final int i, final int x, final int y) {
		return ((0 > y) || (nrows <= y) || (0 > x) || (ncols <= x)) ? null : i;
	}

	/**
	 * To use following method, take care to have created current
	 * HydrologyUtilities object with a directions grid !
	 * 
	 * @param gipSlopesAccumulations
	 * @param riverThreshold
	 * @param i
	 * @return
	 * @throws IOException
	 */
	public boolean isARiverStart(final ImagePlus gipSlopesAccumulations,
			final int riverThreshold, final int i) throws IOException {
		final int y = i / ncols;
		final int x = i % ncols;
		final Float currAcc = gipSlopesAccumulations.getProcessor()
				.getPixelValue(x, y);

		if (riverThreshold == currAcc) {
			return true;
		} else if (riverThreshold < currAcc) {
			final Set<Integer> contributiveArea = fromCellSlopeDirectionIdxToContributiveArea(i);
			for (int contributor : contributiveArea) {
				final int yContributor = contributor / ncols;
				final int xContributor = contributor % ncols;
				if (riverThreshold <= gipSlopesAccumulations.getProcessor()
						.getPixelValue(xContributor, yContributor)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * To use following method, take care to have created current
	 * HydrologyUtilities object with a directions grid !
	 * 
	 * @param idx
	 * @param path
	 * @throws IOException
	 */
	public void hydrologicalPath(final int idx, final Stack<HydroCell> path)
			throws IOException {
		Integer curCellIdx = idx;
		do {
			int tmpDir = (int) getPixelValue(curCellIdx);
			if ((tmpDir >= 1) && (tmpDir <= 8)) {
				path.add(new HydroCell(curCellIdx, d8Distances[tmpDir - 1]));
			} else {
				path.add(new HydroCell(curCellIdx, 0f));
			}
			curCellIdx = fromCellSlopeDirectionToNextCellIndex(curCellIdx);
		} while (null != curCellIdx);
	}

	/**
	 * @param idx
	 * @param path
	 * @param accumulation
	 * @param threshold
	 * @return
	 * @throws IOException
	 */
	public HydroCell shortHydrologicalPath(final int idx,
			final Stack<HydroCell> path, final float[] accumulation,
			final float threshold) throws IOException {
		Integer curCellIdx = idx;
		do {
			int tmpDir = (int) getPixelValue(curCellIdx);
			if ((tmpDir >= 1) && (tmpDir <= 8)) {
				path.add(new HydroCell(curCellIdx, d8Distances[tmpDir - 1]));
			} else {
				path.add(new HydroCell(curCellIdx, 0f));
			}
			curCellIdx = fromCellSlopeDirectionToNextCellIndex(curCellIdx);

			if ((null != curCellIdx) && (threshold <= accumulation[curCellIdx])) {
				// this is a breaking condition
				return new HydroCell(curCellIdx, accumulation[curCellIdx]);
			}
		} while (null != curCellIdx);
		return null;
	}

	public double pathLength(final Stack<HydroCell> path) {
		double length = 0;
		for (HydroCell pathCell : path) {
			length += pathCell.dist;
		}
		return length;
	}
}
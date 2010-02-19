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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;

public class D8OpStrahlerStreamOrder extends D8OpAbstract implements Operation {
	public final static short noDataValue = GeoRaster.SHORT_NO_DATA_VALUE;
	public final static short riversStartValue = Short.MAX_VALUE;

	private ImagePlus gipDirection;
	private ImagePlus gipSlopesAccumulations;
	private HydrologyUtilities hydrologyUtilities;
	private short[] strahlerStreamOrder;
	private int riverThreshold;
	private int ncols;
	private int nrows;

	public D8OpStrahlerStreamOrder(final GeoRaster grSlopesAccumulations,
			final int riverThreshold) throws OperationException {
		try {
			gipSlopesAccumulations = grSlopesAccumulations.getImagePlus();
		} catch (IOException e) {
			throw new OperationException(e);
		}
		this.riverThreshold = riverThreshold;
	}

	@Override
	public GeoRaster evaluateResult(GeoRaster geoRaster, IProgressMonitor pm)
			throws OperationException {
		try {
			hydrologyUtilities = new HydrologyUtilities(geoRaster);

			gipDirection = geoRaster.getImagePlus();
			final RasterMetadata rasterMetadata = geoRaster.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			int maxStrahlerStreamOrder = computeStrahlerStreamOrders(pm);
			final GeoRaster grStrahlerStreamOrder = GeoRasterFactory
					.createGeoRaster(strahlerStreamOrder, rasterMetadata);
			grStrahlerStreamOrder.setNodataValue(noDataValue);
			System.out
					.printf(
							"Strahler stream order (max value = %d, river threshold = %d)\n",
							maxStrahlerStreamOrder, riverThreshold);
			return grStrahlerStreamOrder;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private int computeStrahlerStreamOrders(IProgressMonitor pm)
			throws IOException {
		short maxStrahlerStreamOrder = 1;
		strahlerStreamOrder = new short[nrows * ncols];
		Set<Integer> junctionsStack = new HashSet<Integer>();

		// 1st step: identify all the rivers' starts...
		int i = 0;
		for (int y = 0; y < nrows; y++) {

			if (y / 100 == y / 100.0) {
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo((int) (100 * y / nrows));
				}
			}

			for (int x = 0; x < ncols; x++, i++) {
				if (hydrologyUtilities.isARiverStart(gipSlopesAccumulations,
						riverThreshold, i)) {
					// if (SlopesUtilities.isARiverStart(gipSlopesAccumulations,
					// gipDirection, riverThreshold, ncols, nrows, i)) {
					strahlerStreamOrder[i] = riversStartValue;
					junctionsStack.add(i);
				} else {
					strahlerStreamOrder[i] = noDataValue;
				}
			}
		}

		// 2nd step:
		short step = 1;
		do {
			// System.out.printf("%d junctions in step number %d\n",
			// junctionsStack.size(), step);
			final Set<Integer> nextJunctionsStack = new HashSet<Integer>();
			for (int riverStart : junctionsStack) {
				// final Short colorTag = step;
				final Short colorTag = getStrahlerStreamOrderTag(riverStart);
				if (null != colorTag) {
					maxStrahlerStreamOrder = max(maxStrahlerStreamOrder,
							colorTag);
					// towards the next junction...
					tagUntilNextJunction(riverStart, colorTag,
							nextJunctionsStack);
				}
			}
			junctionsStack = nextJunctionsStack;
			step++;
		} while (0 < junctionsStack.size());

		return maxStrahlerStreamOrder;
	}

	private short max(final short a, final short b) {
		return (a > b) ? a : b;
	}

	private void tagUntilNextJunction(final int startIdx, final short colorTag,
			final Set<Integer> nextJunctionsStack) throws IOException {
		Integer idx = startIdx;
		int rIdx = idx / ncols;
		int cIdx = idx % ncols;

		do {
			strahlerStreamOrder[idx] = colorTag;
			final Integer next = nextCellIsARiversJunction(idx, cIdx, rIdx,
					nextJunctionsStack);
			if (null == next) {
				// new rivers junction
				idx = null;
			} else {
				idx = next;
				rIdx = idx / ncols;
				cIdx = idx % ncols;
			}
		} while (null != idx);
	}

	private Integer nextCellIsARiversJunction(final int idx, final int cIdx,
			final int rIdx, final Set<Integer> nextJunctionsStack)
			throws IOException {
		final Integer next = hydrologyUtilities
				.fromCellSlopeDirectionToNextCellIndex(idx, cIdx, rIdx);
		// SlopesUtilities
		// .fromCellSlopeDirectionToNextCellIndex(gipDirection, ncols,
		// nrows, idx, cIdx, rIdx);
		if (null != next) {
			final Set<Integer> contributiveArea = hydrologyUtilities
					.fromCellSlopeDirectionIdxToContributiveArea(next);
			// SlopesUtilities
			// .fromCellSlopeDirectionIdxToContributiveArea(gipDirection,
			// ncols, nrows, next);
			contributiveArea.remove(idx);
			for (int contributor : contributiveArea) {
				final int rContributor = contributor / ncols;
				final int cContributor = contributor % ncols;
				if (riverThreshold <= gipSlopesAccumulations.getProcessor()
						.getPixelValue(cContributor, rContributor)) {
					// next cell is a junction cell
					nextJunctionsStack.add(next);
					return null;
				}
			}
		}
		return next;
	}

	private Short getStrahlerStreamOrderTag(final int idx) throws IOException {
		if (riversStartValue == strahlerStreamOrder[idx]) {
			return 1;
		} else {
			final Set<Integer> contributiveArea = hydrologyUtilities
					.fromCellSlopeDirectionIdxToContributiveArea(idx);
			// SlopesUtilities
			// .fromCellSlopeDirectionIdxToContributiveArea(gipDirection,
			// ncols, nrows, idx);
			final SortedMap<Short, Short> tm = new TreeMap<Short, Short>();
			for (int contributor : contributiveArea) {
				final int rContributor = contributor / ncols;
				final int cContributor = contributor % ncols;
				if (riverThreshold <= gipSlopesAccumulations.getProcessor()
						.getPixelValue(cContributor, rContributor)) {
					final short sso = strahlerStreamOrder[contributor];
					if (tm.containsKey(sso)) {
						tm.put(sso, (short) (tm.get(sso) + 1));
					} else {
						tm.put(sso, (short) 1);
					}
				}
			}
			if (noDataValue == tm.firstKey()) {
				// the Strahler stream order of at least one contributor has not
				// been yet calculated... do not do anything !
				return null;
			} else if (1 == tm.get(tm.lastKey())) {
				// the Strahler stream order of the junction branch is equal to
				// the unique greatest Strahler stream order of their
				// contributors
				return tm.lastKey();
			} else {
				return (short) (tm.lastKey() + 1);
			}
		}
	}
}
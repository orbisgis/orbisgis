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

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;

public class D8OpAccumulation extends D8OpAbstract implements Operation {
	private float[] d8Accumulation;
	private int ncols;
	private int nrows;
	private HydrologyUtilities hydrologyUtilities;

	@Override
	public GeoRaster evaluateResult(GeoRaster direction, IProgressMonitor pm)
			throws OperationException {
		try {
			hydrologyUtilities = new HydrologyUtilities(direction);

			final RasterMetadata rasterMetadata = direction.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			int nbOfOutlets = accumulateSlopes(pm);
			final GeoRaster grAccumulation = GeoRasterFactory.createGeoRaster(
					d8Accumulation, rasterMetadata);
			grAccumulation.setNodataValue(hydrologyUtilities.ndv);
			System.out.printf("%d outlet(s)\n", nbOfOutlets);
			return grAccumulation;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private int accumulateSlopes(IProgressMonitor pm) throws IOException {
		// slopes accumulations' array initialization
		d8Accumulation = new float[nrows * ncols];

		int nbOfOutlets = 0;

		for (int y = 0, i = 0; y < nrows; y++) {

			if (y / 100 == y / 100.0) {
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo((int) (100 * y / nrows));
				}
			}

			for (int x = 0; x < ncols; x++, i++) {
				if (hydrologyUtilities.isABorder(x, y)
						|| Float.isNaN(hydrologyUtilities.getPixelValue(x, y))) {
					d8Accumulation[i] = hydrologyUtilities.ndv;
				} else if (0 == d8Accumulation[i]) {
					// current cell value has not been yet modified...
					nbOfOutlets += findOutletAndAccumulateSlopes(i);
				}
				// print();
			}
		}
		return nbOfOutlets;
	}

	private int findOutletAndAccumulateSlopes(final int i) throws IOException {
		boolean isProbablyANewOutlet = true;
		Integer curCellIdx = i;
		float acc = 0;

		do {
			final int y = curCellIdx / ncols;
			final int x = curCellIdx % ncols;

			if (Float.isNaN(hydrologyUtilities.getPixelValue(x, y))) {
				return isProbablyANewOutlet ? 1 : 0;
			} else {
				if (0 == d8Accumulation[curCellIdx]) {
					// current cell value has not been yet modified...
					d8Accumulation[curCellIdx] = 1 + acc;
					acc++;
				} else {
					// join an already identified river...
					if (isProbablyANewOutlet) {
						// junction point
						isProbablyANewOutlet = false;
					}
					d8Accumulation[curCellIdx] += acc;
				}
				curCellIdx = hydrologyUtilities
						.fromCellSlopeDirectionToNextCellIndex(curCellIdx, x, y);
			}
		} while (null != curCellIdx);
		return isProbablyANewOutlet ? 1 : 0;
	}

	void print() {
		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++) {
				System.out.printf("%3.0f ", d8Accumulation[r * ncols + c]);
			}
			System.out.println();
		}
		System.out.println("= = = = ");
	}
}
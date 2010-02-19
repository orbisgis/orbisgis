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
import java.util.HashMap;
import java.util.Map;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;

public class D8OpWatershedsWithThreshold extends D8OpAbstract implements
		Operation {
	public final static short ndv = GeoRaster.SHORT_NO_DATA_VALUE;

	private ImagePlus gipAllWatersheds;
	private ImagePlus gipAllOutlets;
	private ImagePlus gipSlopesAccumulations;
	private short[] watershedsWithThreshold;
	private int threshold;
	private int ncols;
	private int nrows;

	public D8OpWatershedsWithThreshold(final GeoRaster grAllWatersheds,
			final GeoRaster grAllOutlets, final int threshold)
			throws OperationException {
		try {
			gipAllWatersheds = grAllWatersheds.getImagePlus();
			gipAllOutlets = grAllOutlets.getImagePlus();
		} catch (IOException e) {
			throw new OperationException(e);
		}
		this.threshold = threshold;
	}

	@Override
	public GeoRaster evaluateResult(GeoRaster grSlopesAccumulations,
			IProgressMonitor pm) throws OperationException {
		try {
			gipSlopesAccumulations = grSlopesAccumulations.getImagePlus();
			final RasterMetadata rasterMetadata = grSlopesAccumulations
					.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			int nbOfWatershedsWithThreshold = computeAllwatershedsWithThreshold(pm);
			final GeoRaster grWatershedsWithThreshold = GeoRasterFactory
					.createGeoRaster(watershedsWithThreshold, rasterMetadata);
			grWatershedsWithThreshold.setNodataValue(ndv);
			System.out.printf("%d watersheds (outlet's threshold = %d)\n",
					nbOfWatershedsWithThreshold, threshold);
			return grWatershedsWithThreshold;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private int computeAllwatershedsWithThreshold(IProgressMonitor pm)
			throws IOException {
		short nbOfWatershedsWithThreshold = 0;
		final Map<Float, Short> mapOfBigOutlets = new HashMap<Float, Short>();

		// 1st step: identify the "good" outlets...
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
				if ((!Float.isNaN(gipAllOutlets.getProcessor().getPixelValue(x,
						y)))
						&& (gipSlopesAccumulations.getProcessor()
								.getPixelValue(x, y) >= threshold)) {
					// current cell is an outlet. It's slopes accumulation value
					// is greater or equal to the threshold value.
					nbOfWatershedsWithThreshold++;
					mapOfBigOutlets.put(gipAllWatersheds.getProcessor()
							.getPixelValue(x, y), nbOfWatershedsWithThreshold);
				}
			}
		}
		// 2nd step:
		watershedsWithThreshold = new short[nrows * ncols];
		i = 0;
		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++, i++) {
				final float tmp = gipAllWatersheds.getProcessor()
						.getPixelValue(c, r);
				watershedsWithThreshold[i] = mapOfBigOutlets.containsKey(tmp) ? mapOfBigOutlets
						.get(tmp)
						: ndv;
			}
		}
		return nbOfWatershedsWithThreshold;
	}
}
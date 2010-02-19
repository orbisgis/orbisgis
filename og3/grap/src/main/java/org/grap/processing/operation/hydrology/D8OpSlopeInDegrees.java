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
import org.grap.processing.cellularAutomata.CASlopeInDegrees;
import org.grap.processing.cellularAutomata.cam.CANFactory;
import org.grap.processing.cellularAutomata.cam.ICA;
import org.grap.processing.cellularAutomata.cam.ICAN;
import org.orbisgis.progress.IProgressMonitor;

public class D8OpSlopeInDegrees extends D8OpAbstractMultiThreads implements
		Operation {
	GeoRaster sequential(final GeoRaster grDEM, IProgressMonitor pm) throws OperationException {
		try {
			final HydrologyUtilities hydrologyUtilities = new HydrologyUtilities(
					grDEM);
			final RasterMetadata rasterMetadata = grDEM.getMetadata();
			final int nrows = rasterMetadata.getNRows();
			final int ncols = rasterMetadata.getNCols();
			final float[] slopes = new float[nrows * ncols];
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
					slopes[i] = hydrologyUtilities.getSlopeInDegrees(x, y);
				}
			}

			final GeoRaster grSlopeInDegrees = GeoRasterFactory
					.createGeoRaster(slopes, rasterMetadata);
			grSlopeInDegrees.setNodataValue(hydrologyUtilities.ndv);
			return grSlopeInDegrees;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	GeoRaster parallel(final GeoRaster grDEM) throws OperationException {
		try {
			final HydrologyUtilities hydrologyUtilities = new HydrologyUtilities(
					grDEM);
			final RasterMetadata rasterMetadata = grDEM.getMetadata();
			final int nrows = rasterMetadata.getNRows();
			final int ncols = rasterMetadata.getNCols();

			final ICA ca = new CASlopeInDegrees(hydrologyUtilities, nrows, ncols);
			final ICAN ccan = CANFactory.createCAN(ca);
			ccan.getStableState();

			final GeoRaster grSlopeInDegrees = GeoRasterFactory
					.createGeoRaster((float[]) ccan.getCANValues(),
							rasterMetadata);
			grSlopeInDegrees.setNodataValue(hydrologyUtilities.ndv);
			return grSlopeInDegrees;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}
}
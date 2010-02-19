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
import java.util.Stack;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;

public class D8OpAllWatersheds extends D8OpAbstract implements Operation {
	public final static float ndv = GeoRaster.FLOAT_NO_DATA_VALUE;
	public final static float notProcessedYet = 0;

	private HydrologyUtilities hydrologyUtilities;
	private float[] watersheds;
	private int ncols;
	private int nrows;

	@Override
	public GeoRaster evaluateResult(GeoRaster direction, IProgressMonitor pm)
			throws OperationException {
		try {
			hydrologyUtilities = new HydrologyUtilities(direction);
			final RasterMetadata rasterMetadata = direction.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			int nbOfWatersheds = computeAllWatersheds(pm);
			final GeoRaster grAllWatersheds = GeoRasterFactory.createGeoRaster(
					watersheds, rasterMetadata);
			grAllWatersheds.setNodataValue(ndv);
			System.out.printf("%d watersheds\n", nbOfWatersheds);
			return grAllWatersheds;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private int computeAllWatersheds(IProgressMonitor pm) throws IOException {
		watersheds = new float[nrows * ncols];
		float newDefaultColor = 1;

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
					watersheds[i] = ndv;
				} else if (notProcessedYet == watersheds[i]) {
					// current cell value has not been yet modified...
					Float color = null;
					final Stack<HydroCell> path = new Stack<HydroCell>();
					HydroCell top = hydrologyUtilities.shortHydrologicalPath(i,
							path, watersheds, 1);
					if (null == top) {
						color = newDefaultColor;
						newDefaultColor++;
					} else {
						color = top.dist;
					}
					colourize(path, color);
				}
			}
		}
		return (int) (newDefaultColor - 1);
	}

	private void colourize(final Stack<HydroCell> path, final float color) {
		for (HydroCell cellItem : path) {
			watersheds[cellItem.index] = color;
		}
	}
}
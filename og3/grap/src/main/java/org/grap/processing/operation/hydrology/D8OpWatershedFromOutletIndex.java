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
import java.util.HashSet;
import java.util.Set;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;

public class D8OpWatershedFromOutletIndex extends D8OpAbstract implements
		Operation {
	public final static byte ndv = 0;
	public final static byte doNotBelongsToTheWatershed = ndv;
	public final static byte belongsToTheWatershed = 1;

	private HydrologyUtilities hydrologyUtilities;
	private float[] sameWatershed;
	private int ncols;
	private int nrows;

	private int outletIdx;

	public D8OpWatershedFromOutletIndex(final int outletIdx) {
		this.outletIdx = outletIdx;
	}

	@Override
	public GeoRaster evaluateResult(GeoRaster direction, IProgressMonitor pm)
			throws OperationException {
		try {
			hydrologyUtilities = new HydrologyUtilities(direction);

			final RasterMetadata rasterMetadata = direction.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			computeSameWatershed();
			final GeoRaster grAllOutlets = GeoRasterFactory.createGeoRaster(
					sameWatershed, rasterMetadata);
			grAllOutlets.setNodataValue(ndv);
			System.out.printf("Watershed for (%d,%d)\n", outletIdx % ncols,
					outletIdx / ncols);
			return grAllOutlets;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private void computeSameWatershed() throws IOException {
		sameWatershed = new float[nrows * ncols];
		Set<Integer> parentsCell = new HashSet<Integer>();
		parentsCell.add(outletIdx);
		do {
			parentsCell = computeSameWatershed(parentsCell);
		} while (0 < parentsCell.size());
	}

	private Set<Integer> computeSameWatershed(final Set<Integer> sonsCell)
			throws IOException {
		final Set<Integer> parentsCell = new HashSet<Integer>();
		for (int sonIdx : sonsCell) {
			sameWatershed[sonIdx] = belongsToTheWatershed;
			parentsCell.addAll(hydrologyUtilities
					.fromCellSlopeDirectionIdxToContributiveArea(sonIdx));
		}
		return parentsCell;
	}
}
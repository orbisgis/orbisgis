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
package org.grap.processing.operation.topographicIndices;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;

/**
 * The stream power index (Moore et al., 1992) is the first of several indices
 * indicating possibke erosion by concentrated flow.
 * 
 * StreamPowerIndex = A s * S
 * 
 * where
 * 
 * A s = unit contributing area (m²/m) S = slope gradient in radians (m/m) (tan
 * B = S)
 * 
 * @author bocher
 * 
 */
public class StreamPowerIndexOp implements Operation {

	private static final double ALMOST_ZERO = 0.0011;

	private int ncols;

	private int nrows;

	private ImageProcessor m_Slope;

	private ImageProcessor slope;

	private GeoRaster accFlow;

	private ImageProcessor m_StreamPowerIndex;

	private ImageProcessor m_accFlow;

	private float cellSize;

	public StreamPowerIndexOp(final GeoRaster accFlow) {
		this.accFlow = accFlow;
	}

	public GeoRaster execute(final GeoRaster geoRaster, IProgressMonitor pm)
			throws OperationException {

		return processAlgorithm(geoRaster);
	}

	public GeoRaster processAlgorithm(final GeoRaster geoRaster)
			throws OperationException {

		try {
			m_Slope = geoRaster.getImagePlus().getProcessor();
			m_accFlow = accFlow.getImagePlus().getProcessor();

			nrows = geoRaster.getMetadata().getNRows();
			ncols = geoRaster.getMetadata().getNCols();
			cellSize = geoRaster.getMetadata().getPixelSize_X();

			m_StreamPowerIndex = new FloatProcessor(ncols, nrows);

			int x, y;

			for (y = 0; y < nrows; y++) {
				for (x = 0; x < ncols; x++) {

					float dSlope = m_Slope.getPixelValue(x, y);
					float dAccFlow = m_accFlow.getPixelValue(x, y);

					if (((Float.isNaN(dSlope)) || (Float.isNaN(dAccFlow)))) {
						m_StreamPowerIndex.putPixelValue(x, y, GeoRaster.FLOAT_NO_DATA_VALUE);

					} else if ((dSlope == GeoRaster.FLOAT_NO_DATA_VALUE)
							|| (dAccFlow == GeoRaster.FLOAT_NO_DATA_VALUE)) {
						m_StreamPowerIndex.putPixelValue(x, y,
								GeoRaster.FLOAT_NO_DATA_VALUE);
					} else {
						dAccFlow /= cellSize;
						dSlope = (float) Math
								.max(Math.tan(dSlope), ALMOST_ZERO);
						m_StreamPowerIndex.putPixelValue(x, y, dAccFlow
								* dSlope);
					}
				}
			}

			GeoRaster gr = GeoRasterFactory.createGeoRaster(m_StreamPowerIndex,
					geoRaster.getMetadata());
			gr.setNodataValue(GeoRaster.FLOAT_NO_DATA_VALUE);
			return gr;
		} catch (IOException e) {
			throw new OperationException(e);
		}

	}

}
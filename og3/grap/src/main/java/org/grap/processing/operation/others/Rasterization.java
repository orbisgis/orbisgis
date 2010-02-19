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
package org.grap.processing.operation.others;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.gui.Roi;
import ij.process.ImageProcessor;

import java.io.IOException;
import java.util.ArrayList;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;

public class Rasterization implements Operation {
	private RasteringMode rasteringMode;
	private ArrayList<Roi> rois;
	private ArrayList<Double> values;

	public Rasterization(final RasteringMode rasteringMode,
			final ArrayList<Roi> rois, ArrayList<Double> values) {
		this.rasteringMode = rasteringMode;
		this.rois = rois;
		this.values = values;
	}

	public Rasterization(final RasteringMode rasteringMode,
			final ArrayList<Roi> rois, final double value) {
		values = new ArrayList<Double>();
		for (int i = 0; i < rois.size(); i++) {
			values.add(new Double(value));
		}
		this.rasteringMode = rasteringMode;
		this.rois = rois;
	}

	public GeoRaster execute(final GeoRaster geoRaster, IProgressMonitor pm)
			throws OperationException {
		try {
			geoRaster.open();
			final int width = geoRaster.getMetadata().getNCols();
			final int heigth = geoRaster.getMetadata().getNRows();

			final ImagePlus resultImp = NewImage.createShortImage(
					"rasterization", width, heigth, 1, NewImage.FILL_WHITE);
			final ImageProcessor processor = resultImp.getProcessor();

			for (int i = 0; i < rois.size(); i++) {
				processor.snapshot();
				final Roi roi = rois.get(i);
				final Double value = values.get(i);
				switch (rasteringMode) {
				case FILL:
					processor.setRoi(roi);
					processor.setValue(value);
					processor.fillPolygon(roi.getPolygon());
					break;
				case DRAW:
					// processor.setColor(Color.red);
					processor.setValue(value);
					roi.drawPixels(processor);
					break;
				default:
					throw new OperationException("Unknown rasterizing mode: "
							+ rasteringMode);
				}

			}
			processor.resetMinAndMax();

			return GeoRasterFactory.createGeoRaster(processor, geoRaster
					.getMetadata());
		} catch (IOException e) {
			throw new OperationException("Cannot rasterizing the data", e);
		}
	}
}
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
package org.grap.processing.operation;

import ij.ImagePlus;
import ij.measure.Calibration;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;

public class GeoRasterCalculator implements Operation {
	public static final int ADD = 3, SUBSTRACT = 4, MULTIPLY = 5, DIVIDE = 6,
			AND = 9, OR = 10, XOR = 11, MIN = 12, MAX = 13, AVERAGE = 7,
			DIFFERENCE = 8, COPY = 0;

	public static Map<String, Integer> operators = new HashMap<String, Integer>();
	static {
		operators.put("Add", ADD);
		operators.put("Substract", SUBSTRACT);
		operators.put("Multiply", MULTIPLY);
		operators.put("Divide", DIVIDE);
		operators.put("Average", AVERAGE);
		operators.put("Difference", DIFFERENCE);
		operators.put("And", AND);
		operators.put("Or", OR);
		operators.put("XOr", XOR);
		operators.put("Min", MIN);
		operators.put("Max", MAX);
		operators.put("Copy", COPY);
	}

	private GeoRaster gr2;
	private int method;

	public GeoRasterCalculator(final GeoRaster gr2, final int method) {
		this.gr2 = gr2;
		this.method = method;
	}

	public GeoRaster execute(final GeoRaster gr1, IProgressMonitor pm) throws OperationException {
		try {
			final ImagePlus img1 = gr1.getImagePlus();
			final ImagePlus img2 = gr2.getImagePlus();

			if (gr1.getMetadata().getEnvelope().equals(
					gr2.getMetadata().getEnvelope())) {
				final ImageProcessor ip1 = img1.getProcessor();
				final ImageProcessor ip2 = img2.getProcessor();
				final Calibration cal1 = img1.getCalibration();
				ip1.copyBits(ip2, 0, 0, method);

				if (!(ip1 instanceof ByteProcessor)) {
					ip1.resetMinAndMax();
				}
				final ImagePlus img3 = new ImagePlus("Result of "
						+ img1.getShortTitle(), ip1);
				img3.setCalibration(cal1);

				return GeoRasterFactory
						.createGeoRaster(img3, gr1.getMetadata());
			}
		} catch (IOException e) {
			throw new OperationException(e);
		}
		return GeoRasterFactory.createNullGeoRaster();
	}

	
}
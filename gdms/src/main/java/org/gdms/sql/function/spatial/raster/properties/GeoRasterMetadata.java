/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.function.spatial.raster.properties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.OperationException;
import org.orbisgis.progress.ProgressMonitor;

public final class GeoRasterMetadata {
	public static final int MIN = 12, MAX = 13, COUNT = 128, WIDTH = 256,
			HEIGHT = 512, PIXELSIZEX = 1024, PIXELSIZEY = 2048, UPPERLEFTX = 4,
			UPPERLEFTY = 8, NUMBANDS = 9, NODATAVALUE = 10;

	public static final Map<String, Integer> operators = new HashMap<String, Integer>();
	static {
		operators.put("min", MIN);
		operators.put("max", MAX);
		operators.put("count", COUNT);
		operators.put("ncols", WIDTH);
		operators.put("nrows", HEIGHT);
		operators.put("pixelsizex", PIXELSIZEX);
		operators.put("pixelsizey", PIXELSIZEY);
		operators.put("upperleftx", UPPERLEFTX);
		operators.put("upperlefty", UPPERLEFTY);
		operators.put("numnands", NUMBANDS);
		operators.put("nodatavalue", NODATAVALUE);
	}

	private int method;

	public GeoRasterMetadata(final int method) {
		this.method = method;
	}

	public Value execute(final GeoRaster gr1, ProgressMonitor pm)
			throws OperationException {
		try {
			RasterMetadata metadata = gr1.getMetadata();
			switch (method) {
			case MIN:
				return ValueFactory.createValue(gr1.getMin());
			case MAX:
				return ValueFactory.createValue(gr1.getMax());
			case COUNT:
				return ValueFactory.createValue(metadata.getNCols()
						* metadata.getNRows());
			case WIDTH:
				return ValueFactory.createValue(metadata.getNCols());
			case HEIGHT:
				return ValueFactory.createValue(metadata.getNRows());
			case PIXELSIZEX:
				return ValueFactory.createValue(metadata.getPixelSize_X());
			case PIXELSIZEY:
				return ValueFactory.createValue(metadata.getPixelSize_Y());
			case UPPERLEFTX:
				return ValueFactory.createValue(metadata.getXulcorner());
			case UPPERLEFTY:
				return ValueFactory.createValue(metadata.getYulcorner());
			case NUMBANDS:
				return ValueFactory.createValue(gr1.getImagePlus()
						.getNChannels());
			case NODATAVALUE:
				return ValueFactory.createValue(metadata.getNoDataValue());
			default:
				break;
			}
			return ValueFactory.createNullValue();

		} catch (IOException e) {
			throw new OperationException(e);
		}
	}
}
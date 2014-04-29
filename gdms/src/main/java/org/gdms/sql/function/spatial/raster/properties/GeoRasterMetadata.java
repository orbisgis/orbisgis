/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
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

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.OperationException;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

public final class GeoRasterMetadata {
	public static final int MIN = 12, MAX = 13, COUNT = 128, WIDTH = 256,
			HEIGHT = 512, PIXELSIZEX = 1024, PIXELSIZEY = 2048, UPPERLEFTX = 4,
			UPPERLEFTY = 8, NUMBANDS = 9, NODATAVALUE = 10;

	public static final Map<String, Integer> OPERATORS = new HashMap<String, Integer>();
	static {
		OPERATORS.put("min", MIN);
		OPERATORS.put("max", MAX);
		OPERATORS.put("count", COUNT);
		OPERATORS.put("ncols", WIDTH);
		OPERATORS.put("nrows", HEIGHT);
		OPERATORS.put("pixelsizex", PIXELSIZEX);
		OPERATORS.put("pixelsizey", PIXELSIZEY);
		OPERATORS.put("upperleftx", UPPERLEFTX);
		OPERATORS.put("upperlefty", UPPERLEFTY);
		OPERATORS.put("numnands", NUMBANDS);
		OPERATORS.put("nodatavalue", NODATAVALUE);
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
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
package org.gdms.sql.function.spatial.geometry.operators;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.geometry.AbstractSpatialFunction;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

public class Buffer extends AbstractSpatialFunction {
	private static final String CAP_STYLE_SQUARE = "square";
	private static final String CAP_STYLE_BUTT = "butt";

	public Value evaluate(final Value[] args) throws FunctionException {
		if ((args[0].isNull()) || (args[1].isNull())) {
			return ValueFactory.createNullValue();
		} else {
			final Geometry geom = args[0].getAsGeometry();
			final double bufferSize = args[1].getAsDouble();
			Geometry buffer;
			if (args.length == 3) {
				final String bufferStyle = args[2].toString();
				buffer = runBuffer(geom, bufferSize, bufferStyle);
			} else {
				buffer = geom.buffer(bufferSize);
			}
			return ValueFactory.createValue(buffer);
		}
	}

	public String getName() {
		return "Buffer";
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] {
				new Arguments(Argument.GEOMETRY, Argument.NUMERIC),
				new Arguments(Argument.GEOMETRY, Argument.NUMERIC,
						Argument.STRING) };
	}

	public boolean isAggregate() {
		return false;
	}

	private Geometry runBuffer(final Geometry geom, final double bufferSize,
			final String endCapStyle) {
		BufferOp bufOp = null;

		if (endCapStyle.equalsIgnoreCase(CAP_STYLE_SQUARE)) {
			bufOp = new BufferOp(geom, new BufferParameters(
					BufferParameters.DEFAULT_QUADRANT_SEGMENTS,
					BufferParameters.CAP_SQUARE));
		} else if (endCapStyle.equalsIgnoreCase(CAP_STYLE_BUTT)) {
			bufOp = new BufferOp(geom, new BufferParameters(
					BufferParameters.DEFAULT_QUADRANT_SEGMENTS,
					BufferParameters.CAP_FLAT));
		} else {
			bufOp = new BufferOp(geom, new BufferParameters(
					BufferParameters.DEFAULT_QUADRANT_SEGMENTS,
					BufferParameters.CAP_ROUND));
		}

		return bufOp.getResultGeometry(bufferSize);
	}

	public String getDescription() {
		return "Compute a buffer around a geometry. Usage: Buffer(the_geom, bufferSize[, 'butt'|'square'|'round'])";
	}

	public String getSqlOrder() {
		return "select Buffer(the_geom, bufferSize[, 'butt'|'square'|'round']) from myTable;";
	}

}
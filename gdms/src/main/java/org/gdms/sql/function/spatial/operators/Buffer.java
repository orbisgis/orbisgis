/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.function.spatial.operators;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.function.spatial.AbstractSpatialFunction;
import org.gdms.sql.strategies.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.buffer.BufferOp;

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

	public void validateTypes(Type[] argumentsTypes)
			throws IncompatibleTypesException {
		FunctionValidator
				.failIfBadNumberOfArguments(this, argumentsTypes, 2, 3);
		FunctionValidator.failIfNotOfType(this, argumentsTypes[0],
				Type.GEOMETRY);
		FunctionValidator.failIfNotNumeric(this, argumentsTypes[1]);
		if (argumentsTypes.length == 3) {
			FunctionValidator.failIfNotOfType(this, argumentsTypes[2],
					Type.STRING);
		}
	}

	public boolean isAggregate() {
		return false;
	}

	private Geometry runBuffer(final Geometry geom, final double bufferSize,
			final String endCapStyle) {
		final BufferOp bufOp = new BufferOp(geom);

		if (endCapStyle.equalsIgnoreCase(CAP_STYLE_SQUARE)) {
			bufOp.setEndCapStyle(BufferOp.CAP_SQUARE);
		} else if (endCapStyle.equalsIgnoreCase(CAP_STYLE_BUTT)) {
			bufOp.setEndCapStyle(BufferOp.CAP_BUTT);
		} else {
			bufOp.setEndCapStyle(BufferOp.CAP_ROUND);
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
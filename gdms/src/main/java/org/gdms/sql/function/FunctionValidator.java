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
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
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
package org.gdms.sql.function;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;

import com.vividsolutions.jts.geom.Geometry;

public class FunctionValidator {

	public static void failIfNull(Value... values) throws FunctionException {
		for (Value value : values) {
			if (value.getType() == Type.NULL) {
				throw new FunctionException("Cannot operate in null values");
			}
		}
	}

	public static void warnIfNull(Value... values) throws WarningException {
		for (Value value : values) {
			if (value.getType() == Type.NULL) {
				throw new WarningException("Cannot operate in null values");
			}
		}
	}

	public static void warnIfGeometryNotValid(Value... values)
			throws WarningException {
		for (Value value : values) {
			Geometry geom = value.getAsGeometry();
			if (!geom.isValid()) {
				throw new WarningException(geom.toText()
						+ " is not a valid geometry");
			}
		}
	}

	public static void failIfBadNumberOfArguments(Function function,
			Value[] args, int i) throws FunctionException {
		if (args.length != i) {
			throw new FunctionException("The function " + function.getName()
					+ " has a wrong number of arguments. " + i + " expected");
		}
	}

	public static void warnIfNotOfType(Value value, int type)
			throws WarningException {
		if (type != value.getType()) {
			throw new WarningException(value.toString() + " is not of type "
					+ type);
		}
	}

	public static void failIfNotOfType(Value value, int type)
			throws FunctionException {
		if (type != value.getType()) {
			throw new FunctionException(value.toString() + " is not of type "
					+ type);
		}
	}
}
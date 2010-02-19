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
package org.gdms.sql.function.alphanumeric;

import java.util.HashSet;

import java.util.Set;

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;

public class IsUID implements Function {
	private Set<Value> setOfUniqValues;

	public Value evaluate(Value[] args) throws FunctionException {
		FunctionValidator.failIfNull(args[0]);
		if (null == setOfUniqValues) {
			setOfUniqValues = new HashSet<Value>();
		}

		if (setOfUniqValues.contains(args[0])) {
			throw new FunctionException("Value " + args[0]
					+ " already exists : redundancy is forbidden !");
		} else {
			setOfUniqValues.add(args[0]);
		}
		return ValueFactory.createValue(true);
	}

	public String getName() {
		return "IsUID";
	}

	public boolean isAggregate() {
		return false;
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.INT),
				new Arguments(Argument.LONG), new Arguments(Argument.SHORT) };
	}

	public String getDescription() {
		return "Check if the column is an unique identifier";
	}

	public String getSqlOrder() {
		return "select * from myTable where isUID(column);";
	}

	@Override
	public Value getAggregateResult() {
		return null;
	}

	@Override
	public Type getType(Type[] argsTypes) throws InvalidTypeException {

		return TypeFactory.createType(Type.BOOLEAN);
	}
}
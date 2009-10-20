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
/***********************************
 * <p>Title: CarThema</p>
 * Perspectives Software Solutions
 * Copyright (c) 2006
 * @author Vladimir Peric, Vladimir Cetkovic
 ***********************************/
package org.gdms.sql.function.alphanumeric;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class Min implements Function {
	private Value min = ValueFactory.createNullValue();

	public Value evaluate(Value[] args) throws FunctionException {
		if (!args[0].isNull()) {
			try {
				if (min.isNull()) {
					min = args[0];
				} else {
					if (args[0].less(min).getAsBoolean()) {
						min = args[0];
					}
				}
			} catch (IncompatibleTypesException e) {
				throw new FunctionException(e);
			}
		}
		return min;
	}

	public String getName() {
		return "Min";
	}

	public boolean isAggregate() {
		return true;
	}

	public Type getType(Type[] types) {
		return types[0];
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.NUMERIC) };
	}

	public void validateTypes(Type[] argumentsTypes)
			throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, argumentsTypes, 1);
		FunctionValidator.failIfNotNumeric(this, argumentsTypes[0]);
	}

	public String getDescription() {
		return "Return the minimum value";
	}

	public String getSqlOrder() {
		return "select Min(myField) from myTable;";
	}

	@Override
	public Value getAggregateResult() {
		return min;
	}

}
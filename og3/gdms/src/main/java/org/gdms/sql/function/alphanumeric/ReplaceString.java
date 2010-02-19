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

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

public class ReplaceString implements Function {

	public Value evaluate(Value[] arg0) throws FunctionException {
		String text;
		if (arg0[0].isNull()) {
			return ValueFactory.createNullValue();
		} else {
			// Get the arguments
			text = arg0[0].getAsString();
			String textFrom = arg0[1].getAsString();
			String textTo = arg0[2].getAsString();

			text.replace(textFrom, textTo);

		}
		return ValueFactory.createValue(text);

	}

	public String getDescription() {

		return "Replace all occurrences in string of substring from with substring to ";
	}

	public String getName() {

		return "Replace";
	}

	public String getSqlOrder() {

		return "select Replace(string text, from text, to text) from myTable";
	}

	public Type getType(Type[] arg0) throws InvalidTypeException {

		return TypeFactory.createType(Type.STRING);
	}

	public boolean isAggregate() {
		return false;
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.STRING,
				Argument.STRING, Argument.STRING) };
	}

	@Override
	public Value getAggregateResult() {
		return null;
	}

}

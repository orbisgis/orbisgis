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
package org.gdms.sql.function.alphanumeric;

import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.AbstractScalarFunction;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;

/**
 * Converts a string into an integer.
 * @author Antoine Gourlay
 */
public class String2IntFunction extends AbstractScalarFunction {
        @Override
	public Value evaluate(SQLDataSourceFactory dsf,Value[] args) throws FunctionException {
		if (args[0].getType() == Type.NULL) {
			return ValueFactory.createNullValue();
		}

		try {
			return ValueFactory.createValue(Integer.parseInt(args[0]
					.getAsString()));
		} catch (NumberFormatException e) {
			throw new FunctionException("impossible to convert " + args[0]
					+ " into an int value", e);
		}
	}

        @Override
	public String getName() {
		return "StringToInt";
	}

        @Override
	public Type getType(Type[] types) {
		return TypeFactory.createType(Type.INT);
	}

	@Override
	public String getDescription() {

		return "Convert a string into integer";
	}

        @Override
	public String getSqlOrder() {
		return "select StringToInt(myStringField) from myTable;";
	}

	@Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[] {
                  new BasicFunctionSignature(getType(null), ScalarArgument.STRING)
                };
        }

}
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

import org.apache.log4j.Logger;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.AbstractAggregateFunction;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.function.SameTypeFunctionSignature;
import org.gdms.data.types.IncompatibleTypesException;

/**
 * Computes the maximum value of a set of numeric values.
 * 
 * NULL values are skipped. If there is no rows to test or if there are all NULL, then this returns
 * NULL as well.
 */
public class Max extends AbstractAggregateFunction {

    private static final Logger LOG = Logger.getLogger(Max.class);

	private Value max = ValueFactory.createNullValue();

        @Override
	public void evaluate(DataSourceFactory dsf, Value[] args)
			throws FunctionException {
            LOG.trace("Evaluating");
		if (!args[0].isNull()) {
			try {
				if (max.isNull()) {
					max = args[0];
				} else {
					if (args[0].greater(max).getAsBoolean()) {
						max = args[0];
					}
				}
			} catch (IncompatibleTypesException e) {
				throw new FunctionException(e);
			}
		}
	}

        @Override
	public String getName() {
		return "Max";
	}

        @Override
	public Type getType(Type[] types) {
                FunctionValidator.failIfTypesDoNotMatchSignature(types, getFunctionSignatures());
		return types[0];
	}

	@Override
	public String getDescription() {
		return "Return the maximum value";
	}

        @Override
	public String getSqlOrder() {
		return "select Max(myField) from myTable;";
	}

	@Override
	public Value getAggregateResult() {
            LOG.trace("Returning aggregated result");
		return max;
	}

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[] {
                        new SameTypeFunctionSignature(ScalarArgument.BYTE),
                        new SameTypeFunctionSignature(ScalarArgument.INT),
                        new SameTypeFunctionSignature(ScalarArgument.SHORT),
                        new SameTypeFunctionSignature(ScalarArgument.LONG),
                        new SameTypeFunctionSignature(ScalarArgument.FLOAT),
                        new SameTypeFunctionSignature(ScalarArgument.DOUBLE)
                };
        }

}
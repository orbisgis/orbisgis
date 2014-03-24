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
package org.gdms.sql.function.math;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.AbstractAggregateFunction;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.SameTypeFunctionSignature;
import org.gdms.sql.function.ScalarArgument;

/**
 * Compute the standard deviation of some numeric values.
 */
public final class StandardDeviation extends AbstractAggregateFunction {
	private double sumOfValues = 0;
	private double sumOfSquareValues = 0;
	private int numberOfValues = 0;

        @Override
	public void evaluate(DataSourceFactory dsf, Value... args) throws FunctionException {
		if (!args[0].isNull()) {
			final double currentValue = args[0].getAsDouble();
			sumOfValues += currentValue;
			sumOfSquareValues += currentValue * currentValue;
			numberOfValues++;
		}
	}

        @Override
	public String getName() {
		return "StandardDeviation";
	}

        @Override
        public String getDescription() {
		return "Compute the standard deviation of some numeric values.";
	}

        @Override
	public String getSqlOrder() {
		return "select StandardDeviation(myNumericField) from myTable;";
	}

	@Override
	public Value getAggregateResult() {
		if (0 == numberOfValues) {
			return ValueFactory.createNullValue();
		}
		final double average = sumOfValues / numberOfValues;
		return ValueFactory.createValue(Math.sqrt(sumOfSquareValues
				/ numberOfValues - average * average));
	}

        @Override
        public int getType(int[] argsTypes) {
                return Type.DOUBLE;
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[] {
                  new SameTypeFunctionSignature(ScalarArgument.DOUBLE)
                };
        }

}
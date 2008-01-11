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

package org.gdms.sql.function.alphanumeric;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.function.WarningException;

public class CollectiveAverage implements Function {
	private double[] sumOfValues = null;
	private int numberOfValues = 0;

	public Value evaluate(Value[] args) throws FunctionException,
			WarningException {
		FunctionValidator.warnIfNull(args);

		if (null == sumOfValues) {
			sumOfValues = new double[args.length];
		}
		numberOfValues++;
		final Value[] values = new Value[sumOfValues.length];
		for (int i = 0; i < sumOfValues.length; i++) {
			if (args[i].getType() == Type.STRING) {
				sumOfValues[i] += Double.valueOf(args[i].toString());
			} else {
				sumOfValues[i] += args[i].getAsDouble();
			}
			values[i] = ValueFactory.createValue(sumOfValues[i]
					/ numberOfValues);
		}
		return ValueFactory.createValue(values);
	}

	public String getName() {
		return "CollectiveAvg";
	}

	public boolean isAggregate() {
		return true;
	}

	public Function cloneFunction() {
		return new CollectiveAverage();
	}

	public int getType(int[] types) {
		return Type.COLLECTION;
	}

	public String getDescription() {
		return "Calculate the average value";
	}

	public String getSqlOrder() {
		return "select CollectiveAvg(myNumericField1,...) from myTable;";
	}
}
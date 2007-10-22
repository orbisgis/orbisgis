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
/***********************************
 * <p>Title: CarThema</p>
 * Perspectives Software Solutions
 * Copyright (c) 2006
 * @author Vladimir Peric, Vladimir Cetkovic
 ***********************************/

package org.gdms.sql.function.statistics;

import java.util.ArrayList;
import java.util.List;

import org.gdms.data.types.Type;
import org.gdms.data.values.DoubleValue;
import org.gdms.data.values.FloatValue;
import org.gdms.data.values.IntValue;
import org.gdms.data.values.LongValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

/**
 * @author Vladimir Peric Adapted by Erwan Bocher
 */
public class PercentileFunction implements Function {

	private Value percentile = null;

	private int valueType = 0;

	private List<Double> list = new ArrayList<Double>();

	private double perVal;

	private PercentileCalculator perCalc = new PercentileCalculator();

	/**
	 * @see org.gdms.sql.function.Function#evaluate(org.gdms.data.values.Value[])
	 */
	public Value evaluate(Value[] args) throws FunctionException {

		try {

			if (null == percentile) {
				valueType = args[0].getType();
				double d = 0.0d;
				percentile = ValueFactory.createValue(d);
				int perValueType = args[1].getType();
				switch (perValueType) {
				case Type.LONG:
					perVal = (double) (((LongValue) args[1]).getValue());
					break;
				case Type.INT:
					perVal = (double) (((IntValue) args[1]).getValue());
					break;
				case Type.FLOAT:
					perVal = (double) (((FloatValue) args[1]).getValue());
					break;
				case Type.DOUBLE:
					perVal = (double) (((DoubleValue) args[1]).getValue());
					break;
				}
			}

			switch (valueType) {
			case 3:
				list
						.add(new Double((double) (((LongValue) args[0])
								.getValue())));
				break;
			case 4:
				list
						.add(new Double((double) (((IntValue) args[0])
								.getValue())));
				break;
			case 6:
			case 7:
				list.add(new Double(
						(double) (((FloatValue) args[0]).getValue())));
				break;
			case 8:
				list.add(new Double((double) (((DoubleValue) args[0])
						.getValue())));
				break;
			}

			final double[] doubleArray = new double[list.size()];
			for (int i = 0; i < list.size(); i++) {
				doubleArray[i] = (((Double) (list.get(i))).doubleValue());
			}

			((DoubleValue) percentile).setValue(perCalc.evaluate(doubleArray,
					perVal));
		} catch (Exception e) {
			throw new FunctionException(e);
		}

		return percentile;
	}

	/**
	 * @see org.gdms.sql.function.Function#getName()
	 */
	public String getName() {
		return "percentile";
	}

	/**
	 * @see org.gdms.sql.function.Function#isAggregate()
	 */
	public boolean isAggregate() {
		return true;
	}

	/**
	 * @see org.gdms.sql.function.Function#cloneFunction()
	 */
	public Function cloneFunction() {
		return new PercentileFunction();
	}

	public int getType(int[] types) {
		return types[0];
	}
}
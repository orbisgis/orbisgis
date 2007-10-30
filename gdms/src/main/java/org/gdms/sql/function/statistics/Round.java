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
 * @author Vladimir Peric
 */
public class Round implements Function {

	private Value result = null;

	/**
	 * @see org.gdms.sql.function.Function#evaluate(org.gdms.data.values.Value[])
	 */
	public Value evaluate(Value[] args) throws FunctionException {

		try {
			int valueTypeBase = args[0].getType();
			int valueTypeGrade = args[1].getType();
			double roundR;
			double base = 1.0d;
			int grade = 0;
			switch (valueTypeBase) {
			case Type.LONG:
				base = (double) (((LongValue) args[0]).getValue());
				break;
			case Type.INT:
				base = (double) (((IntValue) args[0]).getValue());
				break;
			case Type.FLOAT:
				base = (double) (((FloatValue) args[0]).getValue());
				break;
			case Type.DOUBLE:
				base = (double) (((DoubleValue) args[0]).getValue());
				break;
			}
			switch (valueTypeGrade) {
			case Type.LONG:
				grade = (int) (((LongValue) args[1]).getValue());
				break;
			case Type.INT:
				grade = (int) (((IntValue) args[1]).getValue());
				break;
			case Type.FLOAT:
				grade = (int) (((FloatValue) args[1]).getValue());
				break;
			case Type.DOUBLE:
				grade = (int) (((DoubleValue) args[1]).getValue());
				break;
			}
			roundR = round(base, grade);
			if (grade > 0) {
				result = ValueFactory.createValue(roundR);
				((DoubleValue) result).setValue(roundR);
			} else {
				long roundInt = Math.round(roundR);
				result = ValueFactory.createValue(roundInt);
				((LongValue) result).setValue(roundInt);
			}
		} catch (Exception e) {
			throw new FunctionException(e);
		}

		return result;
	}

	/**
	 * @see org.gdms.sql.function.Function#getName()
	 */
	public String getName() {
		return "round";
	}

	/**
	 * @see org.gdms.sql.function.Function#isAggregate()
	 */
	public boolean isAggregate() {
		return false;
	}

	/**
	 * @see org.gdms.sql.function.Function#cloneFunction()
	 */
	public Function cloneFunction() {
		return new Round();
	}

	/**
	 * Round a double value to a specified number of decimal places.
	 * 
	 * @param val
	 *            the value to be rounded.
	 * @param places
	 *            the number of decimal places to round to.
	 * @return val rounded to places decimal places.
	 */
	public static double round(double val, int places) {
		long factor = (long) Math.pow(10, places);

		// Shift the decimal the correct number of places
		// to the right.
		val = val * factor;

		// Round to the nearest integer.
		long tmp = Math.round(val);

		// Shift the decimal the correct number of places
		// back to the left.
		return (double) tmp / factor;
	}

	/**
	 * Round a float value to a specified number of decimal places.
	 * 
	 * @param val
	 *            the value to be rounded.
	 * @param places
	 *            the number of decimal places to round to.
	 * @return val rounded to places decimal places.
	 */
	public static float round(float val, int places) {
		return (float) round((double) val, places);
	}

	public int getType(int[] types) {

		return types[0];
	}
	
public String getDescription() {
		
		return "Return the rounded value";
	}

}

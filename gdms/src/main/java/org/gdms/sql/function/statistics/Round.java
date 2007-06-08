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
	 * @see com.hardcode.gdbms.engine.function.Function#evaluate(com.hardcode.gdbms.engine.values.Value[])
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
	 * @see com.hardcode.gdbms.engine.function.Function#getName()
	 */
	public String getName() {
		return "round";
	}

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#isAggregate()
	 */
	public boolean isAggregate() {
		return false;
	}

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#cloneFunction()
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

}

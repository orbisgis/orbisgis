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
public class Abs implements Function {

	private Value result = null;

	/**
	 * @see org.gdms.sql.function.Function#evaluate(org.gdms.data.values.Value[])
	 */
	public Value evaluate(Value[] args) throws FunctionException {

		try {
			int valueType = args[0].getType();
			switch (valueType) {
			case Type.LONG:
				long longValue = (int) (((LongValue) args[0]).getValue());
				if (longValue < 0) {
					longValue = -longValue;
				}
				result = ValueFactory.createValue(longValue);
				((LongValue) result).setValue(longValue);
				break;
			case Type.INT:
				int intValue = (int) (((IntValue) args[0]).getValue());
				if (intValue < 0) {
					intValue = -intValue;
				}
				result = ValueFactory.createValue(intValue);
				((IntValue) result).setValue(intValue);
				break;
			case Type.FLOAT:
				float floatValue = (float) (((FloatValue) args[0]).getValue());
				if (floatValue < 0) {
					floatValue = -floatValue;
				}
				result = ValueFactory.createValue(floatValue);
				((FloatValue) result).setValue(floatValue);
				break;
			case Type.DOUBLE:
				double doubleValue = (double) (((DoubleValue) args[0])
						.getValue());
				if (doubleValue < 0) {
					doubleValue = -doubleValue;
				}
				result = ValueFactory.createValue(doubleValue);
				((DoubleValue) result).setValue(doubleValue);
				break;
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
		return "abs";
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
		return new Abs();
	}

	public int getType(int[] types) {

		return types[0];
	}

}

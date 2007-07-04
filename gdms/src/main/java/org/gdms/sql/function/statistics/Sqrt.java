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
public class Sqrt implements Function {

	private Value result = null;

	/**
	 * @see org.gdms.sql.function.Function#evaluate(org.gdms.data.values.Value[])
	 */
	public Value evaluate(Value[] args) throws FunctionException {

		try {
			int valueType = args[0].getType();
			double sqrt;
			switch (valueType) {
			case Type.LONG:
				long longValue = (long) (((LongValue) args[0]).getValue());
				if (longValue > 0) {
					sqrt = Math.sqrt((double) longValue);
				} else {
					sqrt = (double) longValue;
				}
				result = ValueFactory.createValue(sqrt);
				((DoubleValue) result).setValue(sqrt);
				break;
			case Type.INT:
				int intValue = (int) (((IntValue) args[0]).getValue());
				if (intValue > 0) {
					sqrt = Math.sqrt((double) intValue);
				} else {
					sqrt = (double) intValue;
				}
				result = ValueFactory.createValue(sqrt);
				((DoubleValue) result).setValue(sqrt);
				break;
			case Type.FLOAT:
				float floatValue = (float) (((FloatValue) args[0]).getValue());
				if (floatValue > 0) {
					sqrt = Math.sqrt((double) floatValue);
				} else {
					sqrt = (double) floatValue;
				}
				result = ValueFactory.createValue(sqrt);
				((DoubleValue) result).setValue(sqrt);
				break;
			case Type.DOUBLE:
				double doubleValue = (double) (((DoubleValue) args[0])
						.getValue());
				if (doubleValue > 0) {
					sqrt = Math.sqrt(doubleValue);
				} else {
					sqrt = doubleValue;
				}
				result = ValueFactory.createValue(sqrt);
				((DoubleValue) result).setValue(sqrt);
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
		return "sqrt";
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
		return new Sqrt();
	}

	public int getType(int[] types) {

		return types[0];
	}

}

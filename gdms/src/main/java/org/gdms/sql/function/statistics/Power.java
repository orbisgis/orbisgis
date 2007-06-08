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
public class Power implements Function {

	private Value result = null;

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#evaluate(com.hardcode.gdbms.engine.values.Value[])
	 */
	public Value evaluate(Value[] args) throws FunctionException {

		try {
			int valueTypeBase = args[0].getType();
			int valueTypeGrade = args[1].getType();
			double power;
			double base = 1.0d;
			double grade = 1.0d;
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
				grade = (double) (((LongValue) args[1]).getValue());
				break;
			case Type.INT:
				grade = (double) (((IntValue) args[1]).getValue());
				break;
			case Type.FLOAT:
				grade = (double) (((FloatValue) args[1]).getValue());
				break;
			case Type.DOUBLE:
				grade = (double) (((DoubleValue) args[1]).getValue());
				break;
			}
			power = Math.pow(base, grade);
			result = ValueFactory.createValue(power);
			((DoubleValue) result).setValue(power);
		} catch (Exception e) {
			throw new FunctionException(e);
		}

		return result;
	}

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#getName()
	 */
	public String getName() {
		return "power";
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
		return new Power();
	}

	public int getType(int[] types) {

		return types[0];
	}

}

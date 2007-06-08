/***********************************
 * <p>Title: CarThema</p>
 * Perspectives Software Solutions
 * Copyright (c) 2006
 * @author Vladimir Peric, Vladimir Cetkovic
 ***********************************/

package org.gdms.sql.function.alphanumeric;

import org.gdms.data.types.Type;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.instruction.IncompatibleTypesException;

/**
 * @author Vladimir Peric
 */
public class Min implements Function {

	private Value min = null;

	private Value smaller = ValueFactory.createValue(false);

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#evaluate(com.hardcode.gdbms.engine.values.Value[])
	 */
	public Value evaluate(Value[] args) throws FunctionException {
		try {
			if (null == min) {
				int t = args[0].getType();
				switch (t) {
				case Type.LONG:
					long l = Long.MAX_VALUE;
					min = ValueFactory.createValue(l);
					break;
				case Type.INT:
					int n = Integer.MAX_VALUE;
					min = ValueFactory.createValue(n);
					break;
				case Type.FLOAT:
					float f = Float.MAX_VALUE;
					min = ValueFactory.createValue(f);
					break;
				case Type.DOUBLE:
					double d = Double.MAX_VALUE;
					min = ValueFactory.createValue(d);
					break;
				}
			}

			smaller = args[0].less(min);
			if (((BooleanValue) smaller).getValue()) {
				min = args[0];
			}
		} catch (IncompatibleTypesException e) {
			throw new FunctionException(e);
		}

		return min;
	}

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#getName()
	 */
	public String getName() {
		return "min";
	}

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#isAggregate()
	 */
	public boolean isAggregate() {
		return true;
	}

	/**
	 * @see com.hardcode.gdbms.engine.function.Function#cloneFunction()
	 */
	public Function cloneFunction() {
		return new Min();
	}

	public int getType(int[] types) {

		return types[0];
	}

}

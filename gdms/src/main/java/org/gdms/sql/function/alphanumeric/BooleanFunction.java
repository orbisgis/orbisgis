package org.gdms.sql.function.alphanumeric;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class BooleanFunction implements Function {
	/**
	 * @see org.gdms.sql.function.Function#evaluate(org.gdms.data.values.Value[])
	 */
	public Value evaluate(Value[] args) throws FunctionException {
		if (args.length != 1) {
			throw new FunctionException("Use: boolean(true|false)");
		}

		return ValueFactory.createValue(Boolean.valueOf(args[0].toString())
				.booleanValue());
	}

	/**
	 * @see org.gdms.sql.function.Function#getName()
	 */
	public String getName() {
		return "boolean";
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
		return new BooleanFunction();
	}

	/**
	 * @see org.gdms.sql.function.Function#getType()
	 */
	public int getType(int[] types) {

		return types[0];
	}
}

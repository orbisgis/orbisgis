package org.gdms.sql.function.alphanumeric;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.instruction.IncompatibleTypesException;

/**
 * @author Fernando Gonzalez Cortes
 */
public class Sum implements Function {

	private Value acum = ValueFactory.createValue(0);

	/**
	 * @see org.gdms.sql.function.Function#evaluate(org.gdms.data.values.Value[])
	 */
	public Value evaluate(Value[] args) throws FunctionException {
		try {
			acum = acum.suma(args[0]);
		} catch (IncompatibleTypesException e) {
			throw new FunctionException(e);
		}

		return acum;
	}

	/**
	 * @see org.gdms.sql.function.Function#getName()
	 */
	public String getName() {
		return "sum";
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
		return new Sum();
	}

	/**
	 * @see org.gdms.sql.function.Function#getType()
	 */
	public int getType(int[] types) {

		return types[0];
	}

}

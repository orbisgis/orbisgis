package org.gdms.sql.function.alphanumeric;

import org.gdms.data.types.Type;
import org.gdms.data.values.StringValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.instruction.SemanticException;
import org.gdms.sql.parser.SQLEngineConstants;

public class IntFunction implements Function {
	public Value evaluate(Value[] args) throws FunctionException {
		if (args.length != 1) {
			throw new FunctionException("int takes only one argument");
		}

		try {
			return ValueFactory.createValue(args[0].toString(),
					SQLEngineConstants.INTEGER_LITERAL);
		} catch (SemanticException e) {
			throw new FunctionException("impossible to convert " + args[0]
					+ " into an int value");
		}
	}

	public String getName() {
		return "int";
	}

	public boolean isAggregate() {
		return false;
	}

	public Function cloneFunction() {
		return new IntFunction();
	}

	/**
	 * @see org.gdms.sql.function.Function#getType()
	 */
	public int getType(int[] types) {

		return Type.INT;
	}
}
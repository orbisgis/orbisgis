package org.gdms.sql.function.alphanumeric;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

public class ToStringFunction implements Function {

	public Value evaluate(Value[] args) throws FunctionException {
		if (args[0].getType() == Type.NULL) {
			return ValueFactory.createNullValue();
		}

		return ValueFactory.createValue(args[0].toString());
	}

	public String getName() {
		return "toString";
	}

	public boolean isAggregate() {
		return false;
	}

	/**
	 * @see org.gdms.sql.function.Function#getType()
	 */
	public Type getType(Type[] types) {
		return TypeFactory.createType(Type.STRING);
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.ANY) };
	}

	public String getDescription() {
		return "Get the textual representation of the value";
	}

	public String getSqlOrder() {
		return "select toString(myField) from myTable;";
	}

}

package org.gdms.sql;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class ZeroArgsFunction implements Function {

	public Value evaluate(Value[] args) throws FunctionException {
		return ValueFactory.createNullValue();
	}

	public String getDescription() {
		return null;
	}

	public String getName() {
		return "zeroargs";
	}

	public String getSqlOrder() {
		return null;
	}

	public Type getType(Type[] argsTypes) {
		return TypeFactory.createType(Type.INT);
	}

	public boolean isAggregate() {
		return false;
	}

	public void validateTypes(Type[] argumentsTypes)
			throws IncompatibleTypesException {
		if (argumentsTypes.length > 0) {
			throw new IncompatibleTypesException(
					"Zeroargs function takes no arguments");
		}
	}

}

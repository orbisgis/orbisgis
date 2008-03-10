/**
 *
 */
package org.gdms.sql;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class FailingFunction implements Function {

	public Value evaluate(Value[] args) throws FunctionException {
		throw new RuntimeException();
	}

	public String getDescription() {
		return null;
	}

	public String getName() {
		return "failing";
	}

	public String getSqlOrder() {
		return null;
	}

	public Type getType(Type[] argsTypes) {
		return TypeFactory.createType(Type.BOOLEAN);
	}

	public boolean isAggregate() {
		return false;
	}

	public void validateTypes(Type[] argumentsTypes)
			throws IncompatibleTypesException {

	}

}
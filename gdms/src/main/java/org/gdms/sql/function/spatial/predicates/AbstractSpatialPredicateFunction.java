package org.gdms.sql.function.spatial.predicates;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;

public abstract class AbstractSpatialPredicateFunction implements Function {
	final public Value evaluate(Value[] args) throws FunctionException {
		if ((args[0].isNull()) || (args[1].isNull())) {
			return ValueFactory.createNullValue();
		} else {
			return evaluateResult(args);
		}
	}

	protected abstract Value evaluateResult(Value[] args)
			throws FunctionException;

	public Type getType(Type[] types) {
		return TypeFactory.createType(Type.BOOLEAN);
	}

	public void validateTypes(Type[] argumentsTypes)
			throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, argumentsTypes, 2);
		FunctionValidator.failIfNotOfType(this, argumentsTypes[0],
				Type.GEOMETRY);
		FunctionValidator.failIfNotOfType(this, argumentsTypes[1],
				Type.GEOMETRY);
	}

}

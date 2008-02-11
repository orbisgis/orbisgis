package org.gdms.sql.function.spatial.geometryProperties;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;

public abstract class AbstractSpatialPropertyFunction implements Function {

	public void validateTypes(Type[] argumentsTypes)
			throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, argumentsTypes, 1);
		FunctionValidator.failIfNotOfType(this, argumentsTypes[0],
				Type.GEOMETRY);
	}

	public Value evaluate(final Value[] args) throws FunctionException {
		if (args[0].isNull()) {
			return ValueFactory.createNullValue();
		} else {
			return evaluateResult(args);
		}
	}

	protected abstract Value evaluateResult(Value[] args)
			throws FunctionException;

}

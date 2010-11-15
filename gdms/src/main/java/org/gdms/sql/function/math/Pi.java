package org.gdms.sql.function.math;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

public class Pi implements Function {

	@Override
	public Value evaluate(DataSourceFactory dsf,Value... args) throws FunctionException {
		return ValueFactory.createValue(Math.PI);
	}

	@Override
	public Value getAggregateResult() {
		return null;
	}

	@Override
	public String getDescription() {
		return "Compute the Pi number.";
	}

	@Override
	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments() };
	}

	@Override
	public String getName() {
		return "Pi";
	}

	@Override
	public String getSqlOrder() {
		return "select Pi() ;";
	}

	@Override
	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.DOUBLE);
	}

	@Override
	public boolean isAggregate() {
		return false;
	}

}

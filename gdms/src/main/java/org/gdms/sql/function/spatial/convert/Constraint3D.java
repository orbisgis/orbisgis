package org.gdms.sql.function.spatial.convert;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.DimensionConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class Constraint3D implements Function {

	public Value evaluate(Value[] args) throws FunctionException {
		return args[0];
	}

	public String getDescription() {
		return "Changes the metadata of the parameter by setting its dimension to 3D.";
	}

	public String getName() {
		return "Constraint3D";
	}

	public String getSqlOrder() {
		return "select Constraint3D(the_geom) from myTable";
	}

	public boolean isAggregate() {
		return false;
	}

	public void validateTypes(Type[] argumentsTypes)
			throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, argumentsTypes, 1);
		FunctionValidator.failIfNotOfType(this, argumentsTypes[0],
				Type.GEOMETRY);
	}

	public Type getType(Type[] argsTypes) {
		Type type = argsTypes[0];
		Constraint[] constrs = type.getConstraints(Constraint.ALL
				& ~Constraint.GEOMETRY_DIMENSION);
		Constraint[] result = new Constraint[constrs.length];
		System.arraycopy(constrs, 0, result, 0, constrs.length);
		result[result.length - 1] = new DimensionConstraint(3);

		return TypeFactory.createType(type.getTypeCode(), result);
	}

}

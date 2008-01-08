package org.gdms.sql.function;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;

import com.vividsolutions.jts.geom.Geometry;

public class FunctionValidator {

	public static void failIfNull(Value... values) throws FunctionException {
		for (Value value : values) {
			if (value.getType() == Type.NULL) {
				throw new FunctionException("Cannot operate in null values");
			}
		}
	}

	public static void warnIfNull(Value... values) throws WarningException {
		for (Value value : values) {
			if (value.getType() == Type.NULL) {
				throw new WarningException("Cannot operate in null values");
			}
		}
	}

	public static void warnIfGeometryNotValid(Value... values)
			throws WarningException {
		for (Value value : values) {
			Geometry geom = value.getAsGeometry();
			if (!geom.isValid()) {
				throw new WarningException(geom.toText()
						+ " is not a valid geometry");
			}
		}
	}

	public static void failIfBadNumberOfArguments(Function function,
			Value[] args, int i) throws FunctionException {
		if (args.length != i) {
			throw new FunctionException("The function " + function.getName()
					+ " has a wrong number of arguments. " + i + " expected");
		}
	}

	public static void warnIfNotOfType(Value value, int type)
			throws WarningException {
		if (type != value.getType()) {
			throw new WarningException(value.toString() + " is not of type "
					+ type);
		}
	}

	public static void failIfNotOfType(Value value, int type)
			throws FunctionException {
		if (type != value.getType()) {
			throw new FunctionException(value.toString() + " is not of type "
					+ type);
		}
	}
}
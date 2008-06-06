package org.gdms.sql.function.spatial.raster.hydrology;

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public class FillSinks implements Function {
	public Value evaluate(Value[] args) throws FunctionException {
		final GeoRaster geoRasterSrc = args[0].getAsRaster();
		
		final double slope = args[1].getAsDouble();
		
		final Operation slopesDirections = new org.grap.processing.operation.hydrology.FillSinks(slope);
		try {
			return ValueFactory.createValue(geoRasterSrc
					.doOperation(slopesDirections));
		} catch (OperationException e) {
			throw new FunctionException("Cannot do the operation", e);
		}
	}

	public String getDescription() {
		return "Depression filling algorithm.  Method from Olivier Planchon & Frederic Darboux (2001)";
	}

	public String getName() {
		return "FillSinks";
	}

	public String getSqlOrder() {
		return "select FillSinks(raster, slopeValue) as raster from mydem;";
	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.RASTER);
	}

	public boolean isAggregate() {
		return false;
	}

	public void validateTypes(Type[] argumentsTypes)
			throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, argumentsTypes, 2);
		FunctionValidator.failIfNotOfType(this, argumentsTypes[0], Type.RASTER);
		FunctionValidator.failIfNotOfType(this, argumentsTypes[1], Type.DOUBLE);
	}
}
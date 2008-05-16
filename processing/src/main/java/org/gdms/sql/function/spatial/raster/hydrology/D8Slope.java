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
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.hydrology.D8OpSlope;

public class D8Slope implements Function {
	public Value evaluate(Value[] args) throws FunctionException {
		final GeoRaster geoRasterSrc = args[0].getAsRaster();
		final Operation d8OpSlope = new D8OpSlope();
		try {
			return ValueFactory.createValue(geoRasterSrc
					.doOperation(d8OpSlope));
		} catch (OperationException e) {
			throw new FunctionException("Cannot do the operation", e);
		} catch (GeoreferencingException e) {
			throw new FunctionException("Cannot do the operation", e);
		}
	}

	public String getDescription() {
		return "Compute the slopes (in percent) using a GRAY16/32 DEM as input table";
	}

	public String getName() {
		return "D8Slope";
	}

	public String getSqlOrder() {
		return "select D8Slope(raster) as raster from mydem;";
	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.RASTER);
	}

	public boolean isAggregate() {
		return false;
	}

	public void validateTypes(Type[] argumentsTypes)
			throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, argumentsTypes, 1);
		FunctionValidator.failIfNotOfType(this, argumentsTypes[0], Type.RASTER);
	}
}
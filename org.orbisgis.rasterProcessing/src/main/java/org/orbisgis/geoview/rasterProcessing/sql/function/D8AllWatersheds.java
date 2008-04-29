package org.orbisgis.geoview.rasterProcessing.sql.function;

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
import org.grap.processing.operation.hydrology.AllWatersheds;

public class D8AllWatersheds implements Function {
	public Value evaluate(Value[] args) throws FunctionException {
		final GeoRaster geoRasterSrc = args[0].getAsRaster();
		final Operation allWatersheds = new AllWatersheds();
		try {
			return ValueFactory.createValue(geoRasterSrc
					.doOperation(allWatersheds));
		} catch (OperationException e) {
			throw new FunctionException("Cannot do the operation", e);
		} catch (GeoreferencingException e) {
			throw new FunctionException("Cannot do the operation", e);
		}
	}

	public String getDescription() {
		return "Compute all the watersheds using a GRAY16/32 DEM slopes directions as input table";
	}

	public String getName() {
		return "D8AllWatersheds";
	}

	public String getSqlOrder() {
		return "select D8AllWatersheds(raster) from mydirections;";
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
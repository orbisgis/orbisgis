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
import org.grap.processing.operation.hydrology.GridAccumulation;
import org.grap.processing.operation.hydrology.StrahlerStreamOrder;

public class D8StrahlerStreamOrder implements Function {
	public Value evaluate(Value[] args) throws FunctionException {
		final GeoRaster d8GridDirection = args[0].getAsRaster();
		final GeoRaster d8GridAccumulation = args[1].getAsRaster();
		final int riverThresholdValue = args[2].getAsInt();
		try {
			
			final Operation strahlerStreamOrder= new StrahlerStreamOrder(d8GridAccumulation, riverThresholdValue);
			
			
			return ValueFactory.createValue(d8GridDirection
					.doOperation(strahlerStreamOrder));
		
		} catch (OperationException e) {
			throw new FunctionException("Cannot calculate the StrahlerStreamOrder grid ", e);
		} catch (GeoreferencingException e) {
			throw new FunctionException("Cannot calculate the StrahlerStreamOrder grid", e);
		}
	}

	public String getDescription() {
		return "Compute the Strahler Stream Order using a GRAY16/32 D8 grid direction and D8 grid accumulations."
				+ "The RiverThreshold is an integer value that corresponds to the minimal value of "
				+ "accumulation for a cell to be seens as a 1st level river.";
	}

	public String getName() {
		return "D8StrahlerStreamOrder";
	}

	public String getSqlOrder() {
		return "select D8StrahlerStreamOrder(r1, r2, riverthresholdValue) from d8GridDirection as r1, d8GridAccumulation as r2;";
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
		FunctionValidator.failIfNotOfType(this, argumentsTypes[1], Type.RASTER);
		FunctionValidator.failIfNotOfType(this, argumentsTypes[2], Type.INT);
	}
}
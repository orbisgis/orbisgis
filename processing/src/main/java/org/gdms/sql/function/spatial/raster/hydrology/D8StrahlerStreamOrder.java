package org.gdms.sql.function.spatial.raster.hydrology;

import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.hydrology.D8OpStrahlerStreamOrder;

public class D8StrahlerStreamOrder implements Function {
	public Value evaluate(Value[] args) throws FunctionException {
		int riverThreshold = args[0].getAsInt();
		GeoRaster grD8Direction = args[1].getAsRaster();
		GeoRaster grD8Accumulation = args[2].getAsRaster();

		try {
			final Operation opeStrahlerStreamOrder = new D8OpStrahlerStreamOrder(
					grD8Accumulation, riverThreshold);
			return ValueFactory.createValue(grD8Direction
					.doOperation(opeStrahlerStreamOrder));
		} catch (OperationException e) {
			throw new FunctionException("Cannot do the operation", e);
		}
	}

	public String getDescription() {
		return "Compute the Strahler Stream Order using a GRAY16/32 DEM slopes accumulations as input table."
				+ "The RiverThreshold is an integer value that corresponds to the minimal value of "
				+ "accumulation for a cell to be seen as a 1st level river.";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.RASTER) }, new String[] { "raster" });
	}

	public String getName() {
		return "D8StrahlerStreamOrder";
	}

	public String getSqlOrder() {
		return "select D8StrahlerStreamOrder(RiverThreshold, d.raster, a.raster) from directions d, accumulations a;";
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 3);
		FunctionValidator.failIfNotNumeric(this, types[0]);
		FunctionValidator.failIfNotOfType(this, types[1], Type.RASTER);
		FunctionValidator.failIfNotOfType(this, types[2], Type.RASTER);
	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.RASTER);
	}

	public boolean isAggregate() {
		return false;
	}
}
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
import org.grap.processing.OperationException;
import org.grap.processing.operation.hydrology.D8OpWatershedsWithThreshold;

public class D8ThresholdedWatershed implements Function {
	public Value evaluate(Value[] args) throws FunctionException {
		int watershedThreshold = args[0].getAsInt();
		GeoRaster grD8Accumulation = args[1].getAsRaster();
		GeoRaster grD8AllWatersheds = args[2].getAsRaster();
		GeoRaster grD8AllOutlets = args[3].getAsRaster();

		try {
			// extract some "big" watersheds
			D8OpWatershedsWithThreshold d8OpWatershedsWithThreshold = new D8OpWatershedsWithThreshold(
					grD8AllWatersheds, grD8AllOutlets, watershedThreshold);
			return ValueFactory.createValue(grD8Accumulation
					.doOperation(d8OpWatershedsWithThreshold));
		} catch (OperationException e) {
			throw new FunctionException("Cannot do the operation", e);
		}
	}

	public String getDescription() {
		return "Compute all the \"big\" watersheds (that is to say all those whose outlet accumulate "
				+ "more than the WatershedThreshold integer value";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.RASTER) }, new String[] { "raster" });
	}

	public String getName() {
		return "D8ThresholdedWatershed";
	}

	public String getSqlOrder() {
		return "select D8ThresholdedWatershed(WatershedThreshold, a.raster, w.raster, o.raster) from accumulations a, allwatersheds w, alloutlets o;";
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 4);
		FunctionValidator.failIfNotNumeric(this, types[0]);
		FunctionValidator.failIfNotOfType(this, types[1], Type.RASTER);
		FunctionValidator.failIfNotOfType(this, types[2], Type.RASTER);
		FunctionValidator.failIfNotOfType(this, types[3], Type.RASTER);
	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.RASTER);
	}

	public boolean isAggregate() {
		return false;
	}
}
package org.gdms.sql.function.spatial.raster.morphology;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.grap.processing.operation.GeoRasterResample;

public class ST_Resample implements Function {

	@Override
	public Value evaluate(DataSourceFactory dsf,Value... args) throws FunctionException {

		GeoRaster geoRasterSrc = args[0].getAsRaster();
		float size = args[1].getAsFloat();

		GeoRaster result;
		try {
			result = geoRasterSrc.doOperation(new GeoRasterResample(size));
			return ValueFactory.createValue(result);
		} catch (OperationException e) {
			throw new FunctionException("Cannot resample the raster", e);
		}
	}

	@Override
	public Value getAggregateResult() {
		return null;
	}

	@Override
	public String getDescription() {
		return "Return a new raster resampled according to a size in float.";
	}

	@Override
	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.RASTER, Argument.FLOAT) };
	}

	@Override
	public String getName() {
		return "ST_Resample";
	}

	@Override
	public String getSqlOrder() {
		return "Select ST_Resample(raster, float ) from myRaster";
	}

	@Override
	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.RASTER);
	}

	@Override
	public boolean isAggregate() {
		return false;
	}

}

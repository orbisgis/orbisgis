package org.gdms.sql.function.spatial.raster.properties;

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
import org.grap.model.RasterMetadata;

public class ST_Count implements Function {

	@Override
	public Value evaluate(DataSourceFactory dsf,Value... args) throws FunctionException {
		RasterMetadata metadata = args[0].getAsRaster().getMetadata();
		return ValueFactory.createValue(metadata.getNCols()
				* metadata.getNRows());
	}

	@Override
	public Value getAggregateResult() {
		return null;
	}

	@Override
	public String getDescription() {
		return "Return the pixels count";
	}

	@Override
	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.RASTER) };
	}

	@Override
	public String getName() {
		return "ST_COUNT";
	}

	@Override
	public String getSqlOrder() {
		return "ST_COUNT(raster)";
	}

	@Override
	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.INT);
	}

	@Override
	public boolean isAggregate() {
		return false;
	}

}

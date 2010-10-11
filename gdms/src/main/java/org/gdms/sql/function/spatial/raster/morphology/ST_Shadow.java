package org.gdms.sql.function.spatial.raster.morphology;

import java.util.HashMap;
import java.util.Map;

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
import org.grap.processing.operation.others.Orientations;
import org.grap.processing.operation.others.Shadows;

public class ST_Shadow implements Function {

	public final static Map<String, Orientations> orientations = new HashMap<String, Orientations>();
	static {
		orientations.put("7", Orientations.NORTH);
		orientations.put("8", Orientations.NORTHEAST);
		orientations.put("1", Orientations.EAST);
		orientations.put("2", Orientations.SOUTHEAST);
		orientations.put("3", Orientations.SOUTH);
		orientations.put("4", Orientations.SOUTHWEST);
		orientations.put("5", Orientations.WEST);
		orientations.put("6", Orientations.NORTHWEST);
	}

	@Override
	public Value evaluate(DataSourceFactory dsf,Value... args) throws FunctionException {

		GeoRaster geoRasterSrc = args[0].getAsRaster();
		int orientationInt = args[1].getAsInt();
		final Orientations orientation = orientations.get(orientationInt);
		if (null != orientation) {
			GeoRaster result;
			try {
				result = geoRasterSrc.doOperation(new Shadows(orientation));
				return ValueFactory.createValue(result);
			} catch (OperationException e) {
				throw new FunctionException("Cannot compute the shadow", e);
			}
		}

		return ValueFactory.createNullValue();
	}

	@Override
	public Value getAggregateResult() {
		return null;
	}

	@Override
	public String getDescription() {
		return "Return a raster shadow according to an azimut. 1 = EAST, 3 = SOUTH ... 8 = NORTH EAST";
	}

	@Override
	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.RASTER, Argument.INT) };
	}

	@Override
	public String getName() {
		return "ST_Shadow()";
	}

	@Override
	public String getSqlOrder() {
		return "Select ST_Shadow(raster, int ) from myRaster";
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

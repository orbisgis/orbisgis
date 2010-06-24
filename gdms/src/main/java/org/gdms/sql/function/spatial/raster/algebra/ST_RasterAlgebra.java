package org.gdms.sql.function.spatial.raster.algebra;

import java.util.Map;

import org.gdms.data.values.RasterValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.raster.AbstractRasterFunction;
import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.grap.processing.operation.GeoRasterCalculator;
import org.grap.processing.operation.GeoRasterMath;

/**
 * So nice raster algebra functions. 
 */

public class ST_RasterAlgebra extends AbstractRasterFunction {

	@Override
	public Value evaluate(Value... args) throws FunctionException {
		final GeoRaster raster1 = args[0].getAsRaster();
		Value value2 = args[1];
		if (value2 instanceof RasterValue) {
			final GeoRaster raster2 = value2.getAsRaster();
			String method = args[2].getAsString();
			try {
				Map<String, Integer> methods = GeoRasterCalculator.operators;
				if (methods.containsKey(method.toLowerCase())) {
					final GeoRaster grResult = raster1
							.doOperation(new GeoRasterCalculator(raster2,
									methods.get(method)));
					return ValueFactory.createValue(grResult);
				}
			} catch (OperationException e) {
				throw new FunctionException("Cannot do the operation", e);
			}
		} else {
			String method = value2.getAsString();
			double value = args[2].getAsDouble();
			try {
				Map<String, Integer> methods = GeoRasterMath.operators;
				if (methods.containsKey(method.toLowerCase())) {
					final GeoRaster grResult = raster1
							.doOperation(new GeoRasterMath(value, methods
									.get(method)));
					return ValueFactory.createValue(grResult);
				}
			} catch (OperationException e) {
				throw new FunctionException("Cannot do the operation", e);
			}
		}

		return ValueFactory.createNullValue();
	}

	@Override
	public String getDescription() {
		return "A function to divide, multiple, substract raster.";
	}

	@Override
	public Arguments[] getFunctionArguments() {
		return new Arguments[] {
				new Arguments(Argument.RASTER, Argument.RASTER, Argument.STRING),
				new Arguments(Argument.RASTER, Argument.STRING,
						Argument.NUMERIC) };
	}

	@Override
	public String getName() {
		return "ST_RasterAlgebra";
	}

	@Override
	public String getSqlOrder() {
		return "Select ST_RasterAlgebra(raster1, raster2, 'method') from table;";
	}

}

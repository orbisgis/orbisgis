package org.gdms.sql.function.spatial.geometry.properties;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;

public class GetY extends AbstractSpatialPropertyFunction implements Function {

	public Value evaluateResult(final Value[] args) throws FunctionException {
		final Geometry geometry = args[0].getAsGeometry();
		double y = Double.NaN;
		if (args.length == 1) {
			y = geometry.getCoordinate().y;
		} else {
			y = geometry.getCoordinates()[args[1].getAsInt()].y;
		}
		if (Double.isNaN(y)) {
			return ValueFactory.createNullValue();
		} else {
			return ValueFactory.createValue(y);
		}
	}

	public String getName() {
		return "GetY";
	}

	public Type getType(Type[] types) {
		return TypeFactory.createType(Type.DOUBLE);
	}

	@Override
	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY),
				new Arguments(Argument.GEOMETRY, Argument.INT) };
	}

	public boolean isAggregate() {
		return false;
	}

	public String getDescription() {
		return "Return the Y coordinate for a geometry.";
	}

	public String getSqlOrder() {
		return "select GetY(the_geom, [index]) from myTable;";
	}

}

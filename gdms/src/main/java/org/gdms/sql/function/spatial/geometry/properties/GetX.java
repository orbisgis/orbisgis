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

public class GetX extends AbstractSpatialPropertyFunction implements Function {

	public Value evaluateResult(final Value[] args) throws FunctionException {
		final Geometry geometry = args[0].getAsGeometry();
		double x = Double.NaN;
		if (args.length == 1) {
			x = geometry.getCoordinate().x;
		} else {
			x = geometry.getCoordinates()[args[1].getAsInt()].x;
		}
		if (Double.isNaN(x)) {
			return ValueFactory.createNullValue();
		} else {
			return ValueFactory.createValue(x);
		}
	}

	public String getName() {
		return "GetX";
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
		return "Return the X coordinate for a geometry.";
	}

	public String getSqlOrder() {
		return "select GetX(the_geom, [index]) from myTable;";
	}

}

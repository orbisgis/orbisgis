package org.gdms.sql.function.spatial.geometry.properties;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class ST_Y extends AbstractSpatialPropertyFunction implements Function {

	public Value evaluateResult(DataSourceFactory dsf, Value[] args) throws FunctionException {
		Geometry value = args[0].getAsGeometry();
		if (value instanceof Point) {
			final Point geometry = (Point) args[0].getAsGeometry();
			double y = geometry.getCoordinate().y;
			return ValueFactory.createValue(y);

		} else {
			return ValueFactory.createNullValue();
		}

	}

	public String getName() {
		return "ST_Y";
	}

	public Type getType(Type[] types) {
		return TypeFactory.createType(Type.DOUBLE);
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY) };
	}

	public boolean isAggregate() {
		return false;
	}

	public String getDescription() {
		return "Return the Y coordinate of the point, or NULL if not available. Input must be a point.";
	}

	public String getSqlOrder() {
		return "select ST_Y(the_geom) from myTable;";
	}

}

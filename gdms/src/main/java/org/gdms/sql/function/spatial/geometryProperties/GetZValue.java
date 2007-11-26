package org.gdms.sql.function.spatial.geometryProperties;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class GetZValue implements Function {
	public Function cloneFunction() {
		return new GetZValue();
	}

	public Value evaluate(final Value[] args) throws FunctionException {
		final Geometry geometry = ((GeometryValue) args[0]).getGeom();
		if (geometry instanceof Point) {
			return ValueFactory.createValue(geometry.getCoordinate().z);
		} else {
			throw new FunctionException("Only operates with point");
		}
	}

	public String getName() {
		return "GetZ";
	}

	public int getType(final int[] types) {
		// return types[0];
		return Type.DOUBLE;
	}

	public boolean isAggregate() {
		return false;
	}

	public String getDescription() {
		return "Return the z value for a point geometry.";
	}

	public String getSqlOrder() {
		return "select GetZ(the_geom) from myTable;";
	}
}
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
import com.vividsolutions.jts.geom.Point;

public class ST_CENTROID extends AbstractSpatialPropertyFunction implements
		Function {

	public Value evaluateResult(final Value[] args) throws FunctionException {
		final Geometry geometry = args[0].getAsGeometry();
		Point pt = geometry.getCentroid();
		if (pt == null) {
			return ValueFactory.createNullValue();
		} else {
			return ValueFactory.createValue(pt);
		}

	}

	public String getName() {
		return "ST_CENTROID";
	}

	public Type getType(Type[] types) {
		return TypeFactory.createType(Type.GEOMETRY);
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY) };
	}

	public boolean isAggregate() {
		return false;
	}

	public String getDescription() {
		return "Computes the geometric center of a geometry, or equivalently, the center of mass of the geometry as a POINT.";
	}

	public String getSqlOrder() {
		return "select ST_CENTROID(the_geom) from myTable;";
	}

}

package org.gdms.sql.function.spatial.convert;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;

// select id,AsWKT(the_geom),AsWKT(ToMultiPoint(the_geom)) from points;

public class ToMultiPoint implements Function {
	public String getName() {
		return "ToMultiPoint";
	}

	public Function cloneFunction() {
		return new ToMultiPoint();
	}

	public Value evaluate(final Value[] args) throws FunctionException {
		final Geometry geom = args[0].getAsGeometry();
		final MultiPoint multiPoint = new GeometryFactory()
				.createMultiPoint(geom.getCoordinates());
		return ValueFactory.createValue(multiPoint);
	}

	public String getDescription() {
		return "Convert any GDMS default spatial field into a MultiPoint";
	}

	public int getType(final int[] paramTypes) {
		return paramTypes[0];
	}

	public boolean isAggregate() {
		return false;
	}

	public String getSqlOrder() {
		return "select ToMultiPoint(the_geom) from myTable;";
	}
}
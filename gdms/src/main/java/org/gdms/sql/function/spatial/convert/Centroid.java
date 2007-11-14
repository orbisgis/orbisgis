package org.gdms.sql.function.spatial.convert;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;

public class Centroid implements Function {
	public Function cloneFunction() {
		return new Centroid();
	}

	public Value evaluate(final Value[] args) throws FunctionException {
		final Geometry geom = ((GeometryValue) args[0]).getGeom();
		return ValueFactory.createValue(geom.getCentroid());
	}

	public String getName() {
		return "Centroid";
	}

	public int getType(final int[] types) {
		// return Type.GEOMETRY;
		return types[0];
	}

	public boolean isAggregate() {
		return false;
	}

	public String getDescription() {
		return "Compute the geometry centroid. The result is a point.";
	}
}
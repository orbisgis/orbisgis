package org.gdms.sql.function.spatial.operators;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class GeomUnion implements Function {
	private GeometryValue result = (GeometryValue) ValueFactory
			.createValue(new GeometryFactory()
					.createGeometryCollection(new Geometry[0]));

	public Function cloneFunction() {
		return new GeomUnion();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		final GeometryValue gv = (GeometryValue) args[0];
		result = new GeometryValue(result.getGeom().union(gv.getGeom()));
		return result;
	}

	public String getName() {
		return "GeomUnion";
	}

	public int getType(int[] types) {

		return types[0];
	}

	public boolean isAggregate() {
		return true;
	}
}
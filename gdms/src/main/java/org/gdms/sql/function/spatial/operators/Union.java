package org.gdms.sql.function.spatial.operators;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;

public class Union implements Function {

	public Function cloneFunction() {
		return new Union();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];
		GeometryValue gv1 = (GeometryValue) args[1];
		Geometry intersection = gv.getGeom().union(gv1.getGeom());
		return ValueFactory.createValue(intersection);
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

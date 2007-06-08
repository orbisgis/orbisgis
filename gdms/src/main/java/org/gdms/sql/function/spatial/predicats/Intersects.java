package org.gdms.sql.function.spatial.predicats;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

public class Intersects implements Function {

	public Function cloneFunction() {
		return new Intersects();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];
		GeometryValue gv1 = (GeometryValue) args[1];
		boolean intersects = gv.getGeom().intersects(gv1.getGeom());
		return ValueFactory.createValue(intersects);
	}

	public String getName() {
		return "Intersects";
	}

	public int getType(int[] types) {

		return types[0];
	}

	public boolean isAggregate() {
		return false;
	}

}

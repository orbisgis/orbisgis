package org.gdms.sql.function.spatial.predicats;

import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

public class IsWithinDistance implements Function {

	public Function cloneFunction() {
		return new IsWithinDistance();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];
		GeometryValue gv1 = (GeometryValue) args[1];
		double distance = ((NumericValue) args[2]).doubleValue();

		boolean isWithin = gv.getGeom().isWithinDistance(gv1.getGeom(),
				distance);
		return ValueFactory.createValue(isWithin);
	}

	public String getName() {
		return "IsWithinDistance";
	}

	public int getType(int[] types) {

		return types[0];
	}

	public boolean isAggregate() {
		return false;
	}

}

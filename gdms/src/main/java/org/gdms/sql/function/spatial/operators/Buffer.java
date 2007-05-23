package org.gdms.sql.function.spatial.operators;

import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.PTTypes;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;

public class Buffer implements Function {

	public Function cloneFunction() {
		return new Buffer();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];
		double bufferSize = ((NumericValue) args[1]).doubleValue();
		Geometry buffer = gv.getGeom().buffer(bufferSize);
		return ValueFactory.createValue(buffer);
	}

	public String getName() {
		return "Buffer";
	}

	public int getType(int[] types) {
		
		return types[0];
	}

	public boolean isAggregate() {
		return false;
	}

}

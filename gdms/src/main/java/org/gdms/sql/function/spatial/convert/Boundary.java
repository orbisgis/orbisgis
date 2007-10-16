package org.gdms.sql.function.spatial.convert;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;

public class Boundary implements Function {

	private Geometry totalenv;

	private GeometryValue geometryValue;

	public Function cloneFunction() {

		return new Boundary();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];
		
		Geometry geom = gv.getGeom();
				
		
		return ValueFactory.createValue(geom.getBoundary());
	}

	public String getName() {
		return "Boundary";
	}

	public int getType(int[] types) {
		return types[0];
	}

	public boolean isAggregate() {
		return false;
	}
}
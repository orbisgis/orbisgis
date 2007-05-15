package org.gdms.sql.function.spatial.io;


import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;

import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;


public class GeomFromText implements Function {

	public Function cloneFunction() {
		return new GeomFromText();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];		
		return ValueFactory.createValue(gv.getGeom().toString());
	}

	public String getName() {
		return "GeomFromText";
	}

	public int getType() {
		return Value.STRING;
	}

	public boolean isAggregate() {
		return false;
	}

}

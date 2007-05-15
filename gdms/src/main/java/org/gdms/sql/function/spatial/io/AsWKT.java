package org.gdms.sql.function.spatial.io;

import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.PTTypes;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;

public class AsWKT implements Function {

	public Function cloneFunction() {
		return new AsWKT();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];		
		return ValueFactory.createValue(gv.getGeom().toText());
	}

	public String getName() {
		return "AsWKT";
	}

	public int getType() {
		return Value.STRING;
	}

	public boolean isAggregate() {
		return false;
	}

}

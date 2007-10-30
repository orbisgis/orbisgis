package org.gdms.sql.function.spatial.geometryProperties;

import org.gdms.data.values.DoubleValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

public class GetZValue implements Function {

	public Function cloneFunction() {
		return new GetZValue();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];
		DoubleValue value = null ;
				
		if (gv.getGeom().getGeometryType().equalsIgnoreCase("POINT")) {
			 value = ValueFactory.createValue(gv.getGeom().getCoordinate().z);
			 
		}
		else {
			new FunctionException("Only operates with point");
		}
		
		return value;
	}

	public String getName() {
		return "GetZ";
	}

	public int getType(int[] types) {

		return types[0];
	}

	public boolean isAggregate() {
		return false;
	}

	public String getDescription() {
		
		return  "Return the z value for a point geometry. ";
	}

}

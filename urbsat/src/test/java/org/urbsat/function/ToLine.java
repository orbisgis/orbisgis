package org.urbsat.function;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ToLine implements Function
{

	private Value result = null;
	
	
	
	public Function cloneFunction() {
		
		return new ToLine();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];		
		Geometry boundary = gv.getGeom().getBoundary();
		Coordinate[] listCoordinate = boundary.getCoordinates();
		GeometryFactory result = new GeometryFactory();
		
		for (int k = 0; k<listCoordinate.length; k++){			
			Coordinate[] tupleCoord = new Coordinate[2];			
			tupleCoord[0] = listCoordinate[k];
			tupleCoord[1] = listCoordinate[k + 1];
			Geometry line = result.createLineString(tupleCoord);
			return ValueFactory.createValue(line);
		}
				
		return ValueFactory.createValue(boundary);
	}

	public String getName() {
		
		return "ToLine";
	}

	public int getType(int[] types) {
		return types[0];
	}

	public boolean isAggregate() {
		
		return false;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}

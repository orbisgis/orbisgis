package org.urbsat.function;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class BuildingDensity implements Function {
	
	
	private int constante = 12;
	private int step =0;
	public Function cloneFunction() {
		
		return new BuildingDensity();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		
		if (step==0) {
			Density.clear();
			step++;
		}
		double result = Density.tari(args,"built up areas");
		
		
		return ValueFactory.createValue(result);
	}

	public String getName() {
		
		return "BuildingDensity";
	}

	public int getType(int[] types) {
		return types[0];
	}

	public boolean isAggregate() {
		
		return true;
	}

}

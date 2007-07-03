package org.urbsat.old;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class BuildLenght implements Function{
	double result =0;
	int nombre = 0;
	public Function cloneFunction() {
		
		return new BuildLenght();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		
		String ts = args[1].toString();
		
		String tog = args[0].toString(); 
	
		Geometry geom = null;
		try {
			geom = new WKTReader().read(tog);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Geometry maillon = null;
		
		try {
			maillon = new WKTReader().read(ts);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
	
			if (geom.intersects(maillon)) {
				result=result+geom.getLength();
				nombre ++;
			}
		
		return ValueFactory.createValue(result/nombre);

	}

	public String getName() {
		
		return "BuildLenght";
	}

	public int getType(int[] types) {
		return types[0];
	}

	public boolean isAggregate() {
		return true;
	}
	
		
}

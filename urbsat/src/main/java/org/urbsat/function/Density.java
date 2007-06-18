package org.urbsat.function;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Give the density of the building in an specified area
 * 
 * @author thebaud
 */

public class Density implements Function {

	private double airebuild = 0;

	private double result = 0;

	public Function cloneFunction() {
		return new Density();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		//String dataName = args[args.length - 1].toString();;
	
		String tog = args[0].toString();
		String tom = args[1].toString();
		
		Geometry geom = null;
		
		try {
			geom = new WKTReader().read(tog);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Geometry maillon = null;
		try {
			maillon = new WKTReader().read(tom);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	

		double airemaillon = maillon.getArea();
		
		Geometry enco = geom.intersection(maillon);
				
		double are = enco.getArea();
		airebuild += are;
		result = airebuild / airemaillon;
				
		
		return ValueFactory.createValue(result);
	}

	public String getName() {

		return "Density";
	}

	public int getType(int[] types) {
		return types[0];
	}

	public boolean isAggregate() {

		return true;
	}

}

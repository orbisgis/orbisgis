package org.urbsat.function;

import java.util.ArrayList;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class Enveloppe implements Function{
private Value result = null;
	
	private int constante = 12;
	private Geometry totalenv = null;
	
	public Function cloneFunction() {
		
		return new Enveloppe();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		String ts = args[0].toString();
		Geometry geom = null;
		try {
			geom = new WKTReader().read(ts);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (totalenv==null) {
			totalenv=geom;
		}
		totalenv = totalenv.union(geom);
		Geometry toenv = totalenv.getEnvelope();
	
		return ValueFactory.createValue(toenv);
	}
	
	public String getName() {
		
		return "Enveloppe";
	}

	public int getType(int[] types) {
		return types[0];
	}

	public boolean isAggregate() {
		
		return false;
	}

}
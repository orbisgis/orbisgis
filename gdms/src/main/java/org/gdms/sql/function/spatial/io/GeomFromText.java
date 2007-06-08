package org.gdms.sql.function.spatial.io;


import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;

import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;


public class GeomFromText implements Function {

	private static WKTReader reader = new WKTReader();

	public Function cloneFunction() {
		return new GeomFromText();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		try {
			return ValueFactory.createValue(reader.read(args[0].toString()));
		} catch (ParseException e) {
			throw new FunctionException();
		}
	}

	public String getName() {
		return "GeomFromText";
	}

	public int getType(int[] types) {

		return types[0];
	}

	public boolean isAggregate() {
		return false;
	}

}

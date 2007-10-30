package org.gdms.sql.function.spatial.convert;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;

public class Envelope implements Function {

	private Geometry totalenv;

	private GeometryValue geometryValue;

	public Function cloneFunction() {

		return new Envelope();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];
		
		Geometry geom = gv.getGeom();
		
		if (totalenv == null) {
			totalenv = geom.getEnvelope();
		}
		if (!totalenv.contains(geom)) {
			totalenv = (totalenv.union(geom)).getEnvelope();
		}

		geometryValue = (GeometryValue) ValueFactory.createValue(totalenv);

		
		return geometryValue;
	}

	public String getName() {
		return "Enveloppe";
	}

	public int getType(int[] types) {
		return types[0];
	}

	public boolean isAggregate() {
		return true;
	}
	
public String getDescription() {
		
		return "Compute the geometry envelope";
	}
}
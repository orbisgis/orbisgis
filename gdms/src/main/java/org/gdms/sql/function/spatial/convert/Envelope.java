package org.gdms.sql.function.spatial.convert;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;

public class Envelope implements Function {
	private Geometry globalEnvelope;

	public Function cloneFunction() {
		return new Envelope();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		final Geometry geom = args[0].getAsGeometry();

		if (null == globalEnvelope) {
			globalEnvelope = geom.getEnvelope();
		} else if (!globalEnvelope.contains(geom)) {
			globalEnvelope = (globalEnvelope.union(geom)).getEnvelope();
		}

		return ValueFactory.createValue(globalEnvelope);
	}

	public String getName() {
		return "Envelope";
	}

	public int getType(int[] types) {
		// return Type.GEOMETRY;
		return types[0];
	}

	public boolean isAggregate() {
		return true;
	}

	public String getDescription() {
		return "Compute the global (for all rows) geometry envelope";
	}

	public String getSqlOrder() {
		return "select Envelope(the_geom) from myTable;";
	}
}
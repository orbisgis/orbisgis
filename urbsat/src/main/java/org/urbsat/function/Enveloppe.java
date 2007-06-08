package org.urbsat.function;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class Enveloppe implements Function {

	private Geometry totalenv;

	private GeometryValue geometryValue;

	public Function cloneFunction() {

		return new Enveloppe();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		String ts = args[0].toString();
		String dataname = args[args.length - 1].toString();
		Geometry geom = null;
		try {
			geom = new WKTReader().read(ts);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (totalenv == null) {
			totalenv = geom.getEnvelope();
		}
		if (!totalenv.contains(geom)) {
			totalenv = (totalenv.union(geom)).getEnvelope();
		}

		geometryValue = (GeometryValue) ValueFactory.createValue(totalenv);

		DataSaved.setEnveloppe(dataname, geometryValue);
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
}
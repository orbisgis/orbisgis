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
		String dataname = args[args.length - 1].toString();
		String ts = args[3].toString();
		int x = Integer.parseInt(args[0].toString());
		int y = Integer.parseInt(args[1].toString());
		String tog = args[2].toString();
		String type = args[4].toString();
		Geometry geom = null;
		try {
			geom = new WKTReader().read(tog);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Geometry maillon = null;

		try {
			maillon = DataSaved.getMaillon(dataname, x, y);
		} catch (java.lang.IndexOutOfBoundsException e) {

			return ValueFactory
					.createValue("erreur : il n'existe pas de maillon (" + x
							+ "," + y + ")");
		}

		double airemaillon = maillon.getArea();
		if (ts.equals(type)) {
			if (geom.intersects(maillon)) {
				System.out.println("ca passe");
				Geometry enco = geom.intersection(maillon);
				
				double are = enco.getArea();
				airebuild += are;
				result = airebuild / airemaillon;
				
			}
		}
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

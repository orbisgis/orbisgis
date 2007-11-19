//useless
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
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class Eclatement implements Function {
	private Value result = null;

	private int constante = 12;

	public Function cloneFunction() {

		return new Eclatement();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		String ts = args[0].toString();
		Geometry geom = null;
		try {
			geom = new WKTReader().read(ts);
		} catch (ParseException e) {
			throw new FunctionException("Not valid WKT", e);
		}
		System.out.println(geom);
		ArrayList<LineString> ls = new ArrayList<LineString>();
		Polygon polybuilding = (Polygon) geom;

		LineString lsBuilding = (LineString) polybuilding.getBoundary();
		// System.out.println(lsBuilding);
		Coordinate[] tab = lsBuilding.getCoordinates();
		// faire les linestring
		GeometryFactory geomf = new GeometryFactory();
		int i = 0;
		boolean boucle = true;
		while (boucle) {
			Coordinate[] tab2 = new Coordinate[2];
			tab2[0] = tab[i];
			tab2[1] = tab[i + 1];
			LineString hey3 = geomf.createLineString(tab2);
			ls.add(hey3);
			i++;
			try {
				tab[i + 1] = tab[i + 1];
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				boucle = false;
			}
		}

		return ValueFactory.createValue(ls.get(0));
	}

	public String getName() {

		return "Eclatement";
	}

	public int getType(int[] types) {
		return types[0];
	}

	public boolean isAggregate() {

		return true;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}

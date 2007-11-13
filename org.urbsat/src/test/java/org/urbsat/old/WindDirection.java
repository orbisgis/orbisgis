package org.urbsat.old;

import java.math.BigDecimal;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class WindDirection implements Function {

	public Function cloneFunction() {

		return new WindDirection();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		double angle = Double.parseDouble(args[0].toString());
		String dataName = args[args.length - 1].toString();
		if (angle > 360 || angle < 0) {
			angle = angle % 360;
		}

		double nangle = 0;

		nangle = (450 - angle) % 360;
		System.out.println(nangle);

		double enradian = Math.toRadians(nangle);
		double corx = Math.cos(enradian);
		BigDecimal enra = new BigDecimal(corx);
		BigDecimal enra2 = enra.setScale(8, BigDecimal.ROUND_DOWN);
		corx = enra2.doubleValue();

		double sinx = Math.sin(enradian);
		BigDecimal senra = new BigDecimal(sinx);
		BigDecimal senra2 = senra.setScale(8, BigDecimal.ROUND_DOWN);
		sinx = senra2.doubleValue();

		Coordinate c1 = new Coordinate(corx * 10, sinx * 10);
		Coordinate[] mals = new Coordinate[2];
		mals[0] = new Coordinate(0, 0);
		mals[1] = c1;
		GeometryFactory fact = new GeometryFactory();
		LineString ligneangle = fact.createLineString(mals);

		System.out.println(ligneangle);
		
		return ValueFactory.createValue(ligneangle);
	}

	public String getName() {

		return "WindDirection";
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

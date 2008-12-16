package org.orbisgis.graphicModeler.functions;

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class LineFunction implements Function {

	@Override
	public Value evaluate(Value[] args) throws FunctionException {
		if (args[0].isNull() || args[1].isNull()) {
			return ValueFactory.createNullValue();
		} else {
			Geometry g1 = args[0].getAsGeometry();
			Geometry g2 = args[1].getAsGeometry();
			if (!(g1 instanceof Point && g2 instanceof Point)) {
				return ValueFactory.createNullValue();
			}

			Coordinate[] coords = { g1.getCoordinate(), g2.getCoordinate() };
			CoordinateArraySequence seq = new CoordinateArraySequence(coords);
			LineString seg = new LineString(seq, new GeometryFactory());
			
			return ValueFactory.createValue(seg);
		}
	}

	@Override
	public Value getAggregateResult() {
		return null;
	}

	@Override
	public String getDescription() {
		return "Creates a line from the first point geometry to the second point geometry.";
	}

	@Override
	public Arguments[] getFunctionArguments() {
		Arguments[] ret = new Arguments[1];
		ret[0] = new Arguments(Argument.GEOMETRY, Argument.GEOMETRY);
		return ret;
	}

	@Override
	public String getName() {
		return "line";
	}

	@Override
	public String getSqlOrder() {
		return "SELECT " + getName() + "(t.an_integer) FROM my_table t";
	}

	@Override
	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.GEOMETRY);
	}

	@Override
	public boolean isAggregate() {
		return false;
	}
}
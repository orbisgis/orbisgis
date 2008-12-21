package org.orbisgis.graphicModeler.functions;

import java.util.ArrayList;

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
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class CutLineFunction implements Function {

	@Override
	public Value evaluate(Value[] args) throws FunctionException {

		if (args[0].isNull() || args[1].isNull()) {
			return ValueFactory.createNullValue();
		} else {
			Geometry g = args[0].getAsGeometry();
			double distance = args[1].getAsDouble();
			if (!(g instanceof LineString)) {
				return ValueFactory.createNullValue();
			}

			ArrayList<LineString> lineStrings = new ArrayList<LineString>();
			for (int i = 0; i < g.getNumGeometries(); i++) {
				if (g.getGeometryN(i) instanceof LineString) {
					LineString string = (LineString) g.getGeometryN(i);
					if (string.getNumPoints() == 2) {
						LineSegment segment = new LineSegment(string
								.getCoordinateN(0), string.getCoordinateN(1));
						double fraction = distance / segment.getLength();
						Coordinate[] points = { segment.pointAlong(fraction),
								segment.pointAlong(1 - fraction) };
						lineStrings.add(new LineString(
								new CoordinateArraySequence(points),
								new GeometryFactory()));
					}
				} else {
					return ValueFactory.createNullValue();
				}
			}

			LineString[] aux = new LineString[lineStrings.size()];
			lineStrings.toArray(aux);
			MultiLineString multi = new MultiLineString(aux,
					new GeometryFactory());
			return ValueFactory.createValue(multi);
		}
	}

	@Override
	public Value getAggregateResult() {
		return null;
	}

	@Override
	public String getDescription() {
		return "Creates a new linestring with the same line directions and median "
				+ "but shorter than the original ones.";
	}

	@Override
	public Arguments[] getFunctionArguments() {
		Arguments[] ret = new Arguments[1];
		ret[0] = new Arguments(Argument.GEOMETRY, Argument.FLOAT);
		return ret;
	}

	@Override
	public String getName() {
		return "cutLine";
	}

	@Override
	public String getSqlOrder() {
		return "SELECT " + getName() + "(lines.the_geom, 0.5) FROM lines;";
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

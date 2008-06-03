package org.gdms.sql.customQuery.spatial.geometry.tin;

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.triangulation.sweepLine4CDT.CDTCircumCircle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class CircumCircle implements Function {
	private final static GeometryFactory gf = new GeometryFactory();

	public Value evaluate(Value[] args) throws FunctionException {
		if (!args[0].isNull()) {
			final Geometry g = args[0].getAsGeometry();
			if ((g instanceof Polygon) && (4 == g.getNumPoints())) {
				final Coordinate[] coordinates = g.getCoordinates();
				final CDTCircumCircle cdtCircumCircle = new CDTCircumCircle(
						coordinates[0], coordinates[1], coordinates[2]);
				return ValueFactory.createValue(cdtCircumCircle.getGeometry());
			}
		}
		return ValueFactory.createNullValue();
	}

	public String getDescription() {
		return "This function builds the circum circle using the triangle given as an argument";
	}

	public String getName() {
		return "CircumCircle";
	}

	public String getSqlOrder() {
		return "select CircumCircle(the_geom) from myTin";
	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.GEOMETRY);
	}

	public boolean isAggregate() {
		return false;
	}

	public void validateTypes(Type[] argumentsTypes)
			throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, argumentsTypes, 1);
		FunctionValidator.failIfNotOfType(this, argumentsTypes[0],
				Type.GEOMETRY);
	}
}
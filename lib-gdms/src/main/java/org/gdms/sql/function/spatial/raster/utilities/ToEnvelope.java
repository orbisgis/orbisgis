package org.gdms.sql.function.spatial.raster.utilities;

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.RasterValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ToEnvelope implements Function {
	private static final GeometryFactory gf = new GeometryFactory();

	public Value evaluate(Value[] args) throws FunctionException {
		Envelope grEnv;
		if (args[0] instanceof RasterValue) {
			grEnv = args[0].getAsRaster().getMetadata().getEnvelope();
		} else {
			grEnv = args[0].getAsGeometry().getEnvelopeInternal();
		}
		return ValueFactory.createValue(toGeometry(grEnv));
	}

	private static Geometry toGeometry(final Envelope envelope) {
		if ((0 == envelope.getWidth()) || (0 == envelope.getHeight())) {
			if (0 == envelope.getWidth() + envelope.getHeight()) {
				return gf.createPoint(new Coordinate(envelope.getMinX(),
						envelope.getMinY()));
			}
			return gf.createLineString(new Coordinate[] {
					new Coordinate(envelope.getMinX(), envelope.getMinY()),
					new Coordinate(envelope.getMaxX(), envelope.getMaxY()) });
		}

		return gf
				.createPolygon(gf
						.createLinearRing(new Coordinate[] {
								new Coordinate(envelope.getMinX(), envelope
										.getMinY()),
								new Coordinate(envelope.getMinX(), envelope
										.getMaxY()),
								new Coordinate(envelope.getMaxX(), envelope
										.getMaxY()),
								new Coordinate(envelope.getMaxX(), envelope
										.getMinY()),
								new Coordinate(envelope.getMinX(), envelope
										.getMinY()) }), null);
	}

	public String getDescription() {
		return "Computes the envelope of each row of the input spatial table and returns a geometry";
	}

	public String getName() {
		return "RowEnvelope";
	}

	public String getSqlOrder() {
		return "select RowEnvelope(raster) from mytif; ---OR--- select RowEnvelope(the_geom) from mytable;";
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
		FunctionValidator.failIfNotOfTypes(this, argumentsTypes[0],
				Type.GEOMETRY, Type.RASTER);
	}
}
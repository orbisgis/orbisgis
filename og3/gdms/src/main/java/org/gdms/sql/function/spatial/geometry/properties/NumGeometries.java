package org.gdms.sql.function.spatial.geometry.properties;

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;

public class NumGeometries extends AbstractSpatialPropertyFunction {

	@Override
	protected Value evaluateResult(Value[] args) throws FunctionException {
		final Geometry g = args[0].getAsGeometry();
		return ValueFactory.createValue(g.getNumGeometries());
	}

	@Override
	public String getDescription() {
		return "Returns the number of Geometries.";
	}

	@Override
	public String getName() {
		return "ST_NumGeometries";
	}

	@Override
	public String getSqlOrder() {
		return "select ST_NumGeometries(the_geom) from myTable;";
	}

	@Override
	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.INT);
	}

	@Override
	public boolean isAggregate() {
		return false;
	}

}

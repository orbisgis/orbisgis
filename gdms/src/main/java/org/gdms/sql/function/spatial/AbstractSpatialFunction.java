package org.gdms.sql.function.spatial;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.sql.function.Function;

public abstract class AbstractSpatialFunction implements Function {

	public Type getType(Type[] types) {
		return TypeFactory.createType(Type.GEOMETRY);
	}
}

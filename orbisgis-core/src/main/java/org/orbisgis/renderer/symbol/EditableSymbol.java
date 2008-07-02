package org.orbisgis.renderer.symbol;

import org.gdms.data.types.GeometryConstraint;

public interface EditableSymbol extends Symbol {

	String getClassName();

	EditableSymbol newInstance();

	boolean acceptGeometryType(GeometryConstraint geometryConstraint);
}

package org.orbisgis.renderer.symbol;

import org.gdms.data.types.GeometryConstraint;

public interface EditableSymbol extends Symbol {

	/**
	 * Returns true if this symbol is suitable for a geometry field with the
	 * specified geometry type constraint
	 *
	 * @param geometryConstraint
	 * @return
	 */
	boolean acceptGeometryType(GeometryConstraint geometryConstraint);
}

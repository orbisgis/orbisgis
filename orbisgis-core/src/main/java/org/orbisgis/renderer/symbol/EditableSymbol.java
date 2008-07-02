package org.orbisgis.renderer.symbol;

import org.gdms.data.types.GeometryConstraint;

public interface EditableSymbol extends Symbol {

	/**
	 * Gets the human readable description of this symbol
	 *
	 * @return
	 */
	String getClassName();

	/**
	 * Creates a new instance of this symbol
	 *
	 * @return
	 */
	EditableSymbol newInstance();

	/**
	 * Returns true if this symbol is suitable for a geometry field with the
	 * specified geometry type constraint
	 *
	 * @param geometryConstraint
	 * @return
	 */
	boolean acceptGeometryType(GeometryConstraint geometryConstraint);

	/**
	 * Returns an unique id. It can be whatever unique string. If this string
	 * changes, previous versions of the symbol collection could not be read.
	 * For persistence purposes.
	 *
	 * @return
	 */
	String getId();
}

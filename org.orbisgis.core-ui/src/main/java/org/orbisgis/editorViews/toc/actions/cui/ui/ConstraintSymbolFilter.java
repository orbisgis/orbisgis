package org.orbisgis.editorViews.toc.actions.cui.ui;

import org.gdms.data.types.GeometryConstraint;
import org.orbisgis.renderer.symbol.Symbol;

public class ConstraintSymbolFilter implements SymbolFilter {

	private GeometryConstraint constraint;

	public ConstraintSymbolFilter(GeometryConstraint constraint) {
		this.constraint = constraint;
	}

	public boolean accept(Symbol symbol) {
		return symbol.acceptGeometryType(constraint);
	}

}

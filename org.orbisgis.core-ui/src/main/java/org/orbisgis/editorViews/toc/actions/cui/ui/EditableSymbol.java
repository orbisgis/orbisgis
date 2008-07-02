package org.orbisgis.editorViews.toc.actions.cui.ui;

import org.gdms.data.types.GeometryConstraint;
import org.orbisgis.renderer.symbol.Symbol;

public interface EditableSymbol extends Symbol {

	String getClassName();

	EditableSymbol newInstance();

	boolean acceptGeometryType(GeometryConstraint geometryConstraint);
}

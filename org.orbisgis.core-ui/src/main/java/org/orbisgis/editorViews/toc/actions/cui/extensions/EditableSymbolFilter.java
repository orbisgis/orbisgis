package org.orbisgis.editorViews.toc.actions.cui.extensions;

import org.orbisgis.editorViews.toc.actions.cui.SymbolFilter;
import org.orbisgis.renderer.symbol.EditableSymbol;
import org.orbisgis.renderer.symbol.Symbol;

public class EditableSymbolFilter implements SymbolFilter {

	public boolean accept(Symbol symbol) {
		return symbol instanceof EditableSymbol;
	}

}

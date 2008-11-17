package org.orbisgis.editorViews.toc.actions.cui.legends;

import org.orbisgis.editorViews.toc.actions.cui.SymbolFilter;
import org.orbisgis.renderer.symbol.StandardSymbol;
import org.orbisgis.renderer.symbol.Symbol;

public class StandardSymbolFilter implements SymbolFilter {

	public boolean accept(Symbol symbol) {
		return symbol instanceof StandardSymbol;
	}

}

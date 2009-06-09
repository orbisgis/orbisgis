package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import org.orbisgis.core.renderer.symbol.StandardSymbol;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.SymbolFilter;

public class StandardSymbolFilter implements SymbolFilter {

	public boolean accept(Symbol symbol) {
		return symbol instanceof StandardSymbol;
	}

}

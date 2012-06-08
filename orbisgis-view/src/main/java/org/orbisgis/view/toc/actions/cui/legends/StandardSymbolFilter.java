package org.orbisgis.view.toc.actions.cui.legends;

import org.orbisgis.core.renderer.symbol.StandardSymbol;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.view.toc.actions.cui.SymbolFilter;

@Deprecated
public class StandardSymbolFilter implements SymbolFilter {

	public boolean accept(Symbol symbol) {
		return symbol instanceof StandardSymbol;
	}

}

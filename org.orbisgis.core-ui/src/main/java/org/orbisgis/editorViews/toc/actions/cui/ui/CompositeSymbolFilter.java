package org.orbisgis.editorViews.toc.actions.cui.ui;

import org.orbisgis.renderer.symbol.Symbol;

public class CompositeSymbolFilter implements SymbolFilter {

	private SymbolFilter[] filters;

	public CompositeSymbolFilter(SymbolFilter... filters) {
		this.filters = filters;
	}

	public boolean accept(Symbol symbol) {
		for (SymbolFilter filter : filters) {
			if (!filter.accept(symbol)) {
				return false;
			}
		}

		return true;
	}

}

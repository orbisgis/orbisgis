/**
 * 
 */
package org.orbisgis.editorViews.toc.actions.cui.ui;

import org.orbisgis.renderer.symbol.Symbol;

public interface SymbolFilter {
	public boolean accept(Symbol symbol);
}
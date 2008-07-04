package org.orbisgis;

import java.util.ArrayList;

import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.collection.SymbolCollection;

public interface SymbolManager extends SymbolCollection {

	/**
	 * Gets all the symbols the system supports
	 *
	 * @return
	 */
	ArrayList<Symbol> getAvailableSymbols();

}

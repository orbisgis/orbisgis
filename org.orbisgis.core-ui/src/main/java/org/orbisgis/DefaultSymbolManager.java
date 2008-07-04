package org.orbisgis;

import java.io.File;
import java.util.ArrayList;

import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.SymbolFactory;
import org.orbisgis.renderer.symbol.collection.DefaultSymbolCollection;

public class DefaultSymbolManager extends DefaultSymbolCollection implements
		SymbolManager {

	public DefaultSymbolManager(File collectionFile) {
		super(collectionFile);
	}

	public ArrayList<Symbol> getAvailableSymbols() {
		return SymbolFactory.getAvailableSymbols();
	}

}

package org.orbisgis.renderer.legend;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Geometry;

public class RenderUtils {

	/**
	 * Returns a symbol that will draw the geometry. If the specified symbol is
	 * a symbol composite it will remove the symbols inside that will not draw
	 * the geometry. It will return null if there is no symbol to draw the
	 * geometry
	 *
	 * @param symbol
	 * @param geometry
	 * @return
	 */
	public static Symbol buildSymbolToDraw(Symbol symbol, Geometry geometry) {
		if (symbol instanceof SymbolComposite) {
			SymbolComposite comp = (SymbolComposite) symbol;
			ArrayList<Symbol> symbols = new ArrayList<Symbol>();
			for (int i = 0; i < comp.getSymbolCount(); i++) {
				Symbol newSymbol = buildSymbolToDraw(comp.getSymbol(i),
						geometry);
				if (newSymbol != null) {
					symbols.add(newSymbol);
				}
			}
			if (symbols.size() == 0) {
				return null;
			} else {
				return SymbolFactory.createSymbolComposite(symbols
						.toArray(new Symbol[0]));
			}
		} else {
			if (symbol.willDraw(geometry)) {
				return symbol;
			} else {
				return null;
			}
		}
	}

}

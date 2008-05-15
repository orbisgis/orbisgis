package org.orbisgis.editorViews.toc.actions.cui.gui.widgets;

import org.orbisgis.renderer.legend.CircleSymbol;
import org.orbisgis.renderer.legend.LineSymbol;
import org.orbisgis.renderer.legend.PolygonSymbol;
import org.orbisgis.renderer.legend.Symbol;

public class SymbolListDecorator {
	Symbol sym;

	public SymbolListDecorator( Symbol sym ) {
		if (sym.getName()==null || sym.getName()==""){
			sym.setName(getSymbolType(sym));
		}
		this.sym=sym;
	}

	private String getSymbolType(Symbol symb) {
		if (symb instanceof PolygonSymbol) {
			return "Poligonal symbol";
		}
		if (symb instanceof CircleSymbol) {
			return "Circle symbol";
		}
		if (symb instanceof LineSymbol) {
			return "Line symbol";
		}
		return "NO_NAMED_SYMBOL";
	}

	public Symbol getSymbol(){
		return sym;
	}

	public String toString(){
		return sym.getName();
	}

	public void setSymbol(Symbol sym){
		this.sym=sym;
	}

}

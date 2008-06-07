package org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table;

import org.gdms.data.values.Value;
import org.orbisgis.renderer.legend.Symbol;

public class SymbolValuePOJO {
	private Symbol sym;
	private Value val;
	private String label;

	public Symbol getSym() {
		return sym;
	}

	public void setSym(Symbol sym) {
		this.sym = sym;
	}

	public Value getVal() {
		return val;
	}

	public void setVal(Value val) {
		this.val = val;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}

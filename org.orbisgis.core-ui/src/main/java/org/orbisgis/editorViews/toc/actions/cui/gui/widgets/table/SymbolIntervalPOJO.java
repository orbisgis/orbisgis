package org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table;


import org.orbisgis.renderer.legend.Interval;
import org.orbisgis.renderer.legend.Symbol;

public class SymbolIntervalPOJO{
	private Symbol sym;
	private Interval val;
	private String label;
	public Symbol getSym() {
		return sym;
	}
	public void setSym(Symbol sym) {
		this.sym = sym;
	}
	public Interval getVal() {
		return val;
	}
	public void setVal(Interval val){
		this.val=val;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}

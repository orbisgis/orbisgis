package org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table;

import org.gdms.data.values.Value;
import org.orbisgis.renderer.legend.Symbol;

public class SymbolValuePOJO implements Comparable<SymbolValuePOJO>{
	private Symbol sym;
	private String val;
	private String label;
	private int type;
	public Symbol getSym() {
		return sym;
	}
	public void setSym(Symbol sym) {
		this.sym = sym;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public void setValueType(int type){
		this.type=type;
	}
	public int getValueType(){
		return this.type;
	}
	
	public int compareTo(SymbolValuePOJO o) {
		return this.val.compareTo(o.val);
	}
	
	
	
	
}

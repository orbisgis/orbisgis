package org.orbisgis.renderer.legend;

public abstract class AbstractSymbol2 implements Symbol {

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addSymbol(Symbol symbol) {
		throw new UnsupportedOperationException(
				"This symbol doesn't allow children");
	}

	public void addSymbol(int index, Symbol symbol) {
		throw new UnsupportedOperationException(
				"This symbol doesn't allow children");
	}

	public void removeSymbol(int index) {
		throw new UnsupportedOperationException(
				"This symbol doesn't allow children");
	}

	public boolean removeSymbol(Symbol symbol) {
		throw new UnsupportedOperationException(
				"This symbol doesn't allow children");
	}

	public boolean acceptsChildren() {
		return false;
	}

}

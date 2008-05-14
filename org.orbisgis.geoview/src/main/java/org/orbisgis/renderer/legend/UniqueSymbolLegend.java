package org.orbisgis.renderer.legend;

public interface UniqueSymbolLegend extends Legend {

	/**
	 * Sets the symbol of the legend
	 *
	 * @param symbol
	 */
	void setSymbol(Symbol symbol);
	
	/**
	 * Gets the symbol of the legend
	 * 
	 * @return symbol
	 */
	public Symbol getSymbol();

}

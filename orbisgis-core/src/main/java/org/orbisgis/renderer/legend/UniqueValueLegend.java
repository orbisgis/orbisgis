package org.orbisgis.renderer.legend;

import org.gdms.data.values.Value;

public interface UniqueValueLegend extends ClassifiedLegend {

	/**
	 * Adds a classification to the legend
	 *
	 * @param value
	 *            Classification value
	 * @param symbol
	 *            Symbol to draw the features that is equal the specified
	 *            classification value
	 */
	void addClassification(Value value, Symbol symbol);

	/**
	 * Gets all the values in this legend that are classified
	 *
	 * @return
	 */
	Value[] getClassificationValues();

	/**
	 * Gets the symbol used for the specified value
	 *
	 * @param value
	 * @return The associated symbol or null if the value is not classified in
	 *         this legend
	 */
	Symbol getValueSymbol(Value value);

}

package org.orbisgis.geoview.renderer.legend;

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
}

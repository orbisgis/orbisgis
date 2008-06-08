package org.orbisgis.renderer.legend;

import java.util.ArrayList;

import org.gdms.data.values.Value;

public interface IntervalLegend extends ClassifiedLegend {

	/**
	 * Adds a classification to the legend
	 *
	 * @param initialValue
	 *            Initial value of the interval to classify
	 * @param minIncluded
	 *            If the initialValue is included in the interval
	 * @param finalValue
	 *            Final value of the interval to classify
	 * @param maxIncluded
	 *            If the finalValue is included in the interval
	 * @param symbol
	 *            Symbol for the interval, including the initial and final
	 *            values
	 */
	void addInterval(Value initialValue, boolean minIncluded, Value finalValue,
			boolean maxIncluded, Symbol symbol);

	/**
	 * Adds a classification to the legend
	 *
	 * @param initialValue
	 *            Initial value of the interval to classify
	 * @param included
	 *            if the value is included in the interval
	 * @param symbol
	 *            symbol for the values that match the interval
	 */
	void addIntervalWithMinLimit(Value initialValue, boolean included,
			Symbol symbol);

	/**
	 * Adds a classification to the legend
	 *
	 * @param finalValue
	 *            Final value of the interval to classify
	 * @param included
	 *            if the value is included in the interval
	 * @param symbol
	 *            symbol for the values that match the interval
	 */
	void addIntervalWithMaxLimit(Value finalValue, boolean included,
			Symbol symbol);
	
	public ArrayList<Interval> getIntervals();
	
	public Symbol getSymbolInterval(Interval inter);

}

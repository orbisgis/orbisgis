package org.orbisgis.core.ui.components.patterns;

public interface PatternChangeListener {

	/**
	 * Notifies that the pattern has changed in the specified row and column
	 * 
	 * @param row
	 * @param column
	 */
	void patternChanged(int row, int column);

}

package org.orbisgis.editors.table;

public interface Selection {

	/**
	 * Sets the selected rows
	 * 
	 * @param indexes
	 */
	void setSelection(int[] indexes);

	/**
	 * Gets the currently selected rows
	 * 
	 * @return
	 */
	int[] getSelection();

	/**
	 * Selects all the rows in the specified interval
	 * 
	 * @param init
	 *            first selected row
	 * @param end
	 *            last selected row
	 */
	void selectInterval(int init, int end);

	/**
	 * Clears the selection
	 */
	void clearSelection();
}

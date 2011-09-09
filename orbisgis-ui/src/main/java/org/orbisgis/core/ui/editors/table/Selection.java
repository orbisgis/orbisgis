package org.orbisgis.core.ui.editors.table;

public interface Selection {

	/**
	 * Set a listener for selection changes
	 * 
	 * @param listener
	 */
	void setSelectionListener(SelectionListener listener);

	/**
	 * Removes the listener for selection changes
	 * 
	 * @param listener
	 */
	void removeSelectionListener(SelectionListener listener);

	/**
	 * Sets the selected rows
	 * 
	 * @param indexes
	 */
	void setSelectedRows(int[] indexes);

	/**
	 * Gets the currently selected rows
	 * 
	 * @return
	 */
	int[] getSelectedRows();

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

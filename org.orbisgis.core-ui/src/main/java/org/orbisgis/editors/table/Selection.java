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
}

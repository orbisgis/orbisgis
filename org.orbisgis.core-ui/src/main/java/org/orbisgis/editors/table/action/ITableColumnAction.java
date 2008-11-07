package org.orbisgis.editors.table.action;

import org.orbisgis.editors.table.TableEditableElement;

public interface ITableColumnAction {

	/**
	 * Executes the column action
	 * 
	 * @param element
	 *            Element being edited
	 * @param selectedColumnIndex
	 *            Index of the column this action is executed on
	 */
	void execute(TableEditableElement element, int selectedColumnIndex);

	/**
	 * Returns true if the action can be executed on the specified column
	 * 
	 * @param element
	 *            Element being edited
	 * @param selectedColumnIndex
	 *            Index of the column this action is executed on
	 * @return
	 */
	boolean accepts(TableEditableElement element, int selectedColumn);

}

package org.orbisgis.core.ui.editors.table.action;

import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.editors.table.TableEditor;

public interface ITableCellAction {

	/**
	 * Executes the column action
	 * 
	 * @param tableEditor
	 *            Editor containing the table
	 * @param element
	 *            Element being edited
	 * @param rowIndex
	 *            Index of the row the selected cell is at
	 * @param columnIndex
	 *            Index of the column the selected cell is at
	 */
	void execute(TableEditor tableEditor, TableEditableElement element,
			int rowIndex, int columnIndex);

	/**
	 * Returns true if the action can be executed on the specified column
	 * 
	 * @param element
	 *            Element being edited
	 * @param rowIndex
	 *            Index of the row the user has clicked
	 * @param columnIndex
	 *            Index of the column the selected cell is at
	 * 
	 * @return
	 */
	boolean accepts(TableEditableElement element, int rowIndex, int columnIndex);

}

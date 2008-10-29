package org.orbisgis.editors.table.action;

import org.gdms.data.DataSource;
import org.orbisgis.editors.table.Selection;

public interface ITableCellAction {

	/**
	 * Executes the column action
	 * 
	 * @param dataSource
	 *            DataSource with the contents of the table
	 * @param selection
	 *            Row selection in the table
	 * @param rowIndex
	 *            Index of the row the selected cell is at
	 * @param columnIndex
	 *            Index of the column the selected cell is at
	 */
	void execute(DataSource dataSource, Selection selection, int rowIndex,
			int columnIndex);

	/**
	 * Returns true if the action can be executed on the specified column
	 * 
	 * @param dataSource
	 *            DataSource with the contents of the table
	 * @param selection
	 *            Row selection in the table
	 * @param rowIndex
	 *            Index of the row the user has clicked
	 * @param columnIndex
	 *            Index of the column the selected cell is at
	 * @return
	 */
	boolean accepts(DataSource dataSource, Selection selection, int rowIndex,
			int columnIndex);

}

package org.orbisgis.editors.table.action;

import org.gdms.data.DataSource;
import org.orbisgis.editors.table.Selection;

public interface ITableColumnAction {

	/**
	 * Executes the column action
	 * 
	 * @param dataSource
	 *            DataSource with the contents of the table
	 * @param selection
	 *            Row selection in the table
	 * @param selectedColumnIndex
	 *            Index of the column this action is executed on
	 */
	void execute(DataSource dataSource, Selection selection,
			int selectedColumnIndex);

	/**
	 * Returns true if the action can be executed on the specified column
	 * 
	 * @param dataSource
	 *            DataSource with the contents of the table
	 * @param selection
	 *            Row selection in the table
	 * @param selectedColumnIndex
	 *            Index of the column this action is executed on
	 * @return
	 */
	boolean accepts(DataSource dataSource, Selection selection,
			int selectedColumn);

}

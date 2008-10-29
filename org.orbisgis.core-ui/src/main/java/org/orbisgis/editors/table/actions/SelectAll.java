package org.orbisgis.editors.table.actions;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editors.table.Selection;
import org.orbisgis.editors.table.action.ITableCellAction;
import org.orbisgis.errorManager.ErrorManager;

public class SelectAll implements ITableCellAction {

	@Override
	public boolean accepts(DataSource dataSource, Selection selection,
			int rowIndex, int columnIndex) {
		try {
			return dataSource.getRowCount() != selection.getSelection().length;
		} catch (DriverException e) {
			return false;
		}
	}

	@Override
	public void execute(DataSource dataSource, Selection selection,
			int rowIndex, int columnIndex) {
		try {
			selection.selectInterval(0, (int) dataSource.getRowCount() - 1);
		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot get the number of rows", e);
		}
	}

}

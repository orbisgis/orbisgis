package org.orbisgis.editors.table.actions;

import org.gdms.data.DataSource;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editors.table.Selection;
import org.orbisgis.editors.table.action.ITableCellAction;
import org.orbisgis.errorManager.ErrorManager;

public class SetNull implements ITableCellAction {

	@Override
	public boolean accepts(DataSource dataSource, Selection selection,
			int rowIndex, int columnIndex) {
		try {
			return !dataSource.isNull(rowIndex, columnIndex);
		} catch (DriverException e) {
			return false;
		}
	}

	@Override
	public void execute(DataSource dataSource, Selection selection,
			int rowIndex, int columnIndex) {
		try {
			dataSource.setFieldValue(rowIndex, columnIndex, ValueFactory
					.createNullValue());
		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error("Cannot set null", e);
		}
	}

}

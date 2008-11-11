package org.orbisgis.editors.table.actions;

import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editors.table.TableEditableElement;
import org.orbisgis.editors.table.TableEditor;
import org.orbisgis.editors.table.action.ITableCellAction;
import org.orbisgis.errorManager.ErrorManager;

public class SetNull implements ITableCellAction {

	@Override
	public boolean accepts(TableEditableElement element, int rowIndex,
			int columnIndex) {
		try {
			return element.isEditable()
					&& !element.getDataSource().isNull(rowIndex, columnIndex);
		} catch (DriverException e) {
			return false;
		}
	}

	@Override
	public void execute(TableEditor editor, TableEditableElement element,
			int rowIndex, int columnIndex) {
		try {
			element.getDataSource().setFieldValue(rowIndex, columnIndex,
					ValueFactory.createNullValue());
		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error("Cannot set null", e);
		}
	}

}

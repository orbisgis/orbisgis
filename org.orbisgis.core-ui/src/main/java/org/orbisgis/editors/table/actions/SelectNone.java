package org.orbisgis.editors.table.actions;

import org.orbisgis.editors.table.TableEditableElement;
import org.orbisgis.editors.table.action.ITableCellAction;

public class SelectNone implements ITableCellAction {

	@Override
	public boolean accepts(TableEditableElement element, int rowIndex,
			int columnIndex) {
		return element.getSelection().getSelectedRows().length > 0;
	}

	@Override
	public void execute(TableEditableElement element, int rowIndex,
			int columnIndex) {
		element.getSelection().clearSelection();
	}

}

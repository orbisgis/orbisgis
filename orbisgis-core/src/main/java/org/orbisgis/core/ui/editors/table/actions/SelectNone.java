package org.orbisgis.core.ui.editors.table.actions;

import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.editors.table.TableEditor;
import org.orbisgis.core.ui.editors.table.action.ITableCellAction;

public class SelectNone implements ITableCellAction {

	@Override
	public boolean accepts(TableEditableElement element, int rowIndex,
			int columnIndex) {
		return element.getSelection().getSelectedRows().length > 0;
	}

	@Override
	public void execute(TableEditor editor, TableEditableElement element,
			int rowIndex, int columnIndex) {
		element.getSelection().clearSelection();
	}

}

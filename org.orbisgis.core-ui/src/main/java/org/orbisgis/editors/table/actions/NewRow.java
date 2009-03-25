package org.orbisgis.editors.table.actions;

import org.orbisgis.editors.table.TableEditableElement;
import org.orbisgis.editors.table.TableEditor;
import org.orbisgis.editors.table.action.ITableCellAction;

public class NewRow implements ITableCellAction {

	@Override
	public boolean accepts(TableEditableElement element, int rowIndex,
			int columnIndex) {

		if (element.isEditable()){
			return true;
		}
		else if (element.getMapContext() == null){
			return org.orbisgis.editors.table.editorActions.NewRow.isEnabled(element);
		}
		return false;

	}

	@Override
	public void execute(TableEditor tableEditor, TableEditableElement element,
			int rowIndex, int columnIndex) {
		org.orbisgis.editors.table.editorActions.NewRow.actionPerformed(element);
	}

}

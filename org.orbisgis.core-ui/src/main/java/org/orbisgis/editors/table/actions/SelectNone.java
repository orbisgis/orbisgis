package org.orbisgis.editors.table.actions;

import org.gdms.data.DataSource;
import org.orbisgis.editors.table.Selection;
import org.orbisgis.editors.table.action.ITableCellAction;

public class SelectNone implements ITableCellAction {

	@Override
	public boolean accepts(DataSource dataSource, Selection selection,
			int rowIndex, int columnIndex) {
		return selection.getSelection().length > 0;
	}

	@Override
	public void execute(DataSource dataSource, Selection selection,
			int rowIndex, int columnIndex) {
		selection.clearSelection();
	}

}

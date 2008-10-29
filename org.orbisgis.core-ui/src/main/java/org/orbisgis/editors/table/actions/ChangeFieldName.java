package org.orbisgis.editors.table.actions;

import javax.swing.JOptionPane;

import org.gdms.data.DataSource;
import org.orbisgis.editors.table.Selection;
import org.orbisgis.editors.table.action.ITableColumnAction;

public class ChangeFieldName implements ITableColumnAction {

	@Override
	public boolean accepts(DataSource dataSource, Selection selection,
			int selectedColumn) {
		return selectedColumn != -1;
	}

	@Override
	public void execute(DataSource dataSource, Selection selection,
			int selectedColumnIndex) {
		JOptionPane.showMessageDialog(null, "MEEEEEEC!");
	}

}

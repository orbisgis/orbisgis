package org.orbisgis.editors.table.actions;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editors.table.FieldNameChooser;
import org.orbisgis.editors.table.Selection;
import org.orbisgis.editors.table.action.ITableColumnAction;
import org.orbisgis.errorManager.ErrorManager;
import org.sif.UIFactory;

public class AddField implements ITableColumnAction {

	@Override
	public boolean accepts(DataSource dataSource, Selection selection,
			int selectedColumn) {
		return selectedColumn != -1;
	}

	@Override
	public void execute(DataSource dataSource, Selection selection,
			int selectedColumnIndex) {
		try {
			FieldNameChooser av = new FieldNameChooser(dataSource
					.getFieldNames(), "New field name", "strlength(txt) > 0",
					"Empty name not allowed", dataSource.getMetadata()
							.getFieldName(selectedColumnIndex));
			if (UIFactory.showDialog(av)) {
				dataSource.setFieldName(selectedColumnIndex, av.getValue());
			}
		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot change field name", e);
		}
	}

}

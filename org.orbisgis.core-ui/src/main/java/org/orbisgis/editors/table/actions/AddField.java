package org.orbisgis.editors.table.actions;

import org.gdms.data.DataSource;
import org.gdms.data.types.TypeDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadWriteDriver;
import org.orbisgis.Services;
import org.orbisgis.editors.table.TableEditableElement;
import org.orbisgis.editors.table.action.ITableColumnAction;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.views.geocatalog.actions.create.FieldEditor;
import org.sif.UIFactory;

public class AddField implements ITableColumnAction {

	@Override
	public boolean accepts(TableEditableElement element, int selectedColumn) {
		return (selectedColumn != -1) && element.getDataSource().isEditable();
	}

	@Override
	public void execute(TableEditableElement element, int selectedColumnIndex) {
		try {
			DataSource dataSource = element.getDataSource();
			ReadWriteDriver driver = (ReadWriteDriver) dataSource.getDriver();
			TypeDefinition[] typeDefinitions = driver.getTypesDefinitions();
			FieldEditor fe = new FieldEditor(typeDefinitions);
			if (UIFactory.showDialog(fe)) {
				dataSource.addField(fe.getFieldName(), fe.getType());
			}
		} catch (DriverException e) {
			Services.getService(ErrorManager.class)
					.error("Cannot add field", e);
		}
	}

}

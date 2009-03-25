package org.orbisgis.editors.table.actions;

import javax.swing.JOptionPane;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editors.table.TableEditableElement;
import org.orbisgis.editors.table.action.ITableColumnAction;
import org.orbisgis.errorManager.ErrorManager;

public class RemoveField implements ITableColumnAction {

	@Override
	public boolean accepts(TableEditableElement element, int selectedColumn) {

		try {


			if ((selectedColumn != -1) && element.isEditable() ){
			Metadata metadata = element.getDataSource().getMetadata();

			Type type = metadata.getFieldType(selectedColumn);

			int typeCode = type.getTypeCode();

			if (typeCode !=Type.GEOMETRY) {

				return true;
			}
			}

		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot access field information", e);
		}

		return false;

		//return (selectedColumn != -1) && element.getDataSource().isEditable();
	}

	@Override
	public void execute(TableEditableElement element, int selectedColumnIndex) {
		try {
			DataSource dataSource = element.getDataSource();
			int option = JOptionPane.showConfirmDialog(null, "Delete field "
					+ dataSource.getFieldName(selectedColumnIndex) + "?",
					"Remove field", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				dataSource.removeField(selectedColumnIndex);
			}
		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot remove field", e);
		}
	}

}

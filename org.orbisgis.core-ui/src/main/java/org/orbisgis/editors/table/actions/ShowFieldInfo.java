package org.orbisgis.editors.table.actions;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editors.table.TableEditableElement;
import org.orbisgis.editors.table.action.ITableColumnAction;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.outputManager.OutputManager;

public class ShowFieldInfo implements ITableColumnAction {

	@Override
	public boolean accepts(TableEditableElement element, int selectedColumn) {
		return true;
	}

	@Override
	public void execute(TableEditableElement element, int selectedColumnIndex) {
		try {
			Metadata metadata = element.getDataSource().getMetadata();
			OutputManager om = Services.getService(OutputManager.class);
			om.print("Field name:"
					+ metadata.getFieldName(selectedColumnIndex) + "\n");
			Type type = metadata.getFieldType(selectedColumnIndex);
			om.print("Field type:"
					+ TypeFactory.getTypeName(type.getTypeCode())
					+ "\nConstraints:\n");
			Constraint[] cons = type.getConstraints();
			for (Constraint constraint : cons) {
				om.print("  "
						+ ConstraintFactory.getConstraintName(constraint
								.getConstraintCode()) + ": "
						+ constraint.getConstraintHumanValue() + "\n");
			}

		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot access field information", e);
		}
	}
}

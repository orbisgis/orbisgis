package org.orbisgis.core.ui.editors.table;

import org.orbisgis.core.ui.components.sif.AskValue;
import org.orbisgis.utils.I18N;

public class FieldNameChooser extends AskValue {

	private String[] fieldNames;

	public FieldNameChooser(String[] fieldNames, String title, String sql,
			String error, String initialValue) {
		super(title, sql, error, initialValue);
		this.fieldNames = fieldNames;
	}

	@Override
	public String postProcess() {
		for (String fieldName : fieldNames) {
			if (fieldName.equals(getValue())) {
				return I18N.getString("orbisgis.org.orbisgis.ui.table.fieldNameChooser.repeatedFieldName"); //$NON-NLS-1$
			}
		}

		return super.postProcess();
	}

}

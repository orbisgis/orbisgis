package org.orbisgis.core.ui.editors.table;

import org.orbisgis.core.ui.components.sif.AskValue;

public class FieldNameChooser extends AskValue {

	private String[] fieldNames;

	public FieldNameChooser(String[] fieldNames, String title, String sql, String error,
			String initialValue) {
		super(title, sql, error, initialValue);
		this.fieldNames = fieldNames;
	}
	
	@Override
	public String postProcess() {
		for (String fieldName : fieldNames) {
			if (fieldName.equals(getValue())) {
				return "Repeated field name";
			}
		}

		return super.postProcess();
	}

}

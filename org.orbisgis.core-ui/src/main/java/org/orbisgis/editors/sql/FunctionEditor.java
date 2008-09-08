package org.orbisgis.editors.sql;

import org.orbisgis.editor.IEditor;
import org.orbisgis.geocognition.sql.GeocognitionFunctionFactory;

public class FunctionEditor extends JavaEditor implements IEditor {

	@Override
	public boolean acceptElement(String typeId) {
		return GeocognitionFunctionFactory.JAVA_FUNCTION_ID.equals(typeId);
	}

}

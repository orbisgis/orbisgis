package org.orbisgis.editors.sql;

import org.orbisgis.editor.IEditor;
import org.orbisgis.geocognition.sql.GeocognitionCustomQueryFactory;

public class CustomQueryEditor extends JavaEditor implements IEditor {

	@Override
	public boolean acceptElement(String typeId) {
		return GeocognitionCustomQueryFactory.JAVA_QUERY_ID.equals(typeId);
	}

}

package org.orbisgis.geoview.sqlConsole.ui;

import java.util.HashMap;
import java.util.Map;

import org.orbisgis.pluginManager.ui.SaveFilePanel;

public class OutFilePanel extends SaveFilePanel {

	public static final String SIF_ID = "org.orbisgis.geoview.sqlConsoleOutFile";

	private static Map<String, String> outFormat = new HashMap<String, String>();
	static {
		outFormat.put("sql", "SQL script (*.sql)");
		outFormat.put("txt", "Text file (*.txt)");

	}

	public OutFilePanel(String title) {
		super(title, outFormat);
	}

	public String getId() {
		return SIF_ID;
	}

	public String[] getErrorMessages() {
		return null;
	}

	public String[] getValidationExpressions() {
		return null;
	}

}

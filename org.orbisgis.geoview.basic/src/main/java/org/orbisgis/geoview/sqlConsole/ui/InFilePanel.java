package org.orbisgis.geoview.sqlConsole.ui;

import java.util.HashMap;
import java.util.Map;

import org.orbisgis.pluginManager.ui.OpenFilePanel;

public class InFilePanel extends OpenFilePanel {

	private static Map<String, String> inFormat = new HashMap<String, String>();
	static {
		inFormat.put("sql", "SQL script (*.sql)");

	}

	public static final String SIF_ID = "org.orbisgis.geoview.sqlConsoleInFile";

	public InFilePanel(String title) {
		super(title, inFormat);
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

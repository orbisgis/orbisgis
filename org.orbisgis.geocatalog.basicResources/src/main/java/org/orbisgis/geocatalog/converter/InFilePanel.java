package org.orbisgis.geocatalog.converter;

import java.util.HashMap;
import java.util.Map;

import org.orbisgis.pluginManager.ui.OpenFilePanel;

public class InFilePanel extends OpenFilePanel {

	private static Map<String, String> inFormat = new HashMap<String, String>();
	static {
		inFormat.put("xyz", "XYZ DEM (*.xyz)");

	}

	public static final String SIF_ID = "org.orbisgis.geocatalog.XYZConverterIn";

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

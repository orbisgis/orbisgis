package org.orbisgis.geocatalog.converter;

import java.util.HashMap;
import java.util.Map;

import org.orbisgis.pluginManager.ui.SaveFilePanel;

public class OutFilePanel extends SaveFilePanel {

	public static final String SIF_ID = "org.orbisgis.geocatalog.XYZConverterOut";

	private static Map<String, String> outFormat = new HashMap<String, String>();
	static {
		outFormat.put("tif", "TIF with TFW format (*.tif)");
		outFormat.put("png", "PNG with PGW format (*.png)");
		outFormat.put("asc", "Esri ascii grid format (*.asc)");

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

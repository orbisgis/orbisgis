package org.orbisgis.core.wizards;

import java.util.HashMap;
import java.util.Map;

import org.orbisgis.pluginManager.ui.OpenFilePanel;

public class OpenGdmsFilePanel extends OpenFilePanel {

	public static final String OPEN_GDMS_FILE_PANEL = "org.orbisgis.OpenGdmsFilePanel";

	private static Map<String, String> formatAndDescription = new HashMap<String, String>();

	static {
		formatAndDescription.put("shp", "Esri shapefile format (*.shp)");
		formatAndDescription.put("cir", "Solene format (*.cir)");
		formatAndDescription.put("dbf", "DBF format (*.dbf)");
		formatAndDescription.put("csv", "CSV format (*.csv)");
		formatAndDescription.put("tif", "TIF with TFW format (*.tif)");
		formatAndDescription.put("png", "PNG with PGW format (*.png)");
		formatAndDescription.put("asc", "Esri ascii grid format (*.asc)");

	}

	public OpenGdmsFilePanel(String title) {
		super(title, formatAndDescription);
	}

	public String[] getErrorMessages() {
		return null;
	}

	public String getId() {
		return OPEN_GDMS_FILE_PANEL;
	}

	public String[] getValidationExpressions() {
		return null;
	}

}

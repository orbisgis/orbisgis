package org.orbisgis.core.wizards;

import org.orbisgis.pluginManager.ui.OpenFilePanel;

public class OpenGdmsFilePanel extends OpenFilePanel {

	public static final String OPEN_GDMS_FILE_PANEL = "org.orbisgis.OpenGdmsFilePanel";

	public OpenGdmsFilePanel(String title) {
		super(OPEN_GDMS_FILE_PANEL, title);
		this.addFilter("shp", "Esri shapefile format (*.shp)");
		this.addFilter("cir", "Solene format (*.cir)");
		this.addFilter("dbf", "DBF format (*.dbf)");
		this.addFilter("csv", "CSV format (*.csv)");
		this.addFilter(new String[] { "tif", "tiff" },
				"TIF with TFW format (*.tif; *.tiff)");
		this.addFilter("png", "PNG with PGW format (*.png)");
		this.addFilter("asc", "Esri ascii grid format (*.asc)");
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

package org.orbisgis.wizards;

import org.orbisgis.pluginManager.ui.SaveFilePanel;

public class SaveGdmsFilePanel extends SaveFilePanel {

	public SaveGdmsFilePanel(String id, String title) {
		super(id, title);
		this.addFilter("shp", "Esri shapefile format (*.shp)");
		this.addFilter("gdms", "GDMS format (*.gdms)");
		this.addFilter("cir", "Solene format (*.cir)");
		this.addFilter("dbf", "DBF format (*.dbf)");
		this.addFilter("csv", "CSV format (*.csv)");
		this.addFilter(new String[] { "tif", "tiff" },
				"TIF with TFW format (*.tif; *.tiff)");
		this.addFilter("png", "PNG with PGW format (*.png)");
		this.addFilter("asc", "Esri ascii grid format (*.asc)");
		this.addFilter("jpg", "JPG with JGW format (*.jpg)");
	}

}

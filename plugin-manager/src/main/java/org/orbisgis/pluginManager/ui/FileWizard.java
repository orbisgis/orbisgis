package org.orbisgis.pluginManager.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


import org.sif.UIPanel;

public class FileWizard {
	public static final String FILE_CHOOSER_SIF_ID = "org.orbisgis.FileChooser";

	private FilePanel filePanel;
	
	private static Map<String, String>  format = new HashMap<String, String>();
	static {
		format.put("shp", "Esri shapefile format (*.shp)");
		format.put("cir", "Solene format (*.cir)");
		format.put("dbf", "DBF format (*.dbf)");
		format.put("csv", "CSV format (*.csv)");
		format.put("tif", "TIF with TFW format (*.tif)");
		format.put("png", "PNG with PGW format (*.png)");
		format.put("asc", "Esri ascii grid format (*.asc)");
		
	}
	
	

	public UIPanel[] getWizardPanels() {
		
		filePanel = new FilePanel(format);
		return new UIPanel[] { filePanel };
	}

	protected File[] getSelectedFiles() {
		return filePanel.fileChooser.getSelectedFiles();
	}

}

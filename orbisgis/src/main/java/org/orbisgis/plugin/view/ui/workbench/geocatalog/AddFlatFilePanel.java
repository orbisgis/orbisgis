package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.io.File;

import javax.swing.JPanel;

import org.orbisgis.plugin.view.ui.workbench.FileChooser;

;

public class AddFlatFilePanel extends JPanel {

	private FileChooser ofc = null;

	/**
	 * Creates a JPanel with a file chooser and good filters for supported
	 * files. See FileChooser.java for the syntax of supported files. It is
	 * located in org.orbisgis.plugin.view.ui.workbench
	 */
	public AddFlatFilePanel() {
		String[][] supportedDSFiles = {
				{ "shp", "csv", "dbf" },
				{ "Vector files (*.shp, *.csv, *.dbf)" },
				{ "tif", "tiff", "asc" },
				{ "Raster Files (*.tif, *.tiff, *.asc)" },
				{ "shp" },
				{ "SHP Files (*.shp)" },
				{ "cir" },
				{ "CIR Files (*.cir)" },
				{ "png" },
				{ "PNG Files (*.png)" },
				{ "shp", "csv", "dbf", "tif", "tiff", "asc", "cir", "png" },
				{ "All supported files (*.shp, *.csv, *.dbf, *.tif, *.tiff, *.asc, *.cir, *.png)" } };
		ofc = new FileChooser(supportedDSFiles);
		ofc.setControlButtonsAreShown(false);
		ofc.setMultiSelectionEnabled(true);
		add(ofc);
	}

	public File[] getFiles() {
		return ofc.getSelectedFiles();
	}

}

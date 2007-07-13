package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.io.File;

import javax.swing.JPanel;

import org.orbisgis.plugin.view.ui.workbench.FileChooser;

public class AddFlatFilePanel extends JPanel {

	private FileChooser ofc = null;

	public AddFlatFilePanel() {
		String[][] supportedDSFiles = {{"shp", "csv", "dbf"}, {"Vector files (*.shp, *.csv, *.dbf)"}, {"tif", "tiff", "asc"}, {"Raster Files (*.tif, *.tiff, *.asc)"}, {"shp", "csv", "dbf", "tif", "tiff", "asc"}, {"All supported files (*.shp, *.csv, *.dbf, *.tif, *.tiff, *.asc)"}};
		ofc = new FileChooser(supportedDSFiles);
		ofc.setControlButtonsAreShown(false);
		add(ofc);
	}

	public File[] getFiles() {
		return ofc.getSelectedFiles();
	}

}

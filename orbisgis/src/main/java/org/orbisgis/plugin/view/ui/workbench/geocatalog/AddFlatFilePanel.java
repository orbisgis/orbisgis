package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.io.File;

import javax.swing.JPanel;

import org.orbisgis.plugin.view.ui.workbench.FileChooser;

public class AddFlatFilePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private FileChooser ofc = null;
	
	public AddFlatFilePanel() {
		String[] supportedDSFiles = {"shp","csv","dbf"};
		ofc = new FileChooser(supportedDSFiles, "Supported files (*.shp, *.csv, *.dbf)", true);
		ofc.setControlButtonsAreShown(false);
		add(ofc);
	}
	
	public File[] getFiles() {
		return ofc.getSelectedFiles();
	}

}

package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import javax.swing.JPanel;

import org.orbisgis.plugin.view.ui.workbench.FileChooser;

public class AddFlatFilePanel extends JPanel {
	
	AddFlatFilePanel() {
		String[] supportedDSFiles = {"shp","csv"};
		FileChooser ofc = new FileChooser(supportedDSFiles, "Supported files (*.shp, *.csv)", true);
		ofc.setControlButtonsAreShown(false);
		add(ofc);
	}
	

}

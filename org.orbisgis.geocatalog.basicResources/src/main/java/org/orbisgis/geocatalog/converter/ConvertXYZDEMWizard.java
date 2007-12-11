package org.orbisgis.geocatalog.converter;

import java.io.File;

import org.orbisgis.pluginManager.ui.OpenFilePanel;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.sif.SIFWizard;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class ConvertXYZDEMWizard {

	private static final String OUT_FILE_ID = "org.orbisgis.geocatalog.XYZConverterOut";

	private static final String IN_FILE_ID = "org.orbisgis.geocatalog.XYZConverterIn";

	private OpenFilePanel outfilePanel;

	private UIXYZDEMPanel uixyzPanel;

	private OpenFilePanel infilePanel;

	public UIPanel[] getWizardPanels() {

		infilePanel = new OpenFilePanel(IN_FILE_ID,
				"Select XYZ file to convert");
		infilePanel.addFilter("xyz", "XYZ DEM (*.xyz)");

		uixyzPanel = new UIXYZDEMPanel();

		outfilePanel = new SaveFilePanel(OUT_FILE_ID,
				"Select the raster file to save to");
		outfilePanel.addFilter("tif", "TIF with TFW format (*.tif)");

		return new UIPanel[] { infilePanel, uixyzPanel, outfilePanel };
	}

	protected File getSelectedInFiles() {
		return infilePanel.getSelectedFile();
	}

	protected File getSelectedOutFiles() {
		return outfilePanel.getSelectedFile();
	}

	public static void main(String[] args) {

		// UIFactory.showDialog(new ConvertFileWizard().getWizardPanels());
		SIFWizard sifDialog = UIFactory.getWizard(new ConvertXYZDEMWizard()
				.getWizardPanels());
		sifDialog.pack();
		sifDialog.setVisible(true);

	}

	public float getPixelSize() {
		return uixyzPanel.getPixelSize();
	}
}

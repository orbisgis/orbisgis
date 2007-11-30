package org.orbisgis.geocatalog.converter;

import java.io.File;

import org.orbisgis.pluginManager.ui.OpenFilePanel;
import org.sif.SIFWizard;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class ConvertXYZDEMWizard {

	private OpenFilePanel outfilePanel;

	private UIXYZDEMPanel uixyzPanel;

	private OpenFilePanel infilePanel;

	public UIPanel[] getWizardPanels() {

		infilePanel = new InFilePanel("Select XYZ file to convert");

		uixyzPanel = new UIXYZDEMPanel();

		outfilePanel = new OutFilePanel("Select the raster file to save to");

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
}

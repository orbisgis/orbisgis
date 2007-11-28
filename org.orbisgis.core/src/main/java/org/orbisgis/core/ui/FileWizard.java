package org.orbisgis.pluginManager.ui;

import java.io.File;


import org.sif.UIPanel;

public class FileWizard {
	public static final String FILE_CHOOSER_SIF_ID = "org.orbisgis.FileChooser";

	private FilePanel filePanel;

	public UIPanel[] getWizardPanels() {
		filePanel = new FilePanel();
		return new UIPanel[] { filePanel };
	}

	protected File[] getSelectedFiles() {
		return filePanel.fileChooser.getSelectedFiles();
	}

}

package org.orbisgis.core;

import java.awt.Component;
import java.io.File;
import java.net.URL;

import javax.swing.JFileChooser;

import org.sif.UIPanel;

public class FileWizard {

	private FilePanel filePanel;

	public UIPanel[] getWizardPanels() {
		filePanel = new FilePanel();
		return new UIPanel[] { filePanel };
	}

	protected class FilePanel implements UIPanel {

		private JFileChooser fileChooser;

		public Component getComponent() {
			fileChooser = new JFileChooser();
			fileChooser.setControlButtonsAreShown(false);
			fileChooser.setMultiSelectionEnabled(true);
			return fileChooser;
		}

		public URL getIconURL() {
			return null;
		}

		public String getTitle() {
			return "New file";
		}

		public void initialize() {

		}

		public String validate() {
			if (fileChooser.getSelectedFile() == null) {
				return "A file have to be selected";
			}

			return null;
		}

	}

	protected File[] getSelectedFiles() {
		return filePanel.fileChooser.getSelectedFiles();
	}

}

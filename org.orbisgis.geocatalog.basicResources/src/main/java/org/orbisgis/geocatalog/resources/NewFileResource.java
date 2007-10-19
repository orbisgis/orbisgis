package org.orbisgis.geocatalog.resources;

import java.awt.Component;
import java.net.URL;

import javax.swing.JFileChooser;

import org.orbisgis.geocatalog.INewResource;
import org.sif.UIPanel;

public class NewFileResource implements INewResource {

	public String getName() {
		return "New file";
	}

	public UIPanel[] getWizardPanels() {
		return new UIPanel[] { new FilePanel() };
	}

	private class FilePanel implements UIPanel {

		private JFileChooser fileChooser;

		public Component getComponent() {
			fileChooser = new JFileChooser();
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

}

package org.orbisgis.geocatalog.resources;

import java.awt.Component;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import org.gdms.data.file.FileSourceDefinition;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.INewResource;
import org.orbisgis.geocatalog.resources.utilities.FileUtility;
import org.sif.UIPanel;

public class NewFileResource implements INewResource {

	private FilePanel filePanel;

	public String getName() {
		return "New file";
	}

	public UIPanel[] getWizardPanels() {
		filePanel = new FilePanel();
		return new UIPanel[] { filePanel };
	}

	private class FilePanel implements UIPanel {

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

	public IResource[] getResources() {
		File[] files = filePanel.fileChooser.getSelectedFiles();
		ArrayList<IResource> resources = new ArrayList<IResource>();
		for (File file : files) {
			String name = file.getName();
			String extension = FileUtility.getFileExtension(file);
			String nickname = name.substring(0, name.indexOf("." + extension));
			String tmpName = nickname;
			int i = 0;
			while (OrbisgisCore.getDSF().existDS(tmpName)) {
				i++;
				tmpName = tmpName + "_" + i;
			}
			OrbisgisCore.getDSF().registerDataSource(tmpName,
					new FileSourceDefinition(file));

			resources.add(new GdmsSource(tmpName));
		}

		return resources.toArray(new IResource[0]);
	}
}

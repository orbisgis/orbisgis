package org.orbisgis.geocatalog.resources.wizards;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.SLDFile;
import org.orbisgis.geocatalog.resources.utilities.FileChooser;
import org.orbisgis.geocatalog.resources.utilities.FileUtility;

;

public class AddFlatFilePanel extends JPanel implements IAddRessourceWizard {

	private FileChooser ofc = null;

	/**
	 * Creates a JPanel with a file chooser and good filters for supported
	 * files.
	 */
	public AddFlatFilePanel(String[][] supportedFiles) {
		ofc = new FileChooser(supportedFiles);
		ofc.setControlButtonsAreShown(false);
		ofc.setMultiSelectionEnabled(true);
		add(ofc);
	}

	/**
	 * Used by AddSourceChoose
	 *
	 * @return
	 */
	public File[] getFiles() {
		return ofc.getSelectedFiles();
	}

	/**
	 * Used to add SLD
	 */
	public IResource[] getNewResources() {
		ArrayList<IResource> ressources = new ArrayList<IResource>();
		File[] files = getFiles();

		for (File file : files) {
			String name = file.getName();
			String extension = FileUtility.getFileExtension(file);
			String nickname = name.substring(0, name.indexOf("." + extension));

			if ("sld".equalsIgnoreCase(extension)) {
				ressources.add(new SLDFile(nickname, file.getPath()));
			}
		}

		return ressources.toArray(new IResource[0]);
	}

	public JPanel getWizardUI() {
		return this;
	}

}

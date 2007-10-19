package org.orbisgis.geocatalog.resources.wizards;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.file.FileSourceDefinition;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.SLDFile;
import org.orbisgis.geocatalog.resources.utilities.FileChooser;
import org.orbisgis.geocatalog.resources.utilities.FileUtility;

/**
 * This class creates a JPanel with a file chooser and good filters for
 * supported files.
 *
 * @author Samuel CHEMLA
 *
 */
public class AddFilesRessources extends JPanel implements IAddRessourceWizard {

	private FileChooser ofc = null;

	/**
	 * See FileChooser.java for the syntax of supported files. It is located in
	 * org.orbisgis.plugin.view.ui.workbench
	 */
	public AddFilesRessources() {
		// String[][] supportedDSFiles = {
		// { "shp", "csv", "dbf" },
		// { "Vector files (*.shp, *.csv, *.dbf)" },
		// { "tif", "tiff", "asc" },
		// { "Raster Files (*.tif, *.tiff, *.asc)" },
		// { "shp" },
		// { "SHP Files (*.shp)" },
		// { "cir" },
		// { "CIR Files (*.cir)" },
		// { "png" },
		// { "PNG Files (*.png)" },
		// { "shp", "csv", "dbf", "tif", "tiff", "asc", "cir", "png" },
		// { "All supported files (*.shp, *.csv, *.dbf, *.tif, *.tiff, *.asc,
		// *.cir, *.png)" } };
		String[][] supportedDSFiles = { { "sld" }, { "SLD files (*.sld)" },
				{ "shp", "csv", "dbf" },
				{ "Vector files (*.shp, *.csv, *.dbf)" }, };
		ofc = new FileChooser(supportedDSFiles);
		ofc.setControlButtonsAreShown(false);
		ofc.setMultiSelectionEnabled(true);
		add(ofc);
	}

	public IResource[] getNewResources() {
		ArrayList<IResource> ressources = new ArrayList<IResource>();
		File[] files = ofc.getSelectedFiles();

		for (File file : files) {
			String name = file.getName();
			String extension = FileUtility.getFileExtension(file);
			String nickname = name.substring(0, name.indexOf("." + extension));
			DataSourceDefinition def = new FileSourceDefinition(file);

			try {

				if ("sld".equalsIgnoreCase(extension)) {
					ressources.add(new SLDFile(nickname, file.getPath()));

				} else if ("shp".equalsIgnoreCase(extension)
						| "csv".equalsIgnoreCase(extension)
						| "cir".equalsIgnoreCase(extension)) {

					// Check for an already existing DataSource with the name
					// provided
					// and change it if necessary TODO : datasourcefactory
					// should rename
					// by itself datasources and return the name he choosed
					int i = 0;
					String tmpName = name;
					while (OrbisgisCore.getDSF().existDS(tmpName)) {
						i++;
						tmpName = name + "_" + i;
					}
					name = tmpName;

					OrbisgisCore.getDSF().registerDataSource(name, def);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ressources.toArray(new IResource[0]);
	}

	public JPanel getWizardUI() {
		return this;
	}
}

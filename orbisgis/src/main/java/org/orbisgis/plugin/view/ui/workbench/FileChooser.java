package org.orbisgis.plugin.view.ui.workbench;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import org.orbisgis.plugin.view.utilities.file.SimpleFileFilter;

public class FileChooser extends JFileChooser {
	private Component parent;

	public FileChooser(final String extensions, final String description,
			final boolean multiSelectionEnabled) {
		this(new String[] { extensions }, description, multiSelectionEnabled);
	}

	public FileChooser(final String[] extensions, final String description,
			final boolean multiSelectionEnabled) {
		super(new File("../../datas2tests/"));
		// jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		setMultiSelectionEnabled(multiSelectionEnabled);
		addChoosableFileFilter(new SimpleFileFilter(extensions, description));
		setAcceptAllFileFilterUsed(false);
	}
	
	/**
	 * Creates a file chooser with many extensions and descriptions.
	 * 
	 * @param extensions
	 *            format : { {ext1, ext2, ... }, {desc}, {exti, exti+1, ...},
	 *            {desc}...}
	 *            
	 *            Example :
	 *            String[][] supportedDSFiles = {
				{ "shp", "csv", "dbf" },
				{ "Vector files (*.shp, *.csv, *.dbf)" },
				{ "tif", "tiff", "asc" },
				{ "Raster Files (*.tif, *.tiff, *.asc)" },
				{ "shp", "csv", "dbf", "tif", "tiff", "asc" },
				{ "All supported files (*.shp, *.csv, *.dbf, *.tif, *.tiff, *.asc)" } };
	 */
	 
	public FileChooser(String[][] extensions) {
		super(new File("../../datas2tests/"));
		setMultiSelectionEnabled(true);
		for (int i = 0; i< extensions.length; i=i+2) {
			addChoosableFileFilter(new SimpleFileFilter(extensions[i], extensions[i+1][0]));
		}
		setAcceptAllFileFilterUsed(false);
	}

	public File[] selectedFiles() {
		if (JFileChooser.APPROVE_OPTION == showOpenDialog(parent)) {
			return super.getSelectedFiles();
		}
		return new File[0];
	}
}
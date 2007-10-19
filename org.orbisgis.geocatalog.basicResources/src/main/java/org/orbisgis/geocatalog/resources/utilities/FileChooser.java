package org.orbisgis.geocatalog.resources.utilities;

import java.io.File;

import javax.swing.JFileChooser;

/**
 * This class extends a little bit JFileChooser to make it easier to create file
 * filters
 */
public class FileChooser extends JFileChooser {
	/**
	 * Creates a file chooser supporting only one type of file with its
	 * description
	 */
	public FileChooser(final String extensions, final String description) {
		this(new String[][] { { extensions }, { description } });
	}

	public FileChooser(String[][] extensions) {
		this(extensions, null);
	}

	/**
	 * Creates a file chooser with many extensions and their descriptions.
	 *
	 * @param extensions
	 *            format : { {ext1, ext2, ... }, {desc}, {exti, exti+1, ...},
	 *            {desc}...}
	 *
	 * Example : String[][] supportedDSFiles = { { "shp", "csv", "dbf" }, {
	 * "Vector files (*.shp, *.csv, *.dbf)" }, { "tif", "tiff", "asc" }, {
	 * "Raster Files (*.tif, *.tiff, *.asc)" }, { "shp", "csv", "dbf", "tif",
	 * "tiff", "asc" }, { "All supported files (*.shp, *.csv, *.dbf, *.tif,
	 * *.tiff, *.asc)" } };
	 */

	public FileChooser(String[][] extensions, String path) {
		super(new File("../../datas2tests/"));
		for (int i = 0; i < extensions.length; i = i + 2) {
			addChoosableFileFilter(new SimpleFileFilter(extensions[i],
					extensions[i + 1][0]));
		}
		setAcceptAllFileFilterUsed(false);
	}
}
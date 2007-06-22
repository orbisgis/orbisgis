package org.orbisgis.plugin.view.ui.workbench;


import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;


/** The file chooser is used by the assistant to provide it the files the user want to add
 *  It reproduces partially the code from OurFileChooser.java which will be soon deprecated
 * 
 * @author Samuel CHEMLA
 *
 */

public class FileChooser {
		private File[] files = null;
		
		public FileChooser(JFrame jFrame) {
			JFileChooser fc = new JFileChooser();
			//Set the file chooser parameters
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fc.setMultiSelectionEnabled(true);
			fc.addChoosableFileFilter(new ShpFilter());
			fc.setAcceptAllFileFilterUsed(false);
			fc.showOpenDialog(jFrame);
			files=fc.getSelectedFiles();
		}
		
		/** return the list of files selected by the user
		 * 
		 * @return list of files selected by the user
		 */
		public File[] getFiles() {
			return files;
		}
	}
	
	class ShpFilter extends FileFilter {
		// Accept all directories and all shp files.
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			final String extension = Utils.getExtension(f);
			if (extension != null) {
				if (extension.equals(Utils.shp)) {
					return true;
				} else {
					return false;
				}
			}

			return false;
		}

		// The description of this filter
		public String getDescription() {
			return "Just shapefiles";
		}
	}
	
	class Utils {
		public final static String shp = "shp";

		/*
		 * Get the extension of a file.
		 */
		public static String getExtension(final File file) {
			String ext = null;
			final String fileName = file.getName();
			final int i = fileName.lastIndexOf('.');

			if ((i > 0) && (i < fileName.length() - 1)) {
				ext = fileName.substring(i + 1).toLowerCase();
			}
			return ext;
		}
	}
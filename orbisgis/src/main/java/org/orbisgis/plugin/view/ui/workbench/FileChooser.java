package org.orbisgis.plugin.view.ui.workbench;


import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.orbisgis.plugin.view.utilities.file.SimpleFileFilter;


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
			/** TODO optimize the following...*/
			fc.addChoosableFileFilter(new SimpleFileFilter("shp","ShapeFiles (*.shp)"));
			fc.addChoosableFileFilter(new SimpleFileFilter("csv","CSV Files (*.csv)"));
			fc.addChoosableFileFilter(new SimpleFileFilter("dbf","DBF Files (*.dbf)"));
			fc.addChoosableFileFilter(new SimpleFileFilter(new String[] {"shp","csv","dbf"},"All supported Files (*.shp, *.csv, *.dbf)"));
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
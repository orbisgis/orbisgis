/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer,
Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */

package org.orbisgis.core.sif;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author alexis, jean-yves
 */
public class OpenFolderPanel extends AbstractUIPanel implements SQLUIPanel {

	public static final String FIELD_NAME = "file";

	public static final String FILTER_NAME = "filter";

	private JFileChooser fileChooser;

	private String title;

	private String id;
	
	public OpenFolderPanel(String id, String title) {
		this.id = id;
		this.title = title;
	}

	public void addFilter(String extension, String description) {
		addFilter(new String[] { extension }, description);
	}

	public void addAllFilter(final String description) {
		getFileChooser().addChoosableFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return description;
			}

			@Override
			public boolean accept(File f) {
				return true;
			}

		});
	}

	public void addFilter(String[] extensions, String description) {
		getFileChooser().addChoosableFileFilter(
				new FormatFilter(extensions, description));
	}

	public FileFilter getSelectedFilter(){
		return getFileChooser().getFileFilter();
	}

	/**
	 * This method validates the input selected in the panel. it returns a message
	 * when a problem has been encountered, and null otherwise.
	 * @return
	 */
	@Override
	public String validateInput() {
		File file = getSelectedFile();
		if (file == null) {
			return "A folder must be selected !";
		} else if (!file.exists()) {
			return "The folder must exist !";
		} else if(!file.isDirectory()){
			return "You are supposed to work in a directory !";
		} else {
			return null;
		}
	}

	@Override
	public Component getComponent() {
		return getFileChooser();
	}

	@Override
	public String getId() {
		return id;
	}

	public JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setControlButtonsAreShown(false);
			fileChooser.setMultiSelectionEnabled(true);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		return fileChooser;
	}

	@Override
	public String[] getFieldNames() {
		return new String[] { FIELD_NAME, FILTER_NAME };
	}

	@Override
	public int[] getFieldTypes() {
		return new int[] { SQLUIPanel.STRING, SQLUIPanel.STRING };
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String[] getValues() {
		String ret = "";
		File[] selectedFiles = getSelectedFiles();
		String separator = "";
		for (File file : selectedFiles) {
			ret = ret + separator + file.getAbsolutePath();
			separator = "||";
		}

		return new String[] { ret,
				getFileChooser().getFileFilter().getDescription() };
	}

	@Override
	public void setValue(String fieldName, String fieldValue) {
		if (fieldName.equals(FIELD_NAME)) {
			String[] files = fieldValue.split("\\Q||\\E");
			File[] selectedFiles = new File[files.length];
			for (int i = 0; i < selectedFiles.length; i++) {
				selectedFiles[i] = new File(files[i]);
			}
			fileChooser.setSelectedFiles(selectedFiles);
		} else {
			FileFilter[] filters = fileChooser.getChoosableFileFilters();
			for (FileFilter fileFilter : filters) {
				if (fieldValue.equals(fileFilter.getDescription())) {
					fileChooser.setFileFilter(fileFilter);
				}
			}
		}
	}

	public File getSelectedFile() {
		return fileChooser.getSelectedFile();
	}

	public File[] getSelectedFiles() {
		if (fileChooser.isMultiSelectionEnabled()) {
			return fileChooser.getSelectedFiles();
		} else {
			return new File[] { fileChooser.getSelectedFile() };
		}
	}

	protected final class FormatFilter extends FileFilter {
		private final String[] extensions;
		private String description;

		private FormatFilter(String[] extensions, String description) {
			this.extensions = extensions;
			this.description = description + " (";
			String separator = "";
			for (String extension : extensions) {
				this.description += separator + "*." + extension;
				separator = ",";
			}
			this.description += ")";
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public boolean accept(File f) {
			if (f == null) {
				return true;
			} else {
				for (String extension : extensions) {
					if (f.getAbsolutePath().toLowerCase().endsWith(
							"." + extension.toLowerCase())
							|| f.isDirectory()) {
						return true;
					}
				}
				return false;
			}
		}

		public File autoComplete(File selectedFile) {
			if (selectedFile.isDirectory()) {
				return null;
			} else {
				if (!selectedFile.isAbsolute()) {
					selectedFile = new File(fileChooser.getCurrentDirectory()
							+ File.separator + selectedFile.getName());
				}
				if (accept(selectedFile)) {
					return selectedFile;
				} else {
					return new File(selectedFile.getAbsolutePath() + "."
							+ extensions[0]);
				}
			}
		}
	}

	@Override
	public String[] getErrorMessages() {
		return null;
	}

	@Override
	public String[] getValidationExpressions() {
		return null;
	}

	public void setSelectedFile(File file) {
		fileChooser.setSelectedFile(file);
	}

	public void setCurrentDirectory(File dir) {
		fileChooser.setCurrentDirectory(dir);
	}


}

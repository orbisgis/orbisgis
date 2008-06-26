/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
/**
 *
 */
package org.orbisgis.pluginManager.ui;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.sif.AbstractUIPanel;
import org.sif.SQLUIPanel;

public class OpenFilePanel extends AbstractUIPanel implements SQLUIPanel {

	public static final String FIELD_NAME = "file";

	public static final String FILTER_NAME = "filter";

	private JFileChooser fileChooser;

	private String title;

	private String id;

	public OpenFilePanel(String id, String title) {
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

	public String validateInput() {
		File file = getSelectedFile();
		if (file == null) {
			return "A file must be selected";
		} else if (!file.exists()) {
			return "The file must exist";
		} else {
			return null;
		}
	}

	public Component getComponent() {
		return getFileChooser();
	}

	public String getId() {
		return id;
	}

	protected JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setControlButtonsAreShown(false);
			fileChooser.setMultiSelectionEnabled(true);
		}
		return fileChooser;
	}

	public String[] getFieldNames() {
		return new String[] { FIELD_NAME, FILTER_NAME };
	}

	public int[] getFieldTypes() {
		return new int[] { SQLUIPanel.STRING, SQLUIPanel.STRING };
	}

	public String getTitle() {
		return title;
	}

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
			this.description = description;
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

	public String[] getErrorMessages() {
		return null;
	}

	public String[] getValidationExpressions() {
		return null;
	}

}
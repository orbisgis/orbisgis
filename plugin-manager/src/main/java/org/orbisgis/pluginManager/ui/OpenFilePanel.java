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
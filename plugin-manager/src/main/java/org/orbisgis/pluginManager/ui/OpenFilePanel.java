/**
 *
 */
package org.orbisgis.pluginManager.ui;

import java.awt.Component;
import java.io.File;
import java.net.URL;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.sif.SQLUIPanel;

public abstract class OpenFilePanel implements SQLUIPanel {

	public static final String FIELD_NAME = "file";

	public static final String FILTER_NAME = "filter";

	private JFileChooser fileChooser;

	private String title;

	private Map<String, String> formatAndDescription;

	public OpenFilePanel(String title) {
		this.title = title;
	}

	public OpenFilePanel(String title, Map<String, String> formatAndDescription) {
		this(title);
		this.formatAndDescription = formatAndDescription;
		if (formatAndDescription != null) {
			for (final String key : formatAndDescription.keySet()) {
				getFileChooser().addChoosableFileFilter(new FormatFilter(key));
			}

		}
	}

	public String validate() {
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

	protected JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setControlButtonsAreShown(false);
			fileChooser.setMultiSelectionEnabled(true);
		}
		return fileChooser;
	}

	public URL getIconURL() {
		return null;
	}

	public void initialize() {

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
		return fileChooser.getSelectedFiles();
	}

	protected final class FormatFilter extends FileFilter {
		private final String key;

		private FormatFilter(String key) {
			this.key = key;
		}

		@Override
		public String getDescription() {
			return formatAndDescription.get(key);
		}

		@Override
		public boolean accept(File f) {
			if (f == null) {
				return true;
			} else {
				return f.getAbsolutePath().endsWith("." + key)
						|| f.isDirectory();
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
					return new File(selectedFile.getAbsolutePath() + "." + key);
				}
			}
		}
	}

}
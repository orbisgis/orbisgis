/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.sif;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Contains utility methods by both OpenFilePanel and OpenFolderPanel.
 * @author alexis
 */
public abstract class AbstractOpenPanel extends AbstractUIPanel implements SQLUIPanel {

	private JFileChooser fileChooser;

	private String title;

	private String id;

	public AbstractOpenPanel(String id, String title) {
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

	@Override
	public Component getComponent() {
		return getFileChooser();
	}

	@Override
	public String getId() {
		return id;
	}

	/**
	 * This method returns the FileChooser attached to this. It creates a new one
	 * if the FileChooser has not been instanciated before.
	 * @return
	 */
	public JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setControlButtonsAreShown(false);
			fileChooser.setMultiSelectionEnabled(true);
			if(showFoldersOnly()){
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			}
		}
		return fileChooser;
	}

	/**
	 * To be set by inheriting classes. True if you want to show the folders only.
	 * @return
	 */
	public abstract boolean showFoldersOnly();

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

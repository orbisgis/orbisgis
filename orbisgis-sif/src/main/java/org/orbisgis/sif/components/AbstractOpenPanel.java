/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 * info _at_ orbisgis.org
 */

package org.orbisgis.sif.components;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.orbisgis.sif.AbstractUIPanel;

/**
 * Contains utility methods by both OpenFilePanel and OpenFolderPanel.
 * @author alexis
 */
public abstract class AbstractOpenPanel extends AbstractUIPanel {

	private JFileChooser fileChooser;

	private String title;


	public AbstractOpenPanel(String id, String title) {
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
	public String getTitle() {
		return title;
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

	public void setSelectedFile(File file) {
		fileChooser.setSelectedFile(file);
	}

	public void setCurrentDirectory(File dir) {
		fileChooser.setCurrentDirectory(dir);
	}

        @Override
        public String getInfoText() {
                final String infoText = super.getInfoText();
                return infoText == null ? " " : infoText;
        }


}

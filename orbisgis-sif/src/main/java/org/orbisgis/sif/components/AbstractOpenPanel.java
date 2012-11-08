/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.sif.components;

import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.sif.UIPersistence;

/**
 * Contains utility methods by both OpenFilePanel and OpenFolderPanel.
 * @author Alexis Guéganno
 */
public abstract class AbstractOpenPanel implements UIPanel,UIPersistence {

	private JFileChooser fileChooser;
        private final String id;
	private String title;


        /**
         * Constructor
         * @param id Identifier of this dialog, used to recover the old state of this dialog
         * @param title Localised title of this dialog
         */
	public AbstractOpenPanel(String id, String title) {
                this.id = id;
		this.title = title;
                
	}
        /**
         * Recover the dialog state on the last save state, using the dialog identifier.
         * The following properties may be overwritten
         *  - Current directory
         *  - Current filter
         */
        @Override
        public void loadState() {
                // Load persistence data
                String currentFolder = UIFactory.getFileDialogPersistence().getProperty(id+":folder");
                if(currentFolder!=null) {
                        setCurrentDirectory(new File(currentFolder));
                }
                String currentFilter = UIFactory.getFileDialogPersistence().getProperty(id+":filter");
                if(currentFilter!=null && !setCurrentFilter(Integer.valueOf(currentFilter))) {
                        // If the filter is not found, do not use filter, if allowed
                        if(getFileChooser().isAcceptAllFileFilterUsed()) {
                                getFileChooser().setFileFilter(getFileChooser().getAcceptAllFileFilter());
                        }
                }
        }
        /**
         * Save the current state of the dialog.
         */
        @Override
        public void saveState() {
                UIFactory.getFileDialogPersistence().setProperty(id+":folder",getCurrentDirectory().getAbsolutePath());
                UIFactory.getFileDialogPersistence().setProperty(id+":filter",Integer.toString(getCurrentFilterId()));
        }
        
	public void addFilter(String extension, String description) {
		addFilter(new String[] { extension }, description);
	}

        /**
         * Determines whether the AcceptAll FileFilter is used as an available
         * choice in the choosable filter list. If false, the AcceptAll file
         * filter is removed from the list of available file filters. If true,
         * the AcceptAll file filter will become the the actively used file filter.
         * @param enableAllFiles 
         */
        public void setAcceptAllFileFilterUsed(boolean enableAllFiles) {
                getFileChooser().setAcceptAllFileFilterUsed(enableAllFiles);
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
         * Set the file dialog to allow single selection.
         * @param singleSelection True for single, false for multiple
         */
        public void setSingleSelection(boolean singleSelection) {
		getFileChooser().setMultiSelectionEnabled(!singleSelection);
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
                public int hashCode() {
                        int hash = 5;
                        hash = 71 * hash + Arrays.deepHashCode(this.extensions);
                        return hash;
                }

                @Override
                public boolean equals(Object obj) {
                        if (!(obj instanceof FormatFilter)) {
                                return false;
                        }
                        final FormatFilter other = (FormatFilter) obj;
                        if (!Arrays.deepEquals(this.extensions, other.extensions)) {
                                return false;
                        }
                        return true;
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
        /**
         * Set the selected file in the directory
         * @param file 
         */
	public void setSelectedFile(File file) {
		fileChooser.setSelectedFile(file);
	}

        /**
         * Set the directory shown to the user
         * @param dir 
         */
	public void setCurrentDirectory(File dir) {
		fileChooser.setCurrentDirectory(dir);
	}

        /**
         * Get the directory browsed by the user
         */
        public File getCurrentDirectory() {
                return fileChooser.getCurrentDirectory();
        }
        
        /**
         * Return the identifier of the current filter
         * @return The filter identifier, given by FileFilter.hashCode()
         * @see setCurrentFilter
         */
        public int getCurrentFilterId() {
                return fileChooser.getFileFilter().hashCode();
        }
        /**
         * Set the selected file filter
         * @return True if the filter has been found and set
         * @see getCurrentFilterId
         */
        public boolean setCurrentFilter(int filterIdentifier) {
                for(FileFilter filter : fileChooser.getChoosableFileFilters()) {
                        if(filter.hashCode()==filterIdentifier) {
                                fileChooser.setFileFilter(filter);
                                return true;
                        }
                }
                return false;
        }
}

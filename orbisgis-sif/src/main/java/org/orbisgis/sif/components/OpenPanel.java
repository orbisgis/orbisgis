/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

import org.orbisgis.sif.UIFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import java.io.File;
import java.net.URL;

/**
 * This class handles the panel used to import or export a file or a directory.
 *
 * @author Alexis Guéganno
 * @author Jean-Yves Martin
 * @author Sylvain PALOMINOS
 */
public class OpenPanel extends AbstractOpenPanel {

    private static final I18n I18N = I18nFactory.getI18n(SaveFilePanel.class);

    /** Default size of the confirm overwrite message. */
    private int messageWidth = 300;

    /** Static names. */
    public static final String FIELD_NAME = "folder";
    public static final String FILTER_NAME = "filter";

    /** Action type done by the panel (Open or save).*/
    public static final String ACTION_OPEN = "ACTION_OPEN";
    public static final String ACTION_SAVE = "ACTION_SAVE";

    /** Data accepted (file, directory or both). */
    public static final String ACCEPT_FILE = "ACCEPT_FILE";
    public static final String ACCEPT_DIRECTORY = "ACCEPT_DIRECTORY";
    public static final String ACCEPT_BOTH = "ACCEPT_BOTH";

    private String action;
    private String dataAccepted;
    private boolean fileMustNotExist;
    private boolean confirmOverwrite;

    /**
     * Simplest constructor which just need the panel unique id and the panel title.
     * @param id Unique identifier of the panel.
     * @param title Human readable title of the panel.
     * @param action Action done by the panel. Can be ACTION_OPEN or ACTION_SAVE.
     * @param dataAccepted Data accepted by the OpenPanel. Can be ACCEPT_FILE, ACCEPT_DIRECTORY or ACCEPT_BOTH.
     */
    public OpenPanel(String id, String title, String action, String dataAccepted) {
        super(id, title);
        this.action = action;
        this.dataAccepted = dataAccepted;

        JFileChooser fileChooser = getFileChooser();
        if(action.equals(ACTION_OPEN)){
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            setAcceptAllFileFilterUsed(true);
        }
        else if(action.equals(ACTION_SAVE)){
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            if(dataAccepted.equals(ACCEPT_FILE)) {
                setAcceptAllFileFilterUsed(false);
            }
            else{
                setAcceptAllFileFilterUsed(true);
            }
            confirmOverwrite = true;
        }
        //Sets the type of selection the file chooser accepts.
        if(dataAccepted.equals(ACCEPT_BOTH)) {
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }
        else if(dataAccepted.equals(ACCEPT_FILE)) {
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
        else if(dataAccepted.equals(ACCEPT_DIRECTORY)) {
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
    }

    /**
     * Returns the selected file filter.
     * @return The selected file filter.
     */
    public FileFilter getSelectedFilter() {
        return getFileChooser().getFileFilter();
    }

    /**
     * This method validates the input selected in the panel. it returns a
     * message when a problem has been encountered, and null otherwise.
     *
     * @return
     */
    @Override
    public String validateInput() {
        File file = getSelectedFile();
        //If nothing is selected, warn the user
        if(file == null) {
            if(dataAccepted.equals(ACCEPT_BOTH)){
                return I18N.tr("A file or a directory must be selected.");
            }
            else if(dataAccepted.equals(ACCEPT_FILE)){
                return I18N.tr("A file must be selected.");
            }
            else if(dataAccepted.equals(ACCEPT_DIRECTORY)){
                return I18N.tr("A directory must be selected.");
            }
        }
        //If the user select a file instead of a directory, warn him
        else if(dataAccepted.equals(ACCEPT_DIRECTORY) && file.isFile()){
            return I18N.tr("A directory must be selected.");
        }
        //If the user select a directory instead of a file, warn him
        else if(dataAccepted.equals(ACCEPT_FILE) && file.isDirectory()){
            return I18N.tr("A file must be selected.");
        }
        //If the selected file does not exists and the action to do is OPEN, warn the user
        else if(!file.exists() && action.equals(ACTION_OPEN)) {
            if(dataAccepted.equals(ACCEPT_BOTH)){
                return I18N.tr("The file or the directory must exists.");
            }
            else if(dataAccepted.equals(ACCEPT_FILE)){
                return I18N.tr("The file must exists.");
            }
            else if(dataAccepted.equals(ACCEPT_DIRECTORY)){
                return I18N.tr("The directory must exists.");
            }
        }
        //If the user save on an existing file but it is not allowed, warn the user
        else if(file.exists() && fileMustNotExist && action.equals(ACTION_SAVE)){
            if(dataAccepted.equals(ACCEPT_BOTH)){
                return I18N.tr("The file or the directory must not exists.");
            }
            else if(dataAccepted.equals(ACCEPT_FILE)){
                return I18N.tr("The file must not exists.");
            }
            else if(dataAccepted.equals(ACCEPT_DIRECTORY)){
                return I18N.tr("The directory must not exists.");
            }
        }
        //If the save a file that exists but the action should be confirmed
        else if(file.isFile() && file.exists() && confirmOverwrite){
            if (JOptionPane.showConfirmDialog(
                    getComponent(),
                    "<html><body><p style='width: " + messageWidth + "px;'>"
                            + I18N.tr("The file {0} already exists. Overwrite?"
                            + "</p></body></html>", getSelectedFile()),
                    I18N.tr("Confirm overwrite"),
                    JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return I18N.tr("Overwrite canceled");
            }
        }
        return null;
    }

    @Override
    public File getSelectedFile() {
        JFileChooser fileChooser = getFileChooser();
        File selectedFile = fileChooser.getSelectedFile();
        //If the action done is save and nothing is selection, complete if needed the file name with the extension.
        if(action.equals(ACTION_SAVE) && (selectedFile == null || selectedFile.isFile())){
            FileChooserUI ui = fileChooser.getUI();
            if (ui instanceof BasicFileChooserUI) {
                BasicFileChooserUI basicUI = (BasicFileChooserUI) ui;
                String fileName = basicUI.getFileName();
                if ((fileName == null) || (fileName.length() == 0)) {
                    selectedFile = null;
                } else {
                    selectedFile = autoComplete(new File(fileName));
                }
            } else {
                selectedFile = autoComplete(super.getSelectedFile());
            }
            if ((selectedFile != null) && !selectedFile.isAbsolute()) {
                selectedFile = new File(fileChooser.getCurrentDirectory(), selectedFile.getName());
            }
            return selectedFile;
        }
        //If the action done is open and nothing is selected, return the actual folder name
        else if(action.equals(ACTION_OPEN) &&
                dataAccepted.equals(ACCEPT_DIRECTORY) ||
                dataAccepted.equals(ACCEPT_BOTH)) {

            if (selectedFile == null) {
                return fileChooser.getCurrentDirectory();
            }
        }
        return selectedFile;
    }

    private File autoComplete(File selectedFile) {
        FileFilter ff = getFileChooser().getFileFilter();
        if (ff instanceof FormatFilter) {
            FormatFilter filter = (FormatFilter) ff;
            return filter.autoComplete(selectedFile);
        } else {
            return selectedFile;
        }
    }

    @Override
    public boolean showFoldersOnly() {
        return dataAccepted.equals(ACCEPT_DIRECTORY);
    }

    /**
     * Return the names of the fields in the FileChooser.
     *
     * @return
     */
    public String[] getFieldNames() {
        return new String[]{FIELD_NAME, FILTER_NAME};
    }

    public void setValue(String fieldName, String fieldValue) {
        if (fieldName.equals(FIELD_NAME)) {
            String[] files = fieldValue.split("\\Q||\\E");
            File[] selectedFiles = new File[files.length];
            for (int i = 0; i < selectedFiles.length; i++) {
                selectedFiles[i] = new File(files[i]);
            }
            getFileChooser().setSelectedFiles(selectedFiles);
        } else {
            FileFilter[] filters = getFileChooser().getChoosableFileFilters();
            for (FileFilter fileFilter : filters) {
                if (fieldValue.equals(fileFilter.getDescription())) {
                    getFileChooser().setFileFilter(fileFilter);
                }
            }
        }
    }

    @Override
    public URL getIconURL() {
        return UIFactory.getDefaultIcon();
    }

    /**
     * Sets if the selected file or directory to save must not exists (to avoid erasing).
     * @param fileMustNotExist True if the selected file or directory must exist or not.
     */
    public void setFileMustNotExist(boolean fileMustNotExist) {
        this.fileMustNotExist = fileMustNotExist;
    }


    /**
     * Tells if this dialog should ask a confirmation for overwriting to the user or not.
     * @return True if this dialog will ask a confirmation for overwriting file, false otherwise.
     */
    public boolean isConfirmOverwrite() {
        return confirmOverwrite;
    }

    /**
     * Sets if this dialog should ask a confirmation for overwriting to the user or not.
     * @param confirmOverwrite True if this dialog will ask a confirmation for overwriting file, false otherwise.
     */
    public void setConfirmOverwrite(boolean confirmOverwrite) {
        this.confirmOverwrite = confirmOverwrite;
    }
}

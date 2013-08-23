/*
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
package org.orbisgis.view.workspace;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.sif.multiInputPanel.DirectoryComboBoxChoice;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * GUI for Workspace selection.
 *
 * @author Nicolas Fortin
 * @author Adam Gouge
 */
public class WorkspaceSelectionDialog {

    private static final I18n I18N = I18nFactory.getI18n(WorkspaceSelectionDialog.class);
    private static final Logger LOGGER = Logger.getLogger(WorkspaceSelectionDialog.class);

    /**
     * Shows a dialog to choose the workspace folder
     *
     * @param parent        Parent component
     * @param coreWorkspace Core workspace
     *
     * @return The user-selected workspace folder
     */
    public static File showWorkspaceFolderSelection(Component parent,
                                                    CoreWorkspace coreWorkspace) {

        // Get the list of known workspaces
        List<File> knownWorkspaces = coreWorkspace.readKnownWorkspacesPath();

        // Remove the currently loaded workspace from the list
        String currentWorkspacePath = coreWorkspace.getWorkspaceFolder();
        if (currentWorkspacePath != null && !currentWorkspacePath.isEmpty()) {
            knownWorkspaces.remove(new File(currentWorkspacePath));
        }

        // Initialize a panel to contain the dialog
        JPanel panel = new JPanel(new MigLayout("wrap 1"));

        // Initialize components
        JLabel chooseLabel = new JLabel(I18N.tr("Choose the workspace folder"));
        String defaultFontName = chooseLabel.getFont().getName();
        Font largeFont = new Font(defaultFontName, Font.BOLD, 16);
        Font smallFont = new Font(defaultFontName, Font.PLAIN, 10);
        chooseLabel.setFont(largeFont);
        JLabel subChooseLabel = new JLabel(
                I18N.tr("Choose a previous OrbisGIS workspace or create a new one"));
        subChooseLabel.setFont(smallFont);
        DirectoryComboBoxChoice comboBox =
                new DirectoryComboBoxChoice(knownWorkspaces);
        if (!knownWorkspaces.isEmpty()) {
            // Select the default workspace on the combo box
            comboBox.setValue(knownWorkspaces.get(0).getAbsolutePath());
        }
        JCheckBox defaultCheckBox = new JCheckBox(I18N.tr("Set as default?"));
        JLabel subCheckBox = new JLabel("<html>" +
                I18N.tr("Setting this workspace as default will allow you to " +
                        "skip this<br>dialog next time") + "</html>");
        subCheckBox.setFont(smallFont);

        // Add components
        panel.add(chooseLabel);
        panel.add(subChooseLabel);
        panel.add(comboBox.getComponent());
        panel.add(Box.createGlue());
        panel.add(defaultCheckBox);
        panel.add(subCheckBox);

        // Show the dialog and get the user's choice.
        int userChoice = JOptionPane.showConfirmDialog(parent,
                panel,
                I18N.tr("Workspace Manager"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                OrbisGISIcon.getIcon("sidebar"));

        // If the user clicked OK, then update the workspace.
        if (userChoice == JOptionPane.OK_OPTION) {
            String chosenWorkspacePath = comboBox.getValue();
            validate(chosenWorkspacePath);
            try {
                updateWorkspace(coreWorkspace, comboBox, currentWorkspacePath,
                        chosenWorkspacePath, defaultCheckBox.isSelected());
            } catch (IOException ex) {
                LOGGER.error(I18N.tr("Problem updating the workspace. ")
                        + ex.getLocalizedMessage(), ex);
                return null;
            }
            return new File(chosenWorkspacePath);
        }

        return null;
    }

    /**
     * Makes sure the chosen workspace is valid.
     *
     * @param chosenWorkspacePath Workspace path
     */
    private static void validate(String chosenWorkspacePath) {
        if (!ViewWorkspace.isWorkspaceValid(new File(chosenWorkspacePath))) {
            LOGGER.error(I18N.tr("The workspace folder version is invalid " +
                    "(!=OrbisGIS {0}), or the folder is not empty",
                    CoreWorkspace.MAJOR_VERSION));
        }
    }

    /**
     * Update and/or initialize the user-selected workspace as necessary.
     *
     *
     * @param coreWorkspace       Core workspace
     * @param comboBox            ComboBox with possible workspace directories
     * @param oldWorkspacePath    Path of the previous workspace
     * @param chosenWorkspacePath Path of the workspace the user chose
     * @param isDefault           True if the user set the workspace as default
     * @throws IOException during certain workspace operations.
     */
    private static void updateWorkspace(CoreWorkspace coreWorkspace,
                                        DirectoryComboBoxChoice comboBox,
                                        String oldWorkspacePath,
                                        String chosenWorkspacePath,
                                        boolean isDefault) throws IOException {
        // Set as default workspace if necessary
        if (isDefault) {
            coreWorkspace.setDefaultWorkspace(new File(chosenWorkspacePath));
        } else {
            coreWorkspace.setDefaultWorkspace(null);
        }
        // Save the workspace list, including the previous one
        List<File> workspaces = comboBox.getValues();
        if (oldWorkspacePath != null && !oldWorkspacePath.isEmpty()) {
            workspaces.add(new File(oldWorkspacePath));
        }
        coreWorkspace.writeKnownWorkspaces(workspaces);
        // Initialize the workspace if empty or new
        File wkFile = new File(chosenWorkspacePath);
        File[] files = wkFile.listFiles();
        if (!wkFile.exists() || (files != null && files.length == 0)) {
            ViewWorkspace.initWorkspaceFolder(wkFile);
        }
    }
}

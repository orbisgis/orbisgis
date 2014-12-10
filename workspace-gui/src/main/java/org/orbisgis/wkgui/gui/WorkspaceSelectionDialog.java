/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.wkgui.gui;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.framework.CoreWorkspaceImpl;
import org.orbisgis.sif.multiInputPanel.DirectoryComboBoxChoice;
import org.orbisgis.wkgui.icons.WKIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.orbisgis.sif.components.CustomButton;

/**
 * GUI for Workspace selection.
 *
 * @author Nicolas Fortin
 * @author Adam Gouge
 */
public class WorkspaceSelectionDialog extends JPanel {

    private static final I18n I18N = I18nFactory.getI18n(WorkspaceSelectionDialog.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceSelectionDialog.class);
    private DirectoryComboBoxChoice comboBox;
    private JCheckBox defaultCheckBox;
    private CoreWorkspaceImpl selectedWorkspace = new CoreWorkspaceImpl();
    private JLabel errorLabel = new JLabel();

    private WorkspaceSelectionDialog(int major) {
        super(new MigLayout("wrap 1"));
    }

    private void init(Component parent,
                      CoreWorkspaceImpl coreWorkspace) {
        // Get the list of known workspaces
        List<File> knownWorkspaces = coreWorkspace.readKnownWorkspacesPath();

        // Remove the currently loaded workspace from the list
        String currentWorkspacePath = coreWorkspace.getWorkspaceFolder();
        if (currentWorkspacePath != null && !currentWorkspacePath.isEmpty()) {
            knownWorkspaces.remove(new File(currentWorkspacePath));
        }
        // Initialize components
        JLabel chooseLabel = new JLabel(I18N.tr("Choose the workspace folder"));
        String defaultFontName = chooseLabel.getFont().getName();
        Font largeFont = new Font(defaultFontName, Font.BOLD, 16);
        Font smallFont = new Font(defaultFontName, Font.PLAIN, 10);
        chooseLabel.setFont(largeFont);
        JLabel subChooseLabel = new JLabel(
                "<html><body><p style='width: 200px;'>" +
                        I18N.tr("Choose a previous OrbisGIS workspace or create a new one")
                        + "</p></body></html>");
        subChooseLabel.setFont(smallFont);
        comboBox = new DirectoryComboBoxChoice(knownWorkspaces);
        if (!knownWorkspaces.isEmpty()) {
            // Select the default workspace on the combo box
            comboBox.setValue(knownWorkspaces.get(0).getAbsolutePath());
        }
        ActionListener selectionDone = EventHandler.create(ActionListener.class, this, "onWorkspaceFolderChange");
        comboBox.getComboBox().addActionListener(selectionDone);
        defaultCheckBox = new JCheckBox(I18N.tr("Set as default?"));
        JLabel subCheckBox = new JLabel("<html><body><p style='width: 200px;'>" +
                I18N.tr("Setting this workspace as default will allow you to " +
                        "skip this dialog next time") + "</p></body></html>");
        JButton deleteButton = new CustomButton(WKIcon.getIcon("remove"));
        deleteButton.addActionListener(EventHandler.create(ActionListener.class, this, "onDeleteWorkspaceEntry"));
        subCheckBox.setFont(smallFont);
        // Add components
        errorLabel.setForeground(Color.RED.darker());
        add(errorLabel);
        add(chooseLabel);
        add(subChooseLabel);
        add(comboBox.getComponent(), "split 2");
        add(deleteButton, "gapleft 0");
        add(Box.createGlue());
        add(defaultCheckBox);
        add(subCheckBox);
        CustomButton customDataBase = new CustomButton(WKIcon.getIcon("database"));
        customDataBase.setText(I18N.tr("Customize your database"));
        customDataBase.setToolTipText(I18N.tr("Click to customize your database."));
        customDataBase.addActionListener(
                EventHandler.create(ActionListener.class, this, "onOpenDBPanel"));
        add(customDataBase);
        onWorkspaceFolderChange();
    }

    /**
     * User click on delete button.
     */
    public void onDeleteWorkspaceEntry() {
        JComboBox combo = comboBox.getComboBox();
        if(combo.getItemCount() != 0 && combo.getSelectedIndex() != -1) {
            combo.removeItemAt(combo.getSelectedIndex());
        }
    }
   
    /**
     * The user click on add open button
     */
    public void onOpenDBPanel() {
        DatabaseSettingsPanel databaseSettingsPanel = new DatabaseSettingsPanel((JDialog) getTopLevelAncestor());
        databaseSettingsPanel.setConnectionName(new File(selectedWorkspace.getWorkspaceFolder()).getName());
        databaseSettingsPanel.setUser(selectedWorkspace.getDataBaseUser());
        databaseSettingsPanel.setURL(selectedWorkspace.getJDBCConnectionReference());
        databaseSettingsPanel.setHasPassword(selectedWorkspace.isRequirePassword());
        databaseSettingsPanel.setAlwaysOnTop(true);
        databaseSettingsPanel.setModal(true);
        databaseSettingsPanel.setVisible(true);
        if(!databaseSettingsPanel.isCanceled()) {
            // Read selected attributes
            selectedWorkspace.setDataBaseUser(databaseSettingsPanel.getUser());
            selectedWorkspace.setRequirePassword(databaseSettingsPanel.hasPassword());
            selectedWorkspace.setJDBCConnectionReference(databaseSettingsPanel.getJdbcURI());
        }
    }

    /**
     * @return The workspace selection combo box
     */
    public DirectoryComboBoxChoice getComboBox() {
        return comboBox;
    }

    /**
     * @return The default workspace check box
     */
    public JCheckBox getDefaultCheckBox() {
        return defaultCheckBox;
    }    

    /**
     * Shows a dialog to choose the workspace folder
     *
     * @param parent        Parent component
     * @param coreWorkspace Core workspace
     *
     * @return True if the user validate workspace change
     */
    public static boolean showWorkspaceFolderSelection(Component parent,
                                                    CoreWorkspaceImpl coreWorkspace) {
        if(!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Not on swing dispatch thread");
        }
        String oldWorkspace = coreWorkspace.getWorkspaceFolder();
        // Initialize a panel to contain the dialog
        WorkspaceSelectionDialog panel = new WorkspaceSelectionDialog();
        panel.init(parent, coreWorkspace);
        // Show the dialog and get the user's choice.
        int userChoice = JOptionPane.showConfirmDialog(parent,
                panel,
                I18N.tr("Workspace Manager"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                WKIcon.getIcon("sidebar"));

        // If the user clicked OK, then update the workspace.
        if (userChoice == JOptionPane.OK_OPTION) {
            String chosenWorkspacePath = panel.getComboBox().getValue();
            if (!ViewWorkspaceImpl.isWorkspaceValid(new File(chosenWorkspacePath))) {
                LOGGER.error(I18N.tr("The workspace folder version is invalid " +
                        "(!=OrbisGIS {0}), or the folder is not empty",
                        CoreWorkspaceImpl.MAJOR_VERSION));
                return false;
            }
            try {
                if(panel.selectedWorkspace.isRequirePassword()) {
                    //The user must input the password
                    JPanel passwordPanel = new JPanel(new BorderLayout());
                    JPasswordField pass = new JPasswordField(10);
                    JLabel message = new JLabel(I18N.tr("<html>{0}<br>DataBase password for {1}:</html>",
                            panel.selectedWorkspace.getJDBCConnectionReference(),
                            panel.selectedWorkspace.getDataBaseUser()));
                    passwordPanel.add(pass, BorderLayout.CENTER);
                    passwordPanel.add(message, BorderLayout.NORTH);
                    if(JOptionPane.showConfirmDialog(panel, passwordPanel,
                            I18N.tr("Enter database password"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                        coreWorkspace.setDataBasePassword(new String(pass.getPassword()));
                    } else {
                        return false;
                    }
                }
                updateWorkspace(coreWorkspace, panel.getComboBox(), oldWorkspace,
                        panel);
            } catch (IOException ex) {
                LOGGER.error(I18N.tr("Problem updating the workspace. ")
                        + ex.getLocalizedMessage(), ex);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * The user select another workspace folder.Update the JDBC uri
     */
    public void onWorkspaceFolderChange() {
        // Check if workspace folder is valid
        if(ViewWorkspaceImpl.isWorkspaceValid(new File(comboBox.getValue()))) {
            selectedWorkspace.setWorkspaceFolder(comboBox.getValue());
            errorLabel.setText("");
        } else {
            errorLabel.setText(I18N.tr("The selected folder is not a valid workspace"));
        }
    }

    /**
     * Called when the user change/set the workspace folder of OrbisGIS.
     * @param coreWorkspace       Core workspace
     * @param comboBox            ComboBox with possible workspace directories
     * @param oldWorkspacePath    Path of the previous workspace
     * @param wkDialog            Workspace panel
     * @throws IOException during certain workspace operations.
     */
    private static void updateWorkspace(CoreWorkspaceImpl coreWorkspace,
                                        DirectoryComboBoxChoice comboBox,
                                        String oldWorkspacePath,WorkspaceSelectionDialog wkDialog) throws IOException {
        // Set as default workspace if necessary
        if (wkDialog.getDefaultCheckBox().isSelected()) {
            coreWorkspace.setDefaultWorkspace(new File(wkDialog.getComboBox().getValue()));
        } else {
            coreWorkspace.setDefaultWorkspace(null);
        }
        // Save the workspace list, including the previous one
        List<File> workspaces = comboBox.getValues();
        File wkFile = new File(wkDialog.getComboBox().getValue());
        workspaces.remove(wkFile);            
        if (oldWorkspacePath != null && !oldWorkspacePath.isEmpty()) {
            workspaces.add(new File(oldWorkspacePath));
        }
        workspaces.add(0, wkFile);
        coreWorkspace.writeKnownWorkspaces(workspaces);
        // Initialize the workspace if empty or new
       
        File[] files = wkFile.listFiles();
        if (!wkFile.exists() || (files != null && files.length == 0)) {
            ViewWorkspaceImpl.initWorkspaceFolder(wkFile);
        }
        // Write chosen jdbc attributes
        wkDialog.selectedWorkspace.writeUriFile();
        // Do this at the end because there is trigger on property change
        coreWorkspace.setWorkspaceFolder(wkDialog.getComboBox().getValue());
    }
}

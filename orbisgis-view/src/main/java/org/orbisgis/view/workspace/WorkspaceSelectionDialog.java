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

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.CheckBoxChoice;
import org.orbisgis.sif.multiInputPanel.DirectoryComboBoxChoice;
import org.orbisgis.sif.multiInputPanel.MIPValidation;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * GUI for Workspace selection.
 * @author Nicolas Fortin
 */
public class WorkspaceSelectionDialog implements MIPValidation {
        private static final I18n I18N = I18nFactory.getI18n(WorkspaceSelectionDialog.class);
        private static final Logger LOGGER = Logger.getLogger(WorkspaceSelectionDialog.class);
        private static final String FOLDER_COMBO_FIELD = "foldersCombo";
        private static final String DEFAULT_WORKSPACE_FIELD = "default workspace";
                
        /**
         * Show a dialog to choose the workspace folder
         * @return The user selected workspace folder
         */
        public static File showWorkspaceFolderSelection(CoreWorkspace coreWorkspace, boolean showCancelButton) {                
                String defaultWorkspace = coreWorkspace.getWorkspaceFolder();
                MultiInputPanel panel = new MultiInputPanel(I18N.tr("Workspace folder"));
                panel.addValidation(new WorkspaceSelectionDialog());
                DirectoryComboBoxChoice comboDir = new DirectoryComboBoxChoice(coreWorkspace.readKnownWorkspacesPath(),
                        I18N.tr("Select an existing workspace or an empty folder."));
                panel.addInput(FOLDER_COMBO_FIELD, I18N.tr("Workspace folder"),comboDir );
                panel.addInput(DEFAULT_WORKSPACE_FIELD, I18N.tr("Default workspace"),
                        new CheckBoxChoice(defaultWorkspace!=null));
                // Select default workspace on the combo box
                if(defaultWorkspace!=null) {
                        comboDir.setValue(defaultWorkspace);
                }
                if(UIFactory.showDialog(panel, showCancelButton, true)) {
                        String workspacePath = panel.getInput(FOLDER_COMBO_FIELD);
                        boolean isDefault = Boolean.valueOf(panel.getInput(DEFAULT_WORKSPACE_FIELD));
                        try {
                                if(isDefault) {
                                        coreWorkspace.setDefaultWorkspace(new File(workspacePath));
                                } else {
                                        coreWorkspace.setDefaultWorkspace(null);
                                }
                                // Save workspace list
                                List<File> workspaces = comboDir.getValues();
                                coreWorkspace.writeKnownWorkspaces(workspaces);
                                // Initialize the workspace if empty or not exists
                                File wkFile = new File(workspacePath);
                                if(!wkFile.exists() || wkFile.listFiles().length==0) {
                                        ViewWorkspace.initWorkspaceFolder(wkFile);
                                }
                        } catch (IOException ex) {
                                LOGGER.error(ex.getLocalizedMessage(),ex);
                        }
                        return new File(workspacePath);
                }
                return null;
        }
        /**
         * Validation of user input
         * @param mid
         * @return 
         */
        @Override
        public String validate(MultiInputPanel mid) {
                String workspacePath = mid.getInput(FOLDER_COMBO_FIELD);
                if(!ViewWorkspace.isWorkspaceValid(new File(workspacePath))) {
                        return I18N.tr("The workspace folder version is invalid (!=OrbisGIS {0}), or the folder is not empty",CoreWorkspace.MAJOR_VERSION);
                } else {
                        return null;
                }
        }
        
}

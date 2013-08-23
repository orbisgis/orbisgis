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
package org.orbisgis.sif.multiInputPanel;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.sif.components.CustomButton;
import org.orbisgis.sif.components.OpenFolderPanel;
import org.orbisgis.sif.icons.SifIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A combo box of folder items with a folder dialog
 * @author Nicolas Fortin
 */
public class DirectoryComboBoxChoice extends ComboBoxChoice {
        private JPanel mainComponent = new JPanel(new MigLayout());
        private JButton browseFolders;
        private static final I18n I18N = I18nFactory.getI18n(DirectoryComboBoxChoice.class);
        private ActionListener browseButtonAction = EventHandler.create(ActionListener.class, this, "onBrowseFolders");
        /**
         * 
         * @param folders Folder list
         */
        public DirectoryComboBoxChoice(List<File> folders) {
                super(fileToStringArray(folders));
                browseFolders = new CustomButton(SifIcon.getIcon("open"));
                browseFolders.setToolTipText(I18N.tr("Select folder"));
                mainComponent.add(comp, "width 250!");
                mainComponent.add(browseFolders, "width 16!");
        }

        /**
         * User click on browse folders button
         */
        public void onBrowseFolders() {
                OpenFolderPanel openDialog = new OpenFolderPanel("DirectoryComboBoxChoice",
                        I18N.tr("Select an existing workspace or an empty folder"));
                openDialog.setSingleSelection(true);
                if(!getValue().isEmpty()) {
                        openDialog.setCurrentDirectory(new File(getValue()));
                }
                // TODO ability to set a validation object
                // used when user select a folder while browsing
                if(UIFactory.showDialog(openDialog,true,true)) {
                        File folder = openDialog.getSelectedFile();
                        setValue(folder.getAbsolutePath());
                }
        }
        private static String[] fileToStringArray(List<File> folderList) {
                if(folderList==null) {
                        return new String[0];
                }
                String[] pathArray = new String[folderList.size()];
                for(int i=0;i<folderList.size();i++) {
                        pathArray[i]=folderList.get(i).getAbsolutePath();
                }
                return pathArray;                
        }
        @Override
        public Component getComponent() {
                // Add the listener if it is not already added
                if(!Arrays.asList(browseFolders.getActionListeners())
                        .contains(browseButtonAction)) {
                        browseFolders.addActionListener(browseButtonAction);                        
                }
                return mainComponent;
        }
        /**
         * 
         * @return The paths in the combo box
         */
        public List<File> getValues() {
                List<File> paths = new ArrayList<File>();
                for(int index=0;index<comp.getItemCount();index++) {
                        paths.add(new File(((ContainerItemProperties)comp.getItemAt(index)).getKey()));
                }
                return paths;
        }
}

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
package org.orbisgis.view.components.sif;

import bibliothek.util.xml.XElement;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import org.orbisgis.sif.components.AbstractOpenPanel;
import org.orbisgis.view.docking.DockingPanelLayout;

/**
 * Persistance of an open dialog.
 * @author Nicolas Fortin
 */
public class OpenDialogPersistence implements DockingPanelLayout {
        private String dialogName; //for xml serialisation
        private String lastFolder="";
        private int lastFileFilter=0;
        private final static int version = 1; // for binary serialisation
        /**
         * Dialog name, used for xml serialisation
         * @param dialogName Name, juste alpha characters
         * @throws IllegalArgumentException If the provided name contain anything else than characters
         */
        public OpenDialogPersistence(String dialogName) throws IllegalArgumentException {
                if(!dialogName.matches("\\p{javaLetter}*")) {
                        throw new IllegalArgumentException("Dialog persistance name must contain only letters");
                }
                this.dialogName = dialogName;
        }        
        
        /**
         * Transfer the state from this object to the ui panel.
         * @param uiPanel Ui panel before the call to UIFactory.show
         */
        public void loadState(AbstractOpenPanel uiPanel) {
                if(!lastFolder.isEmpty()) {
                        uiPanel.setCurrentDirectory(new File(lastFolder));
                        uiPanel.setCurrentFilter(lastFileFilter);
                }
        }
        /**
         * Transfer the state from the ui panel to this object.
         * @param uiPanel Ui panel after the call to UIFactory.show
         */
        public void saveState(AbstractOpenPanel uiPanel) {
                lastFolder = uiPanel.getCurrentDirectory().getAbsolutePath();
                lastFileFilter = uiPanel.getCurrentFilterId();
        }
        @Override
        public void writeStream(DataOutputStream out) throws IOException {
                out.writeInt(version);
                out.writeUTF(lastFolder);
                out.writeInt(lastFileFilter);
        }

        @Override
        public void readStream(DataInputStream in) throws IOException {
                int importVersion = in.readInt();
                lastFolder = in.readUTF();
                lastFileFilter = in.readInt();
        }

        @Override
        public void writeXML(XElement element) {
                XElement dialogNode = element.addElement(dialogName);
                dialogNode.addString("folder", lastFolder);
                dialogNode.addInt("filter", lastFileFilter);
        }

        @Override
        public void readXML(XElement element) {
                XElement dialogNode = element.getElement(dialogName);
                if (dialogNode != null) {
                        lastFolder = dialogNode.getString("folder");
                        lastFileFilter = dialogNode.getInt("filter");
                }
        }
        
}

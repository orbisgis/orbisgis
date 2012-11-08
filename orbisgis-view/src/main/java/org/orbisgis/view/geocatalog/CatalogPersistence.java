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
package org.orbisgis.view.geocatalog;

import bibliothek.util.xml.XElement;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.orbisgis.view.components.sif.OpenDialogPersistence;
import org.orbisgis.view.docking.DockingPanelLayout;

/**
 * Serialisation of GUI parameters of GeoCatalog
 * @author Nicolas Fortin
 */
public class CatalogPersistence implements DockingPanelLayout {
        private static final long SerialVersionUID = 1;
        private OpenDialogPersistence readDialog = new OpenDialogPersistence("read");
        private OpenDialogPersistence exportDialog = new OpenDialogPersistence("export");

        /**
         * @return Export dialog persistance object
         */
        public OpenDialogPersistence getExportDialog() {
                return exportDialog;
        }

        /**
         * @return Open dialog persistance object
         */
        public OpenDialogPersistence getReadDialog() {
                return readDialog;
        }
        
        @Override
        public void writeStream(DataOutputStream out) throws IOException {
                out.writeLong(SerialVersionUID);
                readDialog.writeStream(out);
                exportDialog.writeStream(out);
        }

        @Override
        public void readStream(DataInputStream in) throws IOException {
                long inVersion = in.readLong();
                readDialog.readStream(in);
                exportDialog.readStream(in);
        }

        @Override
        public void writeXML(XElement element) {
                element.addLong("SerialVersionUID", SerialVersionUID);
                readDialog.writeXML(element);
                exportDialog.writeXML(element);
        }

        @Override
        public void readXML(XElement element) {
                readDialog.readXML(element);
                exportDialog.readXML(element);
        }
        
}

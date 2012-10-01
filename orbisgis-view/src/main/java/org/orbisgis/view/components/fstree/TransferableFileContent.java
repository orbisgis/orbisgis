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
package org.orbisgis.view.components.fstree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Reader;

/**
 * This transferable is able to define the name and the content of a file.
 * @author Nicolas Fortin
 */
public class TransferableFileContent implements Transferable {

        public static final DataFlavor FILE_CONTENT_FLAVOR =
                new DataFlavor(Reader.class, "TransferableFileContent");
        private Reader fileContent;
        private String fileNameHint;

        /**
         * @param fileContent The reader returned by this transferable
         * @param fileNameHint File name proposal for this content
         */
        public TransferableFileContent(Reader fileContent, String fileNameHint) {
                this.fileContent = fileContent;
                this.fileNameHint = fileNameHint;
        }

        /**
         * @return File name proposal for this content
         */
        public String getFileNameHint() {
                return fileNameHint;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{FILE_CONTENT_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor df) {
                return df.equals(FILE_CONTENT_FLAVOR);
        }

        @Override
        public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
                if (df.equals(FILE_CONTENT_FLAVOR)) {
                        return fileContent;
                }
                return null;
        }
}

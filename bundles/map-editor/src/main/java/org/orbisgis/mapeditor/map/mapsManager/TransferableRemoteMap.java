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
package org.orbisgis.mapeditor.map.mapsManager;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.coremap.layerModel.mapcatalog.RemoteCommons;
import org.orbisgis.coremap.layerModel.mapcatalog.RemoteMapContext;
import org.orbisgis.sif.components.fstree.TransferableFileContent;
import org.orbisgis.view.edition.TransferableEditableElement;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.mapeditor.map.TransferableMap;
import org.orbisgis.viewapi.workspace.ViewWorkspace;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Download the Map Content only if the user drop this transferable
 * @author Nicolas Fortin
 */
public class TransferableRemoteMap extends TransferableMap {
        private static final Logger LOGGER = Logger.getLogger(TransferableRemoteMap.class);
        private static final I18n I18N = I18nFactory.getI18n(TransferableRemoteMap.class);
        private final RemoteMapContext remoteMap;
        private final File mapContextFolder;

        public TransferableRemoteMap(RemoteMapContext remoteMap,File mapContextFolder) {
                this.remoteMap = remoteMap;
                this.mapContextFolder = mapContextFolder;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor df) {
                return super.isDataFlavorSupported(df) || df.equals(TransferableFileContent.FILE_CONTENT_FLAVOR);
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
                // Append FILE_CONTENT_FLAVOR
                DataFlavor[] flavors = super.getTransferDataFlavors();
                DataFlavor[] extendedFlavors = Arrays.copyOf(flavors, flavors.length + 1);
                extendedFlavors[extendedFlavors.length-1] = TransferableFileContent.FILE_CONTENT_FLAVOR;
                return extendedFlavors;
        }       
        
        /**
         * Transfer is requested, the remote map must be download and
         * a file name must be defined
         * @param df
         * @return
         * @throws UnsupportedFlavorException
         */
        @Override
        public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException {
                if(df.equals(TransferableMap.mapFlavor) || df.equals(TransferableEditableElement.editableElementFlavor) ) {
                        //Find appropriate file name
                        //Load the map context
                        if (!mapContextFolder.exists()) {
                                mapContextFolder.mkdir();
                        }
                        File mapContextFile = new File(mapContextFolder, remoteMap.getDescription().getDefaultTitle() + "." + remoteMap.getFileExtension());
                        try {
                                MapElement mapElement = new MapElement(remoteMap.getMapContext(), mapContextFile);
                                mapElement.setModified(true);
                                return new MapElement[] {mapElement};
                        } catch( IOException ex) {
                                LOGGER.error(I18N.tr("Error while downloading the map content"),ex);
                                return ex;
                        }
                        
                } else if(df.equals(TransferableFileContent.FILE_CONTENT_FLAVOR)) {
                        // Make a Reader
                        try {
                                //Establish connection with the remote map
                                HttpURLConnection connection = remoteMap.getMapContent();
                                return new ReaderWithName(connection.getInputStream(),RemoteCommons.getConnectionCharset(connection),remoteMap);
                        } catch(IOException ex) {
                                LOGGER.error(ex.getLocalizedMessage(),ex);
                                return null;
                        }
                } else {
                        return null;
                }
        }

        /**
         * Reader input stream that provide a content file name
         */
        private static class ReaderWithName extends InputStreamReader implements TransferableFileContent {
                private RemoteMapContext remoteMapContext;

                public ReaderWithName(InputStream inputStream, String encoding, RemoteMapContext remoteMapContext) throws UnsupportedEncodingException, IOException {
                        super(inputStream, encoding);
                        this.remoteMapContext = remoteMapContext;
                }
                
                @Override
                public String getFileNameHint() {
                        return remoteMapContext.getDescription().getDefaultTitle() + "." + remoteMapContext.getFileExtension();
                }    
        }
}

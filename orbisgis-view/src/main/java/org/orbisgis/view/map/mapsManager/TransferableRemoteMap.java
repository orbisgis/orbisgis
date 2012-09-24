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
package org.orbisgis.view.map.mapsManager;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.mapcatalog.RemoteMapContext;
import org.orbisgis.view.edition.TransferableEditableElement;
import org.orbisgis.view.map.MapElement;
import org.orbisgis.view.map.TransferableMap;
import org.orbisgis.view.workspace.ViewWorkspace;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Download the Map Content only if the user drop this transferable
 * @author Nicolas Fortin
 */
public class TransferableRemoteMap extends TransferableMap {
        private static final Logger LOGGER = Logger.getLogger(TransferableRemoteMap.class);
        private static final I18n I18N = I18nFactory.getI18n(TransferableRemoteMap.class);
        RemoteMapContext remoteMap;

        public TransferableRemoteMap(RemoteMapContext remoteMap) {
                this.remoteMap = remoteMap;
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
                        ViewWorkspace viewWorkspace = Services.getService(ViewWorkspace.class);
                        //Load the map context
                        File mapContextFolder = new File(viewWorkspace.getMapContextPath());
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
                } else {
                        return null;
                }
        }
}

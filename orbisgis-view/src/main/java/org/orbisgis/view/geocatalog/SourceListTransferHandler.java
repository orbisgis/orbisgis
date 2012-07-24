/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import org.apache.log4j.Logger;
import org.orbisgis.core.events.EventException;
import org.orbisgis.core.events.ListenerContainer;

/**
 * Swing Handler for dragging data source list items.
 */

public class SourceListTransferHandler extends TransferHandler{
        private static final Logger LOGGER = Logger.getLogger("gui."+SourceListTransferHandler.class);
        private static final long serialVersionUID = 1L;
        private DataFlavor uriListFlavor = null;
        private ListenerContainer<DropUriEventObject> dropListenerHandler = new ListenerContainer<DropUriEventObject>();

        public SourceListTransferHandler() {
                try {
                        uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
                } catch (ClassNotFoundException ex) {
                        LOGGER.error("Uri flavor not supported",ex);
                }
        }

        public ListenerContainer<DropUriEventObject> getDropListenerHandler() {
                return dropListenerHandler;
        }
        
        
        /**
         * Manage import of files
         * @param ts
         * @return 
         */
        @Override
        public boolean canImport(TransferSupport ts) {
                boolean isFileList = ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
                boolean isUriList = false;
                if(uriListFlavor!=null) {
                        isUriList = ts.isDataFlavorSupported(uriListFlavor);
                }
                return isFileList || isUriList;
        }
        
        private boolean dropDataSource(List<URI> dataList) {
                try {
                        dropListenerHandler.callListeners(new DropUriEventObject(dataList, this));
                        return true;
                } catch (EventException ex) {
                        LOGGER.error(ex);
                        return false;
                }
        }
        /**
         * Manage drop of files
         * @param ts
         * @return 
         */
        @Override
        public boolean importData(TransferSupport ts) {
                //Native javaFileList used in priority
                if(ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        try {
                                List<File> files = (List<File>)ts.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                                //Construct URI list from files
                                List<URI> uriList = new ArrayList<URI>(files.size());
                                for(File file : files) {
                                        uriList.add(file.toURI());
                                }                                
                                return dropDataSource(uriList);
                        } catch (UnsupportedFlavorException ex) {
                                LOGGER.error(ex);
                                return false;
                        } catch (IOException ex) {
                                LOGGER.error(ex);
                                return false;
                        }
                } else {
                        try {
                                String filesChain = (String)ts.getTransferable().getTransferData(uriListFlavor);
                                String lineSep = System.getProperty("line.separator"); //RFC 2483 says that lines are terminated with a CRLF pair
                                List<URI> uriList = new ArrayList<URI>();
                                for(StringTokenizer stringTokenizer = new StringTokenizer(filesChain, lineSep); stringTokenizer.hasMoreTokens();)
                                {
                                        String line = stringTokenizer.nextToken();
                                        //If the URI line is not a comment
                                        if(!(line.startsWith("#") || line.isEmpty()))
                                        {
                                                try {
                                                        URI dataUri = new URI(line.trim());
                                                        uriList.add(dataUri);
                                                } catch (URISyntaxException ex) {
                                                        LOGGER.debug(ex);
                                                        continue;
                                                }
                                        }
                                 }
                                return dropDataSource(uriList);
                        } catch (UnsupportedFlavorException ex) {
                                LOGGER.error(ex);
                                return false;
                        } catch (IOException ex) {
                                LOGGER.error(ex);
                                return false;
                        }
                }
        }

        
        
    @Override
    public int getSourceActions(JComponent jc) {
        return COPY;
    }

    @Override
    protected Transferable createTransferable(JComponent jc) {
        JList list = (JList) jc;
        int[] selectedItems = list.getSelectedIndices();
        String[] selectedSources = new String[selectedItems.length];
        int transfCpt=0;
        for(int idItem : selectedItems) {
            CatalogSourceItem sItem = (CatalogSourceItem) list.getModel().getElementAt(idItem);
            selectedSources[transfCpt++]= sItem.getKey(); //The DataSource unique ID
        }        
        return new TransferableSource(selectedSources);
    }


}

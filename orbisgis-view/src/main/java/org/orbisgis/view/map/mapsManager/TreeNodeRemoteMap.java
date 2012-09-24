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

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.TransferHandler.TransferSupport;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.layerModel.mapcatalog.RemoteMapContext;
import org.orbisgis.core.renderer.se.common.Description;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.components.fstree.AbstractTreeNodeLeaf;
import org.orbisgis.view.components.fstree.DragTreeNode;
import org.orbisgis.view.components.fstree.DropDestinationTreeNode;
import org.orbisgis.view.components.fstree.TransferableList;
import org.orbisgis.view.components.fstree.TreeNodeCustomIcon;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.MapElement;
import org.orbisgis.view.map.TransferableMap;
import org.orbisgis.view.map.mapsManager.jobs.UploadMapContext;

/**
 * Map on the server side
 * @author Nicolas Fortin
 */
public class TreeNodeRemoteMap extends AbstractTreeNodeLeaf implements TreeNodeCustomIcon,DragTreeNode, DropDestinationTreeNode {
        private RemoteMapContext remoteMapConnection;
        private static final Logger LOGGER = Logger.getLogger(TreeNodeRemoteMap.class);
        
        public TreeNodeRemoteMap(RemoteMapContext remoteMapConnection) {
                this.remoteMapConnection = remoteMapConnection;
                Description description = remoteMapConnection.getDescription();
                if(description!=null && description.getDefaultTitle()!=null && !description.getDefaultTitle().isEmpty()) {
                        setLabel(description.getDefaultTitle());
                        setToolTipText(description.getDefaultAbstract());
                }else {
                        setLabel("Unnamed");
                }
                setEditable(false);
        }

        @Override
        public void setUserObject(Object o) {
                // Change map title ?
        }

        @Override
        public ImageIcon getLeafIcon() {
                return OrbisGISIcon.getIcon("map");
        }

        @Override
        public ImageIcon getClosedIcon() {
                throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ImageIcon getOpenIcon() {
                throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean completeTransferable(TransferableList transferable) {
                transferable.addTransferable(new TransferableRemoteMap(remoteMapConnection));
                return true;
        }

        @Override
        public boolean canImport(TransferSupport ts) {
                return ts.isDataFlavorSupported(TransferableMap.mapFlavor);
        }

        @Override
        public boolean importData(TransferSupport ts) {
                // Uploading and updating of a Map
                try {
                        // Retrieve the MapContext
                        Object mapObj = ts.getTransferable().getTransferData(TransferableMap.mapFlavor);
                        MapElement[] mapArray = (MapElement[])mapObj;
                        if(mapArray.length!=0) {
                                MapContext mapToUpload = mapArray[0].getMapContext();
                                BackgroundManager bm = Services.getService(BackgroundManager.class);
                                bm.nonBlockingBackgroundOperation(new UploadMapContext(mapToUpload, (TreeNodeWorkspace)getParent(),remoteMapConnection.getId()));
                        }
                        return true;
                } catch (UnsupportedFlavorException ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                        return false;
                } catch (IOException ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                        return false;
                }
        }
}

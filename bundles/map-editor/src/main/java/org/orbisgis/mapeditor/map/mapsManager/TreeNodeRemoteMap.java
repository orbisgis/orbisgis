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

import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler.TransferSupport;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.layerModel.mapcatalog.RemoteMapContext;
import org.orbisgis.coremap.renderer.se.common.Description;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.components.fstree.AbstractTreeNodeLeaf;
import org.orbisgis.view.components.fstree.DragTreeNode;
import org.orbisgis.view.components.fstree.DropDestinationTreeNode;
import org.orbisgis.view.components.fstree.PopupTreeNode;
import org.orbisgis.view.components.fstree.TransferableList;
import org.orbisgis.view.components.fstree.TreeNodeCustomIcon;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.mapeditor.map.TransferableMap;
import org.orbisgis.mapeditor.map.mapsManager.jobs.DeleteRemoteMapContext;
import org.orbisgis.mapeditor.map.mapsManager.jobs.UploadMapContext;
import org.orbisgis.viewapi.util.MenuCommonFunctions;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Map on the server side
 * @author Nicolas Fortin
 */
public class TreeNodeRemoteMap extends AbstractTreeNodeLeaf implements TreeNodeCustomIcon,DragTreeNode, DropDestinationTreeNode, PopupTreeNode {
        private RemoteMapContext remoteMapConnection;
        private static final Logger LOGGER = Logger.getLogger(TreeNodeRemoteMap.class);
        private static final I18n I18N = I18nFactory.getI18n(TreeNodeRemoteMap.class);
        
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
        /**
         * 
         * @return The internal representation of the remote map context
         */
        public RemoteMapContext getRemoteMapContext() {
                return remoteMapConnection;
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
                transferable.addTransferable(new TransferableRemoteMap(remoteMapConnection, remoteMapConnection.getcParams().getMapFolderCache()));
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
                        if(mapObj instanceof MapElement[]) {
                                int response = JOptionPane.showConfirmDialog(UIFactory.getMainFrame(), I18N.tr("Are you sure you want to replace the remote map context ?"), I18N.tr("Overwrite map context confirmation"), JOptionPane.YES_NO_OPTION);
                                if(response == JOptionPane.YES_OPTION) {
                                        MapElement[] mapArray = (MapElement[])mapObj;
                                        if(mapArray.length!=0) {
                                                MapContext mapToUpload = mapArray[0].getMapContext();
                                                BackgroundManager bm = Services.getService(BackgroundManager.class);
                                                bm.nonBlockingBackgroundOperation(new UploadMapContext(mapToUpload, (TreeNodeWorkspace)getParent(),remoteMapConnection.getId()));
                                        }
                                        return true;
                                } else {
                                        return false;
                                }
                        } else {
                                return false;
                        }
                } catch (UnsupportedFlavorException ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                        return false;
                } catch (IOException ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                        return false;
                }
        }
        public void onDeleteMap() {
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.nonBlockingBackgroundOperation(new DeleteRemoteMapContext(this));                
        }
        @Override
        public void feedPopupMenu(JPopupMenu menu) {
                JMenuItem remove = new JMenuItem(I18N.tr("Delete"),
                        OrbisGISIcon.getIcon("remove"));
                remove.setToolTipText(I18N.tr("Remove this map on the server"));
                remove.setActionCommand("delete");
                remove.addActionListener(
                EventHandler.create(ActionListener.class,
                this, "onDeleteMap"));
                MenuCommonFunctions.updateOrInsertMenuItem(menu, remove);
        }
}

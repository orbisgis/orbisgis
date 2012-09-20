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

import javax.swing.ImageIcon;
import org.orbisgis.core.layerModel.mapcatalog.RemoteMapContext;
import org.orbisgis.core.renderer.se.common.Description;
import org.orbisgis.view.components.fstree.AbstractTreeNodeLeaf;
import org.orbisgis.view.components.fstree.DragTreeNode;
import org.orbisgis.view.components.fstree.TransferableList;
import org.orbisgis.view.components.fstree.TreeNodeCustomIcon;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 * Map on the server side
 * @author Nicolas Fortin
 */
public class TreeNodeRemoteMap extends AbstractTreeNodeLeaf implements TreeNodeCustomIcon,DragTreeNode {
        private RemoteMapContext remoteMapConnection;

        public TreeNodeRemoteMap(RemoteMapContext remoteMapConnection) {
                this.remoteMapConnection = remoteMapConnection;
                Description description = remoteMapConnection.getDescription();
                if(description!=null) {
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
}

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
package org.orbisgis.sif.components.fstree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.orbisgis.sif.components.UriListFlavor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transferable that hold a list of TreeNodePath
 * @author Nicolas Fortin
 */
public class TransferableNodePaths implements Transferable {
        public static final DataFlavor PATHS_FLAVOR = new DataFlavor(
			TransferableNodePaths.class, "File paths");
        private List<TreeNodePath> paths = new ArrayList<TreeNodePath>();
        private UriListFlavor uriListFlavor = new UriListFlavor();
        private static final Logger LOGGER = LoggerFactory.getLogger(TransferableNodePaths.class);
        
        /**
         * Default constructor
         */
        public TransferableNodePaths() {          
        }
        
        /**
         * Constructor with an initial node
         * @param node 
         */
        public TransferableNodePaths(TreeNodePath node) {
                addPath(node);
        }
        
        /**
         * @return unmodifiable List of TreeNodePath
         */
        public List<TreeNodePath> getPaths() {
                return Collections.unmodifiableList(paths);
        }
        /**
         * Add a node in this transferable
         * @param node 
         */
        public final void addPath(TreeNodePath node) {
                paths.add(node);
        }
        /**
         * Remove the provided node from the list
         * @param node Node to remove
         * @return True if the remove succeed
         */
        public boolean removePath(TreeNodePath node) {
                return paths.remove(node);
        }
        
        @Override
        public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] {uriListFlavor.getUriListFlavor(),PATHS_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor df) {
                return df.equals(uriListFlavor.getUriListFlavor()) 
                        || df.equals(PATHS_FLAVOR);
        }

        @Override
        public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
                if(df.equals(PATHS_FLAVOR)) {
                        return paths;
                } else if(df.equals(uriListFlavor.getUriListFlavor())) {
                        List<URI> uriList = new ArrayList<URI>();
                        for(TreeNodePath nodePath : paths) {
                                uriList.add(nodePath.getFilePath().toURI());
                        }
                        return uriListFlavor.getTransferableData(uriList);
                } else {
                        return null;
                }
        }
        
}

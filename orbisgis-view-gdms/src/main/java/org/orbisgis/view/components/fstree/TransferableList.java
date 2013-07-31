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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This transferable is able to merge multiple transferable. It returns the data
 * as an array if there is multiple results
 *
 * @author Nicolas Fortin
 */
public class TransferableList implements Transferable  {
        List<Transferable> mergedTransferable = new ArrayList<Transferable>();
        /**
         * Add a managed transferable
         * @param transferable 
         */
        public void addTransferable(Transferable transferable) {
                mergedTransferable.add(transferable);
        }
        
        @Override
        public DataFlavor[] getTransferDataFlavors() {
                Set<DataFlavor> flavorSet = new HashSet<DataFlavor>();
                for(Transferable transferable : mergedTransferable) {
                        flavorSet.addAll(Arrays.asList(transferable.getTransferDataFlavors()));
                }
                return flavorSet.toArray(new DataFlavor[flavorSet.size()]);
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor df) {
                for(Transferable transferable : mergedTransferable) {
                        if(transferable.isDataFlavorSupported(df)) {
                                return true;
                        }
                }
                return false;
        }

        @Override
        public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
                List<Object> listTransferData = new ArrayList<Object>();
                // Collect all compatible data
                for(Transferable transferable : mergedTransferable) {
                        if(transferable.isDataFlavorSupported(df)) {
                                Object internTransferData = transferable.getTransferData(df);
                                listTransferData.add(internTransferData);
                        }
                }
                if(listTransferData.isEmpty()) {
                        return null;
                }
                if(listTransferData.size()==1) {
                        return listTransferData.get(0);
                } else {
                        if(listTransferData.get(0) instanceof Collection) {
                                List<Object> nextListTransferData = new ArrayList<Object>(listTransferData.size());
                                for(Object transferData : listTransferData) {
                                        if(transferData instanceof Collection) {
                                                nextListTransferData.addAll((Collection<? extends Object>) transferData);                                                
                                        }
                                }
                                return nextListTransferData;
                        } else {
                                return listTransferData;
                        }
                }
                
        }
        
        
}

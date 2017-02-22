/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

import java.awt.Font;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.commons.progress.NullProgressMonitor;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.sif.common.MenuCommonFunctions;
import org.orbisgis.sif.components.fstree.AbstractTreeNodeLeaf;
import org.orbisgis.sif.components.fstree.DragTreeNode;
import org.orbisgis.sif.components.fstree.PopupTreeNode;
import org.orbisgis.sif.components.fstree.TransferableList;
import org.orbisgis.sif.components.fstree.TransferableNodePaths;
import org.orbisgis.sif.components.fstree.TreeNodeCustomLabel;
import org.orbisgis.sif.components.fstree.TreeNodePath;
import org.orbisgis.mapeditor.map.TransferableMap;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditor.map.jobs.ReadMapContextJob;
import org.orbisgis.sif.edition.EditorManager;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * TreeLeafMapElement files that contain all implementations of MapContext
 * @author Nicolas Fortin
 */

public abstract class TreeLeafMapElement extends AbstractTreeNodeLeaf implements PopupTreeNode, TreeNodeCustomLabel, DragTreeNode,TreeNodePath {
        private static final I18n I18N = I18nFactory.getI18n(TreeLeafMapElement.class);
        private boolean loaded = false;
        private File filePath; // Call getFilePath() instead of using this variable
        private DataManager dataManager;
        private EditorManager editorManager;

        public TreeLeafMapElement(File filePath, DataManager dataManager,EditorManager editorManager) {
                this.filePath = filePath;
                this.dataManager = dataManager;
                this.editorManager = editorManager;
        }

        
        @Override
        public File getFilePath() {
                if(parent instanceof TreeNodePath) {
                        TreeNodePath parentFolder = (TreeNodePath)parent;
                        return new File(parentFolder.getFilePath(),filePath.getName());
                } else {
                        return filePath;
                }
        }
        /**
         * @return The state of this map
         */
        public boolean isLoaded() {
                return loaded;
        }

        /**
         * Set this Map Context as loaded within the Application
         * @param loaded 
         */
        public void setLoaded(boolean loaded) {
                if(this.loaded != loaded) {
                        this.loaded = loaded;
                        model.nodeChanged(this);
                }
        }      
        
        /**
         * Create the map element associated with this file
         * @param pm 
         * @return 
         */
        abstract public MapElement getMapElement(ProgressMonitor pm, DataManager dataManager);
               
        /**
         * Open the selected map file (only the first one selected)
         */
        public void onOpenMap() {
                new ReadMapContextJob(getMapElement(new NullProgressMonitor(), dataManager), editorManager).execute();
        }

        @Override
        public void feedPopupMenu(JPopupMenu menu) {
                if(!loaded) {
                        JMenuItem openMapItem = new JMenuItem(I18N.tr("Open the map"), MapEditorIcons.getIcon("open_map"));
                        openMapItem.addActionListener(EventHandler.create(ActionListener.class,this,"onOpenMap"));
                        openMapItem.setActionCommand("TreeNodeMapElement:OpenMap");
                        MenuCommonFunctions.updateOrInsertMenuItem(menu, openMapItem);
                }
        }
        
        @Override
        public boolean applyCustomLabel(JLabel label) {
                if(loaded) {
                        Font textFont = label.getFont();
                        label.setFont(textFont.deriveFont(Font.BOLD));
                        return true;
                } else {
                        return false;
                }
        }
        
        @Override
        public boolean completeTransferable(TransferableList transferable) {
                transferable.addTransferable(new TransferableNodePaths(this));
                if(!transferable.isDataFlavorSupported(TransferableMap.mapFlavor)) {
                        transferable.addTransferable(new TransferableMap(getMapElement(new NullProgressMonitor(), dataManager)));
                }
                return true;
        }

        /**
         * @return Map use this database to register URI.
         */
        public DataManager getDataManager() {
            return dataManager;
        }
}

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

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.apache.log4j.Logger;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenFolderPanel;
import org.orbisgis.sif.components.fstree.AbstractTreeNodeContainer;
import org.orbisgis.sif.components.fstree.PopupTreeNode;
import org.orbisgis.sif.components.fstree.TreeNodeCustomIcon;
import org.orbisgis.sif.components.fstree.TreeNodeFileFactoryManager;
import org.orbisgis.sif.components.fstree.TreeNodeFolder;
import org.orbisgis.viewapi.util.MenuCommonFunctions;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * List of local map folders
 * @author Nicolas Fortin
 */
public class TreeNodeLocalRoot extends AbstractTreeNodeContainer implements PopupTreeNode,  TreeNodeCustomIcon {
        private static final I18n I18N = I18nFactory.getI18n(TreeNodeLocalRoot.class);
        private static final Logger LOGGER = Logger.getLogger(TreeNodeLocalRoot.class);
        // This list must be updated to the current state of shown servers
        private MapsManagerPersistence mapsManagerPersistence;
        private TreeNodeFileFactoryManager factoryManager;
               
        /**
         * Default constructor
        * @param factoryManager
         */
        public TreeNodeLocalRoot(TreeNodeFileFactoryManager factoryManager) {
                setLabel(I18N.tr("Local"));
                setEditable(false);
                this.factoryManager = factoryManager;
        }

        /**
         * The server list will keep this instance updated
         * @param mapsManagerPersistence
         */
        public void setMapsManagerPersistence(MapsManagerPersistence mapsManagerPersistence) {
            this.mapsManagerPersistence = mapsManagerPersistence;
            // Track property change
            mapsManagerPersistence.addPropertyChangeListener(MapsManagerPersistence.PROP_FOLDER_LIST,
                    EventHandler.create(PropertyChangeListener.class, this, "onFolderListPropertyChange","newValue"));
            onFolderListPropertyChange(mapsManagerPersistence.getMapCatalogFolderList());
        }

        /**
         * Sync shown user map folders with provided list of folder absolute path.
         * A lister raise this event when the GUI persistence properties change.
         */
        public void onFolderListPropertyChange(List<String> userMapFolders) {
            // Build list of shown user root folder
            List<TreeNodeFolder> userRootFolders = new ArrayList<TreeNodeFolder>(getChildCount());
            for(TreeNode node : children) {
                if(node instanceof TreeNodeUserFolder) {
                    userRootFolders.add((TreeNodeFolder)node);
                }
            }
            Map<File, TreeNodeFolder> currentShownUserFolder = new HashMap<File, TreeNodeFolder>(userRootFolders.size());
            for(TreeNodeFolder treeNodeFolder : userRootFolders) {
                currentShownUserFolder.put(treeNodeFolder.getFilePath(), treeNodeFolder);
            }
            for(String userMapFolder : userMapFolders) {
                File userMapPath = new File(userMapFolder);
                if(currentShownUserFolder.containsKey(userMapPath)) {
                    // This folder was already shown in the GUI
                    // Elements in currentShownUserFolder will be removed from gui after
                    // Then remove from the map in order to keep them in the gui
                    currentShownUserFolder.remove(userMapPath);
                } else if(userMapPath.exists()) {
                    // This folder was not shown in the GUI
                    TreeNodeFolder subDir = new TreeNodeUserFolder(userMapPath, factoryManager, mapsManagerPersistence);
                    subDir.setEditable(false);
                    model.insertNodeInto(subDir, this, 0);
                    subDir.updateTree();
                }
            }
            // Remove user dir not anymore in GUI
            for(TreeNodeFolder removeFolder : currentShownUserFolder.values()) {
                model.removeNodeFromParent(removeFolder);
            }
        }

        @Override
        public void remove(MutableTreeNode mtn) {
                remove(getIndex(mtn));                
        }

        @Override
        public void setUserObject(Object o) {
                // Read only
        }

        /**
         * Prompt the user to fetch local folder
         */
        public void onAddFolder() {
            OpenFolderPanel openDialog = new OpenFolderPanel("MapsManagerLinkLocalFolder",
                    I18N.tr("Select a folder to link with map manager"));
            openDialog.setSingleSelection(true);
            openDialog.loadState();
            if(UIFactory.showDialog(openDialog, true, true)) {
                File folder = openDialog.getSelectedFile();
                List<String> userFolders = new ArrayList<String>(mapsManagerPersistence.getMapCatalogFolderList());
                if(!userFolders.contains(folder.getAbsolutePath())) {
                    userFolders.add(folder.getAbsolutePath());
                    mapsManagerPersistence.setMapCatalogFolderList(userFolders);
                }
            }
        }

        /**
         * Refresh all folders in this nodes
         */
        public void updateTree() {
            for(TreeNode node : children) {
                if(node instanceof TreeNodeFolder) {
                    ((TreeNodeFolder)node).updateTree();
                }
            }
        }

        @Override
        public void feedPopupMenu(JPopupMenu menu) {
                JMenuItem addServer = new JMenuItem(I18N.tr("Add Map Catalog folder"),
                        MapEditorIcons.getIcon("folder_add"));
                addServer.setToolTipText(I18N.tr("Add link to a local folder"));
                addServer.setActionCommand("TreeNodeLocalRoot:addFolder");
                addServer.addActionListener(
                        EventHandler.create(ActionListener.class,
                        this, "onAddFolder"));
                MenuCommonFunctions.updateOrInsertMenuItem(menu, addServer);
        }

    @Override
    public ImageIcon getLeafIcon() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImageIcon getClosedIcon() {
        return MapEditorIcons.getIcon("folder");
    }

    @Override
    public ImageIcon getOpenIcon() {
         return MapEditorIcons.getIcon("folder_open");
    }
        
        
        
}

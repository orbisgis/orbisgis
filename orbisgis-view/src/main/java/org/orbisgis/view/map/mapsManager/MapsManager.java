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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Scrollbar;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.components.fstree.FileTree;
import org.orbisgis.view.components.fstree.FileTreeModel;
import org.orbisgis.view.components.fstree.TreeNodeFileFactoryManager;
import org.orbisgis.view.components.fstree.TreeNodeFolder;
import org.orbisgis.view.components.fstree.TreeNodePath;
import org.orbisgis.view.map.mapsManager.jobs.ReadStoredMap;
import org.orbisgis.view.workspace.ViewWorkspace;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Nicolas Fortin
 */
public class MapsManager extends JPanel {
        // Minimal tree size is incremented by this emptySpace
        private static final long serialVersionUID = 1L;
        private static final I18n I18N = I18nFactory.getI18n(MapsManager.class);
        private static final Logger LOGGER = Logger.getLogger(MapsManager.class);
        private FileTree tree;
        private DefaultTreeModel treeModel;
        private MutableTreeNode rootNode = new DefaultMutableTreeNode();
        private TreeNodeFolder rootFolder;
        private TreeNodeRemoteRoot rootRemote;
        private JScrollPane scrollPane;
        private File loadedMap;
        // Store all the compatible map context
        
        private AtomicBoolean initialized = new AtomicBoolean(false);
        /**
         * Default constructor
         */
        public MapsManager() {
                super(new BorderLayout());
                treeModel = new FileTreeModel(rootNode, true);
                treeModel.setAsksAllowsChildren(true);
                // Add the tree in the panel                
                tree = new FileTree(treeModel);
                tree.setEditable(true);
                // Retrieve the default ows maps folder
                ViewWorkspace workspace = Services.getService(ViewWorkspace.class);
                // Add the root folder
                File rootFolderPath = new File(workspace.getMapContextPath());
                if(!rootFolderPath.exists()) {
                        rootFolderPath.mkdirs();
                }
                rootFolder = new TreeNodeFolder(rootFolderPath,tree);
                rootFolder.setLabel(I18N.tr("Local")); 
                rootRemote = new TreeNodeRemoteRoot();
                initInternalFactories(); // Init file readers
                treeModel.insertNodeInto(rootFolder, rootNode, rootNode.getChildCount());
                treeModel.insertNodeInto(rootRemote, rootNode, rootNode.getChildCount());
                tree.setRootVisible(false);
                scrollPane = new JScrollPane(tree);
                JLabel title = new JLabel(I18N.tr("Maps manager"));
                // Disable mouse event propagation on this label
                title.addMouseListener(new MouseAdapter(){}); 
                add(title,BorderLayout.NORTH);
                add(scrollPane,BorderLayout.CENTER);
                setBorder(BorderFactory.createEtchedBorder());
        }
        
        /**
         * Used by the UI to convert a File into a MapElement
         * @return The Map file factory manager
         */
        public TreeNodeFileFactoryManager getFactoryManager() {
                return tree;
        }
        /**
         * Update the shown elements in the disk tree
         */
        public void updateDiskTree() {
                rootFolder.updateTree();
                applyLoadedMapHint();
        }
        private List<TreeLeafMapElement> getAllMapElements(TreeNode parentNode) {
                List<TreeLeafMapElement> mapElements = new ArrayList<TreeLeafMapElement>();
                if(!parentNode.isLeaf()) {
                        for(int childIndex=0; childIndex < parentNode.getChildCount(); childIndex++) {
                                TreeNode nodeElement = parentNode.getChildAt(childIndex);
                                if(nodeElement instanceof TreeLeafMapElement) {
                                        mapElements.add((TreeLeafMapElement)nodeElement);
                                } else {
                                        mapElements.addAll(getAllMapElements(nodeElement));
                                }
                        }
                }                
                return mapElements;
        }
         
        @Override
        public void setVisible(boolean visible) {
                super.setVisible(visible);
                if(visible && !initialized.getAndSet(true)) {
                        // Set a listener to the root folder
                        rootFolder.updateTree(); //Read the file system tree
                        // Expand Local and remote folder
                        tree.expandPath(new TreePath(new Object[] {rootNode,rootFolder}));  
                        tree.expandPath(new TreePath(new Object[] {rootNode,rootRemote}));  
                        updateMapsTitle();
                        // Apply loaded map property on map nodes
                        applyLoadedMapHint();
                }
        }
        
        private void updateMapsTitle() {
                // Fetch all maps to find their titles
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.nonBlockingBackgroundOperation(new ReadStoredMap(getAllMapElements(rootFolder)));                
        }
        
       /**
        *  Load built-ins map factory
        */
        private void initInternalFactories() {
                tree.addFactory("ows",new TreeNodeOwsMapContextFactory());
        }
        /**
         * 
         * @return The internal tree
         */
        public JTree getTree() {
                return tree;
        }

        /**
         * The map manager will read and update the map server list
         * @param mapCatalogServers 
         */
        public void setServerList(List<String> mapCatalogServers) {
                rootRemote.setServerList(mapCatalogServers);
        }

        /**
         * Update the state of the tree to show to the user a visual hint that a
         * map is currently shown in the MapEditor or not.
         *
         * @param loadedMap
         
         */
        public void setLoadedMap(File loadedMap) {
                this.loadedMap = loadedMap;
                applyLoadedMapHint();
        }
        private void applyLoadedMapHint() {
                if(loadedMap!=null) {
                        List<TreeLeafMapElement> mapElements = getAllMapElements(rootFolder);
                        for(TreeLeafMapElement mapEl : mapElements) {
                                if(mapEl instanceof TreeNodePath) {
                                        if(((TreeNodePath)mapEl).getFilePath().equals(loadedMap)) {
                                                mapEl.setLoaded(true);
                                        } else {
                                                mapEl.setLoaded(false);
                                        }
                                }
                        }
                }
        }
        
        /**
         * Compute the best height to show all the items of the JTree 
         * plus the decoration height.
         * @return Height in pixels
         */
        public Dimension getMinimalComponentDimension() {                
                Dimension panel = getPreferredSize();
                Dimension treeDim = tree.getPreferredSize();
                // Get the vertical scrollbar width
                JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
                if(scrollBar!=null && scrollBar.isVisible()) {
                        return new Dimension(panel.width+scrollBar.getWidth(),treeDim.height+getMinimumSize().height);
                } else {
                        return new Dimension(panel.width,treeDim.height+getMinimumSize().height);
                }
        }
}

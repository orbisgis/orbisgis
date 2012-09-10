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
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.jobs.ReadMapContextJob;
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
        private JTree tree;
        private DefaultTreeModel treeModel;
        private MutableTreeNode rootNode = new DefaultMutableTreeNode();
        TreeNodeFolder rootFolder;
        private JScrollPane scrollPane;
        // Store all the compatible map context
        private TreeNodeMapFactoryManager factoryManager = new TreeNodeMapFactoryManager();
        private MouseListener treeMouse = EventHandler.create(MouseListener.class,this,"onMouseEvent","");
        private AtomicBoolean initialized = new AtomicBoolean(false);
        private PropertyChangeListener listener =
                EventHandler.create(PropertyChangeListener.class,this,"onTreeChildrenPropertyChange","");
        /**
         * Default constructor
         */
        public MapsManager() {
                super(new BorderLayout());
                treeModel = new DefaultTreeModel(rootNode, true);
                treeModel.setAsksAllowsChildren(true);
                // Retrieve the default ows maps folder
                ViewWorkspace workspace = Services.getService(ViewWorkspace.class);
                // Add the root folder
                rootFolder = new TreeNodeFolder(new File(workspace.getMapContextPath()),factoryManager);
                rootFolder.setLabel(I18N.tr("Local"));
                rootNode.insert(rootFolder, 0);
                initInternalFactories(); // Init file readers
                // Add the tree in the panel                
                tree = new JTree(treeModel);
                tree.addMouseListener(treeMouse);
                tree.setRootVisible(false);
                scrollPane = new JScrollPane(tree);
                add(scrollPane,BorderLayout.EAST);
                setBorder(BorderFactory.createEtchedBorder());
        }
        
       
        /**
         * Called on tree structure change
         * @param evt 
         */
        public void onTreeChildrenPropertyChange(PropertyChangeEvent evt) {
                LOGGER.debug("onTreeChildrenPropertyChange "+evt.getSource());
                if(evt instanceof IndexedPropertyChangeEvent) {
                        IndexedPropertyChangeEvent evtIndexed = (IndexedPropertyChangeEvent)evt;
                        TreeNodeFolder folderNode = (TreeNodeFolder)evt.getSource();
                        TreeNode newNode = (TreeNode)evt.getNewValue();
                        int childIndex = folderNode.getIndex(newNode);
                        if(childIndex == -1) {
                                //Deletion of a child Node
                                treeModel.nodesWereRemoved(folderNode,
                                        new int[] {evtIndexed.getIndex()},
                                        new Object[] {evt.getOldValue()});
                        } else {
                                //Insertion of a child Node
                                LOGGER.debug("onTreeChildrenPropertyChange insertion");
                                treeModel.nodesWereInserted(folderNode, new int[] {childIndex});
                        }
                        evtIndexed.getIndex();
                } else {                        
                        // Multiple insertion and deletion
                }
                // Register the listener on new Folder
                if(evt.getNewValue() instanceof TreeNodeFolder) {
                        TreeNodeFolder newFolder = (TreeNodeFolder)evt.getNewValue();
                        newFolder.addPropertyChangeListener(TreeNodeFolder.PROP_CHILDREN, listener);
                }
        }
        /**
         * Used by the UI to convert a File into a MapElement
         * @return The Map file factory manager
         */
        public TreeNodeMapFactoryManager getFactoryManager() {
                return factoryManager;
        }
        
        
         private boolean contains(TreePath[] selectionPaths, TreePath path) {
                for (TreePath treePath : selectionPaths) {
                        boolean equals = true;
                        Object[] objectPath = treePath.getPath();
                        Object[] testPath = path.getPath();
                        if (objectPath.length != testPath.length) {
                                equals = false;
                        } else {
                                for (int i = 0; i < testPath.length; i++) {
                                        if (testPath[i] != objectPath[i]) {
                                                equals = false;
                                        }
                                }
                        }
                        if (equals) {
                                return true;
                        }
                }

                return false;
        }
         
        @Override
        public void setVisible(boolean visible) {
                super.setVisible(visible);
                if(visible && !initialized.getAndSet(true)) {
                        //Set a listener to the root folder
                        rootFolder.addPropertyChangeListener(TreeNodeFolder.PROP_CHILDREN, listener);
                        rootFolder.updateTree(); //Read the file system tree
                        tree.setCellRenderer(new CustomTreeCellRenderer(tree));
                        //Expand Local folder
                        tree.expandPath(new TreePath(new Object[] {rootNode,rootFolder}));                                               
                }
        }
        /**
         * Event on Tree, called by listener
         * @param evt 
         */
        public void onMouseEvent(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                        //Update selection
                        TreePath path = tree.getPathForLocation(evt.getX(), evt.getY());
                        TreePath[] selectionPaths = tree.getSelectionPaths();
                        if ((selectionPaths != null) && (path != null)) {
                                if (!contains(selectionPaths, path)) {
                                        if (evt.isControlDown()) {
                                                tree.addSelectionPath(path);
                                        } else {
                                                tree.setSelectionPath(path);
                                        }
                                }
                        } else {
                                tree.setSelectionPath(path);
                        }
                        //Show popup
                        makePopupMenu().show(evt.getComponent(),
                                evt.getX(), evt.getY());
                }
        }
        /**
         * Fetch all selected items to make a popup menu
         * @return 
         */
        private JPopupMenu makePopupMenu() {
                JPopupMenu menu = new JPopupMenu();
                boolean hasMapSelected = false;
                TreePath[] paths = tree.getSelectionPaths();
                if(paths!=null) {
                        for(TreePath treePath : paths) {
                                Object component = treePath.getLastPathComponent();
                                // All nodes
                                if(component instanceof MutableTreeNode) {
                                        MutableTreeNode node = (MutableTreeNode)component;
                                        for(TreeNodeMapFactory fact : factoryManager.getFactories()) {
                                                fact.feedTreeNodePopupMenu(node, menu);
                                        }
                                }
                                // Specific nodes
                                if(component instanceof PopupTreeNode) {
                                        PopupTreeNode treeNode = (PopupTreeNode) component;
                                        treeNode.feedPopupMenu(menu);
                                } if(component instanceof TreeNodeMapElement) {
                                        hasMapSelected = true;
                                }
                        }
                }
                if(hasMapSelected) {
                        JMenuItem openMapItem = new JMenuItem(I18N.tr("Open the map"), OrbisGISIcon.getIcon("map"));
                        openMapItem.addActionListener(EventHandler.create(ActionListener.class,this,"onOpenMap"));
                        menu.insert(openMapItem, 0);
                        if(menu.getComponentCount()>1) {                                
                                menu.insert(new JSeparator(), 0);
                        }
                }
                return menu;
        }
        /**
         * 
         * @return The first selected TreeNodeMapElement
         */
        private TreeNodeMapElement getFirstSelectedMap() {
                for(TreePath treePath : tree.getSelectionPaths()) {
                        Object component = treePath.getLastPathComponent();
                        if(component instanceof TreeNodeMapElement) {
                            return (TreeNodeMapElement) component;
                        }
                }
                return null;                
        }
        
        /**
         * Open the selected map file (only the first one selected)
         */
        public void onOpenMap() {
                TreeNodeMapElement mapNode = getFirstSelectedMap();
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.backgroundOperation(new ReadMapContextJob(mapNode.getMapElement(new NullProgressMonitor())));
        }
        
       /**
        *  Load built-ins map factory
        */
        private void initInternalFactories() {
                factoryManager.addFactory("ows",new TreeNodeOwsMapContextFactory(rootFolder));
        }
        /**
         * 
         * @return The internal tree
         */
        public JTree getTree() {
                return tree;
        }
        
        /**
         * Compute the best height to show all the items of the JTree 
         * plus the decoration height.
         * @return Height in pixels
         */
        public Dimension getMinimalComponentDimension() {                
                Insets borders = getInsets();
                Insets sBorders = scrollPane.getInsets();
                Dimension treeDim = tree.getPreferredSize();
                return new Dimension(treeDim.width+
                        borders.left+
                        borders.right+
                        sBorders.left+
                        sBorders.right
                        ,treeDim.height+
                        borders.top+
                        borders.bottom+
                        sBorders.top+
                        sBorders.bottom);
        }
}

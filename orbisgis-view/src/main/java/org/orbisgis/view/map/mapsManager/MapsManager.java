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

import org.orbisgis.view.components.fstree.TreeNodeFileFactoryManager;
import org.orbisgis.view.components.fstree.CustomTreeCellRenderer;
import org.orbisgis.view.components.fstree.PopupTreeNode;
import org.orbisgis.view.components.fstree.TreeNodeFileFactory;
import org.orbisgis.view.components.fstree.TreeNodeFolder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
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
        private TreeNodeFileFactoryManager factoryManager = new TreeNodeFileFactoryManager();
        private MouseListener treeMouse = EventHandler.create(MouseListener.class,this,"onMouseEvent","");
        private AtomicBoolean initialized = new AtomicBoolean(false);
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
                File rootFolderPath = new File(workspace.getMapContextPath());
                if(!rootFolderPath.exists()) {
                        rootFolderPath.mkdirs();
                }
                rootFolder = new TreeNodeFolder(rootFolderPath,factoryManager);
                rootFolder.setLabel(I18N.tr("Local"));                
                initInternalFactories(); // Init file readers
                // Add the tree in the panel                
                tree = new JTree(treeModel);
                treeModel.insertNodeInto(rootFolder,rootNode, 0);
                tree.addMouseListener(treeMouse);
                tree.setRootVisible(false);
                scrollPane = new JScrollPane(tree);
                add(scrollPane,BorderLayout.EAST);
                setBorder(BorderFactory.createEtchedBorder());
        }
        
        /**
         * Used by the UI to convert a File into a MapElement
         * @return The Map file factory manager
         */
        public TreeNodeFileFactoryManager getFactoryManager() {
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
                        rootFolder.setModel(treeModel);
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
                TreePath[] paths = tree.getSelectionPaths();
                if(paths!=null) {
                        for(TreePath treePath : paths) {
                                Object component = treePath.getLastPathComponent();
                                // All nodes
                                if(component instanceof MutableTreeNode) {
                                        MutableTreeNode node = (MutableTreeNode)component;
                                        for(TreeNodeFileFactory fact : factoryManager.getFactories()) {
                                                fact.feedTreeNodePopupMenu(node, menu);
                                        }
                                }
                                // Specific nodes
                                if(component instanceof PopupTreeNode) {
                                        PopupTreeNode treeNode = (PopupTreeNode) component;
                                        treeNode.feedPopupMenu(menu);
                                }
                        }
                }
                return menu;
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

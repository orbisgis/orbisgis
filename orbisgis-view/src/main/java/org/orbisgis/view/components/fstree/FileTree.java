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

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.apache.commons.io.FilenameUtils;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * This JTree Can be linked with the file system thanks to the TreeNodeFolder
 * @author Nicolas Fortin
 */
public class FileTree extends JTree implements TreeNodeFileFactoryManager {
        private static final long serialVersionUID = 1L;
        private MouseListener treeMouse = EventHandler.create(MouseListener.class,this,"onMouseEvent","");
        private AtomicBoolean initialized = new AtomicBoolean(false);
        private static final I18n I18N = I18nFactory.getI18n(FileTree.class);
        
        public FileTree(TreeModel tm) {
                super(tm);
        }

        public FileTree() {
        }

        @Override
        public void addNotify() {
                super.addNotify();
                if(!initialized.getAndSet(true)) {
                        setTransferHandler(new FileTreeTransferHandler());
                        setDragEnabled(true);
                        setCellRenderer(new CustomTreeCellRenderer(this));
                        addMouseListener(treeMouse);
                        getSelectionModel().addTreeSelectionListener(
                                EventHandler.create(TreeSelectionListener.class,
                                this,"onSelectionChange"));
                        javax.swing.ToolTipManager.sharedInstance().registerComponent(this);
                }
        }
        /**
         * Called when the tree selection change.
         * Update the tree editable state
         */
        public void onSelectionChange() {
                TreePath[] Selected = getSelectionPaths();
                
                if(Selected!=null) {
                        TreePath firstSelected = Selected[Selected.length-1];
                        Object comp = firstSelected.getLastPathComponent();
                        if(comp instanceof AbstractTreeNode) {
                                if(!isEditing()) {
                                        setEditable(((AbstractTreeNode)comp).isEditable());
                                }
                        }
                }
        }
         private boolean contains(TreePath[] selectionPaths, TreePath path) {
                for (TreePath treePath : selectionPaths) {
                        Object[] objectPath = treePath.getPath();
                        Object[] testPath = path.getPath();
                        if (objectPath.length != testPath.length) {
                                return false;
                        } else {
                                for (int i = 0; i < testPath.length; i++) {
                                        if (testPath[i] != objectPath[i]) {
                                                return false;
                                        }
                                }
                        }
                        return true;
                }
                return false;
        }
        /**
         * Event on Tree, called by listener
         * @param evt 
         */
        public void onMouseEvent(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                        //Update selection
                        TreePath path = getPathForLocation(evt.getX(), evt.getY());
                        TreePath[] selectionPaths = getSelectionPaths();
                        if ((selectionPaths != null) && (path != null)) {
                                if (!contains(selectionPaths, path)) {
                                        if (evt.isControlDown()) {
                                                addSelectionPath(path);
                                        } else {
                                                setSelectionPath(path);
                                        }
                                }
                        } else {
                                setSelectionPath(path);
                        }
                        //Show popup
                        makePopupMenu().show(evt.getComponent(),
                                evt.getX(), evt.getY());
                }
        }
        
        /**
         * The user select the menu rename
         */
        public void onRenameItem() {
                TreePath[] paths = getSelectionPaths();
                if(paths!=null && paths.length==1) {
                        super.startEditingAtPath(paths[0]);
                }
                
        }
        /**
         * Fetch all selected items to make a pop-up menu
         * @return 
         */
        private JPopupMenu makePopupMenu() {
                JPopupMenu menu = new JPopupMenu();
                TreePath[] paths = getSelectionPaths();
                if(paths!=null) {
                        // Generic action on single TreeNode
                        if(paths.length == 1) {
                                Object component = paths[0].getLastPathComponent();
                                if(component instanceof AbstractTreeNode) {
                                        AbstractTreeNode aTreeNode = (AbstractTreeNode) component;
                                        if(aTreeNode.isEditable()) {
                                                JMenuItem editMenu = new JMenuItem(I18N.tr("Rename"));
                                                editMenu.addActionListener(
                                                        EventHandler.create(ActionListener.class,this,"onRenameItem"));
                                                editMenu.setActionCommand("rename");
                                                menu.add(editMenu);
                                        }
                                }
                        }
                        for(TreePath treePath : paths) {
                                Object component = treePath.getLastPathComponent();
                                // All nodes
                                if(component instanceof MutableTreeNode) {
                                        MutableTreeNode node = (MutableTreeNode)component;
                                        for(TreeNodeFileFactory fact : getFactories()) {
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
        
        // Map of extensions
        private Map<String,TreeNodeFileFactory> factories =
                new HashMap<String,TreeNodeFileFactory>();
        
        
        @Override
        public void addFactory(String extension, TreeNodeFileFactory factory) {
                factories.put(extension.toLowerCase(), factory);
        }
        
        @Override
        public Collection<TreeNodeFileFactory> getFactories() {
                return factories.values();
        }      
        
        @Override
        public AbstractTreeNode create(File filePath) {
                TreeNodeFileFactory factory = factories.get(FilenameUtils.getExtension(filePath.getName()).toLowerCase());
                if(factory!=null) {
                        return factory.create(filePath);
                } else {
                        return null;
                }
        }
}

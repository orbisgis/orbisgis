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

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.apache.log4j.Logger;
import org.orbisgis.sif.common.MenuCommonFunctions;
import org.orbisgis.view.components.resourceTree.EnumIterator;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

        
/**
 * Represent a folder in the file system
 * @author Nicolas Fortin
 */
public final class TreeNodeFolder extends AbstractTreeNode implements PopupTreeNode, TreeNodePath {
        private List<MutableTreeNode> childs = new ArrayList<MutableTreeNode>();
        private File folderPath;
        private static final Logger LOGGER = Logger.getLogger(TreeNodeFolder.class);
        private static final I18n I18N = I18nFactory.getI18n(TreeNodeFolder.class);
        private TreeNodeMapFactoryManager factoryManager;

        /**
         * @param folderPath
         * @param factoryManager 
         * @throws IllegalArgumentException If the provided path represent a file
         */
        public TreeNodeFolder(File folderPath,TreeNodeMapFactoryManager factoryManager) {
                this.factoryManager = factoryManager;
                this.folderPath = folderPath;
                if(!folderPath.isDirectory()) {
                        throw new IllegalArgumentException("The file path must be a directory");
                }
                setLabel(folderPath.getName());
        }
        /**
         * Read the file system and insert the new files and folders
         */
        public final void updateTree() {
                Map<String,AbstractTreeNode> existingChilds = new HashMap<String,AbstractTreeNode>();
                for(MutableTreeNode child : childs) {
                        if(child instanceof TreeNodePath) {
                                existingChilds.put(
                                        ((TreeNodePath)child).getFilePath()
                                        .getName(),
                                        (AbstractTreeNode)child);
                        }
                }
                List<AbstractTreeNode> oldChilds = getChildren();
                //Clear the child list
                childs = new ArrayList<MutableTreeNode>(oldChilds.size());
                
                //Find new files and sub-folders
                try {
                        String[] list = folderPath.list();
                        for(String childPath : list) {
                                File newChild = new File(folderPath,childPath);
                                AbstractTreeNode existingChild = existingChilds.get(newChild.getName());
                                if(existingChild==null) {
                                        if(newChild.isDirectory()) {
                                                TreeNodeFolder subDir = new TreeNodeFolder(newChild,factoryManager);
                                                model.insertNodeInto(subDir, this, getChildCount());
                                                subDir.updateTree();
                                        } else {
                                                TreeNodeMapElement child = factoryManager.create(newChild);
                                                if(child == null) {
                                                        //Not find appropriate reader for the file
                                                } else {
                                                        if(child instanceof AbstractTreeNode) {
                                                                model.insertNodeInto((AbstractTreeNode)child,this, getChildCount());
                                                        } else {
                                                                LOGGER.error("Illegal factory return object, it must be a extension of AbstractTreeNode");
                                                        }
                                                }
                                        }
                                } else {
                                        childs.add(existingChild);
                                        if(existingChild instanceof TreeNodeFolder) {
                                                ((TreeNodeFolder)existingChild).updateTree();
                                        }
                                }
                        }
                } catch( SecurityException ex) {
                        LOGGER.error(I18N.tr("Cannot list the directory content"),ex);
                }
        }        
        private void internalInsert(AbstractTreeNode mtn, int i) {
                childs.add(i, mtn);
                mtn.setParent(this);
                mtn.setModel(model);
        }

        @Override
        public void insert(MutableTreeNode mtn, int i) {
                internalInsert((AbstractTreeNode)mtn,i);
        }

        @Override
        public void remove(int i) {
                childs.remove(i);
        }

        @Override
        public void remove(MutableTreeNode mtn) {
                childs.remove(mtn);
        }

        @Override
        public void setUserObject(Object o) {
                //User set the folder name
        }


        @Override
        public TreeNode getChildAt(int i) {
                return childs.get(i);
        }

        @Override
        public int getChildCount() {
                return childs.size();
        }

        @Override
        public int getIndex(TreeNode tn) {
                return childs.indexOf(tn);
        }

        @Override
        public boolean getAllowsChildren() {
                return true;
        }

        @Override
        public boolean isLeaf() {
                return false;
        }

        @Override
        public Enumeration<MutableTreeNode> children() {
                return new EnumIterator<MutableTreeNode>(childs.iterator());
        }

        @Override
        public void feedPopupMenu(JPopupMenu menu) {
                JMenuItem updateMenu = new JMenuItem(I18N.tr("Update"), OrbisGISIcon.getIcon("arrow_refresh"));
                updateMenu.setToolTipText(I18N.tr("Update the content of this folder from the file system"));
                updateMenu.setActionCommand("TreeNodeFolder:Update");
                updateMenu.addActionListener(
                        EventHandler.create(ActionListener.class,
                        this,"updateTree"));
                MenuCommonFunctions.updateOrInsertMenuItem(menu, updateMenu);
        }

        @Override
        public File getFilePath() {
                if(parent instanceof TreeNodePath) {
                        File parentPath = ((TreeNodePath)parent).getFilePath();
                        return new File(parentPath,folderPath.getName());
                } else {
                        return folderPath;
                }
        }
        
        List<AbstractTreeNode> getChildren() {
                List<AbstractTreeNode> childsRet = new ArrayList<AbstractTreeNode>(getChildCount());
                for(MutableTreeNode child : childs) {
                        childsRet.add((AbstractTreeNode)child);
                }
                return childsRet;
        }
        
}

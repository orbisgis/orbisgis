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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.common.MenuCommonFunctions;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

        
/**
 * Represent a folder in the file system.
 * @author Nicolas Fortin
 */
public class TreeNodeFolder extends AbstractTreeNodeContainer implements PopupTreeNode, TreeNodePath, DropDestinationTreeNode, DragTreeNode {
        private File folderPath;
        private static final Logger LOGGER = Logger.getLogger("gui." + TreeNodeFolder.class);
        private static final I18n I18N = I18nFactory.getI18n(TreeNodeFolder.class);
        private TreeNodeFileFactoryManager factoryManager;

        /**
         * @param folderPath
         * @param factoryManager 
         * @throws IllegalArgumentException If the provided path represent a file
         */
        public TreeNodeFolder(File folderPath,TreeNodeFileFactoryManager factoryManager) {
                this.factoryManager = factoryManager;
                this.folderPath = folderPath;
                if(!folderPath.isDirectory()) {
                        throw new IllegalArgumentException("The file path must be a directory");
                }
                setLabel(folderPath.getName());
        }

        @Override
        public void setParent(MutableTreeNode mtn) {
                super.setParent(mtn);
                // Only sub folder can be renamed
                setEditable(mtn instanceof TreeNodeFolder);
        }
        
        
        /**
         * Read the file system and insert the new files and folders
         */
        public void updateTree() {    
                List<String> fsList;
                try {
                        fsList = new ArrayList<String>(Arrays.asList(getFilePath().list()));
                } catch( SecurityException ex) {
                        LOGGER.error(I18N.tr("Cannot list the directory content"),ex);
                        return;
                }
                // Find deleted sub-elements, and update existing sub-folders 
                List<MutableTreeNode> childrenToRemove = new ArrayList<MutableTreeNode>(children.size());
                for(MutableTreeNode child : children) {
                        if(child instanceof TreeNodePath) {
                                String childFileName = ((TreeNodePath)child).getFilePath()
                                        .getName();
                                if(!fsList.contains(childFileName)) {
                                        childrenToRemove.add(child);
                                } else {
                                        fsList.remove(childFileName);
                                        if(child instanceof TreeNodeFolder) {
                                                ((TreeNodeFolder)child).updateTree();
                                        }
                                }
                        }
                }
                // Effective children removal
                for(MutableTreeNode child : childrenToRemove) {
                        model.removeNodeFromParent(child);
                }
                // Add the new children, and update new sub-folders 
                for(String childPath : fsList) {
                        File newChild = new File(getFilePath(), childPath);
                        if (newChild.isDirectory()) {
                                TreeNodeFolder subDir = new TreeNodeFolder(newChild, factoryManager);
                                model.insertNodeInto(subDir, this, getChildCount());
                                subDir.updateTree();
                        } else {
                                AbstractTreeNode child = factoryManager.create(newChild);
                                if (child != null) {
                                        model.insertNodeInto(child, this, getChildCount());
                                }
                        }
                }
        }        

        @Override
        public void setUserObject(Object o) {
                //User set the folder name
                File curPath = getFilePath();
                File dest = new File(curPath.getParentFile(),o.toString());
                if(dest.exists()) {
                        LOGGER.error("The specified folder already exists");
                        return;
                }
                if(curPath.renameTo(dest)) {
                        folderPath = dest;
                        setLabel(folderPath.getName());
                } else {
                        LOGGER.error("The specified folder name is not correct");                        
                }
        }
        
        /**
         * File&Folder deletion
         */
        public void onDeleteFolder() {
                if(getFilePath().exists()) {
                        int result = JOptionPane.showConfirmDialog(UIFactory.getMainFrame(),
                                I18N.tr("Are you sure you want to delete permanently the selected files ?"),
                                I18N.tr("Remove the files"),
                                JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                        if(result == JOptionPane.YES_OPTION) {
                                try {
                                        FileUtils.deleteDirectory(getFilePath());
                                } catch(IOException ex) {
                                        LOGGER.error(I18N.tr("Cannot remove the folder {0}",getFilePath().getName()),ex);
                                }                        
                        } else {
                                return;
                        }
                }
                model.removeNodeFromParent(this);
        }
        /**
         * Copy the folder path to the ClipBoard
         */
        public void onCopyPath() {
                StringSelection pathString = new StringSelection(getFilePath().getAbsolutePath());
                try {
                        Clipboard clipboard = 
                                Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(pathString, pathString);
                        LOGGER.info(I18N.tr("The path {0} has been copied to the clipboard",getFilePath().getAbsolutePath()));
                } catch (Throwable ex) {
                        LOGGER.error(I18N.tr("Could not copy the folder path to the clipboard"),ex);
                }
        }
        
        @Override
        public void feedPopupMenu(JPopupMenu menu) {
                if (menu.getComponentCount() > 0) {
                        menu.addSeparator();
                }
                // Open the folder
                JMenuItem copyPathMenu = new JMenuItem(I18N.tr("Copy the path"));
                copyPathMenu.setToolTipText(I18N.tr("Copy the folder path in the clipboard"));
                copyPathMenu.setActionCommand("TreeNodeFolder:CopyPath");
                copyPathMenu.addActionListener(
                        EventHandler.create(ActionListener.class,
                        this, "onCopyPath"));
                MenuCommonFunctions.updateOrInsertMenuItem(menu, copyPathMenu);
                // Read the file system to update the tree
                JMenuItem updateMenu = new JMenuItem(I18N.tr("Update"),
                        OrbisGISIcon.getIcon("arrow_refresh"));
                updateMenu.setToolTipText(I18N.tr("Update the content of this folder from the file system"));
                updateMenu.setActionCommand("Update");
                updateMenu.addActionListener(
                        EventHandler.create(ActionListener.class,
                        this, "updateTree"));
                MenuCommonFunctions.updateOrInsertMenuItem(menu, updateMenu);
                // Add a new Sub Folder
                JMenuItem newSubFolder = new JMenuItem(I18N.tr("New folder"),
                        OrbisGISIcon.getIcon("folder_add"));
                newSubFolder.setToolTipText(I18N.tr("Create a sub-folder"));
                newSubFolder.setActionCommand("TreeNodeFolder:newSubFolder");
                newSubFolder.addActionListener(
                        EventHandler.create(ActionListener.class,
                        this, "onNewSubFolder"));
                MenuCommonFunctions.updateOrInsertMenuItem(menu, newSubFolder);
                // Remove the folder
                //The root folder cannot be removed
                if(parent instanceof TreeNodeFolder) {
                        JMenuItem folderRemove = new JMenuItem(I18N.tr("Delete"),
                                OrbisGISIcon.getIcon("remove"));
                        folderRemove.setToolTipText(I18N.tr("Remove permanently the folder"));
                        folderRemove.setActionCommand("delete");
                        folderRemove.addActionListener(
                        EventHandler.create(ActionListener.class,
                        this, "onDeleteFolder"));
                        MenuCommonFunctions.updateOrInsertMenuItem(menu,folderRemove,true);
                }
        }

        /**
         * Create a sub folder, the folder name is given through an input dialog
         */
        public void onNewSubFolder() {                
                String folderName = JOptionPane.showInputDialog(UIFactory.getMainFrame(), I18N.tr("Enter the folder name"), I18N.tr("Folder"));
                if(folderName!=null) {
                        File newFolderPath = getUniqueFileName(new File(getFilePath(),folderName));
                        try {
                                if(!newFolderPath.mkdir()) {
                                        LOGGER.error(I18N.tr("The folder creation has failed"));
                                } else {
                                        updateTree();
                                }
                        } catch(Throwable ex) {
                                LOGGER.error(I18N.tr("The folder creation has failed"),ex);
                        }
                }
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
        /**
         * Return the children of this folder
         * @return 
         */
        public List<AbstractTreeNode> getChildren() {
                List<AbstractTreeNode> childrenRet = new ArrayList<AbstractTreeNode>(getChildCount());
                for(MutableTreeNode child : children) {
                        childrenRet.add((AbstractTreeNode)child);
                }
                return childrenRet;
        }

        @Override
        public boolean canImport(TransferSupport ts) {
                return ts.isDataFlavorSupported(TransferableNodePaths.PATHS_FLAVOR) || ts.isDataFlavorSupported(TransferableFileContent.FILE_CONTENT_FLAVOR);
        }

        /**
         * Extract all components of the path to compare with the second argument
         * @param path
         * @param compPath Absolute path
         * @return 
         */
        private static boolean hasAParent(File path,String compPath) {
                File parent = path.getParentFile();
                while(parent!=null) {
                        if(compPath.equals(parent.getAbsolutePath())) {
                                return true;
                        }
                        parent = parent.getParentFile();
                }
                return false;
        }
        

        /**
         * Extract all components of the path to compare with the second argument
         * @param path
         * @param paths Set of Absolute path
         * @return 
         */
        private static boolean hasAParentInPathList(File path,Set<String> paths) {
                File parent = path.getParentFile();
                while(parent!=null) {
                        if(paths.contains(parent.getAbsolutePath())) {
                                return true;
                        }
                        parent = parent.getParentFile();
                }
                return false;
        }
        /**
         * Return the appropriate file path to not overwrite existing files
         * @param basePath Path of the file
         * @return Non existing file path
         */
        private static File getUniqueFileName(final File fullPath) {
                int cpt = 0;
                final String fileNameWithExt = fullPath.getName();
                final File basePath = fullPath.getParentFile();
                final String extension = FilenameUtils.getExtension(fileNameWithExt);
                final String baseName = FilenameUtils.getBaseName(fileNameWithExt);
                File fileName = fullPath;
                while (fileName.exists()) {
                        String newFileName = baseName
                                +"_"+(cpt++)+"."
                                + extension;
                        fileName = new File(basePath,newFileName);
                }
                return fileName;
        }
        
        /**
         * Create a file from a Reader instance in this folder
         * @param ts
         * @param flavor
         * @return 
         */
        private boolean importReader(Transferable tf, DataFlavor flavor) {
                try {
                        // From this transferable, a file can be created
                        Object transferData = tf.getTransferData(flavor);
                        if(!(transferData instanceof Reader)) {
                                return false;
                        }
                        BufferedReader br = new BufferedReader((Reader)transferData );
                        File fileName;
                        if (transferData instanceof TransferableFileContent) {
                                // The filename is given by the drag source
                                fileName = new File(getFilePath(), ((TransferableFileContent) transferData).getFileNameHint());
                        } else {
                                // The filename must be given by the user
                                String contentFileName = JOptionPane.showInputDialog(UIFactory.getMainFrame(), I18N.tr("Enter the folder name"), I18N.tr("Folder"));
                                if (contentFileName != null) {
                                        fileName = new File(getFilePath(), contentFileName);
                                } else {
                                        return false;
                                }
                        }
                        // If the file exists found a new one
                        fileName = getUniqueFileName(fileName);
                        FileWriter writer = new FileWriter(fileName);
                        String res = br.readLine();
                        while (res != null) {
                                writer.write(res+"\n");
                                res = br.readLine();
                        }
                        writer.close();
                        return true;
                } catch (UnsupportedFlavorException ex) {
                        LOGGER.error(ex.getLocalizedMessage(), ex);
                        return false;
                } catch (IOException ex) {
                        LOGGER.error(ex.getLocalizedMessage(), ex);
                        return false;
                }
        }
        @Override
        public boolean importData(TransferSupport ts) {
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                if(ts.isDataFlavorSupported(TransferableNodePaths.PATHS_FLAVOR)) {
                        // Move Nodes and Move Files
                        bm.nonBlockingBackgroundOperation(new DropTransferable(ts.getTransferable(), this));
                        return true;                
                } else {
                        DataFlavor[] flavors = ts.getDataFlavors();
                        for(DataFlavor flavor : flavors) {
                                if(flavor.isRepresentationClassReader()) {
                                        bm.nonBlockingBackgroundOperation(new DropTransferable(ts.getTransferable(), this));
                                        return true;
                                }
                        }
                        return false;
                }
        }

        @Override
        public boolean completeTransferable(TransferableList transferable) {
                if(parent instanceof TreeNodeFolder) {
                        transferable.addTransferable(new TransferableNodePaths(this));
                        return true;
                } else {
                        return false;
                }
        }
        
        private void transferTreeNodePath(List<TreeNodePath> nodePath) {
                // Moved paths set
                Set<String> paths = new HashSet<String>();
                for (TreeNodePath treeNode : nodePath) {
                        File treeFilePath = treeNode.getFilePath();
                        paths.add(treeFilePath.getAbsolutePath());
                }
                // Process folder and files moves
                for (TreeNodePath treeNode : nodePath) {
                        // Ignore if a parent path is already on the list
                        // or if this folder node is the child of a transfered path
                        File treeFilePath = treeNode.getFilePath();
                        if (!hasAParentInPathList(treeFilePath, paths)
                                && !hasAParent(getFilePath(), treeFilePath.getAbsolutePath())) {
                                try {
                                        File dest = new File(getFilePath(), treeFilePath.getName());
                                        if (!dest.exists()) {
                                                // Move the folder
                                                FileUtils.moveToDirectory(treeFilePath, getFilePath(), false);
                                        } else {
                                                LOGGER.warn(I18N.tr("Destination file {0} already exists, cannot move {1}", dest, treeFilePath));
                                        }
                                } catch (IOException ex) {
                                        LOGGER.error(ex.getLocalizedMessage(), ex);
                                        return;
                                }
                        }
                }
        }
        
        /**
         * Process drop operation on this node
         */
        private static class DropTransferable implements BackgroundJob {
                Transferable transferable;
                TreeNodeFolder folderNode;

                public DropTransferable(Transferable transferable, TreeNodeFolder folderNode) {
                        this.transferable = transferable;
                        this.folderNode = folderNode;
                }
                
                @Override
                public void run(ProgressMonitor pm) {

                        if (transferable.isDataFlavorSupported(TransferableNodePaths.PATHS_FLAVOR)) {
                                // Move Nodes and Move Files
                                List<TreeNodePath> nodePath = new ArrayList<TreeNodePath>();
                                try {
                                        Object objTrans = transferable.
                                                getTransferData(TransferableNodePaths.PATHS_FLAVOR);
                                        if (objTrans instanceof List) {
                                                nodePath = (List<TreeNodePath>) objTrans;
                                        }
                                } catch (UnsupportedFlavorException ex) {
                                        LOGGER.error(ex.getLocalizedMessage(), ex);
                                        return;
                                } catch (IOException ex) {
                                        LOGGER.error(ex.getLocalizedMessage(), ex);
                                        return;
                                }
                                folderNode.transferTreeNodePath(nodePath);   
                        } else {
                                DataFlavor[] flavors = transferable.getTransferDataFlavors();
                                for (DataFlavor flavor : flavors) {
                                        if (flavor.isRepresentationClassReader()) {
                                                folderNode.importReader(transferable, flavor);
                                                break;
                                        }
                                }
                        }
                        SwingUtilities.invokeLater(new UpdateTree(folderNode));
                }

                @Override
                public String getTaskName() {
                        return I18N.tr("Drop the content in the folder..");
                }
                
        }
        /**
         * Update the whole folder tree
         */
        private static class UpdateTree implements Runnable {
                TreeNodeFolder folderNode;

                public UpdateTree(TreeNodeFolder folderNode) {
                        this.folderNode = folderNode;
                }
                
                @Override
                public void run() {
                        // Retrieve the top folder
                        TreeNodeFolder topFolder = folderNode;
                        TreeNode cursor = folderNode;
                        while(cursor instanceof TreeNodeFolder) {
                                topFolder = (TreeNodeFolder) cursor;
                                cursor = cursor.getParent();
                        }
                        topFolder.updateTree();
                }                
        }
}

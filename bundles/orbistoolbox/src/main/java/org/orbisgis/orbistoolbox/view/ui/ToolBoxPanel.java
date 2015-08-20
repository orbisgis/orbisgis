/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.view.ui;

import org.orbisgis.orbistoolbox.controller.ProcessManager;
import org.orbisgis.orbistoolbox.model.Metadata;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.view.utils.ToolBoxIcon;
import org.orbisgis.orbistoolbox.view.utils.TreeNodeWps;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.sif.components.fstree.CustomTreeCellRenderer;
import org.orbisgis.sif.components.fstree.FileTree;
import org.orbisgis.sif.components.fstree.FileTreeModel;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.*;
import java.beans.EventHandler;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel of the tool box containing the JTree of process.
 *
 * @author Sylvain PALOMINOS
 **/

public class ToolBoxPanel extends JPanel {

    private static final String ADD_SOURCE = "ADD_SOURCE";
    private static final String RUN_SCRIPT = "RUN_SCRIPT";
    private static final String REFRESH_SOURCE = "REFRESH_SOURCE";
    private static final String REMOVE = "REMOVE";

    private final static String CATEGORY_MODEL = "Category";
    private final static String FILE_MODEL = "File";

    private final static String UNDEFINED = "Undefined";

    /** ComboBox with the different model of the tree */
    private JComboBox<String> treeNodeBox;

    /** Reference to the toolbox.*/
    private ToolBox toolBox;
    /** Root node of the JTree */
    private TreeNodeWps root;

    /** Model of the Jtree */
    private FileTreeModel fileModel;
    /** Model of the Jtree */
    private FileTreeModel categoryModel;

    /** JTree */
    private FileTree tree;
    /** Node that permit to add a process on double click */
    private TreeNodeWps addWps;

    /** Action available in the right click popup on selecting the panel */
    private ActionCommands popupGlobalActions;
    /** Action available in the right click popup on selecting a node */
    private ActionCommands popupNodeActions;
    /** Action available in the right click popup on selecting a process (leaf) */
    private ActionCommands popupLeafActions;

    public ToolBoxPanel(ToolBox toolBox){
        super(new BorderLayout());

        this.toolBox = toolBox;

        this.addWps = new TreeNodeWps();
        addWps.setIsCustomIcon(true);
        addWps.setCustomIcon("folder_add");
        addWps.setUserObject("Add folder");
        addWps.setcanBeLeaf(false);
        addWps.setValid(false);

        TreeNodeWps fileRoot = new TreeNodeWps();
        fileRoot.setUserObject(FILE_MODEL);
        fileRoot.setIsRoot(true);
        fileModel = new FileTreeModel(fileRoot);

        TreeNodeWps categoryRoot = new TreeNodeWps();
        categoryRoot.setUserObject(CATEGORY_MODEL);
        categoryRoot.setIsRoot(true);
        categoryModel = new FileTreeModel(categoryRoot);

        treeNodeBox = new JComboBox<>();
        treeNodeBox.addItem(FILE_MODEL);
        treeNodeBox.addItem(CATEGORY_MODEL);
        treeNodeBox.setSelectedItem(CATEGORY_MODEL);
        treeNodeBox.addActionListener(EventHandler.create(ActionListener.class, this, "onModelSelected"));

        tree = new FileTree();
        tree.setRootVisible(false);
        tree.setScrollsOnExpand(true);
        tree.setCellRenderer(new CustomTreeCellRenderer(tree));
        tree.addMouseListener(EventHandler.create(MouseListener.class, this, "onMouseClicked", "", "mouseClicked"));

        JScrollPane treeScrollPane = new JScrollPane(tree);
        this.add(treeScrollPane, BorderLayout.CENTER);
        this.add(treeNodeBox, BorderLayout.PAGE_END);

        onModelSelected();

        popupGlobalActions = new ActionCommands();
        popupGlobalActions.setAccelerators(this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        createPopupActions(toolBox);
    }

    /**
     * Returns the selected node.
     * @return The selected node.
     */
    public TreeNodeWps getSelectedNode(){
        return (TreeNodeWps)tree.getLastSelectedPathComponent();
    }

    /**
     * Action done when the mouse is clicked.
     * @param event Mouse event.
     */
    public void onMouseClicked(MouseEvent event){
        //Test if it is a right click
        if(event.getButton() == MouseEvent.BUTTON3) {
            JPopupMenu popupMenu = new JPopupMenu();
            //find what was clicked to give to the popup the good action
            if(event.getSource().equals(tree)){
                if(tree.getLastSelectedPathComponent() == null ||
                        tree.getLastSelectedPathComponent().equals(addWps) ||
                        tree.getLastSelectedPathComponent().equals(fileModel.getRoot()) ||
                        tree.getLastSelectedPathComponent().equals(categoryModel.getRoot())){
                    popupGlobalActions.copyEnabledActions(popupMenu);
                }
                else {
                    if (((TreeNodeWps) tree.getLastSelectedPathComponent()).isLeaf() &&
                            ((TreeNodeWps) tree.getLastSelectedPathComponent()).canBeLeaf()) {
                        popupLeafActions.copyEnabledActions(popupMenu);
                    } else {
                        popupNodeActions.copyEnabledActions(popupMenu);
                    }
                }
            }
            if (popupMenu.getComponentCount()>0) {
                popupMenu.show(event.getComponent(), event.getX(), event.getY());
            }
        }
        else {
            TreeNodeWps selectedNode = (TreeNodeWps) ((FileTree)event.getSource()).getLastSelectedPathComponent();
            //if a simple click is done
            if(event.getClickCount() == 1 && selectedNode.isLeaf() && selectedNode.canBeLeaf()) {
                selectedNode.setValid(toolBox.checkProcess(selectedNode.getFilePath()));
            }
            //If a double click is done
            if (event.getClickCount() == 2) {
                if (selectedNode != null) {
                    if (selectedNode.equals(addWps)) {
                        toolBox.addNewLocalSource();
                    }
                    if (selectedNode.isValid() && selectedNode.isLeaf()) {
                        toolBox.openProcess();
                    }
                }
            }
        }
    }

    /**
     * Action done when a model is selected in the comboBox.
     */
    public void onModelSelected(){
        if(treeNodeBox.getSelectedItem().equals(FILE_MODEL)){
            if(((TreeNodeWps) categoryModel.getRoot()).isNodeChild(addWps)) {
                ((TreeNodeWps) categoryModel.getRoot()).add(addWps);
            }
            ((TreeNodeWps) fileModel.getRoot()).add(addWps);
            root = (TreeNodeWps) fileModel.getRoot();
            tree.setModel(fileModel);
        }
        else if(treeNodeBox.getSelectedItem().equals(CATEGORY_MODEL)){
            if(((TreeNodeWps) fileModel.getRoot()).isNodeChild(addWps)) {
                ((TreeNodeWps) fileModel.getRoot()).add(addWps);
            }
            ((TreeNodeWps) categoryModel.getRoot()).add(addWps);
            root = (TreeNodeWps) categoryModel.getRoot();
            tree.setModel(categoryModel);
        }
    }

    /**
     * Adds a process in the category model.
     * @param p Process to add.
     * @param f Process file.
     */
    public void addScriptInCategoryModel(Process p, File f){
        String[] categories = decodeCategories(p);

        TreeNodeWps root = (TreeNodeWps) categoryModel.getRoot();
        TreeNodeWps script = new TreeNodeWps();
        script.setFilePath(f);
        TreeNodeWps categoryNode = getSubNode(categories[0], root);
        if(categoryNode == null){
            categoryNode = new TreeNodeWps();
            categoryNode.setUserObject(categories[0]);
            categoryNode.setValid(false);
            root.add(categoryNode);
        }

        if(categories[1] != null){
            TreeNodeWps subCategoryNode = getSubNode(categories[1], categoryNode);
            if(subCategoryNode == null){
                subCategoryNode = new TreeNodeWps();
                subCategoryNode.setUserObject(categories[1]);
                subCategoryNode.setValid(false);
                categoryNode.add(subCategoryNode);
            }

            if(categories[2] != null){
                TreeNodeWps subSubCategoryNode = getSubNode(categories[2], subCategoryNode);
                if(subSubCategoryNode == null){
                    subSubCategoryNode = new TreeNodeWps();
                    subSubCategoryNode.setUserObject(categories[2]);
                    subCategoryNode.add(subSubCategoryNode);
                    subSubCategoryNode.setValid(false);
                }
                if(!isNodeExisting(script.getFilePath(), subSubCategoryNode)) {
                    subSubCategoryNode.add(script);
                }
            }
            else {
                if(!isNodeExisting(script.getFilePath(), subCategoryNode)) {
                    subCategoryNode.add(script);
                }
            }
        }
        else {
            if(!isNodeExisting(script.getFilePath(), categoryNode)) {
                categoryNode.add(script);
            }
        }
        this.reload();
    }

    /**
     * Tests if the parent node contain a child representing the given file.
     * @param file File to test.
     * @param parent Parent to test.
     * @return True if the parent contain the file.
     */
    private boolean isNodeExisting(File file, TreeNodeWps parent){
        boolean exist = false;
        for(int l=0; l<parent.getChildCount(); l++){
            if(((TreeNodeWps)parent.getChildAt(l)).getFilePath().equals(file)){
                exist = true;
            }
        }
        return exist;
    }

    /**
     * Gets the the child node of the parent node which has the given userObject.
     * @param nodeUserObject UserObject to test.
     * @param parent Parent to analyse.
     * @return The child which has the given userObject. Null if not found.
     */
    private TreeNodeWps getSubNode(String nodeUserObject, TreeNodeWps parent){
        TreeNodeWps child = null;
        for(int i = 0; i < parent.getChildCount(); i++){
            if(((TreeNodeWps)parent.getChildAt(i)).getUserObject().equals(nodeUserObject)){
                child = (TreeNodeWps)parent.getChildAt(i);
            }
        }
        return child;
    }

    /**
     * Returns the categories of a process.
     * @param p Process to decode.
     * @return List of categories.
     */
    private String[] decodeCategories(Process p){
        String[] categories = new String[3];
        categories[0] = UNDEFINED;
        categories[1] = null;
        categories[2] = null;
        if(p != null && p.getMetadata() != null) {
            for (Metadata m : p.getMetadata()) {
                if (m.getRole().equals(URI.create("orbisgis:wps:utils:category"))) {
                    categories[0] = m.getTitle();
                }
                if (m.getRole().equals(URI.create("orbisgis:wps:utils:subCategory"))) {
                    categories[1] = m.getTitle();
                }
                if (m.getRole().equals(URI.create("orbisgis:wps:utils:subSubCategory"))) {
                    categories[2] = m.getTitle();
                }
            }
        }
        return categories;
    }

    /**
     * Reload the JTree.
     */
    public void reload(){
        if(root.isNodeChild(addWps)) {
            root.remove(addWps);
        }
        root.add(addWps);
        categoryModel.reload();
        fileModel.reload();
        this.revalidate();
    }

    /**
     * Adds a local source. Open the given directory and find all the groovy script contained.
     * @param directory Directory to analyse.
     * @param processManager ProcessManager.
     */
    public void addLocalSource(File directory, ProcessManager processManager) {
        addScriptInFileModel(directory);
        for(File f : directory.listFiles()) {
            if(f.getName().endsWith(".groovy")) {
                addScriptInCategoryModel(processManager.getProcess(f), f);
            }
        }
    }

    /**
     * Adds a script in the file model.
     * @param file Script file to add.
     */
    private void addScriptInFileModel(File file){
        TreeNodeWps root = (TreeNodeWps) fileModel.getRoot();

        boolean exists = false;
        for(int i=0; i<root.getChildCount(); i++){
            if(((TreeNodeWps)root.getChildAt(i)).getUserObject().equals(file.getName())){
                exists = true;
            }
        }
        TreeNodeWps source = new TreeNodeWps();
        source.setcanBeLeaf(false);
        source.setValid(false);
        if(!exists){
            boolean isScript = false;
            source.setUserObject(file.getName());
            source.setFilePath(file);
            root.add(source);
            for(File f : getAllWpsScript(file)){
                TreeNodeWps script = new TreeNodeWps();
                script.setUserObject(f.getName().replace(".groovy", ""));
                script.setFilePath(f);
                source.add(script);
                isScript = true;
            }
            source.setValid(isScript);
        }
        this.reload();
    }

    /**
     * Returns all the WPS script file contained by the directory.
     * @param directory Directory to analyse.
     * @return The list of files.
     */
    private List<File> getAllWpsScript(File directory) {
        List<File> scriptList = new ArrayList<>();
        if (directory.exists() && directory.isDirectory()) {
            for (File f : directory.listFiles()) {
                if (f != null) {
                    if (f.isFile() && f.getName().endsWith(".groovy")) {
                        scriptList.add(f);
                    }
                }
            }
        }
        return scriptList;
    }

    /**
     * Remove the selected node.
     */
    public void removeSelected(){
        TreeNodeWps selected = (TreeNodeWps)tree.getLastSelectedPathComponent();
    }

    public void remove(TreeNodeWps node){
        if(!node.equals(fileModel.getRoot()) && !node.equals(categoryModel.getRoot())){
            removeNodeFromCategoryModel(node);
            removeNodeFromFileModel(node);
            this.reload();
        }
        toolBox.removeProcess(node.getFilePath());
    }

    /**
     * Remove the process represented by the given node from the file model.
     * @param node Node representing the process to remove.
     */
    private void removeNodeFromFileModel(TreeNodeWps node){
        File f = node.getFilePath();
        TreeNodeWps root = (TreeNodeWps)fileModel.getRoot();
        for(int i = 0; i < root.getChildCount(); i++){
            int count = root.getChildAt(i).getChildCount();
            for(int j = 0; j < count; j++){
                TreeNodeWps selected = ((TreeNodeWps)root.getChildAt(i).getChildAt(j));
                if(selected.getFilePath().equals(f)){
                    cleanParentNode(selected, fileModel);
                }
            }
        }
    }

    /**
     * Remove the process represented by the given node from the category model.
     * @param node Node representing the process to remove.
     */
    private void removeNodeFromCategoryModel(TreeNodeWps node){
        File f = node.getFilePath();
        String[] categories = decodeCategories(toolBox.getProcessManager().getProcess(f));
        TreeNodeWps root = (TreeNodeWps)categoryModel.getRoot();
        TreeNodeWps parent = getSubNode(categories[0], root);
        if(categories[1] != null){
            parent = getSubNode(categories[1], parent);
            if(categories[2] != null){
                parent = getSubNode(categories[2], parent);
            }
        }

        for(int l = 0; l < parent.getChildCount(); l++){
            if(((TreeNodeWps)parent.getChildAt(l)).getFilePath().equals(f)){
                TreeNodeWps child = (TreeNodeWps)parent.getChildAt(l);
                cleanParentNode(child, categoryModel);
            }
        }
    }

    /**
     * If the given parent node is empty, remove it except if it is the root of the model.
     * Then do the same for its parent.
     * @param node Node to check.
     * @param model Model containing the node.
     */
    private void cleanParentNode(TreeNodeWps node, FileTreeModel model){
        TreeNode[] treeNodeTab = model.getPathToRoot(node);
        model.removeNodeFromParent(node);
        if(treeNodeTab.length>1) {
            TreeNodeWps parent = (TreeNodeWps) treeNodeTab[treeNodeTab.length - 2];
            if (parent != model.getRoot() && parent.isLeaf() && parent.getParent() != null) {
                cleanParentNode(parent, model);
            }
        }
    }

    public void refresh(){
        TreeNodeWps node = (TreeNodeWps) tree.getLastSelectedPathComponent();
        if(node.isLeaf()){
            node.setValid(toolBox.checkProcess(node.getFilePath()));
        }
        if(tree.getModel().equals(categoryModel)){
            for(TreeNodeWps child : getAllLeaf(node)){
                child.setValid(toolBox.checkProcess(child.getFilePath()));
            }
        }
        if(tree.getModel().equals(fileModel)){
            this.remove(node);
            toolBox.addLocalSource(node.getFilePath());
        }
    }

    private List<TreeNodeWps> getAllLeaf(TreeNodeWps node){
        List<TreeNodeWps> nodeList = new ArrayList<>();
        for(int i=0; i<node.getChildCount(); i++){
            TreeNodeWps child = (TreeNodeWps) node.getChildAt(i);
            if(child.isLeaf()){
                nodeList.add(child);
            }
            else{
                nodeList.addAll(getAllLeaf(child));
            }
        }
        return nodeList;
    }

    /**
     * Creates the action for the popup.
     * @param toolBox ToolBox.
     */
    private void createPopupActions(ToolBox toolBox) {
        DefaultAction addSource = new DefaultAction(
                ADD_SOURCE,
                "Add",
                "Add a local source",
                ToolBoxIcon.getIcon("folder_add"),
                EventHandler.create(ActionListener.class, toolBox, "addNewLocalSource"),
                null
        );
        DefaultAction runScript = new DefaultAction(
                RUN_SCRIPT,
                "Run",
                "Run a script",
                ToolBoxIcon.getIcon("execute"),
                EventHandler.create(ActionListener.class, toolBox, "openProcess"),
                null
        );
        DefaultAction refresh_source = new DefaultAction(
                REFRESH_SOURCE,
                "Refresh",
                "Refresh a source",
                ToolBoxIcon.getIcon("refresh"),
                EventHandler.create(ActionListener.class, this, "refresh"),
                null
        );
        DefaultAction remove = new DefaultAction(
                REMOVE,
                "Remove",
                "Remove a source or a script",
                ToolBoxIcon.getIcon("remove"),
                EventHandler.create(ActionListener.class, this, "removeSelected"),
                null
        );

        popupGlobalActions = new ActionCommands();
        popupGlobalActions.addAction(addSource);

        popupLeafActions = new ActionCommands();
        popupLeafActions.addAction(addSource);
        popupLeafActions.addAction(runScript);
        popupLeafActions.addAction(refresh_source);
        popupLeafActions.addAction(remove);

        popupNodeActions = new ActionCommands();
        popupNodeActions.addAction(addSource);
        popupNodeActions.addAction(refresh_source);
        popupNodeActions.addAction(remove);
    }
}

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
import org.orbisgis.orbistoolbox.view.utils.*;
import org.orbisgis.orbistoolbox.view.utils.Filter.IFilter;
import org.orbisgis.orbistoolbox.view.utils.Filter.SearchFilter;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.sif.components.filter.DefaultActiveFilter;
import org.orbisgis.sif.components.filter.FilterFactoryManager;
import org.orbisgis.sif.components.fstree.CustomTreeCellRenderer;
import org.orbisgis.sif.components.fstree.FileTree;
import org.orbisgis.sif.components.fstree.FileTreeModel;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.beans.EventHandler;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final static String CATEGORY_MODEL = "Advanced interface";
    private final static String FILE_MODEL = "Simple interface";
    private final static String FILTERED_MODEL = "Filtered";

    private final static String UNDEFINED = "Undefined";

    /** ComboBox with the different model of the tree */
    private JComboBox<String> treeNodeBox;

    /** Reference to the toolbox.*/
    private ToolBox toolBox;

    /** JTree */
    private JTree tree;
    /** Model of the JTree */
    private FileTreeModel fileModel;
    /** Model of the JTree */
    private FileTreeModel categoryModel;
    /** Model of the JTree*/
    private FileTreeModel filteredModel;
    /** Model of the JTree*/
    private FileTreeModel selectedModel;

    /** Action available in the right click popup on selecting the panel */
    private ActionCommands popupGlobalActions;
    /** Action available in the right click popup on selecting a node */
    private ActionCommands popupNodeActions;
    /** Action available in the right click popup on selecting a process (leaf) */
    private ActionCommands popupLeafActions;

    private Map<URI, TreeNodeWps> mapHostNode;

    private static final String LOCALHOST_STRING = "localhost";
    private static final URI LOCALHOST_URI = URI.create(LOCALHOST_STRING);

    private FilterFactoryManager<IFilter,DefaultActiveFilter> filterFactoryManager;

    public ToolBoxPanel(ToolBox toolBox){
        super(new BorderLayout());

        this.toolBox = toolBox;

        mapHostNode = new HashMap<>();
        TreeNodeWps localhostNode = new TreeNodeWps();
        localhostNode.setNodeType(TreeNodeWps.HOST_LOCAL);
        localhostNode.setUserObject(LOCALHOST_STRING);
        mapHostNode.put(LOCALHOST_URI, localhostNode);

        TreeNodeWps fileRoot = new TreeNodeWps();
        fileRoot.setUserObject(FILE_MODEL);
        fileModel = new FileTreeModel(fileRoot);
        fileModel.insertNodeInto(localhostNode, fileRoot, 0);

        TreeNodeWps categoryRoot = new TreeNodeWps();
        categoryRoot.setUserObject(CATEGORY_MODEL);
        categoryModel = new FileTreeModel(categoryRoot);

        TreeNodeWps filteredRoot = new TreeNodeWps();
        filteredRoot.setUserObject(FILTERED_MODEL);
        filteredModel = new FileTreeModel(filteredRoot);

        treeNodeBox = new JComboBox<>();
        treeNodeBox.addItem(FILE_MODEL);
        treeNodeBox.addItem(CATEGORY_MODEL);
        treeNodeBox.setSelectedItem(FILE_MODEL);
        treeNodeBox.addActionListener(EventHandler.create(ActionListener.class, this, "onModelSelected"));

        tree = new FileTree();
        tree.setRootVisible(false);
        tree.setScrollsOnExpand(true);
        tree.setToggleClickCount(1);
        tree.setCellRenderer(new CustomTreeCellRenderer(tree));
        tree.addMouseListener(EventHandler.create(MouseListener.class, this, "onMouseClicked", "", "mouseReleased"));

        JScrollPane treeScrollPane = new JScrollPane(tree);
        this.add(treeScrollPane, BorderLayout.CENTER);
        this.add(treeNodeBox, BorderLayout.PAGE_END);

        popupGlobalActions = new ActionCommands();
        popupGlobalActions.setAccelerators(this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        createPopupActions(toolBox);

        //Sets the filter
        filterFactoryManager = new FilterFactoryManager<>();
        FilterFactoryManager.FilterChangeListener refreshFilterListener = EventHandler.create(
                FilterFactoryManager.FilterChangeListener.class,
                this,
                "setFilters",
                "source.getFilters");
        filterFactoryManager.getEventFilterChange().addListener(this, refreshFilterListener);
        filterFactoryManager.getEventFilterFactoryChange().addListener(this, refreshFilterListener);
        this.add(filterFactoryManager.makeFilterPanel(false), BorderLayout.NORTH);
        SearchFilter searchFilter = new SearchFilter();
        filterFactoryManager.registerFilterFactory(searchFilter);
        filterFactoryManager.setUserCanRemoveFilter(false);
        filterFactoryManager.addFilter(new SearchFilter().getDefaultFilterValue());

        tree.setModel(categoryModel);
        onModelSelected();
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
                        tree.getLastSelectedPathComponent().equals(fileModel.getRoot()) ||
                        tree.getLastSelectedPathComponent().equals(categoryModel.getRoot())){
                    popupGlobalActions.copyEnabledActions(popupMenu);
                }
                else {
                    if (((TreeNodeWps) tree.getLastSelectedPathComponent()).isLeaf()) {
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
            if(selectedNode != null) {
                //if a simple click is done
                if (event.getClickCount() == 1) {
                    boolean isValid = false;
                    switch(selectedNode.getNodeType()){
                        case TreeNodeWps.HOST_DISTANT:
                            //TODO : check if the host is reachable an if it contains a WPS service.
                            isValid = true;
                            break;
                        case TreeNodeWps.HOST_LOCAL:
                            //TODO : check if the OrbisGIS WPS script folder is available or not
                            isValid = true;
                            break;
                        case TreeNodeWps.FOLDER:
                            //Check if the folder exists and it it contains some scripts
                            isValid = toolBox.checkFolder(selectedNode.getFilePath());
                            break;
                        case TreeNodeWps.PROCESS:
                            isValid = toolBox.checkProcess(selectedNode.getFilePath());
                            break;
                        case TreeNodeWps.INSTANCE:
                            isValid = true;
                            break;
                    }
                    selectedNode.setValidNode(isValid);
                }
                //If a double click is done
                if (event.getClickCount() == 2) {
                    if (selectedNode.isValidNode()) {
                        //if the selected node is a PROCESS node, open a new instance.
                        if(selectedNode.getNodeType().equals(TreeNodeWps.PROCESS)) {
                            ProcessEditableElement pee = toolBox.openProcess(selectedNode.getFilePath(), getNextIndex(selectedNode));
                            TreeNodeWps instanceNode = new TreeNodeWps();
                            instanceNode.setNodeType(TreeNodeWps.INSTANCE);
                            instanceNode.setUserObject(pee.getId());
                            addNodeToAllModels(instanceNode, selectedNode);
                            pee.addAllNodes(getNodeInAllModels(instanceNode));
                        }
                        //if the selected node is an INSTANCE node, reopen it if closed.
                        else if(selectedNode.getNodeType().equals(TreeNodeWps.INSTANCE)) {
                            toolBox.openInstance(selectedNode.getUserObject().toString());
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the lower available index for a new instance node.
     * @param node Process node to analyse.
     * @return Lower available index.
     */
    private int getNextIndex(TreeNodeWps node){
        if(node.getChildCount() == 0){
            return 0;
        }
        int index = 0;
        for(int i=0; i<node.getChildCount(); i++){
            String nodeName = ((TreeNodeWps)node.getChildAt(i)).getUserObject().toString();
            int j = nodeName.length()-1;
            while(nodeName.charAt(j)<='9' && nodeName.charAt(j)>=0){
                j--;
            }
            j++;
            index = Math.max(Integer.parseInt(nodeName.substring(j)), index);
        }
        return index+1;
    }

    /**
     * Adds a node to a parent in all the model.
     * For each model registered, add a copy of the node.
     * @param node Node to add.
     * @param parent Parent of the node
     */
    private void addNodeToAllModels(TreeNodeWps node, TreeNodeWps parent){
        List<FileTreeModel> modelList = new ArrayList<>();
        modelList.add(categoryModel);
        modelList.add(fileModel);
        modelList.add(filteredModel);

        for(FileTreeModel model : modelList){
            TreeNodeWps modelNode = node.deepCopy();
            TreeNodeWps modelParent = getNodeInModel(parent, model);
            if(modelParent != null) {
                model.insertNodeInto(modelNode, modelParent, 0);
                tree.expandPath(new TreePath(modelNode.getPath()));
            }
        }
    }

    /**
     * Return the node of a model corresponding to the given node.
     * If the node has a file set, it is used to find the copy.
     * Else, uses the UserObject.
     * @param node Original node which the copy is found.
     * @param model Model where the copy is found.
     * @return The copy of the node in the model.
     */
    private TreeNodeWps getNodeInModel(TreeNodeWps node, FileTreeModel model){
        if(node.getFilePath()!= null && node.getFilePath().exists()){
            return getChildWithFile(node.getFilePath(), (TreeNodeWps)model.getRoot());
        }
        else{
            return getChildWithUserObject(node.getUserObject(), (TreeNodeWps)model.getRoot());
        }
    }

    /**
     * Return the node from all the models corresponding to the given node.
     * If the node has a file set, it is used to find the copy.
     * Else, uses the UserObject.
     * @param node Original node which the copy is found.
     * @return The copy of the node in all the models.
     */
    private List<TreeNodeWps> getNodeInAllModels(TreeNodeWps node){
        List<TreeNodeWps> nodeList = new ArrayList<>();
        List<FileTreeModel> modelList = new ArrayList<>();
        modelList.add(categoryModel);
        modelList.add(fileModel);
        modelList.add(filteredModel);
        for(FileTreeModel model : modelList) {
            TreeNodeWps n = getNodeInModel(node, model);
            if(n != null){
                nodeList.add(n);
            }
        }
        return nodeList;
    }

    /**
     * Action done when a model is selected in the comboBox.
     */
    public void onModelSelected(){
        if(treeNodeBox.getSelectedItem().equals(FILE_MODEL)){
            selectedModel = fileModel;
        }
        else if(treeNodeBox.getSelectedItem().equals(CATEGORY_MODEL)){
            selectedModel = categoryModel;
        }
        tree.setModel(selectedModel);
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
        script.setNodeType(TreeNodeWps.PROCESS);
        TreeNodeWps categoryNode = getSubNode(categories[0], root);
        if(categoryNode == null){
            categoryNode = new TreeNodeWps();
            categoryNode.setUserObject(categories[0]);
            categoryModel.insertNodeInto(categoryNode, root, 0);
        }

        if(categories[1] != null){
            TreeNodeWps subCategoryNode = getSubNode(categories[1], categoryNode);
            if(subCategoryNode == null){
                subCategoryNode = new TreeNodeWps();
                subCategoryNode.setUserObject(categories[1]);
                categoryModel.insertNodeInto(subCategoryNode, categoryNode, 0);
            }

            if(categories[2] != null){
                TreeNodeWps subSubCategoryNode = getSubNode(categories[2], subCategoryNode);
                if(subSubCategoryNode == null){
                    subSubCategoryNode = new TreeNodeWps();
                    subSubCategoryNode.setUserObject(categories[2]);
                    categoryModel.insertNodeInto(subSubCategoryNode, subCategoryNode, 0);
                }
                if(!isNodeExisting(script.getFilePath(), subSubCategoryNode)) {
                    script.setValidNode((toolBox.getProcessManager().getProcess(f) != null));
                    categoryModel.insertNodeInto(script, subSubCategoryNode, 0);
                    tree.expandPath(new TreePath(subSubCategoryNode.getPath()));
                }
            }
            else {
                if(!isNodeExisting(script.getFilePath(), subCategoryNode)) {
                    script.setValidNode((toolBox.getProcessManager().getProcess(f) != null));
                    categoryModel.insertNodeInto(script, subCategoryNode, 0);
                    tree.expandPath(new TreePath(subCategoryNode.getPath()));
                }
            }
        }
        else {
            if(!isNodeExisting(script.getFilePath(), categoryNode)) {
                script.setValidNode((toolBox.getProcessManager().getProcess(f) != null));
                categoryModel.insertNodeInto(script, categoryNode, 0);
                tree.expandPath(new TreePath(categoryNode.getPath()));
            }
        }
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
                if (m.getRole().equals(URI.create("utils:category"))) {
                    categories[0] = m.getTitle();
                }
                if (m.getRole().equals(URI.create("utils:subCategory"))) {
                    categories[1] = m.getTitle();
                }
                if (m.getRole().equals(URI.create("utils:subSubCategory"))) {
                    categories[2] = m.getTitle();
                }
            }
        }
        return categories;
    }

    /**
     * Adds a local source. Open the given directory and find all the groovy script contained.
     * @param directory Directory to analyse.
     * @param processManager ProcessManager.
     */
    public void addLocalSource(File directory, ProcessManager processManager) {
        addLocalSourceInFileModel(directory, mapHostNode.get(LOCALHOST_URI));
        for (File f : directory.listFiles()) {
            if (f.getName().endsWith(".groovy")) {
                addScriptInCategoryModel(processManager.getProcess(f), f);
            }
        }
        refresh();
    }

    /**
     * Adds a source in the file model.
     * @param directory Script file to add.
     */
    private void addLocalSourceInFileModel(File directory, TreeNodeWps hostNode){
        TreeNodeWps root = (TreeNodeWps) fileModel.getRoot();

        TreeNodeWps source = null;
        boolean isScript = false;

        for(int i=0; i<root.getChildCount(); i++){
            if(((TreeNodeWps)root.getChildAt(i)).getUserObject().equals(directory.getName())){
                source = (TreeNodeWps)root.getChildAt(i);
                isScript = true;
            }
        }
        if(source == null) {
            source = new TreeNodeWps();
            source.setValidNode(false);
            source.setUserObject(directory.getName());
            source.setFilePath(directory);
            source.setNodeType(TreeNodeWps.FOLDER);
            fileModel.insertNodeInto(source, hostNode, 0);
        }

        for(File f : getAllWpsScript(directory)){
            if(getChildWithFile(f, source) == null) {
                TreeNodeWps script = new TreeNodeWps();
                script.setUserObject(f.getName().replace(".groovy", ""));
                script.setFilePath(f);
                script.setValidNode(toolBox.getProcessManager().getProcess(f) != null);
                script.setNodeType(TreeNodeWps.PROCESS);
                fileModel.insertNodeInto(script, source, 0);
                isScript = true;
            }
        }
        source.setValidNode(isScript);
        tree.expandPath(new TreePath(source.getPath()));
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
        remove(selected);
    }

    /**
     * Remove from the toolBox a node and the associated process.
     * @param node
     */
    public void remove(TreeNodeWps node){
        if(!node.equals(fileModel.getRoot()) && !node.equals(categoryModel.getRoot())){
            List<TreeNodeWps> leafList = new ArrayList<>();
            if(node.isLeaf()) {
                leafList.add(node);
            }
            else{
                leafList.addAll(getAllLeaf(node));
            }
            for(TreeNodeWps leaf : leafList){
                TreeNodeWps fileModelRoot = (TreeNodeWps) fileModel.getRoot();
                TreeNodeWps cateModelRoot = (TreeNodeWps) categoryModel.getRoot();
                TreeNodeWps filtModelRoot = (TreeNodeWps) filteredModel.getRoot();
                switch(leaf.getNodeType()){
                    case TreeNodeWps.FOLDER:
                    case TreeNodeWps.PROCESS:
                        File file = leaf.getFilePath();
                        cleanParentNode(getChildWithFile(file, fileModelRoot), fileModel);
                        cleanParentNode(getChildWithFile(file, cateModelRoot), categoryModel);
                        cleanParentNode(getChildWithFile(file, filtModelRoot), filteredModel);
                        toolBox.removeProcess(leaf.getFilePath());
                        break;
                    case TreeNodeWps.INSTANCE:
                        TreeNodeWps child = getChildWithUserObject(node.getUserObject(), fileModelRoot);
                        if(child != null) {
                            fileModel.removeNodeFromParent(child);
                        }
                        child = getChildWithUserObject(node.getUserObject(), cateModelRoot);
                        if(child != null) {
                            categoryModel.removeNodeFromParent(child);
                        }
                        child = getChildWithUserObject(node.getUserObject(), filtModelRoot);
                        if(child != null) {
                            filteredModel.removeNodeFromParent(child);
                        }
                        toolBox.closeInstance(leaf.getUserObject().toString());
                        break;
                }
            }
        }
    }

    /**
     * Get the child node of a parent which represent the given file.
     * @param file File represented by the node.
     * @param parent Parent of the node.
     * @return The child node.
     */
    private TreeNodeWps getChildWithFile(File file, TreeNodeWps parent){
        for(int i=0; i<parent.getChildCount(); i++){
            TreeNodeWps child = (TreeNodeWps)parent.getChildAt(i);
            if(child.getFilePath() != null && child.getFilePath().equals(file)){
                return child;
            }
            else{
                TreeNodeWps result = getChildWithFile(file, child);
                if(result != null){
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Get the first encountered child node of a parent which represent the same user object.
     * @param userObject Object represented by the node.
     * @param parent Parent of the node.
     * @return The child node.
     */
    private TreeNodeWps getChildWithUserObject(Object userObject, TreeNodeWps parent){
        for(int i=0; i<parent.getChildCount(); i++){
            TreeNodeWps child = (TreeNodeWps)parent.getChildAt(i);
            if(child.getUserObject() != null && child.getUserObject().equals(userObject)){
                return child;
            }
            else{
                TreeNodeWps result = getChildWithUserObject(userObject, child);
                if(result != null){
                    return result;
                }
            }
        }
        return null;
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

    /**
     * Refresh the selected node.
     * If the node is a process (a leaf), check if it is valid or not,
     * If the node is a category check the contained process,
     * If the node is a folder, check the folder to re-add all the contained processes.
     */
    public void refresh(){
        TreeNodeWps node = (TreeNodeWps) tree.getLastSelectedPathComponent();
        if(node != null) {
            if (node.isLeaf()) {
                node.setValidNode(toolBox.checkProcess(node.getFilePath()));
            } else {
                //For each node, test if it is valid, and set the state of the corresponding node in the trees.
                for (TreeNodeWps child : getAllLeaf(node)) {
                    boolean isValid = toolBox.checkProcess(child.getFilePath());
                    TreeNodeWps updated;
                    updated = getChildWithFile(child.getFilePath(), (TreeNodeWps) categoryModel.getRoot());
                    updated.setValidNode(isValid);
                    categoryModel.nodeChanged(updated);
                    updated = getChildWithFile(child.getFilePath(), (TreeNodeWps) fileModel.getRoot());
                    updated.setValidNode(isValid);
                    fileModel.nodeChanged(updated);
                }
                if (tree.getModel().equals(fileModel)) {
                    toolBox.addLocalSource(node.getFilePath());
                }
            }
        }
    }

    /**
     * Returns all the leaf child of a node.
     * @param node Node to explore.
     * @return List of child leaf.
     */
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
     * Returns all the node child of a node with the specified type.
     * @param node Node to explore.
     * @param nodeType Type of the nodes.
     * @return List of child nodes.
     */
    private List<TreeNodeWps> getAllChild(TreeNodeWps node, String nodeType){
        List<TreeNodeWps> nodeList = new ArrayList<>();
        for(int i=0; i<node.getChildCount(); i++){
            TreeNodeWps child = (TreeNodeWps) node.getChildAt(i);
            if(child.getNodeType().equals(nodeType)){
                nodeList.add(child);
            }
            else if(!child.isLeaf()){
                nodeList.addAll(getAllChild(child, nodeType));
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

    /**
     * Sets and applies the filters to the list of WPS scripts and display only the compatible one.
     * @param filters List of IFilter to apply.
     */
    public void setFilters(List<IFilter> filters){
        if(filters.size() == 1){
            IFilter filter = filters.get(0);
            //If the filter is empty, use the previously selected model and open the tree.
            if(filter.acceptsAll()){
                tree.setModel(selectedModel);
                if(selectedModel != null) {
                    TreeNodeWps root = (TreeNodeWps) selectedModel.getRoot();
                    tree.expandPath(new TreePath(((TreeNodeWps)root.getChildAt(0)).getPath()));
                }
            }
            //Else, use the filteredModel
            else {
                tree.setModel(filteredModel);
                for (TreeNodeWps node : getAllChild((TreeNodeWps) fileModel.getRoot(), TreeNodeWps.PROCESS)) {
                    //For all the leaf, tests if they are accepted by the filter or not.
                    TreeNodeWps filteredRoot = (TreeNodeWps) filteredModel.getRoot();
                    TreeNodeWps filteredNode = getChildWithFile(node.getFilePath(), filteredRoot);
                    if (filteredNode == null) {
                        if (filter.accepts(node)) {
                            TreeNodeWps newNode = node.deepCopy();
                            filteredModel.insertNodeInto(newNode, filteredRoot, 0);
                            for(int i=0; i<node.getChildCount(); i++){
                                filteredModel.insertNodeInto(((TreeNodeWps)node.getChildAt(i)).deepCopy(), newNode, 0);
                            }
                            tree.expandPath(new TreePath(newNode.getPath()));
                        }
                    } else {
                        if (!filter.accepts(filteredNode)) {
                            filteredModel.removeNodeFromParent(filteredNode);
                        }
                        else{
                            tree.expandPath(new TreePath(filteredNode.getPath()));
                        }
                    }
                }
            }
        }
    }

    public void dispose(){
        filterFactoryManager.getEventFilterChange().clearListeners();
        filterFactoryManager.getEventFilterFactoryChange().clearListeners();
    }

    public void updateAllNodes(ProcessEditableElement pee){
        TreeNodeWps node = pee.getNodes().get(0);

        List<FileTreeModel> modelList = new ArrayList<>();
        modelList.add(categoryModel);
        modelList.add(fileModel);
        modelList.add(filteredModel);
        for(FileTreeModel model : modelList) {
            model.nodeChanged(getNodeInModel(node, model));
        }
    }
}

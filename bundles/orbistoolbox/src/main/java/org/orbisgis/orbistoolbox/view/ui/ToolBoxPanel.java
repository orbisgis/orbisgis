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
import sun.reflect.generics.tree.Tree;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
    private final static String FILTERED_MODEL = "Filtered";

    private final static String UNDEFINED = "Undefined";

    /** ComboBox with the different model of the tree */
    private JComboBox<String> treeNodeBox;

    /** Reference to the toolbox.*/
    private ToolBox toolBox;

    /** JTree */
    private FileTree tree;
    /** Model of the JTree */
    private FileTreeModel fileModel;
    /** Model of the JTree */
    private FileTreeModel categoryModel;
    /** Model of the JTree*/
    private FileTreeModel filteredModel;
    /** Model of the JTree*/
    private FileTreeModel selectedModel;
    /** Root node of the JTree */
    private TreeNodeWps root;
    /** Node that permit to add a process on double click */
    private TreeNodeWps addWps;

    /** Action available in the right click popup on selecting the panel */
    private ActionCommands popupGlobalActions;
    /** Action available in the right click popup on selecting a node */
    private ActionCommands popupNodeActions;
    /** Action available in the right click popup on selecting a process (leaf) */
    private ActionCommands popupLeafActions;

    private FilterFactoryManager<IFilter,DefaultActiveFilter> filterFactoryManager;

    public ToolBoxPanel(ToolBox toolBox){
        super(new BorderLayout());

        this.toolBox = toolBox;

        this.addWps = new TreeNodeWps();
        addWps.setIsCustomIcon(true);
        addWps.setCustomIcon("folder_add");
        addWps.setUserObject("Add folder");
        addWps.setcanBeLeaf(false);
        addWps.setValidProcess(false);

        TreeNodeWps fileRoot = new TreeNodeWps();
        fileRoot.setUserObject(FILE_MODEL);
        fileRoot.setIsRoot(true);
        fileModel = new FileTreeModel(fileRoot);

        TreeNodeWps categoryRoot = new TreeNodeWps();
        categoryRoot.setUserObject(CATEGORY_MODEL);
        categoryRoot.setIsRoot(true);
        categoryModel = new FileTreeModel(categoryRoot);

        TreeNodeWps filteredRoot = new TreeNodeWps();
        filteredRoot.setUserObject(FILTERED_MODEL);
        filteredRoot.setIsRoot(true);
        filteredModel = new FileTreeModel(filteredRoot);

        treeNodeBox = new JComboBox<>();
        treeNodeBox.addItem(FILE_MODEL);
        treeNodeBox.addItem(CATEGORY_MODEL);
        treeNodeBox.setSelectedItem(CATEGORY_MODEL);
        treeNodeBox.addActionListener(EventHandler.create(ActionListener.class, this, "onModelSelected"));

        tree = new FileTree();
        tree.setRootVisible(false);
        tree.setScrollsOnExpand(true);
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
        FilterFactoryManager.FilterChangeListener refreshFilterListener = EventHandler.create(FilterFactoryManager.FilterChangeListener.class,
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

        tree.setModel(fileModel);
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
            if(selectedNode != null) {
                //if a simple click is done
                if (event.getClickCount() == 1 && selectedNode.isLeaf() && selectedNode.canBeLeaf()) {
                    selectedNode.setValidProcess(toolBox.checkProcess(selectedNode.getFilePath()));
                }
                //If a double click is done
                if (event.getClickCount() == 2) {
                    if (selectedNode.equals(addWps)) {
                        toolBox.addNewLocalSource();
                    }
                    if (selectedNode.isValidProcess() && selectedNode.isLeaf()) {
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
            selectedModel = fileModel;
        }
        else if(treeNodeBox.getSelectedItem().equals(CATEGORY_MODEL)){
            if(((TreeNodeWps) fileModel.getRoot()).isNodeChild(addWps)) {
                ((TreeNodeWps) fileModel.getRoot()).add(addWps);
            }
            ((TreeNodeWps) categoryModel.getRoot()).add(addWps);
            root = (TreeNodeWps) categoryModel.getRoot();
            tree.setModel(categoryModel);
            selectedModel = categoryModel;
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
            categoryNode.setValidProcess(false);
            root.add(categoryNode);
        }

        if(categories[1] != null){
            TreeNodeWps subCategoryNode = getSubNode(categories[1], categoryNode);
            if(subCategoryNode == null){
                subCategoryNode = new TreeNodeWps();
                subCategoryNode.setUserObject(categories[1]);
                subCategoryNode.setValidProcess(false);
                categoryNode.add(subCategoryNode);
            }

            if(categories[2] != null){
                TreeNodeWps subSubCategoryNode = getSubNode(categories[2], subCategoryNode);
                if(subSubCategoryNode == null){
                    subSubCategoryNode = new TreeNodeWps();
                    subSubCategoryNode.setUserObject(categories[2]);
                    subCategoryNode.add(subSubCategoryNode);
                    subSubCategoryNode.setValidProcess(false);
                }
                if(!isNodeExisting(script.getFilePath(), subSubCategoryNode)) {
                    script.setValidProcess((toolBox.getProcessManager().getProcess(f) != null));
                    subSubCategoryNode.add(script);
                }
            }
            else {
                if(!isNodeExisting(script.getFilePath(), subCategoryNode)) {
                    script.setValidProcess((toolBox.getProcessManager().getProcess(f) != null));
                    subCategoryNode.add(script);
                }
            }
        }
        else {
            if(!isNodeExisting(script.getFilePath(), categoryNode)) {
                script.setValidProcess((toolBox.getProcessManager().getProcess(f) != null));
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
        addLocalSourceInFileModel(directory);
        for (File f : directory.listFiles()) {
            if (f.getName().endsWith(".groovy")) {
                addScriptInCategoryModel(processManager.getProcess(f), f);
            }
        }
    }

    /**
     * Adds a source in the file model.
     * @param directory Script file to add.
     */
    private void addLocalSourceInFileModel(File directory){
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
            source.setcanBeLeaf(false);
            source.setValidProcess(false);
            source.setUserObject(directory.getName());
            source.setFilePath(directory);
            root.add(source);
        }

        for(File f : getAllWpsScript(directory)){
            if(getNodeFromFile(f, source) == null) {
                TreeNodeWps script = new TreeNodeWps();
                script.setUserObject(f.getName().replace(".groovy", ""));
                script.setFilePath(f);
                script.setValidProcess((toolBox.getProcessManager().getProcess(f) != null));
                source.add(script);
                isScript = true;
            }
        }
        source.setValidProcess(isScript);

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
                File file = leaf.getFilePath();
                if(!toolBox.isProcessRunning(file)) {
                    cleanParentNode(getNodeFromFile(file, (TreeNodeWps) fileModel.getRoot()), fileModel);
                    cleanParentNode(getNodeFromFile(file, (TreeNodeWps) categoryModel.getRoot()), categoryModel);
                    toolBox.removeProcess(leaf.getFilePath());
                }
            }
            this.reload();
        }
    }

    /**
     * Get the child node of a parent which represent the given file.
     * @param file File represented by the node.
     * @param parent Parent of the node.
     * @return The child node.
     */
    private TreeNodeWps getNodeFromFile(File file, TreeNodeWps parent){
        for(int i=0; i<parent.getChildCount(); i++){
            TreeNodeWps child = (TreeNodeWps)parent.getChildAt(i);
            if(child.getFilePath() != null && child.getFilePath().equals(file)){
                return child;
            }
            else{
                TreeNodeWps result = getNodeFromFile(file, child);
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
        if(node.isLeaf()){
            node.setValidProcess(toolBox.checkProcess(node.getFilePath()));
        }
        else {
            if (tree.getModel().equals(categoryModel)) {
                for (TreeNodeWps child : getAllLeaf(node)) {
                    child.setValidProcess(toolBox.checkProcess(child.getFilePath()));
                }
            }
            if (tree.getModel().equals(fileModel)) {
                this.remove(node);
                toolBox.addLocalSource(node.getFilePath());
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
            if(filter.acceptsAll()){
                tree.setModel(selectedModel);
            }
            else {
                tree.setModel(filteredModel);
                for (TreeNodeWps node : getAllLeaf((TreeNodeWps) fileModel.getRoot())) {
                    if(node != addWps) {
                        TreeNodeWps filteredNode = getNodeFromFile(node.getFilePath(), (TreeNodeWps) filteredModel.getRoot());
                        if (filteredNode == null) {
                            if (filter.accepts(node)) {
                                ((TreeNodeWps) filteredModel.getRoot()).add(node.deepCopy());
                            }
                        } else {
                            if (!filter.accepts(filteredNode)) {
                                filteredModel.removeNodeFromParent(filteredNode);
                            }
                        }
                    }
                }
                filteredModel.reload();
            }
        }
    }

    public void dispose(){
        filterFactoryManager.getEventFilterChange().clearListeners();
        filterFactoryManager.getEventFilterFactoryChange().clearListeners();
    }

    /**
     * Return the list of nodes from all the model which has the same file as the selected one
     * @return The node list.
     */
    public List<TreeNodeWps> getNodesFromSelectedOne() {
        File f = ((TreeNodeWps)tree.getLastSelectedPathComponent()).getFilePath();
        List<TreeNodeWps> list = new ArrayList<>();
        list.add(getNodeFromFile(f, (TreeNodeWps)categoryModel.getRoot()));
        list.add(getNodeFromFile(f, (TreeNodeWps)fileModel.getRoot()));
        return list;
    }
}

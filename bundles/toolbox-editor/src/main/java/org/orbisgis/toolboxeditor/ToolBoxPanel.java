/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

package org.orbisgis.toolboxeditor;

import net.opengis.ows._2.LanguageStringType;
import net.opengis.wps._2_0.ProcessOffering;
import net.opengis.wps._2_0.ProcessOfferings;
import net.opengis.wps._2_0.ProcessSummaryType;
import org.orbisgis.orbiswps.client.api.filter.IFilter;
import org.orbisgis.orbiswps.client.api.utils.ProcessExecutionType;
import org.orbisgis.orbiswps.serviceapi.process.ProcessMetadata;
import org.orbisgis.orbiswps.serviceapi.process.ProcessMetadata.INTERNAL_METADATA;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.sif.components.filter.DefaultActiveFilter;
import org.orbisgis.sif.components.filter.FilterFactoryManager;
import org.orbisgis.sif.components.fstree.CustomTreeCellRenderer;
import org.orbisgis.sif.components.fstree.FileTree;
import org.orbisgis.sif.components.fstree.FileTreeModel;
import org.orbisgis.toolboxeditor.filter.SearchFilter;
import org.orbisgis.toolboxeditor.utils.ToolBoxIcon;
import org.orbisgis.toolboxeditor.utils.TreeNodeWps;
import org.orbisgis.toolboxeditor.utils.Wps1_0_0Request;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main panel of the ToolBox.
 * This panel contains the JTree of all the loaded scripts.
 *
 * @author Sylvain PALOMINOS
 **/

public class ToolBoxPanel extends JPanel {

    private static final String ADD_SOURCE = "ADD_SOURCE";
    private static final String ADD_HOST = "ADD_HOST";
    private static final String ADD_SCRIPT = "ADD_SCRIPT";
    private static final String RUN_SCRIPT = "RUN_SCRIPT";
    private static final String REFRESH_SOURCE = "REFRESH_SOURCE";
    private static final String REMOVE = "REMOVE";

    private static String TAG_MODEL;
    private static String FILE_MODEL;
    private static String FILTERED_MODEL;

    private static final String ORBISGIS_STRING = "OrbisGIS";

    private static final String LOCALHOST_STRING = "localhost";
    private static final URI LOCALHOST_URI = URI.create(LOCALHOST_STRING);
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(ToolBoxPanel.class);

    /** ComboBox with the different model of the tree */
    private JComboBox<String> treeNodeBox;

    /** Reference to the toolbox.*/
    private WpsClientImpl wpsClient;

    /** JTree */
    private JTree tree;
    /** Model of the JTree */
    private FileTreeModel fileModel;
    /** Model of the JTree */
    private FileTreeModel tagModel;
    /** Model of the JTree*/
    private FileTreeModel filteredModel;
    /** Model of the JTree*/
    private FileTreeModel selectedModel;
    /** Last selected node */
    private TreeNodeWps lastSelectedNode;

    /** Action available in the right click popup on selecting the panel */
    private ActionCommands popupGlobalActions;
    /** Action available in the right click popup on selecting a node */
    private ActionCommands popupNodeActions;
    /** Action available in the right click popup on selecting a process (leaf) */
    private ActionCommands popupLeafActions;
    /** Action available in the right click popup on selecting a default OrbisGIS process (leaf) */
    private ActionCommands popupOrbisGISLeafActions;
    /** Action available in the right click popup on selecting a default OrbisGIS node (folder) */
    private ActionCommands popupOrbisGISNodeActions;
    /** Action available in the right click popup on selecting a host node (folder) */
    private ActionCommands popupHostNodeActions;
    /** Action available in the right click popup on selecting a host leaf node (folder) */
    private ActionCommands popupHostLeafNodeActions;

    /** Map containing all the host (localhost ...) and the associated node. */
    private Map<URI, TreeNodeWps> mapHostNode;
    /** List of existing tree model. */
    private List<FileTreeModel> modelList;

    private static final String DEFAULT_FILTER_FACTORY = "name_contains";
    private FilterFactoryManager<IFilter,DefaultActiveFilter> filterFactoryManager;
    private Wps1_0_0Request wps1_0_0Request;

    public ToolBoxPanel(WpsClientImpl wpsClient){
        super(new BorderLayout());

        TAG_MODEL = I18N.tr("Advanced interface");
        FILE_MODEL = I18N.tr("Simple interface");
        FILTERED_MODEL = I18N.tr("Filtered");

        this.wpsClient = wpsClient;
        this.wps1_0_0Request = new Wps1_0_0Request(this);

        //By default add the localhost
        mapHostNode = new HashMap<>();
        TreeNodeWps localhostNode = new TreeNodeWps();
        localhostNode.setNodeType(TreeNodeWps.NodeType.HOST_LOCAL);
        localhostNode.setUserObject(LOCALHOST_STRING);
        mapHostNode.put(LOCALHOST_URI, localhostNode);

        TreeNodeWps fileRoot = new TreeNodeWps();
        fileRoot.setUserObject(FILE_MODEL);
        fileModel = new FileTreeModel(localhostNode);
        //fileModel.insertNodeInto(localhostNode, fileRoot, 0);

        TreeNodeWps tagRoot = new TreeNodeWps();
        tagRoot.setUserObject(TAG_MODEL);
        tagModel = new FileTreeModel(tagRoot);

        TreeNodeWps filteredRoot = new TreeNodeWps();
        filteredRoot.setUserObject(FILTERED_MODEL);
        filteredModel = new FileTreeModel(filteredRoot);

        treeNodeBox = new JComboBox<>();
        treeNodeBox.addItem(FILE_MODEL);
        treeNodeBox.addItem(TAG_MODEL);
        treeNodeBox.setSelectedItem(FILE_MODEL);
        treeNodeBox.addActionListener(EventHandler.create(ActionListener.class, this, "onModelSelected"));

        tree = new FileTree();
        tree.setRootVisible(false);
        tree.setScrollsOnExpand(true);
        tree.setToggleClickCount(1);
        tree.setCellRenderer(new CustomTreeCellRenderer(tree));
        tree.addMouseListener(EventHandler.create(MouseListener.class, this, "onMouseReleased", "", "mouseReleased"));
        tree.addMouseListener(EventHandler.create(MouseListener.class, this, "onMouseClicked", ""));

        JScrollPane treeScrollPane = new JScrollPane(tree);
        this.add(treeScrollPane, BorderLayout.CENTER);
        this.add(treeNodeBox, BorderLayout.PAGE_END);

        popupGlobalActions = new ActionCommands();
        popupGlobalActions.setAccelerators(this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        createPopupActions(wpsClient);

        //Sets the filter
        filterFactoryManager = new FilterFactoryManager<>();
        filterFactoryManager.setDefaultFilterFactory(DEFAULT_FILTER_FACTORY);
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

        modelList = new ArrayList<>();
        modelList.add(tagModel);
        modelList.add(fileModel);
        modelList.add(filteredModel);
        tree.setModel(tagModel);
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
     * @param e Mouse event.
     */
    public void onMouseClicked(MouseEvent e){
        //Test if it is a right click
        if(e.getButton() == MouseEvent.BUTTON3 || e.isPopupTrigger()) {
            JPopupMenu popupMenu = new JPopupMenu();
            //find what was clicked to give to the popup the good action
            if(e.getSource().equals(tree)){
                if(tree.getLastSelectedPathComponent() == null ||
                        tree.getLastSelectedPathComponent().equals(fileModel.getRoot()) ||
                        tree.getLastSelectedPathComponent().equals(tagModel.getRoot())){
                    popupGlobalActions.copyEnabledActions(popupMenu);
                }
                else {
                    TreeNodeWps node = (TreeNodeWps) tree.getLastSelectedPathComponent();
                    if(!node.isRemovable()){
                        if (node.isLeaf() && !node.getNodeType().equals(TreeNodeWps.NodeType.FOLDER)) {
                            popupOrbisGISLeafActions.copyEnabledActions(popupMenu);
                        } else {
                            popupOrbisGISNodeActions.copyEnabledActions(popupMenu);
                        }
                    }
                    else {
                        if (node.isLeaf() && !node.getNodeType().equals(TreeNodeWps.NodeType.FOLDER)) {
                            popupLeafActions.copyEnabledActions(popupMenu);
                        } else if (!node.isLeaf() && node.getNodeType().equals(TreeNodeWps.NodeType.HOST_DISTANT)){
                            popupHostNodeActions.copyEnabledActions(popupMenu);
                        } else if (node.getNodeType().equals(TreeNodeWps.NodeType.HOST_DISTANT)){
                            popupHostLeafNodeActions.copyEnabledActions(popupMenu);
                        } else {
                            popupNodeActions.copyEnabledActions(popupMenu);
                        }
                    }
                }
            }
            if (popupMenu.getComponentCount()>0) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public void onMouseReleased(MouseEvent e){
        TreeNodeWps selectedNode = (TreeNodeWps) ((FileTree)e.getSource()).getLastSelectedPathComponent();
        if(selectedNode != null) {
            //if a simple click is done
            if (e.getClickCount() == 1) {
                switch(selectedNode.getNodeType()){
                    case HOST_DISTANT:
                        if(selectedNode.isLeaf()) {
                            wps1_0_0Request.getCapabilities(selectedNode.getIdentifier());
                        }
                        break;
                    case HOST_LOCAL:
                        //TODO : check if the OrbisGIS WPS script folder is available or not
                        break;
                    case FOLDER:
                        if(selectedNode.getChildCount() != 0) {
                            //Check if the folder exists and it it contains some scripts
                            if (selectedModel == fileModel) {
                                refresh(selectedNode);
                            }
                        }
                        else{
                            wpsClient.addLocalSource(selectedNode.getIdentifier());
                        }
                        break;
                    case PROCESS:
                        refresh(selectedNode);
                        break;
                }
            }
            //If a double click is done
            if (e.getClickCount() == 2 && lastSelectedNode.equals(selectedNode)) {
                if (selectedNode.isValidNode()) {
                    //if the selected node is a PROCESS node, open a new instance.
                    if(selectedNode.getNodeType().equals(TreeNodeWps.NodeType.PROCESS)) {
                        if(selectedNode.getHostURI() != null) {
                            ProcessOfferings processOffering = wps1_0_0Request.describeProcess(
                                    selectedNode.getHostURI(), selectedNode.getIdentifier());
                            for(ProcessOffering process : processOffering.getProcessOffering()) {
                                wpsClient.openProcess(process, new HashMap<URI, Object>(),
                                        ProcessExecutionType.STANDARD);
                            }
                        }
                        else{
                            wpsClient.openProcess(selectedNode.getIdentifier(), null,
                                    ProcessExecutionType.STANDARD);
                        }
                    }
                }
            }
        }
        lastSelectedNode = selectedNode;
    }

    /**
     * Action done when a model is selected in the comboBox.
     */
    public void onModelSelected(){
        if(treeNodeBox.getSelectedItem().equals(FILE_MODEL)){
            selectedModel = fileModel;
        }
        else if(treeNodeBox.getSelectedItem().equals(TAG_MODEL)){
            selectedModel = tagModel;
        }
        tree.setModel(selectedModel);
    }

    /**
     * Tests if the parent node contain a child representing the given file.
     * @param uri URI to test.
     * @param parent Parent to test.
     * @return True if the parent contain the file.
     */
    private boolean isNodeExisting(URI uri, TreeNodeWps parent){
        boolean exist = false;
        for(int l=0; l<parent.getChildCount(); l++){
            if(((TreeNodeWps)parent.getChildAt(l)).getIdentifier().equals(uri)){
                exist = true;
            }
        }
        return exist;
    }

    /**
     * Gets the the child node of the parent node which has the given userObject.
     * @param nodeURI URI of the node.
     * @param parent Parent to analyse.
     * @return The child which has the given userObject. Null if not found.
     */
    private TreeNodeWps getSubNode(URI nodeURI, TreeNodeWps parent){
        TreeNodeWps child = null;
        for(int i = 0; i < parent.getChildCount(); i++){
            if(((TreeNodeWps)parent.getChildAt(i)).getIdentifier().equals(nodeURI)){
                child = (TreeNodeWps)parent.getChildAt(i);
            }
        }
        return child;
    }

    public void addFolder(URI folderUri, URI parentUri){
        TreeNodeWps hostNode = mapHostNode.get(LOCALHOST_URI);
        List<TreeNodeWps> sourceList = getChildrenWithUri(parentUri, hostNode);
        TreeNodeWps parentNode;
        if(sourceList.isEmpty()){
            parentNode = null;
        }
        else{
            parentNode = sourceList.get(0);
            if(getSubNode(folderUri, parentNode) != null){
                return;
            }
        }
        for(TreeNodeWps node : getChildrenWithUri(folderUri, hostNode)){
            remove(node);
        }
        String folderName = new File(folderUri).getName();
        TreeNodeWps folderNode = new TreeNodeWps();
        folderNode.setValidNode(true);
        folderNode.setUserObject(folderName);
        folderNode.setIdentifier(folderUri);
        folderNode.setNodeType(TreeNodeWps.NodeType.FOLDER);

        if (parentNode == null) {
            fileModel.insertNodeInto(folderNode, hostNode, 0);
        } else {
            fileModel.insertNodeInto(folderNode, parentNode, 0);
            tree.expandPath(new TreePath(parentNode.getPath()));
        }
    }

    public void addHost(URI hostUri, String name){
        TreeNodeWps hostNode = new TreeNodeWps();
        hostNode.setIdentifier(hostUri);
        hostNode.setNodeType(TreeNodeWps.NodeType.HOST_DISTANT);
        hostNode.setUserObject(name);
        hostNode.setIsRemovable(true);

        mapHostNode.put(hostUri, hostNode);
        fileModel.insertNodeInto(hostNode, (MutableTreeNode)tree.getModel().getRoot(), 0);
    }

    /**
     * Returns all the WPS script file contained by the directory.
     * @param directory URI to analyse.
     * @return The list of URI.
     */
    private List<URI> getAllWpsScript(URI directory) {
        List<URI> scriptList = new ArrayList<>();
        File f = new File(directory);
        if (f.exists() && f.isDirectory()) {
            for (File file : f.listFiles()) {
                if (file != null) {
                    if (file.isFile() && file.getName().endsWith(".groovy")) {
                        scriptList.add(file.toURI());
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

    public void remove(URI uri){
        List<TreeNodeWps> list = getChildrenWithUri(uri, (TreeNodeWps)tree.getModel().getRoot());
        for(TreeNodeWps node : list){
            remove(node);
        }
    }

    /**
     * Remove from the toolBox a node and the associated process.
     * @param node
     */
    public void remove(TreeNodeWps node){
        if(!node.equals(fileModel.getRoot()) && !node.equals(tagModel.getRoot())){
            if(node.isRemovable()) {
                switch(node.getNodeType()) {
                    case FOLDER:
                        for (TreeNodeWps child : getChildrenWithUri(node.getIdentifier(),
                                (TreeNodeWps) selectedModel.getRoot())) {
                            if (child.isRemovable()) {
                                cleanParentNode(child, selectedModel);
                            }
                        }
                        break;
                    case PROCESS:
                        for (FileTreeModel model : modelList) {
                            for (TreeNodeWps child : getChildrenWithUri(node.getIdentifier(),
                                    (TreeNodeWps) model.getRoot())) {
                                if (child != null && child.isRemovable()) {
                                    cleanParentNode(child, model);
                                    if(child.getParent()!=null) {
                                        model.removeNodeFromParent(child);
                                    }
                                }
                            }
                        }
                        wpsClient.removeProcess(node.getIdentifier());
                        break;
                }
            }
        }
    }

    /**
     * Get the child node of a parent which represent the given file.
     * @param uri URI represented by the node.
     * @param parent Parent of the node.
     * @return The child node.
     */
    private List<TreeNodeWps> getChildrenWithUri(URI uri, TreeNodeWps parent){
        List<TreeNodeWps> nodeList = new ArrayList<>();
        for(int i=0; i<parent.getChildCount(); i++){
            TreeNodeWps child = (TreeNodeWps)parent.getChildAt(i);
            if(child.getIdentifier() != null && child.getIdentifier().equals(uri)){
                nodeList.add(child);
            }
            else{
                nodeList.addAll(getChildrenWithUri(uri, child));
            }
        }
        return nodeList;
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
        if(node.getParent() != null) {
            //If the node is the last one from its parent, call 'cleanParentNode()' on it
            if (node.getParent() != null && node.getParent().getChildCount() == 1) {
                cleanParentNode((TreeNodeWps) node.getParent(), model);
            } else {
                model.removeNodeFromParent(node);
            }
        }
        else{
            model.removeNodeFromParent((TreeNodeWps)node.getChildAt(0));
        }
    }

    /**
     * Refresh the selected node.
     * If the node is a process (a leaf), check if it is valid or not,
     * If the node is a category check the contained process,
     * If the node is a folder, check the folder to re-add all the contained processes.
     */
    public void refresh(){
        refresh((TreeNodeWps) tree.getLastSelectedPathComponent());
    }

    public void refreshAll(){
        List<TreeNodeWps> leafList = getAllLeaf((TreeNodeWps)selectedModel.getRoot());
        for(TreeNodeWps node : leafList){
            refresh(node);
        }
    }

    public void cleanAll(){
        if(selectedModel != tagModel) {
            List<TreeNodeWps> leafList = getAllLeaf((TreeNodeWps) fileModel.getRoot());
            for (TreeNodeWps node : leafList) {
                cleanParentNode(node, fileModel);
            }
            leafList = getAllLeaf((TreeNodeWps) tagModel.getRoot());
            for (TreeNodeWps node : leafList) {
                cleanParentNode(node, tagModel);
            }
        }
    }

    /**
     * Refresh the given node.
     * If the node is a process (a leaf), check if it is valid or not,
     * If the node is a category check the contained process,
     * If the node is a folder, check the folder to re-add all the contained processes.
     */
    public void refresh(TreeNodeWps node){
        if(node != null) {
            if (node.getNodeType().equals(TreeNodeWps.NodeType.PROCESS)) {
                if(!wpsClient.checkProcess(node.getIdentifier())){
                    remove(node);
                }
            } else {
                //For each node, test if it is valid, and set the state of the corresponding node in the trees.
                for (TreeNodeWps child : getAllLeaf(node)) {
                    refresh(child);
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
     * Returns all the child of a node.
     * @param node Node to explore.
     * @return List of child.
     */
    private List<TreeNodeWps> getAllChild(TreeNodeWps node){
        List<TreeNodeWps> nodeList = new ArrayList<>();
        for(int i=0; i<node.getChildCount(); i++){
            TreeNodeWps child = (TreeNodeWps) node.getChildAt(i);
            if(child.isLeaf()){
                nodeList.add(child);
            }
            else{
                nodeList.addAll(getAllChild(child));
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
    private List<TreeNodeWps> getAllChildWithType(TreeNodeWps node, TreeNodeWps.NodeType nodeType){
        List<TreeNodeWps> nodeList = new ArrayList<>();
        for(int i=0; i<node.getChildCount(); i++){
            TreeNodeWps child = (TreeNodeWps) node.getChildAt(i);
            if(child.getNodeType().equals(nodeType)){
                nodeList.add(child);
            }
            else if(!child.isLeaf()){
                nodeList.addAll(getAllChildWithType(child, nodeType));
            }
        }
        return nodeList;
    }

    /**
     * Creates the action for the popup.
     * @param wpsClient ToolBox.
     */
    private void createPopupActions(WpsClientImpl wpsClient) {
        DefaultAction addSource = new DefaultAction(
                ADD_SOURCE,
                I18N.tr("Add folder"),
                I18N.tr("Add a local folder"),
                ToolBoxIcon.getIcon(ToolBoxIcon.FOLDER_ADD),
                EventHandler.create(ActionListener.class, wpsClient, "addNewLocalSource"),
                null
        );
        DefaultAction addHost = new DefaultAction(
                ADD_HOST,
                I18N.tr("Add host"),
                I18N.tr("Add a distant host"),
                ToolBoxIcon.getIcon(ToolBoxIcon.HOST_ADD),
                EventHandler.create(ActionListener.class, wpsClient, "addDistantHost"),
                null
        );
        DefaultAction addFile = new DefaultAction(
                ADD_SCRIPT,
                I18N.tr("Add file"),
                I18N.tr("Add a local file"),
                ToolBoxIcon.getIcon(ToolBoxIcon.SCRIPT_ADD),
                EventHandler.create(ActionListener.class, wpsClient, "addNewLocalScript"),
                null
        );
        DefaultAction runScript = new DefaultAction(
                RUN_SCRIPT,
                I18N.tr("Run"),
                I18N.tr("Run a script"),
                ToolBoxIcon.getIcon(ToolBoxIcon.EXECUTE),
                EventHandler.create(ActionListener.class, wpsClient, "openProcess"),
                null
        );
        DefaultAction refresh_source = new DefaultAction(
                REFRESH_SOURCE,
                I18N.tr("Refresh"),
                I18N.tr("Refresh a source"),
                ToolBoxIcon.getIcon(ToolBoxIcon.REFRESH),
                EventHandler.create(ActionListener.class, this, "refresh"),
                null
        );
        DefaultAction remove = new DefaultAction(
                REMOVE,
                "Remove",
                "Remove a source or a script",
                ToolBoxIcon.getIcon(ToolBoxIcon.REMOVE),
                EventHandler.create(ActionListener.class, this, "removeSelected"),
                null
        );

        popupGlobalActions = new ActionCommands();
        popupGlobalActions.addAction(addHost);
        popupGlobalActions.addAction(addSource);
        popupGlobalActions.addAction(addFile);
        popupGlobalActions.addAction(refresh_source);

        popupOrbisGISLeafActions = new ActionCommands();
        popupOrbisGISLeafActions.addAction(runScript);
        popupOrbisGISLeafActions.addAction(refresh_source);


        popupLeafActions = new ActionCommands();
        popupLeafActions.addAction(runScript);
        popupLeafActions.addAction(refresh_source);
        popupLeafActions.addAction(remove);

        popupNodeActions = new ActionCommands();
        popupNodeActions.addAction(addHost);
        popupNodeActions.addAction(addSource);
        popupNodeActions.addAction(addFile);
        popupNodeActions.addAction(refresh_source);
        popupNodeActions.addAction(remove);

        popupOrbisGISNodeActions = new ActionCommands();
        popupOrbisGISNodeActions.addAction(addHost);
        popupOrbisGISNodeActions.addAction(addSource);
        popupOrbisGISNodeActions.addAction(addFile);
        popupOrbisGISNodeActions.addAction(refresh_source);

        popupHostNodeActions = new ActionCommands();
        popupHostNodeActions.addAction(addHost);
        popupHostNodeActions.addAction(addSource);
        popupHostNodeActions.addAction(addFile);
        popupHostNodeActions.addAction(refresh_source);
        popupHostNodeActions.addAction(remove);

        popupHostLeafNodeActions = new ActionCommands();
        popupHostLeafNodeActions.addAction(runScript);
        popupHostLeafNodeActions.addAction(refresh_source);
        popupHostLeafNodeActions.addAction(remove);
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
                for (TreeNodeWps node : getAllChildWithType((TreeNodeWps) fileModel.getRoot(), TreeNodeWps.NodeType.PROCESS)) {
                    //For all the leaf, tests if they are accepted by the filter or not.
                    TreeNodeWps filteredRoot = (TreeNodeWps) filteredModel.getRoot();
                    List<TreeNodeWps> filteredNode = getChildrenWithUri(node.getIdentifier(), filteredRoot);
                    if (filteredNode.isEmpty()) {
                        if (filter.accepts(node.getUserObject().toString())) {
                            TreeNodeWps newNode = node.deepCopy();
                            filteredModel.insertNodeInto(newNode, filteredRoot, 0);
                            filteredModel.nodeStructureChanged(filteredRoot);
                            tree.expandPath(new TreePath(newNode.getPath()));
                        }
                    }
                    else {
                        if (!filter.accepts(filteredNode.get(0).getUserObject().toString())) {
                            filteredModel.removeNodeFromParent(filteredNode.get(0));
                        }
                        else{
                            tree.expandPath(new TreePath(filteredNode.get(0).getPath()));
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

    /**
     * Open in the tree the node corresponding to the give name in the given model
     * @param nodeName Name of the node to open.
     * @param modelName Name of the model where the node is.
     */
    public void openNode(String nodeName, String modelName){
        FileTreeModel model = null;
        if (modelName.equals(FILE_MODEL)) {
            model = fileModel;

        } else if (modelName.equals(TAG_MODEL)) {
            model = tagModel;

        }
        TreePath treePath = null;
        if(model != null){
            TreeNodeWps node = getChildWithUserObject(nodeName, (TreeNodeWps)model.getRoot());
            if(node != null) {
                treePath = new TreePath(node.getPath());
            }
        }
        else{
            TreeNodeWps node =getChildWithUserObject(ORBISGIS_STRING, (TreeNodeWps)tagModel.getRoot());
            if(node != null) {
                treePath = new TreePath(node.getPath());
            }
        }
        if(treePath != null){
            tree.expandPath(treePath);
        }
        tree.setModel(model);
        selectedModel = model;
    }

    public void addProcess(String title, String identifier, List<String> keywords, Map<String, Object> metadataMap) {
        addProcess(null, null, title, identifier, keywords, metadataMap);
    }

    public void addProcess(URI host, String hostName, String title, String identifier,
                           List<String> keywords, Map<String, Object> metadataMap) {
        if(host == null){
            host = LOCALHOST_URI;
        }
        boolean isRemovable = true;
        String nodePath = hostName;
        String[] iconArray = null;
        //Retrieve the process metadata
        if(metadataMap != null){
            if (metadataMap.containsKey(INTERNAL_METADATA.IS_REMOVABLE.name())) {
                isRemovable = Boolean.parseBoolean((String)metadataMap.get(INTERNAL_METADATA.IS_REMOVABLE.name()));
            }
            if (metadataMap.containsKey(INTERNAL_METADATA.NODE_PATH.name())) {
                nodePath = (String) metadataMap.get(INTERNAL_METADATA.NODE_PATH.name());
            }
            if (metadataMap.containsKey(INTERNAL_METADATA.ICON_ARRAY.name())) {
                iconArray = ((String)metadataMap.get(INTERNAL_METADATA.ICON_ARRAY.name())).split(",");
            }
        }
        addLocalSourceInFileModel(host, mapHostNode.get(host), iconArray, title, identifier, nodePath, isRemovable);
        addScriptInTagModel(host, identifier, title, keywords, null, isRemovable);
        refresh();
    }

    /**
     * Adds a source in the file model.
     * @param iconName Name of the icon to use for the node representing the process to add to the tree
     */
    private void addLocalSourceInFileModel(URI parentUri, TreeNodeWps parentNode, String[] iconName, String title, String identifier,
                                           String nodePath, boolean isRemovable){
        String[] split;
        TreeNodeWps parent = parentNode;
        TreeNodeWps node = null;
        if(parentUri.toString().startsWith("file:/") || parentUri.toString().startsWith("/")){
            split = new String[]{new File(parentUri).getName()};
        }
        else if(nodePath != null && !nodePath.isEmpty()) {
            if (parentUri.toString().contains("/")) {
                split = nodePath.split("/");
            } else {
                split = nodePath.split("[\\\\/]");
            }
        }
        else{
            node = parent;
            split = new String[]{};
        }
        int index = 0;
        for(String str : split){
            node = getChildWithUserObject(str, parent);
            if(node == null){
                node = new TreeNodeWps();
                node.setValidNode(true);
                node.setUserObject(str);
                node.setIdentifier(URI.create(str.replaceAll(" ", "_")));
                node.setNodeType(TreeNodeWps.NodeType.FOLDER);
                if(iconName != null) {
                    if (iconName.length > index) {
                        node.setCustomIcon(iconName[index]);
                    } else {
                        node.setCustomIcon(iconName[iconName.length - 1]);
                    }
                }
                fileModel.insertNodeInto(node, parent, 0);
            }
            parent = node;
            index++;
        }

        URI processUri = URI.create(identifier);
        if(getChildrenWithUri(processUri, node).isEmpty()) {
            TreeNodeWps script = new TreeNodeWps();
            script.setIdentifier(processUri);
            script.setNodeType(TreeNodeWps.NodeType.PROCESS);
            script.setIsRemovable(isRemovable);
            if(parentUri != LOCALHOST_URI){
                script.setHostUri(parentUri);
            }
            if(!title.isEmpty()) {
                script.setUserObject(title);
            }
            else{
                script.setUserObject(new File(processUri).getName().replace(".groovy", ""));
            }
            fileModel.insertNodeInto(script, node, 0);
        }
        tree.expandPath(new TreePath(node.getPath()));
    }

    /**
     * Adds a process in the tag model.
     */
    public void addScriptInTagModel(URI hostUri, String identifier, String title, List<String> keywords, String iconName,
                                    boolean isRemovable){
        TreeNodeWps root = (TreeNodeWps) tagModel.getRoot();
        TreeNodeWps script = new TreeNodeWps();
        URI uri = URI.create(identifier);
        script.setIdentifier(uri);
        script.setNodeType(TreeNodeWps.NodeType.PROCESS);
        script.setIsRemovable(isRemovable);

        if(iconName != null){
            script.setCustomIcon(iconName);
        }
        if(title!=null && !title.isEmpty() && keywords!= null){
            script.setUserObject(title);
            if(!keywords.isEmpty()) {
                for (String tag : keywords) {
                    TreeNodeWps tagNode = getChildWithUserObject(tag, root);
                    if (tagNode == null) {
                        tagNode = new TreeNodeWps();
                        tagNode.setNodeType(TreeNodeWps.NodeType.FOLDER);
                        tagNode.setUserObject(tag);
                        tagNode.setCustomIcon(tag.toLowerCase());
                        tagNode.setValidNode(true);
                        if(hostUri != LOCALHOST_URI){
                            tagNode.setHostUri(hostUri);
                        }
                        tagModel.insertNodeInto(tagNode, root, 0);
                    }
                    if (getChildrenWithUri(uri, tagNode).isEmpty()) {
                        tagModel.insertNodeInto(script.deepCopy(), tagNode, 0);
                    }
                }
            }
            else{
                TreeNodeWps tagNode = getChildWithUserObject("no_tag", root);
                if(tagNode == null){
                    tagNode = new TreeNodeWps();
                    tagNode.setNodeType(TreeNodeWps.NodeType.FOLDER);
                    tagNode.setUserObject("no_tag");
                    tagNode.setValidNode(true);
                    if(hostUri != LOCALHOST_URI){
                        tagNode.setHostUri(hostUri);
                    }
                    tagModel.insertNodeInto(tagNode, root, 0);
                }
                if(getChildrenWithUri(uri, tagNode).isEmpty()){
                    tagModel.insertNodeInto(script.deepCopy(), tagNode, 0);
                }
            }
        }
        else{
            script.setUserObject(new File(uri).getName().replace(".groovy", ""));
            TreeNodeWps tagNode = getChildWithUserObject("invalid", root);
            if(tagNode == null){
                tagNode = new TreeNodeWps();
                tagNode.setNodeType(TreeNodeWps.NodeType.FOLDER);
                tagNode.setUserObject("invalid");
                tagNode.setValidNode(true);
                if(hostUri != LOCALHOST_URI){
                    tagNode.setHostUri(hostUri);
                }
                tagModel.insertNodeInto(tagNode, root, 0);
            }
            if(getChildrenWithUri(uri, tagNode).isEmpty()){
                tagModel.insertNodeInto(script.deepCopy(), tagNode, 0);
            }
        }
    }
}

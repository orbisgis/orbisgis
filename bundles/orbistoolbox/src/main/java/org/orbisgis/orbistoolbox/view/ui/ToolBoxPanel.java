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

import net.miginfocom.swing.MigLayout;
import org.orbisgis.orbistoolbox.model.Metadata;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.view.utils.ToolBoxIcon;
import org.orbisgis.orbistoolbox.view.utils.TreeNodeWps;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenFolderPanel;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.sif.components.fstree.CustomTreeCellRenderer;
import org.orbisgis.sif.components.fstree.FileTree;
import org.orbisgis.sif.components.fstree.FileTreeModel;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
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

    private TreeNodeWps addWps;

    private ActionCommands popupGlobalActions;
    private ActionCommands popupNodeActions;
    private ActionCommands popupLeafActions;

    public ToolBoxPanel(ToolBox toolBox){
        super(new MigLayout());

        this.toolBox = toolBox;

        this.addWps = new TreeNodeWps();
        addWps.setIsCustomIcon(true);
        addWps.setCustomIcon("folder_add");
        addWps.setUserObject("Add folder");
        addWps.setcanBeLeaf(false);

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
        tree.setCellRenderer(new CustomTreeCellRenderer(tree));
        tree.addMouseListener(EventHandler.create(MouseListener.class, this, "onMouseClicked", ""));

        JScrollPane treeScrollPane = new JScrollPane(tree);
        this.add(treeScrollPane, "wrap");
        this.add(treeNodeBox, "wrap");

        onModelSelected();

        createPopupActions();
    }

    public void onMouseClicked(MouseEvent event){
        if(event.getButton() == MouseEvent.BUTTON3) {
            JPopupMenu popupMenu = new JPopupMenu();
            if(event.getSource().equals(tree)){
                if(tree.getLastSelectedPathComponent() == null){
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
        else if(event.getClickCount() >= 2){
            TreeNodeWps selectedNode = (TreeNodeWps) ((FileTree)event.getSource()).getLastSelectedPathComponent();
            if(selectedNode != null) {
                if(selectedNode.equals(addWps)){
                    addLocalSource();
                }
                if (selectedNode.isLeaf() && selectedNode.canBeLeaf()) {
                    selectProcess(selectedNode);
                }
            }
        }
    }

    public void selectProcess(TreeNodeWps selectedNode){
        boolean isValidProcess = toolBox.selectProcess(selectedNode.getFilePath());
        selectedNode.setValid(isValidProcess);
    }

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

    public void addProcess(Process p, File f){
        String category = null;
        String subCategory = null;
        String subSubCategory = null;
        if(p != null && p.getMetadata() != null) {
            for (Metadata m : p.getMetadata()) {
                if (m.getRole().equals(URI.create("orbisgis:wps:utils:category"))) {
                    category = m.getTitle();
                }
                if (m.getRole().equals(URI.create("orbisgis:wps:utils:subCategory"))) {
                    subCategory = m.getTitle();
                }
                if (m.getRole().equals(URI.create("orbisgis:wps:utils:subSubCategory"))) {
                    subSubCategory = m.getTitle();
                }
            }
        }

        TreeNodeWps root = (TreeNodeWps) categoryModel.getRoot();
        TreeNodeWps script = new TreeNodeWps();
        script.setFilePath(f);

        if(category != null){
            TreeNodeWps categoryNode = null;
            for(int i = 0; i < categoryModel.getChildCount(root); i++){
                if(((TreeNodeWps)root.getChildAt(i)).getUserObject().equals(category)){
                    categoryNode = (TreeNodeWps)root.getChildAt(i);
                }
            }
            if(categoryNode == null){
                categoryNode = new TreeNodeWps();
                categoryNode.setUserObject(category);
                root.add(categoryNode);
            }

            if(subCategory != null){
                TreeNodeWps subCategoryNode = null;
                for(int i = 0; i < categoryNode.getChildCount(); i++){
                    if(((TreeNodeWps)categoryNode.getChildAt(i)).getUserObject().equals(subCategory)){
                        subCategoryNode = (TreeNodeWps)categoryNode.getChildAt(i);
                    }
                }
                if(subCategoryNode == null){
                    subCategoryNode = new TreeNodeWps();
                    subCategoryNode.setUserObject(subCategory);
                    categoryNode.add(subCategoryNode);
                }

                if(subSubCategory != null){
                    TreeNodeWps subSubCategoryNode = null;
                    for(int i = 0; i < subCategoryNode.getChildCount(); i++){
                        if(((TreeNodeWps)subCategoryNode.getChildAt(i)).getUserObject().equals(subSubCategory)){
                            subSubCategoryNode = (TreeNodeWps)subCategoryNode.getChildAt(i);
                        }
                    }
                    if(subSubCategoryNode == null){
                        subSubCategoryNode = new TreeNodeWps();
                        subSubCategoryNode.setUserObject(subSubCategory);
                        subCategoryNode.add(subSubCategoryNode);
                    }
                    subSubCategoryNode.add(script);
                }
                else {
                    subCategoryNode.add(script);
                }
            }
            else {
                categoryNode.add(script);
            }
        }
        else{
            TreeNodeWps undefined = null;
            for(int i = 0; i < categoryModel.getChildCount(root); i++){
                if(((TreeNodeWps)root.getChildAt(i)).getUserObject().equals("Undefined")){
                    undefined = (TreeNodeWps)root.getChildAt(i);
                }
            }
            if(undefined == null){
                undefined = new TreeNodeWps();
                undefined.setUserObject("Undefined");
                root.add(undefined);
            }
            undefined.add(script);
        }
        this.refresh();
    }

    public void refresh(){
        if(root.isNodeChild(addWps))
        root.remove(addWps);
        root.add(addWps);
        categoryModel.reload();
        fileModel.reload();
        this.revalidate();
    }

    /**
     * Adds a new local source for the toolBox.
     */
    public void addLocalSource() {
        File file = null;
        OpenFolderPanel openFolderPanel = new OpenFolderPanel("ToolBoxPanel.AddSource", "Add a source");

        //Wait the window answer and if the user validate set and run the export thread.
        if(UIFactory.showDialog(openFolderPanel)){
            file = openFolderPanel.getSelectedFile();
        }

        addLocalSource(file);
    }

    private TreeNodeWps addLocalSource(File file){
        if(file == null) {
            return null;
        }

        TreeNodeWps root = (TreeNodeWps) fileModel.getRoot();

        boolean exists = false;
        for(int i=0; i<root.getChildCount(); i++){
            if(((TreeNodeWps)root.getChildAt(i)).getUserObject().equals(file.getName())){
                exists = true;
            }
        }
        TreeNodeWps source = new TreeNodeWps();
        source.setcanBeLeaf(true);
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
                toolBox.addProcess(f);
                isScript = true;
            }
            source.setValid(isScript);
        }
        this.refresh();
        return source;
    }

    /**
     * Refreshes the selected source.
     */
    public void refreshSource(){
        TreeNodeWps node = ((TreeNodeWps)tree.getLastSelectedPathComponent());
        if(!node.isRoot() && !node.isLeaf()) {
            root.remove(node);
            addLocalSource(node.getFilePath());
        }
    }

    private List<File> getAllWpsScript(File file) {
        List<File> scriptList = new ArrayList<>();
        if (file.exists() && file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f != null) {
                    if (f.isFile() && f.getName().endsWith(".groovy")) {
                        scriptList.add(f);
                    }
                }
            }
        }
        return scriptList;
    }

    public void removeSelected(){
        TreeNodeWps selected = (TreeNodeWps)tree.getLastSelectedPathComponent();
        if(!selected.equals(root)){
            TreeNodeWps parent = (TreeNodeWps)selected.getParent();
            fileModel.removeNodeFromParent(selected);
            cleanParentNode(parent, fileModel);
            /*categoryModel.removeNodeFromParent(selected);
            cleanParentNode(parent, categoryModel);*/
            toolBox.removeSelected();
            this.refresh();
        }
    }

    private void cleanParentNode(TreeNodeWps node, FileTreeModel model){
        TreeNode[] treeNodeTab = model.getPathToRoot(node);
        TreeNodeWps parent = (TreeNodeWps)treeNodeTab[treeNodeTab.length-1];
        if(parent != root && parent.isLeaf() && parent.getParent() != null){
            model.removeNodeFromParent(parent);
            cleanParentNode(parent, model);
        }
    }


    private void createPopupActions() {
        DefaultAction addSource = new DefaultAction(
                ADD_SOURCE,
                "Add",
                "Add a local source",
                ToolBoxIcon.getIcon("folder_add"),
                EventHandler.create(ActionListener.class, this, "addLocalSource"),
                null
        );
        DefaultAction runScript = new DefaultAction(
                RUN_SCRIPT,
                "Run",
                "Run a script",
                ToolBoxIcon.getIcon("execute"),
                EventHandler.create(ActionListener.class, this, "selectProcess"),
                null
        );
        DefaultAction refresh_source = new DefaultAction(
                REFRESH_SOURCE,
                "Refresh",
                "Refresh a source",
                ToolBoxIcon.getIcon("refresh"),
                EventHandler.create(ActionListener.class, this, "refreshSource"),
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

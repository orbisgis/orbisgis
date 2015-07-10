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

package org.orbisgis.orbistoolbox.view;

import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenFolderPanel;
import org.orbisgis.sif.components.fstree.CustomTreeCellRenderer;
import org.orbisgis.sif.components.fstree.FileTree;
import org.orbisgis.sif.components.fstree.FileTreeModel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.beans.EventHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sylvain PALOMINOS
 **/

public class ToolBoxPanel extends JPanel {

    private ToolBox toolBox;
    private TreeNodeWps root;
    private FileTreeModel model;
    private JPanel panelProcess;
    private FileTree tree;

    public ToolBoxPanel(ToolBox toolBox){
        super(new BorderLayout());

        this.toolBox = toolBox;

        root = new TreeNodeWps();
        root.setUserObject("Local script");
        model = new FileTreeModel(root);
        tree = new FileTree(model);
        tree.setCellRenderer(new CustomTreeCellRenderer(tree));

        JScrollPane treeScrollPane = new JScrollPane(tree);
        ProcessInfoPanel processInfoPanel = new ProcessInfoPanel();
        panelProcess = new JPanel(new BorderLayout());
        JScrollPane infoScrollPane = new JScrollPane(panelProcess);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, infoScrollPane);
        splitPane.setResizeWeight(0.5);
        this.add(splitPane, BorderLayout.CENTER);

        tree.addTreeSelectionListener(EventHandler.create(TreeSelectionListener.class, this, "onNodeSelected", ""));
    }

    public void onNodeSelected(TreeSelectionEvent event){
        TreeNodeWps selectedNode = (TreeNodeWps) ((FileTree)event.getSource()).getLastSelectedPathComponent();
        if(selectedNode != null) {
            if (selectedNode.isLeaf()) {
                boolean isValidProcess = toolBox.selectProcess(selectedNode.getFilePath());
                selectedNode.setValid(isValidProcess);
            }
        }
    }

    public void setProcessUI(JComponent component){
        panelProcess.removeAll();
        panelProcess.add(component, BorderLayout.CENTER);
        panelProcess.revalidate();
    }

    public void addSource(){

        File file = null;

        OpenFolderPanel openFolderPanel = new OpenFolderPanel("ToolBoxPanel.AddSource", "Add a source");

        //Wait the window answer and if the user validate set and run the export thread.
        if(UIFactory.showDialog(openFolderPanel)){
            file = openFolderPanel.getSelectedFile();
        }

        addSource(file);
    }

    private TreeNodeWps addSource(File file){
        if(file == null) {
            return null;
        }

        boolean exists = false;
        for(int i=0; i<root.getChildCount(); i++){
            if(((TreeNodeWps)root.getChildAt(i)).getUserObject().equals(file.getName())){
                exists = true;
            }
        }
        TreeNodeWps source = new TreeNodeWps();
        if(!exists){
            source.setUserObject(file.getName());
            source.setFilePath(file);
            root.add(source);
            for(File f : getAllWpsScript(file)){
                TreeNodeWps script = new TreeNodeWps();
                script.setUserObject(f.getName().replace(".groovy",""));
                script.setFilePath(f);
                source.add(script);
            }
        }
        model.reload();
        return source;
    }

    public void refreshSource(){
        TreeNodeWps node = ((TreeNodeWps)tree.getLastSelectedPathComponent());
        root.remove(node);
        addSource(node.getFilePath());
    }

    private List<File> getAllWpsScript(File file) {
        List<File> scriptList = new ArrayList<>();
        if (file.exists() && file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f != null) {
                    /*if (file.isDirectory()) {
                        scriptList.addAll(getAllWpsScript(f));
                    }*/
                    if (f.isFile() && f.getName().endsWith(".groovy")) {
                        scriptList.add(f);
                    }
                }
            }
        }
        return scriptList;
    }
}

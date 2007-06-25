package org.orbisgis.plugin.view.ui.workbench;

import java.awt.GridLayout;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;

/**
 * Catalog class for GeoCatalog3
 * Provides a catalog of Sources and SQL Queries with their management
 * @author Samuel Chemla
 * TODO Save/Restore the catalog
 */

public class Catalog extends JPanel {

	private static final long serialVersionUID = 1L;
	protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected JTree tree;
    private DataSourceFactory dsf = null;	//The DataSourceFactory
    private DefaultMutableTreeNode sources, queries;	//Two main nodes
    
    public Catalog() {
    	
    	//Set tree parameters
        super(new GridLayout(1,0));
        rootNode = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(new MyTreeModelListener());
        tree = new JTree(treeModel);
        tree.setEditable(false);
        tree.setDragEnabled(true);	//Enables drag possibilities
        tree.getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        
        //Add the two main nodes (below the root node)
        sources = this.addObject(null, "Sources");
        queries = this.addObject(null, "SQL Queries");
        
        //Creating the DataSourceFactory
        dsf = new DataSourceFactory();
        
        //Puts the tree in a Scroll Pane
        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);
        
        //Expands the root node	then hide it...
        tree.expandPath(new TreePath( rootNode.getPath()));
        tree.setRootVisible(false);
    }

    /** Gets the currently selected node and delete it using removeNode()
     *
     */
    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                         (currentSelection.getLastPathComponent());
            this.removeNode(currentNode);
        }
        return;
    }

    /** Removes a node wether it is a source or a SQL request
     * @param currentNode : the node to remove
     * 
     */
    public void removeNode(DefaultMutableTreeNode currentNode) {
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {
            	if ("Sources".equals(parent.toString())) {
            		System.out.println("INFO GeoCatalog : Removing datasource "+currentNode);
            		dsf.remove(currentNode.toString());
            		treeModel.removeNodeFromParent(currentNode);
            	}
            	if ("SQL Queries".equals(parent.toString())) {
            		treeModel.removeNodeFromParent(currentNode);
            	}
            }
        return;
    }
    
	/**Add a source to GeoCatalog according to its path (flat file)
	 * 
	 * @param file The file you add
	 * @param name The name to give to the DataSource
	 * @return true if ok
	 * @throws Exception
	 */
	public boolean addSource(File file, String name) throws Exception {
		//TODO : maybe manage the fileNotFound exception
		DataSourceDefinition def = new FileSourceDefinition(file);
		dsf.registerDataSource(name, def);
		addObject(sources, name, true);
		//Print the name of the driver DataSource.getDriver().getName()
		//TODO : print it whithin the tree, at the end of the line
		System.out.println("INFO GeoCatalog : Added datasource " + name);
		return true;
	}
	
	/** Add a SQL Query to GeoCatalog and make it visible
	 * 
	 * @param command : The SQL request as String
	 * @return true if the command was successfull
	 */
	public boolean addQuery(String command) {
		addObject(queries, command,true);
		System.out.println("INFO GeoCatalog : Added the SQL query \""+command+"\"");
		return true;
	}

	/** Remove all Datasources children
	 *  
	 */
    public void clearsources() {
    	int total=sources.getChildCount();
        for (int count=0;count<total;count++) {
        	DefaultMutableTreeNode nodeToRemove=(DefaultMutableTreeNode)sources.getChildAt(0);
        	this.removeNode(nodeToRemove);
        }
        return;
    }
	
    /** Add child to the currently selected node. */
    private DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
        } else {
            parentNode = (DefaultMutableTreeNode)
                         (parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }

    /** Add child to any node
     * 
     * @param parent : the parent node
     * @param child : any object, here the Datasource's name
     * @return
     */
    private DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child) {
    		return addObject(parent, child, false);
    }

    /** Add child to any node
     * 
     * @param parent
     * @param child
     * @param shouldBeVisible tells if the path to the node should be expanded or not
     * @return
     */
    private DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean shouldBeVisible) {
    	DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

    	if (parent == null) {
    		parent = rootNode;
    	}

    	treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

    	//Make sure the user can see the lovely new node.
    	if (shouldBeVisible) {
    		tree.scrollPathToVisible(new TreePath(childNode.getPath()));
    	}
    	return childNode;
    }
    
    public class MyTreeModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
        	/*
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode)
                     (e.getTreePath().getLastPathComponent());

            /*
             * If the event lists children, then the changed
             * node is the child of the node we've already
             * gotten.  Otherwise, the changed node and the
             * specified node are the same.
             *
            try {
                int index = e.getChildIndices()[0];
                node = (DefaultMutableTreeNode)
                       (node.getChildAt(index));
            } catch (NullPointerException exc) {}

            System.out.println("The user has finished editing the node.");
            System.out.println("New value: " + node.getUserObject());
            */
        }
        public void treeNodesInserted(TreeModelEvent e) {
        }
        public void treeNodesRemoved(TreeModelEvent e) {
        }
        public void treeStructureChanged(TreeModelEvent e) {
        }
    }
}
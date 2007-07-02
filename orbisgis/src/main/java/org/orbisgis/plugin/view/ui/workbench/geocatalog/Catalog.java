package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;
import org.orbisgis.plugin.view.utilities.file.FileUtility;

/** This class contains a JTree used to represent instances of MyNode
 *  and manage them
 *  and drop them on some elements of a GeoView2DFrame
 * 
 * @author Samuel Chemla
 */
public class Catalog extends JPanel implements DropTargetListener {
	private static final long serialVersionUID = 1L;
	private final static DataSourceFactory dsf = TempPluginServices.dsf;//TODO : anything better than this
	
	private DefaultMutableTreeNode rootNode = null;
	private DefaultTreeModel treeModel = null;
	private JTree tree = null;
	private JPopupMenu treePopup = null;
	
	//Each time mouse is pressed we fill currentMyNode with the node the mouse was pressed on
	//TODO : manage also another MyNode when we are in dropOver to tell the user if he can do or not a drop
	private MyNode currentMyNode = null;
	
	private ActionsListener acl = null;//Handles all the actions performed in Catalog (and GeoCatalog)
    
	
	public Catalog(ActionsListener acl) {
		super(new GridLayout(1,0));
		rootNode = new DefaultMutableTreeNode("Root");
		treeModel = new DefaultTreeModel(rootNode);
		tree = new JTree(rootNode);
		tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(false);
        tree.setCellRenderer(new MyRenderer());
        tree.setDragEnabled(true);	//Enables drag possibilities
        tree.setDropTarget(new DropTarget(this, this));	//Enables drop possibilities
        add(new JScrollPane(tree));
        tree.addMouseListener(new MyMouseAdapter());
        this.acl = acl;
        
        getPopupMenu(); //Add the popup menu to the tree

        tree.expandPath(new TreePath( rootNode.getPath()));
        tree.setRootVisible(false);
	}
	
	 /** JTree : Add myNode to a specific node
     * 
     * @param myNode : the node you add (instance of MyNode)
     * @param father : its father (instance of DefaultMutableTreeNode)
     */
	private void addNode(MyNode myNode, DefaultMutableTreeNode father) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(myNode);
		myNode.setTreeNode(childNode);	//Registers the DefaultMutableTreeNode in myNode
		treeModel.insertNodeInto(childNode, father, father.getChildCount());
		
		//expand the path and refresh
		tree.scrollPathToVisible(new TreePath(childNode.getPath()));
		tree.updateUI();
	}
	
	/** Add myNode to the currently selected node
	 * Add it to the root node if nothing is selected
	 * @param myNode : the node you want to add
	 */
	public void addNode(MyNode myNode) {
		DefaultMutableTreeNode father = rootNode;
		if (currentMyNode!=null && currentMyNode.getType()==MyNode.folder) {
			father = (DefaultMutableTreeNode)currentMyNode.getTreeNode();
		}
		addNode(myNode,father);
	}
	
	/** Move exMyNode and put it as child of newMyNode
	 * 
	 * @param exMyNode
	 * @param newMyNode
	 */
	private void moveNode(MyNode exMyNode, MyNode newMyNode) {
		DefaultMutableTreeNode exNode = exMyNode.getTreeNode();
		DefaultMutableTreeNode newNode = newMyNode.getTreeNode();
		//TODO : We must check we wont put the parent in the child
		//TODO : Handle complex arborescence moving...
		if (!exNode.isLeaf()) {
		//	int total=exNode.getChildCount();
		//	for (int count=0;count<total;count++) {
		//       	DefaultMutableTreeNode nodeToMove=(DefaultMutableTreeNode)exNode.getChildAt(0);
		 //      	moveNode((MyNode)nodeToMove.getUserObject(),(MyNode)exNode.getUserObject());
		 //      }
		}
		
		treeModel.removeNodeFromParent(exNode);
		addNode(exMyNode,newNode);
		tree.updateUI();
	}
	
	/** Retrieves myNode at the location point and select the node at this point
	 * Use it like this : currentMyNode = getMyNodeAtPoint(anypoint);
	 * so the selected node and currentMyNode remains coherent
	 * 
	 * @param point
	 * @return
	 */
	private MyNode getMyNodeAtPoint(Point point){
		TreePath treePath = tree.getPathForLocation(point.x, point.y);
		MyNode myNode = null;
		tree.setSelectionPath(treePath);
		if (treePath!=null) {
			DefaultMutableTreeNode node =(DefaultMutableTreeNode) treePath.getLastPathComponent();
			myNode = (MyNode)node.getUserObject();
		}
		return myNode;
	}
	
    /** Removes a node wether it is a source or a SQL request
     * @param currentNode : the node to remove
     * 
     */
    public void removeNode() {
    	if (currentMyNode!=null) {
    		MutableTreeNode toDeleteNode = currentMyNode.getTreeNode();
    		int type = currentMyNode.getType();
    		switch (type) {
    			case MyNode.folder : 
    				break;
    			
    			case MyNode.datasource : 
    				//First we remove in geoview all the layers from the datasource we remove
    				//TODO : This code isn't so good because it imports Layers . . .
    				for (ILayer myLayer : TempPluginServices.lc.getLayers()) {
    					if (myLayer instanceof VectorLayer) {
    						VectorLayer myVectorLayer = (VectorLayer)myLayer;
    						if (myVectorLayer.getDataSource().getName().equals(currentMyNode.toString())) {
    							TempPluginServices.lc.remove(myLayer.getName());
    						}
    					}
    				}
    				//Then we remove the datasource
    				dsf.remove(currentMyNode.toString());
    				treeModel.removeNodeFromParent(toDeleteNode);
    				//TODO : check if sld links are removed from memory...
    				break;
    			case MyNode.sldfile : 
    				treeModel.removeNodeFromParent(toDeleteNode);
    				break;
    			case MyNode.sldlink : 
    				treeModel.removeNodeFromParent(toDeleteNode);
    				break;
    			case MyNode.sqlquery : 
    				treeModel.removeNodeFromParent(toDeleteNode);
    			case MyNode.raster :
    				for (ILayer myLayer : TempPluginServices.lc.getLayers()) {
    					if (myLayer instanceof RasterLayer) {
    						RasterLayer myVectorLayer = (RasterLayer)myLayer;
    						if (myVectorLayer.getName().equals(currentMyNode.toString())) {
    							TempPluginServices.lc.remove(myLayer.getName());
    						}
    					}
    				}
    				treeModel.removeNodeFromParent(toDeleteNode);
    				break;
    			default : 
    		}
    		tree.updateUI();
    		//If GeoView is opened, let's refresh it !
    		if (TempPluginServices.vf!=null) {
        		TempPluginServices.vf.refresh();
    		}
    	}
    }

	
	/** Edit here the popup menu */
	private void getPopupMenu() {
        JMenuItem menuItem;
        treePopup = new JPopupMenu();
        //Edit the popup menu.
        menuItem = new JMenuItem("New folder");
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("NEWFOLDER");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Add a data source file");
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("ADDSRCFILE");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Add a raster file");
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("ADDRASTER");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Add a SLD file");
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("ADDSLDFILE");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Add a SQL Query");
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("ADDSQL");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Delete");
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("DEL");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Clear catalog");
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("CLRCATALOG");
        treePopup.add(menuItem);
	}
	
	/**Add a file to GeoCatalog, wether it is a datasource or a sld file
	 * 
	 * @param file The file you add
	 * @param name The name to give to the DataSource
	 * @throws Exception
	 */
	public void addFile(File file, String name) throws Exception {
		DataSourceDefinition def = new FileSourceDefinition(file);
		String extension = FileUtility.getFileExtension(file);
		MyNode node = null;
		
		//removes the extension
		name = name.substring(0, name.indexOf("."+extension));
		if ("sld".equalsIgnoreCase(extension)) {
			node = new MyNode(name,MyNode.sldfile,null,file);
		} else {
			//Check for an already existing DataSource with the name provided and change it if necessary
			int i = 0;
			String tmpName = name;
			while (dsf.existDS(tmpName)) {
				i++; 
				tmpName=name+"_"+i;
			}
			name = tmpName;
			
			dsf.registerDataSource(name, def);
			node = new MyNode(name,MyNode.datasource,dsf.getDataSource(name).getDriver().getName(),file);
		}
		addNode(node);
	}
	
	public MyNode getCurrentMyNode() {
		return currentMyNode;
	}
	
	private class MyMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			currentMyNode = getMyNodeAtPoint(new Point(e.getX(),e.getY()));
			ShowPopup(e);
		}
		
		public void mouseReleased(MouseEvent e) {
			ShowPopup(e);
		}
		
		public void mouseClicked(MouseEvent e) {
		}
		
		private void ShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
            	
            	//Where did we click ?
            	if (currentMyNode==null) {
            		//Click outside the nodes
            	} else {
            		//Click on a node
            	}
                treePopup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
		
	}

	private class MyRenderer extends DefaultTreeCellRenderer {
		Icon folder = new ImageIcon(this.getClass().getResource("folder.png"));
		Icon open_folder = new ImageIcon(this.getClass().getResource("open_folder.png"));
		Icon datasource = new ImageIcon(this.getClass().getResource("datasource.png"));
		Icon sldfile = new ImageIcon(this.getClass().getResource("../sldStyle.png"));
		Icon sqlquery = new ImageIcon(this.getClass().getResource("sqlquery.png"));
		Icon sldlink = new ImageIcon(this.getClass().getResource("sldlink.png"));
		Icon shpfile = new ImageIcon(this.getClass().getResource("shp_file.png"));
		Icon csvfile = new ImageIcon(this.getClass().getResource("csv_file.png"));
		Icon tiffile = new ImageIcon(this.getClass().getResource("asc_file.png"));
		
		private static final long serialVersionUID = 1L;
		
		public Component getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel,expanded, leaf, row,hasFocus);
			
			if (value!=rootNode) { //Don't do anything on root node
				MyNode myNode = getMyNode(value);
				int type = myNode.getType();
				
				switch(type) {
				case MyNode.folder : setIcon(folder);
					setOpenIcon(open_folder);
					setLeafIcon(open_folder);
					break;
				case MyNode.datasource : setIcon(datasource);
					if ("Shapefile driver".equalsIgnoreCase(myNode.getDriverName())) {
						setIcon(shpfile);
					} else if ("csv string".equalsIgnoreCase(myNode.getDriverName())) {
						setIcon(csvfile);
					}
					break;
				case MyNode.sldfile : setIcon(sldfile);
					break;
				case MyNode.sldlink : setIcon(sldlink);
					break;
				case MyNode.sqlquery : setIcon(sqlquery);
					break;
				case MyNode.raster : setIcon(tiffile);
					
					break;
				default : setIcon(null);
				}
			}
			return this;
		}
		
		/** retrieves the object MyNode in the object value */
		private MyNode getMyNode(Object value) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			return (MyNode)node.getUserObject();
		}
	}

	public void dragEnter(DropTargetDragEvent dtde) {
	}
	public void dragExit(DropTargetEvent dte) {
	}
	public void dragOver(DropTargetDragEvent dtde) {
	}
	public void drop(DropTargetDropEvent dtde) {
		//Get the node where we drop
		MyNode dropNode = getMyNodeAtPoint(dtde.getLocation());
		if (dropNode!=null) {
			int dropType = dropNode.getType();
			int dragType = currentMyNode.getType();
			
			//Let's see where we dropped the node...
			switch(dropType) {
			
			//User dropped sth in a folder
			//TODO : finish and refine
			case MyNode.folder : 
				//TODO : enable D'n D for the folders...
				if (dragType == MyNode.datasource | dragType == MyNode.sldfile | dragType == MyNode.sqlquery/* | dragType == MyNode.folder*/) {
					moveNode(currentMyNode,dropNode);
				}
				dtde.rejectDrop();
				break;
			
			//User dropped a SLD file on a datasource : creates a link
			//TODO : finish and refine
			case MyNode.datasource : 
				if (dragType==MyNode.sldfile) {
					MyNode link = currentMyNode.createLink();
					addNode(link,dropNode.getTreeNode());
					System.out.println("You put a SLD file on a datasource !");
				}
				break;
			
			//No other operation possible in GeoCatalog
			default : dtde.rejectDrop();
			}
		}
	}
	public void dropActionChanged(DropTargetDragEvent dtde) {

	}
}
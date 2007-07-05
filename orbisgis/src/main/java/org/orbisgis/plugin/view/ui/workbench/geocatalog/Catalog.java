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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.csvstring.CSVStringDriver;
import org.gdms.driver.shapefile.ShapefileDriver;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;
import org.orbisgis.plugin.view.utilities.file.FileUtility;

import com.hardcode.driverManager.DriverLoadException;

/** This class contains a JTree used to represent instances of MyNode
 *  and manage them
 *  and drop them on some elements of a GeoView2DFrame
 * 
 * @author Samuel Chemla
 */
public class Catalog extends JPanel implements DropTargetListener {
	private static final String TIF = "tif";
	private static final String ASC = "asc";
	private static final long serialVersionUID = 1L;
	private final static DataSourceFactory dsf = TempPluginServices.dsf;//TODO : anything better than this
	
	private DefaultMutableTreeNode rootNode = null;
	private DefaultTreeModel treeModel = null;
	private JTree tree = null;
	private JPopupMenu treePopup = null;
	
	//Each time mouse is pressed we fill currentMyNode with the node the mouse was pressed on
	//TODO : manage also another MyNode when we are in dropOver to tell the user if he can do or not a drop
	private MyNode currentMyNode = null;
	
	private ActionsListener acl = null;		//Handles all the actions performed in Catalog (and GeoCatalog)
	private boolean isMovingNode = false;	//Helps to determine if we are in a moving operation
	
	private DsfListener dsfListener = null;
	
	//Icons
	private Icon addDataIcon = new ImageIcon(this.getClass().getResource("addData.png"));
	private Icon removeNodeIcon = new ImageIcon(this.getClass().getResource("remove.png"));
	private Icon clearIcon = new ImageIcon(this.getClass().getResource("clear.png"));
    private Icon newFolderIcon = new ImageIcon(this.getClass().getResource("new_folder.png"));
    private Icon openAttributesIcon =new ImageIcon(this.getClass().getResource("openattributes.png"));
	
	public Catalog(ActionsListener acl) {
		super(new GridLayout(1,0));
		
		MyNode rootMyNode = new MyNode("Root",MyNode.folder);
		rootNode = new DefaultMutableTreeNode("Root");
		rootMyNode.setTreeNode(rootNode);
		rootNode.setUserObject(rootMyNode);	//RootNode is a folder
		
		treeModel = new DefaultTreeModel(rootNode);
		tree = new JTree(rootNode);
		tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(false);
        tree.setCellRenderer(new MyRenderer());
        tree.setDragEnabled(true);	//Enables drag possibilities
        tree.setDropTarget(new DropTarget(this, this));	//Enables drop possibilities
        add(new JScrollPane(tree));
        
        //Register listeners
        tree.addMouseListener(new MyMouseAdapter());
        this.acl = acl;
        treeModel.addTreeModelListener(new MyTreeModelListener());
        //dsf Listener
        dsfListener = new DsfListener();
        dsfListener.setCatalog(this);
        dsf.addDataSourceFactoryListener(dsfListener);
        
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
		isMovingNode = true;
		//TODO : We must check we wont put the parent in the child
		//TODO : Handle complex arborescence moving...
		if (exNode.getParent()!=newNode) {
			treeModel.removeNodeFromParent(exNode);
			addNode(exMyNode,newNode);
			tree.updateUI();
		}
		if (!exNode.isLeaf()) {
		//	int total=exNode.getChildCount();
		//	for (int count=0;count<total;count++) {
		//       	DefaultMutableTreeNode nodeToMove=(DefaultMutableTreeNode)exNode.getChildAt(0);
		 //      	moveNode((MyNode)nodeToMove.getUserObject(),(MyNode)exNode.getUserObject());
		 //      }
		}

		
		isMovingNode = false;
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
	
	/**  Removes the currently selected node */
	public void removeNode() {
		removeNode(currentMyNode);
	}
	
    /** Removes a node whatever it is
     * @param myNodeToRemove : the node to remove
     * 
     */
    public void removeNode(MyNode myNodeToRemove) {
    	if (myNodeToRemove!=null) {
    		MutableTreeNode toDeleteNode = myNodeToRemove.getTreeNode();
    		if (myNodeToRemove.getType()==MyNode.folder) {
    			DefaultMutableTreeNode folderNode = myNodeToRemove.getTreeNode();
    			
    			//Checks now if folder is empty
    			if (!folderNode.isLeaf()) {
    				//We must convert from Enumeration to List.
    				//If not we miss some elements during the removing process
					Enumeration folderNodes = folderNode.depthFirstEnumeration();
					List <MyNode> myNodesRemove = new ArrayList<MyNode>();
					while (folderNodes.hasMoreElements()) {
						DefaultMutableTreeNode mi = (DefaultMutableTreeNode)folderNodes.nextElement();
						myNodesRemove.add((MyNode)mi.getUserObject());
					}
					while (!myNodesRemove.isEmpty()) {
						treeModel.removeNodeFromParent(myNodesRemove.get(0).getTreeNode());
						myNodesRemove.remove(0);
					}
    			} else treeModel.removeNodeFromParent(toDeleteNode);
    		}else treeModel.removeNodeFromParent(toDeleteNode);
    	}
    }

	
	/** Edit here the popup menu */
	private void getPopupMenu() {
        JMenuItem menuItem;
        treePopup = new JPopupMenu();
        //Edit the popup menu.
        menuItem = new JMenuItem("New folder");
        menuItem.setIcon(newFolderIcon  );
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("NEWFOLDER");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Add a DataSource");
        menuItem.setIcon(addDataIcon );
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("ADDSRC");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Add a raster file");
        menuItem.setIcon(addDataIcon );
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("ADDRASTER");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Add a SLD file");
        menuItem.setIcon(addDataIcon );
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("ADDSLDFILE");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Add a SQL Query");
        menuItem.setIcon(addDataIcon );
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("ADDSQL");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Open attributes");
        menuItem.setIcon(openAttributesIcon  );
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("OPENATTRIBUTES");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Delete");
        menuItem.setIcon(removeNodeIcon  );
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("DEL");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Clear catalog");
        menuItem.addActionListener(acl);
        menuItem.setIcon(clearIcon  );
        menuItem.setActionCommand("CLRCATALOG");
        treePopup.add(menuItem);
	}
	
	/** Add a file to GeoCatalog, wether it is a datasource or a sld file
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
		} else if (ASC.equalsIgnoreCase(extension)) {
			node = new MyNode(name,MyNode.raster,ASC,file);
		} else if (TIF.equalsIgnoreCase(extension) | "tiff".equalsIgnoreCase(extension)) {
			node = new MyNode(name,MyNode.raster,TIF,file);
		} else { //shp or csv
			//Check for an already existing DataSource with the name provided and change it if necessary
			int i = 0;
			String tmpName = name;
			while (dsf.existDS(tmpName)) {
				i++; 
				tmpName=name+"_"+i;
			}
			name = tmpName;
			
			dsf.registerDataSource(name, def);
			//DEPRECATED : Now we use the dsf listener...
			//node = new MyNode(name,MyNode.datasource,dsf.getDataSource(name).getDriver().getName(),file);
		}
		
		if (node!=null) {
			addNode(node);
		}		
	}
	
	/** Some preprocessing for addFile()
	 * 
	 * @param files the files you want to add
	 * @throws Exception
	 */
	public void addFiles(File[] files) {
		for (File file : files) {
			String name = file.getName();
			try {
				addFile(file, name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addDataBase(String[] parameters) {
		String request = "call register(";
		int length = parameters.length;
		
		//Creates the query
		for (int i=0; i<length; i++) {
			request = request + "'" + parameters[i] + "'";
			if (i<length-1) {
				request = request + ",";
			}
		}
		request = request + ");";
		System.out.println("GeoCatalog executing " + request);
		//And then execute it...
		
		try {
			dsf.executeSQL(request);
		} catch (SyntaxException e) {
			e.printStackTrace();
		} catch (DriverLoadException e) {
			e.printStackTrace();
		} catch (NoSuchTableException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
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
		Icon sldfile = new ImageIcon(this.getClass().getResource("sldStyle.png"));
		Icon sqlquery = new ImageIcon(this.getClass().getResource("sqlquery.png"));
		Icon sldlink = new ImageIcon(this.getClass().getResource("sldlink.png"));
		Icon shpfile = new ImageIcon(this.getClass().getResource("shp_file.png"));
		Icon csvfile = new ImageIcon(this.getClass().getResource("csv_file.png"));
		Icon tiffile = new ImageIcon(this.getClass().getResource("tif_file.png"));
		Icon ascfile = new ImageIcon(this.getClass().getResource("asc_file.png"));
		Icon dbffile = new ImageIcon(this.getClass().getResource("dbf_file.png"));
		
		private static final long serialVersionUID = 1L;
		
		public Component getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel,expanded, leaf, row,hasFocus);
			
			//Don't do anything on root node
			if (value!=rootNode) {
				
				//Else set a lovely style...
				MyNode myNode = getMyNode(value);
				int type = myNode.getType();
				switch(type) { 
				
				case MyNode.folder :
					if (leaf) {
						setIcon(open_folder);
					} else if (!expanded) {
						setIcon(folder);
					} else setIcon(open_folder);
					break;
				
				case MyNode.datasource : setIcon(datasource);
					if (ShapefileDriver.DRIVER_NAME.equalsIgnoreCase(myNode.getDriverName())) {
						setIcon(shpfile);
					} else if (CSVStringDriver.DRIVER_NAME.equalsIgnoreCase(myNode.getDriverName())) {
						setIcon(csvfile);
					} else if ("Dbf driver".equalsIgnoreCase(myNode.getDriverName())) {
						setIcon(dbffile);
					}
					break;
				
				case MyNode.sldfile : setIcon(sldfile);
					break;
				
				case MyNode.sldlink : setIcon(sldlink);
					break;
				
				case MyNode.sqlquery : setIcon(sqlquery);
					break;
				
				case MyNode.raster :
					if (ASC.equalsIgnoreCase(myNode.getDriverName())) {
						setIcon(ascfile);
					} else if (TIF.equalsIgnoreCase(myNode.getDriverName())) {
						setIcon(tiffile);
					}
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
		
		//By default drop on rootNode
		if (dropNode==null) {
			dropNode = (MyNode)rootNode.getUserObject();
		}
		
			int dropType = dropNode.getType();
			int dragType = currentMyNode.getType();
			
			//Let's see where we dropped the node...
			switch(dropType) {
			
			//User dropped sth in a folder
			case MyNode.folder : 
				//TODO : enable D'n D for the folders and complex arborescence...
				if (dragType == MyNode.datasource | dragType == MyNode.sldfile | dragType == MyNode.sqlquery | dragType == MyNode.raster/* | dragType == MyNode.folder*/) {
					moveNode(currentMyNode,dropNode);
				}
				dtde.rejectDrop();
				break;
			
			//User dropped a SLD file on a datasource : creates a link
			case MyNode.datasource : 
				if (dragType==MyNode.sldfile) {
					MyNode link = currentMyNode.createLink();
					addNode(link,dropNode.getTreeNode());
				}
				break;
			
			//No other operation possible in GeoCatalog
			default : dtde.rejectDrop();
			}
		//} 
	}
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}
	
	private class MyTreeModelListener implements TreeModelListener {
	    public void treeNodesChanged(TreeModelEvent e) {
	    }
	    
	    public void treeNodesInserted(TreeModelEvent e) {
	    }
	    
	    public void treeNodesRemoved(TreeModelEvent e) {
	    	//If we are in a moving node operation, don't do anything
			if (!isMovingNode) {
		    	//A node has been deleted, let's remove some linked stuff
		    	//(remove linked layers and entries in DatasourceFactory)
				for (Object obj : e.getChildren()) {
					DefaultMutableTreeNode deletedNode = (DefaultMutableTreeNode) obj;
					MyNode deletedMyNode = (MyNode) deletedNode.getUserObject();
					int type = deletedMyNode.getType();
					switch (type) {
					case MyNode.datasource:
						//First we remove in geoview all the layers from the datasource we remove
						//TODO : This code isn't so good because it imports Layers . . .
						for (ILayer myLayer : TempPluginServices.lc.getLayers()) {
							if (myLayer instanceof VectorLayer) {
								VectorLayer myVectorLayer = (VectorLayer) myLayer;
								if (myVectorLayer.getDataSource().getName()
										.equals(deletedMyNode.toString())) {
									TempPluginServices.lc.remove(myLayer
											.getName());
								}
							}
						}
						//Then we remove the datasource
						dsf.remove(deletedMyNode.toString());
						break;
					case MyNode.raster:
						for (ILayer myLayer : TempPluginServices.lc.getLayers()) {
							if (myLayer instanceof RasterLayer) {
								RasterLayer myVectorLayer = (RasterLayer) myLayer;
								if (myVectorLayer.getName().equals(
										deletedMyNode.toString())) {
									TempPluginServices.lc.remove(myLayer
											.getName());
								}
							}
						}
						break;
					default:
					}
					tree.updateUI();
					//If GeoView is opened, let's refresh it !
					if (TempPluginServices.vf != null) {
						TempPluginServices.vf.refresh();
					}
				}
			}	    	
	    }
	    
	    public void treeStructureChanged(TreeModelEvent e) {
	    }
	}
}
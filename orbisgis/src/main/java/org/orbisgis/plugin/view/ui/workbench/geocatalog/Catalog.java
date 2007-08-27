package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;
import org.orbisgis.plugin.view.utilities.file.FileUtility;

import com.hardcode.driverManager.DriverLoadException;

/**
 * This class contains a JTree used to represent instances of MyNode and manage
 * them and drop them on some elements of a GeoView2DFrame
 * 
 * @author Samuel Chemla
 */
public class Catalog extends JPanel implements DropTargetListener,
		DragGestureListener, DragSourceListener {

	public static final String TIF = "tif";

	public static final String ASC = "asc";

	// TODO : How long are we going to use TempPluginServices ??
	private final static DataSourceFactory dsf = TempPluginServices.dsf;

	private MyNode rootNode = new MyNode("Root", MyNode.folder);

	private CatalogModel catalogModel = null;

	private CatalogRenderer catalogRenderer = null;

	private CatalogEditor catalogEditor = null;

	private JTree tree = null;

	private CatalogPopups catalogPopups = null;

	// Used to create a transfer when dragging
	private DragSource source = null;

	// Each time mouse is pressed we fill currentNode with the node the mouse
	// was pressed on
	// TODO : manage also another MyNode when we are in dropOver to tell the
	// user if he can do or not a drop
	private MyNode currentNode = null;

	// Handles all the actions performed in Catalog (and GeoCatalog)
	private ActionsListener acl = null;

	// DataSourceFactory listener used to listen to dsf changes
	private DsfListener dsfListener = null;

	private MyTreeModelListener treeModelListener = null;

	/** *** Catalog constructor **** */
	public Catalog(ActionsListener acl) {
		super(new GridLayout(1, 0));

		catalogModel = new CatalogModel(rootNode);
		tree = new JTree(catalogModel);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(false);
		catalogRenderer = new CatalogRenderer();
		tree.setCellRenderer(catalogRenderer);
		catalogEditor = new CatalogEditor(tree, catalogRenderer);
		tree.setCellEditor(catalogEditor);

		/***********************************************************************
		 * DO NOT UNCOMMENT *
		 * 
		 * setDragEnabled(true);
		 * 
		 * This method is a swing method while our DnD is using awt. Using both
		 * swing and awt creates horrible exceptions... Please use DragSource
		 * instead
		 * 
		 */

		/** *** Drag and Drop stuff **** */
		tree.setDropTarget(new DropTarget(this, this));
		source = new DragSource();
		source.createDefaultDragGestureRecognizer(tree,
				DnDConstants.ACTION_MOVE, this);

		/** *** Register listeners **** */
		tree.addMouseListener(new MyMouseAdapter());
		this.acl = acl;
		treeModelListener = new MyTreeModelListener();
		catalogModel.addTreeModelListener(treeModelListener);
		dsfListener = new DsfListener();
		dsfListener.setCatalog(this);
		dsf.addDataSourceFactoryListener(dsfListener);

		/** *** UI stuff **** */
		add(new JScrollPane(tree));
		catalogPopups = new CatalogPopups(this.acl); // Add the popups menus
		// to the tree
		tree.setRootVisible(false);
	}

	/**
	 * JTree : Add myNode to a specific node
	 * 
	 * @param myNode :
	 *            the node you add (instance of MyNode)
	 * @param father :
	 *            its father (instance of DefaultMutableTreeNode)
	 */
	public void addNode(MyNode myNode, MyNode father) {
		int index = father.getChildCount();

		// If we have a folder, let's put it at the top
		if (myNode.getType() == MyNode.folder) {
			index = 0;
		}

		catalogModel.insertNodeInto(myNode, father, index);

		// expand the path and refresh
		tree.scrollPathToVisible(new TreePath(myNode.getPath()));
		tree.updateUI();
	}

	/**
	 * Add myNode to the currently selected node Add it to the root node if
	 * nothing is selected
	 * 
	 * @param myNode :
	 *            the node you want to add
	 */
	public void addNode(MyNode myNode) {
		MyNode father = rootNode;
		if (currentNode != null && currentNode.getType() == MyNode.folder) {
			father = currentNode;
		}
		addNode(myNode, father);
	}

	public void clearCatalog() {
		catalogModel.removeAllNodes();
		tree.updateUI();
	}

	/**
	 * Move exMyNode and put it as child of newMyNode
	 * 
	 * @param exMyNode
	 * @param newMyNode
	 */
	private void moveNode(MyNode exNode, MyNode newNode) {
		ArrayList<MyNode> children = exNode.depthChildList();

		// We must check we wont put a parent in one of its children
		if (!children.contains(newNode)) {
			catalogModel.removeNodeFromParent(exNode, false);
			addNode(exNode, newNode);
			tree.updateUI();
		}
	}

	/**
	 * Retrieves myNode at the location point and select the node at this point
	 * Use it like this : currentNode = getMyNodeAtPoint(anypoint); so the
	 * selected node and currentNode remains coherent
	 * 
	 * @param point
	 * @return
	 */
	private MyNode getMyNodeAtPoint(Point point) {
		TreePath treePath = tree.getPathForLocation(point.x, point.y);
		MyNode myNode = null;
		tree.setSelectionPath(treePath);
		if (treePath != null) {
			myNode = (MyNode) treePath.getLastPathComponent();
		}
		return myNode;
	}

	/** Removes the currently selected node */
	public void removeNode() {
		removeNode(currentNode);
	}

	/**
	 * Removes a node whatever it is
	 * 
	 * @param myNodeToRemove :
	 *            the node to remove
	 * 
	 */
	public void removeNode(MyNode nodeToRemove) {
		if (nodeToRemove != null) {
			catalogModel.removeNodeFromParent(nodeToRemove, true);
			tree.updateUI();
		}
	}

	/**
	 * Add a file to GeoCatalog, wether it is a datasource or a sld file
	 * 
	 * @param file
	 *            The file you add
	 * @param name
	 *            The name to give to the DataSource
	 * @throws Exception
	 */
	public void addFile(File file, String name) throws Exception {
		DataSourceDefinition def = new FileSourceDefinition(file);
		String extension = FileUtility.getFileExtension(file);
		MyNode node = null;
		String path = file.getPath();

		// removes the extension
		name = name.substring(0, name.indexOf("." + extension));
		if ("sld".equalsIgnoreCase(extension)) {
			node = new MyNode(name, MyNode.sldfile, null, path);

		} else if (ASC.equalsIgnoreCase(extension)) {
			node = new MyNode(name, MyNode.raster, ASC, path);

		} else if (TIF.equalsIgnoreCase(extension)
				| "tiff".equalsIgnoreCase(extension)) {
			node = new MyNode(name, MyNode.raster, TIF, path);

		} else if ("png".equalsIgnoreCase(extension)) {
			node = new MyNode(name, MyNode.raster, null, path);

		} else if ("shp".equalsIgnoreCase(extension)
				| "csv".equalsIgnoreCase(extension)
				| "cir".equalsIgnoreCase(extension)) {

			// Check for an already existing DataSource with the name provided
			// and change it if necessary
			int i = 0;
			String tmpName = name;
			while (dsf.existDS(tmpName)) {
				i++;
				tmpName = name + "_" + i;
			}
			name = tmpName;

			dsf.registerDataSource(name, def);
		} else throw new Error("Unknown node added at addFile(), Catalog.java");

		if (node != null) {
			// Change the name if necessary
			node = setName(node);
			// Then add the node
			addNode(node);
		}
	}

	/**
	 * Some preprocessing for addFile()
	 * 
	 * @param files
	 *            the files you want to add
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

		// Creates the query
		for (int i = 0; i < length; i++) {
			request = request + "'" + parameters[i] + "'";
			if (i < length - 1) {
				request = request + ",";
			}
		}
		request = request + ");";
		System.out.println("GeoCatalog executing " + request);
		// And then execute it...

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

	public MyNode getCurrentNode() {
		return currentNode;
	}

	/**
	 * Once the user begins a drag, this is executed. It creates an instance of
	 * MyNodeTransferable, which can be retrieved during the drop.
	 */
	public void dragGestureRecognized(DragGestureEvent dge) {
		currentNode = getMyNodeAtPoint(dge.getDragOrigin());
		if (currentNode != null) {
			MyNodeTransferable data = new MyNodeTransferable(currentNode);
			if (data != null) {
				source.startDrag(dge, DragSource.DefaultMoveDrop, data, this);
			}
		}
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
	}

	public void dragEnter(DragSourceDragEvent dsde) {
	}

	public void dragExit(DragSourceEvent dse) {
	}

	public void dragOver(DragSourceDragEvent dsde) {
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
	}

	public void dragEnter(DropTargetDragEvent dtde) {
	}

	public void dragExit(DropTargetEvent dte) {
	}

	public void dragOver(DropTargetDragEvent dtde) {
	}

	public void drop(DropTargetDropEvent dtde) {

		/** *** DROP STUFF **** */
		// Get the node where we drop
		MyNode dropNode = getMyNodeAtPoint(dtde.getLocation());
		// By default drop on rootNode
		if (dropNode == null) {
			dropNode = rootNode;
		}
		int dropType = dropNode.getType();

		/** *** DRAG STUFF **** */
		Transferable transferable = dtde.getTransferable();
		if (transferable.isDataFlavorSupported(MyNodeTransferable.myNodeFlavor)) {
			try {
				MyNode myNode = (MyNode) transferable
						.getTransferData(MyNodeTransferable.myNodeFlavor);
				int dragType = myNode.getType();

				// Let's see where we dropped the node...
				switch (dropType) {

				// User dropped sth in a folder
				case MyNode.folder:
					if (dragType == MyNode.datasource
							| dragType == MyNode.sldfile
							| dragType == MyNode.folder
							| dragType == MyNode.sqlquery
							| dragType == MyNode.raster) {

						// Ensure we are not moving within the same directory or
						// on itself
						if (!myNode.getParent().equals(dropNode)
								&& !myNode.equals(dropNode)) {
							moveNode(myNode, dropNode);
						}
					}
					break;

				// User dropped a SLD file on a datasource : creates a link
				case MyNode.datasource:
					if (dragType == MyNode.sldfile) {
						DataSource ds;
						try {
							// Let's see if we have a spacial DataSource.
							// if so let's create a SLD link
							ds = TempPluginServices.dsf.getDataSource(dropNode
									.getName());
							if (TypeFactory.IsSpatial(ds)) {
								MyNode link = myNode.createLink();
								addNode(link, dropNode);
							}
						} catch (DriverLoadException e) {
							e.printStackTrace();
						} catch (NoSuchTableException e) {
							e.printStackTrace();
						} catch (DataSourceCreationException e) {
							e.printStackTrace();
						} catch (DriverException e) {
							e.printStackTrace();
						}
					}
					break;

				// No other operation possible in GeoCatalog
				default:
				}
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		dtde.rejectDrop();
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	/**
	 * check for an identical name in ALL the catalog nodes and set a new name
	 * if necessary
	 */
	private MyNode setName(MyNode node) {
		MyNode nodeToReturn = node;

		int i = 0;
		String name = node.getName();
		while (catalogModel.existNode(node)) {
			i++;
			node.setName(name + "_" + i);
		}

		return nodeToReturn;
	}

	public void setRootNode(MyNode root) {
		rootNode = root;
		catalogModel.setRootNode(root);
		tree.updateUI();
	}

	public MyNode getRootNode() {
		return rootNode;
	}

	private class MyMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			currentNode = getMyNodeAtPoint(new Point(e.getX(), e.getY()));
			ShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			ShowPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
			// TODO could be interesting...but not that simple
			// catalogEditor.stopCellEditing();
		}

		private void ShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {

				// Where did we click ?
				if (currentNode == null) {
					// Clik in void
					catalogPopups.getVoidPopup().show(e.getComponent(),
							e.getX(), e.getY());
				} else {
					// Click on a node
					catalogPopups.getNodePopup(currentNode).show(
							e.getComponent(), e.getX(), e.getY());
				}
			}
		}

	}

	private class MyTreeModelListener implements TreeModelListener {
		public void treeNodesChanged(TreeModelEvent e) {
		}

		public void treeNodesInserted(TreeModelEvent e) {
		}

		public void treeNodesRemoved(TreeModelEvent e) {

			// A node has been deleted, let's remove some linked stuff
			// ie (remove linked layers and entries in DatasourceFactory)

			for (Object obj : e.getChildren()) {
				MyNode deletedNode = (MyNode) obj;
				int type = deletedNode.getType();
				switch (type) {
				case MyNode.datasource:
					// First we remove in geoview all the layers from the
					// datasource we remove
					// TODO : This code isn't so good because it imports
					// Layers . . .
					for (ILayer myLayer : TempPluginServices.lc.getLayers()) {
						if (myLayer instanceof VectorLayer) {
							VectorLayer myVectorLayer = (VectorLayer) myLayer;
							if (myVectorLayer.getDataSource().getName().equals(
									deletedNode.toString())) {
								TempPluginServices.lc.remove(myLayer.getName());
							}
						}
					}
					// Then we remove the datasource
					dsf.remove(deletedNode.toString());
					break;
				case MyNode.raster:
					for (ILayer myLayer : TempPluginServices.lc.getLayers()) {
						if (myLayer instanceof RasterLayer) {
							RasterLayer myVectorLayer = (RasterLayer) myLayer;
							if (myVectorLayer.getName().equals(
									deletedNode.toString())) {
								TempPluginServices.lc.remove(myLayer.getName());
							}
						}
					}
					break;
				default:
				}

				// If GeoView is opened, let's refresh it !
				if (TempPluginServices.vf != null) {
					TempPluginServices.vf.refresh();
				}
			}
		}

		public void treeStructureChanged(TreeModelEvent e) {
		}
	}

}
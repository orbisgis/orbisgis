package org.orbisgis.geocatalog;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.gdms.data.DataSourceFactoryEvent;
import org.gdms.data.DataSourceFactoryListener;
import org.gdms.data.NoSuchTableException;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.resources.Folder;
import org.orbisgis.geocatalog.resources.GdmsSource;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.ResourceWizardEP;
import org.orbisgis.geocatalog.resources.TransferableResource;

public class Catalog extends JPanel implements DropTargetListener,
		DragGestureListener, DragSourceListener {

	private static final String CLRCATALOG = "CLRCATALOG";
	private static final String NEWFOLDER = "NEWFOLDER";
	private static final String DEL = "DEL";
	private static final String NEW = "NEW";

	private Folder rootNode = new Folder("Root");

	private CatalogModel catalogModel = null;

	private CatalogRenderer catalogRenderer = null;

	private CatalogEditor catalogEditor = null;

	private JTree tree = null;

	// Used to create a transfer when dragging
	private DragSource source = null;

	// Each time mouse is pressed we fill currentNode with the node the mouse
	// was pressed on
	// TODO : manage also another MyNode when we are in dropOver to tell the
	// user if he can do or not a drop
	private IResource currentNode = null;

	// Handles all the actions performed in Catalog (and GeoCatalog)
	private ActionListener acl = null;

	// DataSourceFactory listener used to listen to dsf changes
	// private DsfListener dsfListener = null;

	private MyTreeModelListener treeModelListener = null;

	private boolean ignoreSourceOperations = false;

	/** *** Catalog constructor **** */
	public Catalog() {
		super(new GridLayout(1, 0));

		catalogModel = new CatalogModel(rootNode);
		tree = new JTree(catalogModel);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		catalogRenderer = new CatalogRenderer();
		tree.setCellRenderer(catalogRenderer);
		catalogEditor = new CatalogEditor(tree);
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
		this.acl = new GeocatalogActionListener();
		treeModelListener = new MyTreeModelListener();
		catalogModel.addTreeModelListener(treeModelListener);
		// dsfListener = new DsfListener();
		// dsfListener.setCatalog(this);
		// dsf.addDataSourceFactoryListener(dsfListener);

		/** *** UI stuff **** */
		add(new JScrollPane(tree));
		// to the tree
		tree.setRootVisible(false);

		OrbisgisCore.getDSF().addDataSourceFactoryListener(
				new DataSourceFactoryListener() {

					public void sqlExecuted(DataSourceFactoryEvent event) {
						// TODO Auto-generated method stub

					}

					public void sourceRemoved(final DataSourceFactoryEvent e) {
						if (ignoreSourceOperations) {
							return;
						}
						IResource[] res = getCatalogModel().getNodes(
								new NodeFilter() {

									public boolean accept(IResource resource) {
										if (resource instanceof GdmsSource) {
											if (resource.getName().equals(
													e.getName())) {
												return true;
											} else {
												return false;
											}
										} else {
											return false;
										}
									}

								});
						getCatalogModel().removeNode(res[0]);
					}

					public void sourceNameChanged(DataSourceFactoryEvent e) {
						// TODO Auto-generated method stub

					}

					public void sourceAdded(DataSourceFactoryEvent e) {
						if (ignoreSourceOperations) {
							return;
						}
						String name = e.getName();
						String driver = null;

						if (e.isWellKnownName()) {
							try {
								driver = OrbisgisCore.getDSF().getDriver(name);
							} catch (NoSuchTableException e2) {
								e2.printStackTrace();
							}
							if (driver != null) {
								GdmsSource node = new GdmsSource(name);
								getCatalogModel().insertNode(node);
							}
						}

					}

				});

	}

	public void clearCatalog() {
		catalogModel.removeAllNodes();
	}

	/**
	 * Move exMyNode and put it as child of newMyNode
	 *
	 * @param exMyNode
	 * @param newMyNode
	 */
	private void moveNode(IResource exNode, IResource newNode) {
		// Ensure we are not moving within the same directory or
		// on itself
		if (exNode != newNode && exNode.getParent() != newNode) {
			ArrayList<IResource> children = exNode.depthChildList();
			// We must check we wont put a parent in one of its children
			if (!children.contains(newNode)) {
				catalogModel.removeNode(exNode, false);
				catalogModel.insertNodeInto(exNode, newNode);
			}
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
	private IResource getMyNodeAtPoint(Point point) {
		TreePath treePath = tree.getPathForLocation(point.x, point.y);
		IResource myNode = null;
		tree.setSelectionPath(treePath);
		if (treePath != null) {
			myNode = (IResource) treePath.getLastPathComponent();
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
	public void removeNode(IResource nodeToRemove) {
		if (nodeToRemove != null) {
			catalogModel.removeNode(nodeToRemove, true);
		}
	}

	public IResource getCurrentNode() {
		return currentNode;
	}

	/**
	 * Once the user begins a drag, this is executed. It creates an instance of
	 * TransferableResource, which can be retrieved during the drop.
	 */
	public void dragGestureRecognized(DragGestureEvent dge) {
		currentNode = getMyNodeAtPoint(dge.getDragOrigin());
		if (currentNode != null) {
			TransferableResource data = new TransferableResource(currentNode);
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
		IResource dropNode = getMyNodeAtPoint(dtde.getLocation());

		// By default drop on rootNode
		if (dropNode == null) {
			dropNode = rootNode;
		}

		/** *** DRAG STUFF **** */
		Transferable transferable = dtde.getTransferable();
		if (transferable
				.isDataFlavorSupported(TransferableResource.myNodeFlavor)) {
			try {
				IResource myNode = (IResource) transferable
						.getTransferData(TransferableResource.myNodeFlavor);

				// If we dropped on a folder, move the resource
				if (dropNode instanceof Folder) {
					moveNode(myNode, dropNode);

					// Else we do addChild() so the drop resource decide the
					// action to do
					// TODO : it may be better to create a new function boolean
					// ressource.moveOn(IRessource). If the boolean returns true
					// do the move else abort...
				} else {
					dropNode.addChild(myNode);
					tree.scrollPathToVisible(new TreePath(myNode.getPath()));
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

	private class MyMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			currentNode = getMyNodeAtPoint(new Point(e.getX(), e.getY()));
			showPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			showPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
			// TODO could be interesting...but not that simple
			// catalogEditor.stopCellEditing();
		}

		private void showPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				getPopup().show(e.getComponent(), e.getX(), e.getY());
			}
		}

		private JPopupMenu getPopup() {
			JPopupMenu popup = new JPopupMenu();

			popup.add(getMenu("New...", NEW, null));

			return popup;
		}

		/**
		 * A command to clear catalog.
		 */
		private JMenuItem getMenu(String text, String actionCommand, String icon) {
			JMenuItem menuItem = new JMenuItem(text);
			menuItem.addActionListener(acl);
			menuItem.setActionCommand(actionCommand);

			if (icon != null) {
				Icon clearIcon = new ImageIcon(this.getClass()
						.getResource(icon));
				menuItem.setIcon(clearIcon);
			}

			return menuItem;
		}

	}

	private class MyTreeModelListener implements TreeModelListener {
		public void treeNodesChanged(TreeModelEvent e) {
		}

		public void treeNodesInserted(TreeModelEvent e) {
		}

		public void treeNodesRemoved(TreeModelEvent e) {
		}

		public void treeStructureChanged(TreeModelEvent e) {
		}
	}

	private class GeocatalogActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (NEW.equals(e.getActionCommand())) {
				IResource parent = rootNode;
				if (currentNode != null) {
					parent = currentNode;
				}
				IResource[] resources = ResourceWizardEP.openWizard(Catalog.this);
				for (IResource resource : resources) {
					getCatalogModel().insertNodeInto(resource, parent);
				}
			}
//
//			if (DEL.equals(e.getActionCommand())) {
//				// Removes the selected node
//				if (JOptionPane.showConfirmDialog(parent,
//						"Are you sure you want to delete this node ?",
//						"Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
//					Catalog.this.removeNode();
//				}
//
//			} else if (NEWFOLDER.equals(e.getActionCommand())) {
//				String name = JOptionPane.showInputDialog(parent, "Name");
//				if (name != null && name.length() != 0) {
//					Folder newNode = new Folder(name);
//					catalogModel.insertNode(newNode);
//				}
//
//			} else if (CLRCATALOG.equals(e.getActionCommand())) {
//				// Clears the catalog
//				if (JOptionPane.showConfirmDialog(parent,
//						"Are you sure you want to clear the catalog ?",
//						"Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
//					Catalog.this.clearCatalog();
//				}
//			}
		}

	}

	public CatalogModel getCatalogModel() {
		return catalogModel;
	}

	public void setIgnoreSourceOperations(boolean b) {
		ignoreSourceOperations = b;
	}

}
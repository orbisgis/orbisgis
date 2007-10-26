package org.orbisgis.core.resourceTree;

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
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public abstract class ResourceTree extends JPanel implements DropTargetListener,
		DragGestureListener, DragSourceListener {

	private Folder rootNode = new Folder("Root");

	private ResourceTreeModel catalogModel = null;

	private ResourceTreeRenderer catalogRenderer = null;

	private ResourceTreeEditor resourceTreeEditor = null;

	protected JTree tree = null;

	// Used to create a transfer when dragging
	private DragSource source = null;

	/** *** Catalog constructor **** */
	public ResourceTree() {
		super(new GridLayout(1, 0));

		tree = new JTree();
		/** *** Register listeners **** */
		tree.addMouseListener(new MyMouseAdapter());
		catalogModel = new ResourceTreeModel(tree, rootNode);
		tree.setModel(catalogModel);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		catalogRenderer = new ResourceTreeRenderer();
		tree.setCellRenderer(catalogRenderer);
		resourceTreeEditor = new ResourceTreeEditor(tree);
		tree.setCellEditor(resourceTreeEditor);

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
		source = DragSource.getDefaultDragSource();
		source.createDefaultDragGestureRecognizer(tree,
				DnDConstants.ACTION_COPY_OR_MOVE, this);


		/** *** UI stuff **** */
		add(new JScrollPane(tree));
		tree.setRootVisible(false);

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
				catalogModel.removeNode(exNode, true);
				catalogModel.insertNodeInto(exNode, newNode);
			}
		}
	}

	protected IResource[] getSelectedResources() {
		TreePath[] paths = tree.getSelectionPaths();
		if (paths == null) {
			return new IResource[0];
		} else {
			IResource[] ret = new IResource[paths.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = (IResource) paths[i].getLastPathComponent();
			}

			return ret;
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
		if (treePath != null) {
			myNode = (IResource) treePath.getLastPathComponent();
		}
		return myNode;
	}

	/**
	 * Once the user begins a drag, this is executed. It creates an instance of
	 * TransferableResource, which can be retrieved during the drop.
	 */
	public void dragGestureRecognized(DragGestureEvent dge) {
		IResource[] resources = getSelectedResources();
		if (resources.length > 0) {
			TransferableResource data = new TransferableResource(resources);
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
				IResource[] myNode = (IResource[]) transferable
						.getTransferData(TransferableResource.myNodeFlavor);

				// If we dropped on a folder, move the resource
				if (dropNode instanceof Folder) {
					for (IResource resource : myNode) {
						moveNode(resource, dropNode);
					}
				} else {
					for (IResource resource : myNode) {
						dropNode.addChild(resource);
						tree.scrollPathToVisible(new TreePath(resource
								.getPath()));
					}
				}

			} catch (UnsupportedFlavorException e) {
			} catch (IOException e) {
			}
		}
		dtde.rejectDrop();
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public ResourceTreeModel getTreeModel() {
		return catalogModel;
	}

	protected class MyMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			showPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			showPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
		}

		private void showPopup(MouseEvent e) {
			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			TreePath[] selectionPaths = tree.getSelectionPaths();
			if ((selectionPaths != null) && (path != null)) {
				if (!contains(selectionPaths, path)) {
					tree.setSelectionPath(path);
				}
			} else {
				tree.setSelectionPath(path);
			}
			if (e.isPopupTrigger()) {
				getPopup().show(e.getComponent(), e.getX(), e.getY());
			}
		}

		private boolean contains(TreePath[] selectionPaths, TreePath path) {
			for (TreePath treePath : selectionPaths) {
				boolean equals = true;
				Object[] objectPath = treePath.getPath();
				Object[] testPath = path.getPath();
				if (objectPath.length != testPath.length) {
					equals = false;
				}
				for (int i = 0; i < testPath.length; i++) {
					if (testPath[i] != objectPath[i]) {
						equals = false;
					}
				}
				if (equals) {
					return true;
				}
			}

			return false;
		}
	}

	public abstract JPopupMenu getPopup();
}
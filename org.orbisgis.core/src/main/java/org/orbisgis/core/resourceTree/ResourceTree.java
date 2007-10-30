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
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.orbisgis.core.resourceTree.TransferableResource.Data;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;

public abstract class ResourceTree extends JPanel implements
		DropTargetListener, DragGestureListener, DragSourceListener {

	private IResource rootNode = new Folder("Root");

	private ResourceTreeModel treeModel = null;

	private ResourceTreeEditor resourceTreeEditor = null;

	protected JTree tree = null;

	// Used to create a transfer when dragging
	private DragSource dragSource = null;

	/** *** Catalog constructor **** */
	public ResourceTree() {
		super(new GridLayout(1, 0));

		tree = new JTree();
		/** *** Register listeners **** */
		tree.addMouseListener(new MyMouseAdapter());
		treeModel = new ResourceTreeModel(tree, rootNode);
		tree.setModel(treeModel);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new ResourceTreeRenderer());
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
		dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(tree,
				DnDConstants.ACTION_COPY_OR_MOVE, this);

		/** *** UI stuff **** */
		add(new JScrollPane(tree));
		tree.setRootVisible(false);

	}

	public void clearCatalog() {
		treeModel.removeAllNodes();
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
			TransferableResource data = new TransferableResource(
					getDnDExtensionPointId(), resources);
			if (data != null) {
				dragSource.startDrag(dge, DragSource.DefaultMoveDrop, data,
						this);
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
				Data data = (TransferableResource.Data) transferable
						.getTransferData(TransferableResource.myNodeFlavor);

				if (isValidDragAndDrop(data.resources, dropNode)) {
					ExtensionPointManager<IResourceDnD> epm = new ExtensionPointManager<IResourceDnD>(
							getDnDExtensionPointId());
					ArrayList<ItemAttributes<IResourceDnD>> dndManagers = epm
							.getItemAttributes("/extension/dnd");
					boolean reject = true;
					for (ItemAttributes<IResourceDnD> dndManager : dndManagers) {
						String source = dndManager.getAttribute("source");
						// If the source and destination of the dnd is the same
						// extension point we accept those extensions explicitly
						// specifying the concrete source or those not
						// specifying source at all
						if (getDnDExtensionPointId().equals(
								data.sourceExtensionPoint)) {
							if ((source != null)
									&& !source
											.equals(data.sourceExtensionPoint)) {
								continue;
							}
						} else {
							// source and destination is different, we only
							// consider those specifying the concrete source
							if (!data.sourceExtensionPoint.equals(source)) {
								continue;
							}
						}
						IResourceDnD dndInstance = dndManager
								.getInstance("class");
						if (dndInstance.drop(treeModel, data.resources,
								dropNode)) {
							tree.scrollPathToVisible(new TreePath(
									data.resources[0].getPath()));
							reject = false;
							break;
						}
					}
					if (reject) {
						dtde.rejectDrop();
					}
				} else {
					dtde.rejectDrop();
				}

			} catch (UnsupportedFlavorException e) {
				dtde.rejectDrop();
			} catch (IOException e) {
				dtde.rejectDrop();
			}
		}
	}

	private boolean isValidDragAndDrop(IResource[] nodes, IResource dropNode) {
		for (IResource node : nodes) {
			if (contains(dropNode.getPath(), node)) {
				return false;
			} else if (node.getParent() == dropNode) {
				return false;
			} else {
				ArrayList<IResource> children = node.depthChildList();
				// We must check we wont put a parent in one of its children
				if (children.contains(dropNode)) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean contains(IResource[] path, IResource exNode) {
		for (IResource resource : path) {
			if (resource == exNode) {
				return true;
			}
		}

		return false;
	}

	protected abstract String getDnDExtensionPointId();

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public ResourceTreeModel getTreeModel() {
		return treeModel;
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
				} else {
					for (int i = 0; i < testPath.length; i++) {
						if (testPath[i] != objectPath[i]) {
							equals = false;
						}
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

	protected void setRootNode(IResource rootNode) {
		this.rootNode = rootNode;
		this.treeModel.setRootNode(rootNode);
	}

	protected void setTreeCellRenderer(TreeCellRenderer renderer) {
		tree.setCellRenderer(renderer);
	}

	protected void setTreeCellEditor(TreeCellEditor editor) {
		tree.setCellEditor(editor);
	}
}
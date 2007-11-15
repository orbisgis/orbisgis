package org.orbisgis.core.resourceTree;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
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

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public abstract class ResourceTree extends JPanel implements
		DropTargetListener, DragGestureListener, DragSourceListener {

	protected JTree tree = null;

	// Used to create a transfer when dragging
	protected DragSource dragSource = null;

	protected MyTreeUI myTreeUI;

	/** *** Catalog constructor **** */
	public ResourceTree() {
		super(new GridLayout(1, 0));

		tree = new JTree();
		myTreeUI = new MyTreeUI();
		tree.setUI(myTreeUI);
		/** *** Register listeners **** */
		tree.addMouseListener(new MyMouseAdapter());
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setShowsRootHandles(true);

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

	public void setModel(TreeModel treeModel) {
		tree.setModel(treeModel);
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

	public abstract void drop(DropTargetDropEvent dtde);

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	/**
	 * Retrieves myNode at the location point and select the node at this point
	 * Use it like this : currentNode = getMyNodeAtPoint(anypoint); so the
	 * selected node and currentNode remains coherent
	 *
	 * @param point
	 * @return
	 */
	protected Object getMyNodeAtPoint(Point point) {
		TreePath treePath = tree.getPathForLocation(point.x, point.y);
		Object myNode = null;
		if (treePath != null) {
			myNode = treePath.getLastPathComponent();
		}
		return myNode;
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
			if (e.getButton() == MouseEvent.BUTTON3) {
				TreePath path = tree.getPathForLocation(e.getX(), e.getY());
				TreePath[] selectionPaths = tree.getSelectionPaths();
				if ((selectionPaths != null) && (path != null)) {
					if (!contains(selectionPaths, path)) {
						tree.setSelectionPath(path);
					}
				} else {
					tree.setSelectionPath(path);
				}
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

	protected void setTreeCellRenderer(TreeCellRenderer renderer) {
		tree.setCellRenderer(renderer);
	}

	protected void setTreeCellEditor(TreeCellEditor editor) {
		tree.setCellEditor(editor);
	}

	protected TreePath[] getSelection() {
		TreePath[] selectionPaths = tree.getSelectionPaths();
		if (selectionPaths == null) {
			return new TreePath[0];
		} else {
			return selectionPaths;
		}
	}
}
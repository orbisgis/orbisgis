/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui.components.resourceTree;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
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

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public abstract class ResourceTree extends JPanel implements
		DropTargetListener, DragGestureListener, DragSourceListener {

	protected JTree tree = null;

	// Used to create a transfer when dragging
	protected DragSource dragSource = null;

	protected MyTreeUI myTreeUI;

	private TreePath currentDropTarget = null;
	private boolean dragDrawDirty = false;

	/** *** Catalog constructor **** */
	public ResourceTree() {
		super(new GridLayout(1, 0));

		tree = new JTree() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (dragDrawDirty) {
					dragDrawDirty = false;
					Rectangle bounds = null;
					if (currentDropTarget != null) {
						bounds = tree.getPathBounds(currentDropTarget);
					} else {
						bounds = tree.getBounds();
					}
					g.setColor(Color.black);
					g.drawRect(bounds.x, bounds.y, bounds.width - 1,
							bounds.height - 1);
				}
			}
		};

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
		Icon openIcon = OrbisGISIcon.TREE_PLUS;
		Icon closedIcon = OrbisGISIcon.TREE_MINUS;

		((javax.swing.plaf.basic.BasicTreeUI) tree.getUI())
				.setExpandedIcon(closedIcon);
		((javax.swing.plaf.basic.BasicTreeUI) tree.getUI())
				.setCollapsedIcon(openIcon);
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
		Point location = dtde.getLocation();
		TreePath nearestLayerPath = getNearestDroppablePath(location.x,
				location.y);
		currentDropTarget = nearestLayerPath;
		dragDrawDirty = true;
		this.repaint();
	}

	private TreePath getNearestDroppablePath(int refX, int refY) {
		int row = -1;
		for (int i = 0; i < tree.getRowCount(); i++) {
			TreePath path = tree.getPathForRow(i);
			if (isDroppable(path)) {
				Rectangle rowBounds = tree.getPathBounds(path);
				if (rowBounds.contains(refX, refY)) {
					row = i;
					break;
				}
			}
		}
		TreePath nearestPath = null;
		if (row != -1) {
			nearestPath = tree.getPathForRow(row);
		} else {
			nearestPath = null;
		}
		return nearestPath;
	}

	protected abstract boolean isDroppable(TreePath path);

	public void drop(DropTargetDropEvent dtde) {
		Transferable trans = dtde.getTransferable();
		Point location = dtde.getLocation();
		Object component = null;
		TreePath nearestLayerPath = getNearestDroppablePath(location.x,
				location.y);
		if (nearestLayerPath != null) {
			component = nearestLayerPath.getLastPathComponent();
		}
		if (!doDrop(trans, component)) {
			dtde.rejectDrop();
		}
		currentDropTarget = null;
		dragDrawDirty = false;
		this.repaint();
	}

	public void dragGestureRecognized(DragGestureEvent dge) {
		Transferable dragData = getDragData(dge);
		if (dragData != null) {
			dragSource.startDrag(dge, DragSource.DefaultMoveDrop, dragData,
					this);
		}
	}

	protected abstract Transferable getDragData(DragGestureEvent dge);

	protected abstract boolean doDrop(Transferable trans, Object node);

	public void dropActionChanged(DropTargetDragEvent dtde) {
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
						if (e.isControlDown()) {
							tree.addSelectionPath(path);
						} else {
							tree.setSelectionPath(path);
						}
					}
				} else {
					tree.setSelectionPath(path);
				}
			}
			if (e.isPopupTrigger()) {
				JPopupMenu popup = getPopup();
				if (popup != null) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
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

	public TreePath[] getSelection() {
		TreePath[] selectionPaths = tree.getSelectionPaths();
		if (selectionPaths == null) {
			return new TreePath[0];
		} else {
			return selectionPaths;
		}
	}

	public void setSelection(TreePath[] paths) {
		tree.setSelectionPaths(paths);
	}

	public JTree getTree() {
		return tree;
	}
}
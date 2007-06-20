package org.orbisgis.plugin.view.ui.workbench;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import org.orbisgis.plugin.view.layerModel.LayerCollectionEvent;
import org.orbisgis.plugin.view.layerModel.LayerCollectionListener;

public class TOC extends JTree {
	private LayerTreeCellRenderer ourTreeCellRenderer;

	private LayerTreeCellEditor ourTreeCellEditor;

	private LayerTreeModel model;

	private class MyMouseAdapter extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			e.getClickCount();

			TreePath treePath = TOC.this.getPathForLocation(x, y);
			int rowNodeLocation = TOC.this.getRowForLocation(x, y);
			Rectangle layerNodeLocation = TOC.this
					.getRowBounds(rowNodeLocation);

			if (null != treePath) {
				ILayer layer = (ILayer) treePath.getLastPathComponent();

				Rectangle checkBoxBounds = ourTreeCellRenderer
						.getCheckBoxBounds();
				checkBoxBounds.translate((int) layerNodeLocation.getX(),
						(int) layerNodeLocation.getY());

				if (checkBoxBounds.contains(e.getPoint())) {
					// mouse click inside checkbox
					layer.setVisible(!layer.isVisible());
				} else if (2 <= e.getClickCount()) {
					startEditingAtPath(treePath);
				}
			}
		}
	}

	public TOC(LayerCollection root) {
		model = new LayerTreeModel(root);
		setModel(model);
		// node's rendering
		ourTreeCellRenderer = new LayerTreeCellRenderer();
		setCellRenderer(ourTreeCellRenderer);
		// node's edition
		ourTreeCellEditor = new LayerTreeCellEditor(this);
		setCellEditor(ourTreeCellEditor);
		setInvokesStopCellEditing(true);
		setEditable(true);

		setRootVisible(false);
		setShowsRootHandles(true);
		addMouseListener(new MyMouseAdapter());
		model.setTree(this);
		root.addCollectionListener(new LayerCollectionListener() {

			public void layerRemoved(LayerCollectionEvent listener) {
				TOC.this.setModel(model);
			}

			public void layerMoved(LayerCollectionEvent listener) {
				TOC.this.setModel(model);
			}

			public void layerAdded(LayerCollectionEvent listener) {
				TOC.this.setModel(model);
			}

		});
	}
}
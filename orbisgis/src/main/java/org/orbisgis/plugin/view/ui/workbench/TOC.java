package org.orbisgis.plugin.view.ui.workbench;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.orbisgis.plugin.view.layerModel.BasicLayer;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import org.orbisgis.plugin.view.ui.style.UtilStyle;

public class TOC extends JTree {
	private LayerTreeCellRenderer ourTreeCellRenderer;

	private LayerTreeCellEditor ourTreeCellEditor;

	private class MyMouseAdapter extends MouseAdapter {
		private ILayer layer;

		public void mouseClicked(MouseEvent e) {
			final int x = e.getX();
			final int y = e.getY();
			final int mouseButton = e.getButton();

			TreePath treePath = TOC.this.getPathForLocation(x, y);
			int rowNodeLocation = TOC.this.getRowForLocation(x, y);
			Rectangle layerNodeLocation = TOC.this
					.getRowBounds(rowNodeLocation);

			if (null != treePath) {
				layer = (ILayer) treePath.getLastPathComponent();

				Rectangle checkBoxBounds = ourTreeCellRenderer
						.getCheckBoxBounds();
				checkBoxBounds.translate((int) layerNodeLocation.getX(),
						(int) layerNodeLocation.getY());
				System.out.println(e.getButton());
				if (checkBoxBounds.contains(e.getPoint())) {
					// mouse click inside checkbox
					layer.setVisible(!layer.isVisible());
				} else if ((MouseEvent.BUTTON1 == mouseButton)
						&& (2 <= e.getClickCount())) {
					startEditingAtPath(treePath);
				} else if (MouseEvent.BUTTON3 == mouseButton) {
					final OurFileChooser ofc = new OurFileChooser("sld",
							"SLD file (*.sld)", false);
					if (JFileChooser.APPROVE_OPTION == ofc
							.showOpenDialog(TOC.this)) {
						final File sldFile = ofc.getSelectedFile();
						ILayer myLayer = MyMouseAdapter.this.layer;
						System.out.printf("=== %s : %s\n", sldFile, myLayer
								.getName());
						if (myLayer instanceof BasicLayer) {
							try {
								((BasicLayer) myLayer).setStyle(UtilStyle
										.loadStyleFromXml(sldFile
												.getAbsolutePath()));
							} catch (Exception ee) {
								ee.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	public TOC(LayerCollection root) {
		LayerTreeModel model = new LayerTreeModel(root);
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
	}
}
package org.urbsat.plugin.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.xml.bind.JAXBException;

public class UrbSATFunctionsPanel extends JPanel {
	private JTree jTree;
	private DescriptionScrollPane descriptionScrollPane;

	public UrbSATFunctionsPanel(
			final DescriptionScrollPane descriptionScrollPane)
			throws JAXBException {
		this.descriptionScrollPane = descriptionScrollPane;

		jTree = new JTree(new UrbSATTreeModel(UrbSATTreeModel.class
				.getResource("urbsat.xml")));

		// define a TreeCellRenderer...
		// final DefaultTreeCellRenderer treeCellRenderer = new
		// DefaultTreeCellRenderer();
		final UrbSATTreeCellRenderer treeCellRenderer = new UrbSATTreeCellRenderer();
		// treeCellRenderer.setLeafIcon(new
		// ImageIcon(this.getClass().getResource(
		// "map.png")));
		// treeCellRenderer.setClosedIcon(new ImageIcon(this.getClass()
		// .getResource("folder.png")));
		// treeCellRenderer.setOpenIcon(new
		// ImageIcon(this.getClass().getResource(
		// "folder_magnify.png")));
		jTree.setCellRenderer(treeCellRenderer);

		expandAll();

		jTree.setRootVisible(false);
		jTree.setDragEnabled(true);
		jTree.addMouseListener(new UrbSATMouseAdapter());

		add(jTree);
	}

	private void expandAll() {
		for (int i = 0; i < jTree.getRowCount(); i++) {
			jTree.expandRow(i);
		}
	}

	private class UrbSATMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			mouseClicked(e);
		}

		public void mouseReleased(MouseEvent e) {
			mouseClicked(e);
		}

		public void mouseClicked(MouseEvent e) {
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree
					.getLastSelectedPathComponent();
			if (node == null) {
				return;
			} else {
				if (node.isLeaf()) {
					descriptionScrollPane.jTextArea.setText("oui");
					// descriptionScrollPane.jTextArea.setText(getQuery(node
					// .getUserObject().toString()));
				} else {
				}
			}
		}

		private void showPopup(MouseEvent e) {
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree
					.getLastSelectedPathComponent();
			if (node == null) {
				return;
			} else {
				if (node.isLeaf()) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						final TreePath path = jTree.getPathForLocation(
								e.getX(), e.getY());
						final TreePath[] selectionPaths = jTree
								.getSelectionPaths();
						if ((selectionPaths != null) && (path != null)) {
							if (!contains(selectionPaths, path)) {
								jTree.setSelectionPath(path);
							}
						} else {
							jTree.setSelectionPath(path);
						}
					}
					final TreePath tp = jTree.getSelectionPath();
					if (e.isPopupTrigger()) {
						// getPopup().show(e.getComponent(), e.getX(),
						// e.getY());
					}

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

}

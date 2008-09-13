package org.orbisgis.views.geocognition.sync.tree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.images.IconLoader;
import org.orbisgis.views.geocognition.sync.ComparePanel;
import org.orbisgis.views.geocognition.sync.SyncManager;
import org.orbisgis.views.geocognition.wizard.ElementRenderer;

public class CompareTreePanel extends JPanel {
	private static final Icon EXPAND_ALL = IconLoader.getIcon("plus.png");
	private static final Icon COLLAPSE_ALL = IconLoader.getIcon("minus.png");
	private static final Icon SYNCHRONIZE = IconLoader
			.getIcon("arrow_refresh.png");
	private static final Icon EXPORT = IconLoader
			.getIcon("blended_rightarrow.png");
	private static final Icon IMPORT = IconLoader
			.getIcon("blended_leftarrow.png");
	private static final Icon BOTH = IconLoader.getIcon("blended_both.png");

	private static final Dimension BUTTON_SIZE = new Dimension(30, 30);

	// Interface
	private JTree tree;
	private JScrollPane pane;
	private JPanel buttonPanel;
	private JToggleButton importModeButton, exportModeButton, bothModesButton;

	// Model
	private CompareTreeModel treeModel;

	private CompareTreeRenderer renderer;
	private ComparePanel comparePanel;

	private boolean installedAdvancedFeatures;
	private ImportExportButtonsListener toggleButtonsListener;

	/**
	 * Creates a new CompareTreePanel
	 */
	public CompareTreePanel(ComparePanel panel) {
		comparePanel = panel;
		renderer = new CompareTreeRenderer();

		installedAdvancedFeatures = false;
		toggleButtonsListener = new ImportExportButtonsListener();

		tree = new JTree();
		treeModel = new CompareTreeModel();
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		pane = new JScrollPane(tree);

		// toolbar
		buttonPanel = new JPanel();

		JButton synchronize = new JButton(SYNCHRONIZE);
		synchronize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					comparePanel.synchronize();
				} catch (IOException e1) {
					Services.getErrorManager().error(
							"The remote source cannot be readed", e1);
				} catch (PersistenceException e1) {
					Services.getErrorManager().error(
							"The remote source is not a "
									+ "valid geocognition xml source", e1);
				} catch (GeocognitionException e1) {
					Services.getErrorManager().error(
							"Cannot read the local geocognition", e1);
				}
			}
		});
		synchronize.setToolTipText("Synchronize");
		synchronize.setPreferredSize(BUTTON_SIZE);
		synchronize.setMinimumSize(BUTTON_SIZE);
		buttonPanel.add(synchronize);

		buttonPanel.add(new ToolbarSeparator());

		JButton expandAll = new JButton(EXPAND_ALL);
		expandAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < tree.getRowCount(); i++) {
					tree.expandRow(i);
				}
			}
		});
		expandAll.setToolTipText("Expand All");
		expandAll.setPreferredSize(BUTTON_SIZE);
		expandAll.setMinimumSize(BUTTON_SIZE);
		buttonPanel.add(expandAll);

		JButton collapseAll = new JButton(COLLAPSE_ALL);
		collapseAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = tree.getRowCount() - 1; i >= 1; i--) {
					tree.collapseRow(i);
				}
			}
		});
		collapseAll.setToolTipText("Collapse All");
		collapseAll.setPreferredSize(BUTTON_SIZE);
		collapseAll.setMinimumSize(BUTTON_SIZE);
		buttonPanel.add(collapseAll);

		setLayout(new BorderLayout());
		add(pane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.NORTH);
	}

	public boolean installAdvancedFeatures() {
		if (installedAdvancedFeatures) {
			return false;
		}

		buttonPanel.add(new ToolbarSeparator());

		importModeButton = new JToggleButton(IMPORT);
		importModeButton.addActionListener(toggleButtonsListener);
		importModeButton.setToolTipText("Import Mode");
		importModeButton.setPreferredSize(BUTTON_SIZE);
		importModeButton.setMinimumSize(BUTTON_SIZE);
		buttonPanel.add(importModeButton);

		exportModeButton = new JToggleButton(EXPORT);
		exportModeButton.addActionListener(toggleButtonsListener);
		exportModeButton.setToolTipText("Export Mode");
		exportModeButton.setPreferredSize(BUTTON_SIZE);
		exportModeButton.setMinimumSize(BUTTON_SIZE);
		buttonPanel.add(exportModeButton);

		bothModesButton = new JToggleButton(BOTH);
		bothModesButton.addActionListener(toggleButtonsListener);
		bothModesButton.setToolTipText("Import / Export Mode");
		bothModesButton.setPreferredSize(BUTTON_SIZE);
		bothModesButton.setMinimumSize(BUTTON_SIZE);
		bothModesButton.setSelected(true);
		buttonPanel.add(bothModesButton);

		installedAdvancedFeatures = true;
		return true;
	}

	/**
	 * Sets the model of the tree panel
	 * 
	 * @param sync
	 *            the comparer between two trees
	 */
	public void setModel(SyncManager sync, int syncType) {
		treeModel.setModel(sync, tree);
		tree.setModel(treeModel);
		renderer.setSyncManager(sync, syncType);
		tree.setCellRenderer(renderer);
	}

	/**
	 * Gets the tree of the panel
	 * 
	 * @return the JTree contained in this panel
	 */
	public JTree getTree() {
		return tree;
	}

	@Override
	public synchronized void addMouseListener(MouseListener l) {
		if (tree == null) {
			super.addMouseListener(l);
		} else {
			tree.addMouseListener(l);
		}
	}

	public void setIconRenderers(ElementRenderer[] renderers) {
		renderer.setIconRenderers(renderers);
	}

	private class ImportExportButtonsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JToggleButton clicked = (JToggleButton) e.getSource();
			try {
				if (clicked.isSelected()) {
					if (clicked == importModeButton) {
						if (comparePanel.synchronize(ComparePanel.IMPORT)) {
							exportModeButton.setSelected(false);
							bothModesButton.setSelected(false);
						} else {
							importModeButton.setSelected(false);
						}
					} else if (clicked == exportModeButton) {
						if (comparePanel.synchronize(ComparePanel.EXPORT)) {
							importModeButton.setSelected(false);
							bothModesButton.setSelected(false);
						} else {
							exportModeButton.setSelected(false);
						}
					} else if (clicked == bothModesButton) {
						if (comparePanel
								.synchronize(ComparePanel.SYNCHRONIZATION)) {
							importModeButton.setSelected(false);
							exportModeButton.setSelected(false);
						} else {
							bothModesButton.setSelected(false);
						}
					}
				} else {
					clicked.setSelected(true);
				}
			} catch (IOException e1) {
				Services.getErrorManager().error(
						"The remote source cannot be readed", e1);
			} catch (PersistenceException e1) {
				Services.getErrorManager().error(
						"The remote source is not a "
								+ "valid geocognition xml source", e1);
			} catch (GeocognitionException e1) {
				Services.getErrorManager().error(
						"Cannot read the local geocognition", e1);
			}
		}
	}

	private class ToolbarSeparator extends JSeparator {
		private ToolbarSeparator() {
			super();
			Dimension d = new Dimension(11, 31);
			setMaximumSize(d);
			setMinimumSize(d);
			setPreferredSize(d);
			setSize(d);
		}

		@Override
		public void paint(Graphics g) {
			int x = getWidth() / 2;
			g.drawLine(x, 0, x, getHeight());
		}
	}
}

package org.orbisgis.views.geocognition.sync.tree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.images.IconLoader;
import org.orbisgis.views.geocognition.sync.IdPath;
import org.orbisgis.views.geocognition.sync.SyncListener;
import org.orbisgis.views.geocognition.sync.SyncManager;
import org.orbisgis.views.geocognition.sync.SyncPanel;
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

	// Interface
	private JTree tree;
	private JPanel toolbarPanel;
	private JToggleButton importModeButton, exportModeButton, bothModesButton;

	// Model
	private CompareTreeModel model;
	private CompareTreeRenderer renderer;
	private SyncPanel syncPanel;
	private SyncManager syncManager;
	private SyncListener syncListener;

	// Flag to determine if the advanced features are already installed
	private boolean installedAdvancedFeatures;

	/**
	 * Creates a new CompareTreePanel
	 */
	public CompareTreePanel(SyncPanel panel) {
		syncPanel = panel;
		installedAdvancedFeatures = false;
		renderer = new CompareTreeRenderer();
		syncListener = new SyncListener() {
			@Override
			public void syncDone() {
				refresh();
			}
		};

		tree = new JTree();
		model = new CompareTreeModel();
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		JScrollPane treeScrollPane = new JScrollPane(tree);

		// toolbar
		toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JButton synchronize = new JButton(SYNCHRONIZE);
		synchronize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					syncPanel.synchronize();
				} catch (IOException e1) {
					Services.getErrorManager().error(
							"The remote source cannot be readed", e1);
				} catch (PersistenceException e1) {
					Services.getErrorManager().error(
							"The remote source is not a "
									+ "valid geocognition xml source", e1);
				} catch (GeocognitionException e1) {
					Services.getErrorManager().error(
							"The geocognition cannot be readed", e1);
				}
			}
		});
		synchronize.setToolTipText("Synchronize");
		synchronize.setMargin(new Insets(0, 0, 0, 0));
		toolbarPanel.add(synchronize);

		toolbarPanel.add(new ToolbarSeparator());

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
		expandAll.setMargin(new Insets(0, 0, 0, 0));
		toolbarPanel.add(expandAll);

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
		collapseAll.setMargin(new Insets(0, 0, 0, 0));
		toolbarPanel.add(collapseAll);

		setLayout(new BorderLayout());
		add(treeScrollPane, BorderLayout.CENTER);
		add(toolbarPanel, BorderLayout.NORTH);
	}

	/**
	 * Adds to the toolbar the import, export and import/export mode buttons
	 */
	public void installAdvancedFeatures() {
		if (installedAdvancedFeatures) {
			return;
		}

		toolbarPanel.add(new ToolbarSeparator());

		ImportExportButtonsListener toggleButtonsListener = new ImportExportButtonsListener();

		importModeButton = new JToggleButton(IMPORT);
		importModeButton.addActionListener(toggleButtonsListener);
		importModeButton.setToolTipText("Import Mode");
		importModeButton.setMargin(new Insets(0, 0, 0, 0));
		toolbarPanel.add(importModeButton);

		exportModeButton = new JToggleButton(EXPORT);
		exportModeButton.addActionListener(toggleButtonsListener);
		exportModeButton.setToolTipText("Export Mode");
		exportModeButton.setMargin(new Insets(0, 0, 0, 0));
		toolbarPanel.add(exportModeButton);

		bothModesButton = new JToggleButton(BOTH);
		bothModesButton.addActionListener(toggleButtonsListener);
		bothModesButton.setToolTipText("Import / Export Mode");
		bothModesButton.setMargin(new Insets(0, 0, 0, 0));
		bothModesButton.setSelected(true);
		toolbarPanel.add(bothModesButton);

		installedAdvancedFeatures = true;
	}

	/**
	 * Sets the model of the tree panel
	 * 
	 * @param sync
	 *            the comparer between two trees
	 */
	public void setModel(SyncManager sync, int syncType) {
		if (sync == null) {
			Services.getErrorManager().error(
					"bug!",
					new IllegalArgumentException(
							"The synchronization manager cannot be null"));
		}

		// Update sync manager
		if (syncManager != null) {
			syncManager.removeSyncListener(syncListener);
		}
		syncManager = sync;
		syncManager.addSyncListener(syncListener);

		// Update tree model
		model.setSyncManager(sync);
		tree.setModel(model);

		// Update tree renderer
		renderer.setModel(sync, syncType);
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

	/**
	 * Sets the icon renderers
	 * 
	 * @param renderers
	 *            the renderers to set
	 */
	public void setIconRenderers(ElementRenderer[] renderers) {
		renderer.setIconRenderers(renderers);
	}

	/**
	 * Listener for the advanced import, export and import/export mode buttons
	 * 
	 * @author victorzinho
	 * 
	 */
	private class ImportExportButtonsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JToggleButton clicked = (JToggleButton) e.getSource();
			if (clicked.isSelected()) {
				if (clicked == importModeButton) {
					syncPanel.refresh(SyncPanel.IMPORT);
					exportModeButton.setSelected(false);
					bothModesButton.setSelected(false);
				} else if (clicked == exportModeButton) {
					syncPanel.refresh(SyncPanel.EXPORT);
					importModeButton.setSelected(false);
					bothModesButton.setSelected(false);
				} else if (clicked == bothModesButton) {
					syncPanel.refresh(SyncPanel.SYNCHRONIZATION);
					importModeButton.setSelected(false);
					exportModeButton.setSelected(false);
				}
			} else {
				clicked.setSelected(true);
			}
		}
	}

	/**
	 * Separator for the toolbar of the tree
	 * 
	 * @author victorzinho
	 * 
	 */
	private class ToolbarSeparator extends JSeparator {
		private static final int VERT_MARGIN = 3;

		private ToolbarSeparator() {
			super();
			Dimension size = new Dimension(11, 31);
			setMinimumSize(size);
			setPreferredSize(size);
		}

		@Override
		public void paint(Graphics g) {
			int x = getWidth() / 2;
			g.drawLine(x, VERT_MARGIN, x, getHeight() - VERT_MARGIN);
		}
	}

	/**
	 * Refreshes the tree
	 */
	private void refresh() {
		// Preserve tree expansions
		Enumeration<TreePath> expanded = null;
		expanded = tree.getExpandedDescendants(tree.getPathForRow(0));

		model.fireTreeStructureChanged();

		// Recover tree expansion
		if (expanded != null && syncManager.getDifferenceTree() != null) {
			while (expanded.hasMoreElements()) {
				TreePath path = expanded.nextElement();
				IdPath idPath = new IdPath();
				ArrayList<TreeElement> treePath = new ArrayList<TreeElement>();
				for (int i = 0; i < path.getPathCount(); i++) {
					TreeElement element = (TreeElement) path.getPath()[i];
					idPath.addLast(element.getId());
					treePath.add(syncManager.getDifferenceTree().find(idPath));
				}

				tree.expandPath(new TreePath(treePath.toArray()));
			}
		}
	}
}

package org.orbisgis.views.geocognition.sync.tree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.images.IconLoader;
import org.orbisgis.ui.resourceTree.ReadOnlyCellEditor;
import org.orbisgis.ui.resourceTree.ResourceTree;
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
	private TreePanel treePanel;
	private JPanel toolbarPanel;
	private JToggleButton importModeButton, exportModeButton, bothModesButton;

	// Model
	private CompareTreeModel model;
	private CompareTreeRenderer renderer;
	private SyncPanel syncPanel;
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
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						refresh();
					}
				});
			}
		};

		treePanel = new TreePanel();
		treePanel.getTree().setRootVisible(true);
		model = new CompareTreeModel();

		// toolbar
		toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JButton synchronize = new JButton(SYNCHRONIZE);
		synchronize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int option = JOptionPane.showConfirmDialog(null,
							"All changes will be lost. Are you sure?",
							"Synchronize", JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.YES_OPTION) {
						syncPanel.synchronize();
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
				for (int i = 0; i < treePanel.getTree().getRowCount(); i++) {
					treePanel.getTree().expandRow(i);
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
				for (int i = treePanel.getTree().getRowCount() - 1; i >= 1; i--) {
					treePanel.getTree().collapseRow(i);
				}
			}
		});
		collapseAll.setToolTipText("Collapse All");
		collapseAll.setMargin(new Insets(0, 0, 0, 0));
		toolbarPanel.add(collapseAll);

		setLayout(new BorderLayout());
		add(treePanel, BorderLayout.CENTER);
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
		SyncManager syncManager = syncPanel.getSyncManager();

		if (syncManager != null) {
			syncManager.removeSyncListener(syncListener);
		}
		syncManager = sync;
		syncManager.addSyncListener(syncListener);

		// Update tree model
		model.setSyncManager(sync);
		treePanel.getTree().setModel(model);

		// Update tree renderer
		renderer.setModel(sync, syncType);
		treePanel.getTree().setCellRenderer(renderer);
	}

	/**
	 * Gets the tree of the panel
	 * 
	 * @return the JTree contained in this panel
	 */
	public JTree getTree() {
		return treePanel.getTree();
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
		JTree tree = treePanel.getTree();

		// Set root as visible if necessary (to show the 'no changes' message in
		// the tree)
		if (syncPanel.getSyncManager().getDifferenceTree() == null) {
			tree.setRootVisible(true);
		} else {
			tree.setRootVisible(false);
		}

		// Preserve tree expansions
		Enumeration<TreePath> expanded = null;
		expanded = tree.getExpandedDescendants(new TreePath(tree.getModel()
				.getRoot()));

		model.fireTreeStructureChanged();
		SyncManager syncManager = syncPanel.getSyncManager();

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

				treePanel.getTree()
						.expandPath(new TreePath(treePath.toArray()));
			}
		}
	}

	private class TreePanel extends ResourceTree {
		// String constants for popup menu
		private static final String ADD_TO_GEOCOGNITION = "Add to geocognition";
		private static final String ADD_TO_FILE = "Add to file";
		private static final String REMOVE_FROM_GEOCOGNITION = "Remove from geocognition";
		private static final String REMOVE_FROM_FILE = "Remove from file";
		private static final String OVERRIDE_IN_GEOCOGNITION = "Override in geocognition";
		private static final String OVERRIDE_IN_FILE = "Override in file";
		private static final String CHANGE_GEOCOGNITION = "Change geocognition content";
		private static final String CHANGE_FILE = "Change file content";
		private static final String NO_ACTION = "No action available";

		private JPopupMenu popup;
		private JMenuItem addToGeocognition, addToFile, removeFromGeocognition,
				removeFromFile, overrideInGeocognition, overrideInFile,
				changeGeocognition, changeFile, noAction;

		private TreePanel() {
			// Popup menu
			popup = new JPopupMenu();

			addToGeocognition = new JMenuItem(ADD_TO_GEOCOGNITION);
			addToGeocognition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					syncPanel.execute(SyncPanel.UPDATE);
				}
			});

			addToFile = new JMenuItem(ADD_TO_FILE);
			addToFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					syncPanel.execute(SyncPanel.COMMIT);
				}
			});

			removeFromGeocognition = new JMenuItem(REMOVE_FROM_GEOCOGNITION);
			removeFromGeocognition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					syncPanel.execute(SyncPanel.UPDATE);
				}
			});

			removeFromFile = new JMenuItem(REMOVE_FROM_FILE);
			removeFromFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					syncPanel.execute(SyncPanel.COMMIT);
				}
			});

			overrideInGeocognition = new JMenuItem(OVERRIDE_IN_GEOCOGNITION);
			overrideInGeocognition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					syncPanel.execute(SyncPanel.UPDATE);
				}
			});

			overrideInFile = new JMenuItem(OVERRIDE_IN_FILE);
			overrideInFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					syncPanel.execute(SyncPanel.COMMIT);
				}
			});

			changeGeocognition = new JMenuItem(CHANGE_GEOCOGNITION);
			changeGeocognition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					syncPanel.execute(SyncPanel.UPDATE);
				}
			});

			changeFile = new JMenuItem(CHANGE_FILE);
			changeFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					syncPanel.execute(SyncPanel.COMMIT);
				}
			});

			noAction = new JMenuItem(NO_ACTION);
			noAction.setEnabled(false);

			popup.add(addToGeocognition);
			popup.add(addToFile);
			popup.add(removeFromGeocognition);
			popup.add(removeFromFile);
			popup.add(overrideInGeocognition);
			popup.add(overrideInFile);
			popup.add(changeGeocognition);
			popup.add(changeFile);
			popup.add(noAction);

			getTree().setCellEditor(new ReadOnlyCellEditor());
		}

		@Override
		protected boolean doDrop(Transferable trans, Object node) {
			return false;
		}

		@Override
		protected Transferable getDragData(DragGestureEvent dge) {
			return null;
		}

		@Override
		public JPopupMenu getPopup() {
			TreePath[] paths = tree.getSelectionPaths();

			if (paths == null
					|| paths[0].getLastPathComponent() instanceof String) {
				return null;
			}

			ArrayList<IdPath> idPaths = new ArrayList<IdPath>();
			for (int i = 0; i < paths.length; i++) {
				TreeElement element = (TreeElement) paths[i]
						.getLastPathComponent();
				idPaths = merge(idPaths, getChangedElements(element));
			}

			SyncManager syncManager = syncPanel.getSyncManager();

			// Determines if all changed elements in the IdPath(s) list are
			// added, deleted, modified or conflict
			boolean deleted = true;
			boolean added = true;
			boolean modified = true;
			boolean conflict = true;

			for (IdPath idPath : idPaths) {
				if (deleted && !syncManager.isDeleted(idPath)) {
					deleted = false;
				}

				if (added && !syncManager.isAdded(idPath)) {
					added = false;
				}

				if (modified && !syncManager.isModified(idPath)) {
					modified = false;
				}

				if (conflict && !syncManager.isConflict(idPath)) {
					conflict = false;
				}
			}

			// Show popup menu
			boolean noActionVisible = true;
			if (syncPanel.isRemoteEditable()) {
				noActionVisible &= (!added && !deleted && !modified && !conflict);
				addToFile.setVisible(added);
				removeFromFile.setVisible(deleted);
				changeFile.setVisible(modified);
				overrideInFile.setVisible(conflict);
			} else {
				addToFile.setVisible(false);
				removeFromFile.setVisible(false);
				overrideInFile.setVisible(false);
				changeFile.setVisible(false);
			}

			if (syncPanel.isLocalEditable()) {
				noActionVisible &= (!added && !deleted && !modified && !conflict);
				addToGeocognition.setVisible(deleted);
				removeFromGeocognition.setVisible(added);
				changeGeocognition.setVisible(modified);
				overrideInGeocognition.setVisible(conflict);
			} else {
				addToGeocognition.setVisible(false);
				removeFromGeocognition.setVisible(false);
				changeGeocognition.setVisible(false);
				overrideInGeocognition.setVisible(false);
			}

			noAction.setVisible(noActionVisible);

			return popup;
		}

		/**
		 * Returns a list if id paths containing the children with changes
		 * (added, deleted modified or conflict). The element id path is also
		 * returned if it has changes
		 * 
		 * @param element
		 *            the element to check
		 * @return a list of id paths with changes
		 */
		private ArrayList<IdPath> getChangedElements(TreeElement element) {
			ArrayList<IdPath> arrayList = new ArrayList<IdPath>();
			if (syncPanel.getSyncManager().hasChanged(element.getIdPath())) {
				arrayList.add(element.getIdPath());
			} else {
				for (int i = 0; i < element.getElementCount(); i++) {
					TreeElement child = element.getElement(i);
					arrayList = merge(arrayList, getChangedElements(child));
				}
			}

			return arrayList;
		}

		/**
		 * Adds all the elements of the adding list to the destination list
		 * 
		 * @param <T>
		 *            The type of the list
		 * @param dest
		 *            the list where the elements are added
		 * @param adding
		 *            the list with the element to add
		 * @return the merged list
		 */
		@SuppressWarnings("unchecked")
		private <T> ArrayList<T> merge(ArrayList<T> dest, ArrayList<T> adding) {
			Object[] aux = adding.toArray();
			for (int i = 0; i < aux.length; i++) {
				dest.add((T) aux[i]);
			}

			return dest;
		}
	}
}

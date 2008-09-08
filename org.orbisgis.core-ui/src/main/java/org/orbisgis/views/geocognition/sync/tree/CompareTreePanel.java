package org.orbisgis.views.geocognition.sync.tree;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
	// TODO find icons
	private static final Icon EXPAND_ALL = IconLoader.getIcon("add.png");
	private static final Icon COLLAPSE_ALL = IconLoader.getIcon("add.png");
	private static final Icon SYNCHRONIZE = IconLoader.getIcon("add.png");

	// Interface
	private JTree tree;
	private JScrollPane pane;

	// Model
	private CompareTreeModel treeModel;

	private CompareTreeRenderer renderer;
	private ComparePanel comparePanel;

	/**
	 * Creates a new CompareTreePanel
	 */
	public CompareTreePanel(ComparePanel panel) {
		comparePanel = panel;
		renderer = new CompareTreeRenderer();

		tree = new JTree();
		treeModel = new CompareTreeModel();
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		pane = new JScrollPane(tree);

		// toolbar
		JPanel north = new JPanel();
		JButton expandAll = new JButton(EXPAND_ALL);
		expandAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < tree.getRowCount(); i++) {
					tree.expandRow(i);
				}
			}
		});
		expandAll.setToolTipText("Expand");
		north.add(expandAll);

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
		north.add(synchronize);

		JButton collapseAll = new JButton(COLLAPSE_ALL);
		collapseAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = tree.getRowCount() - 1; i >= 1; i--) {
					tree.collapseRow(i);
				}
			}
		});
		collapseAll.setToolTipText("Collapse");
		north.add(collapseAll);

		setLayout(new BorderLayout());
		add(pane, BorderLayout.CENTER);
		add(north, BorderLayout.NORTH);
	}

	/**
	 * Sets the model of the tree panel
	 * 
	 * @param sync
	 *            the comparer between two trees
	 */
	public void setModel(SyncManager sync) {
		treeModel.setModel(sync, tree);
		tree.setModel(treeModel);
		renderer.setSyncManager(sync);
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
}

package org.orbisgis.configuration;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.orbisgis.Services;
import org.orbisgis.action.IMenu;

public class ConfigurationTree extends JPanel {
	private JTree tree;

	/**
	 * Creates a new configuration tree
	 * 
	 * @param root
	 *            the root menu of the configuration tree
	 * @param configs
	 *            the installed configurations
	 */
	public ConfigurationTree(IMenu root, List<ConfigurationDecorator> configs) {
		super();

		ConfigurationTreeModel model = new ConfigurationTreeModel(root, configs);
		ConfigurationTreeRenderer renderer = new ConfigurationTreeRenderer();

		tree = new JTree();
		tree.setModel(model);
		tree.setCellRenderer(renderer);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		setLayout(new BorderLayout());
		add(tree, BorderLayout.CENTER);
	}

	/**
	 * Adds a new selection listener
	 * 
	 * @param l
	 *            the listener to add
	 */
	void addSelectionListener(TreeSelectionListener l) {
		tree.addTreeSelectionListener(l);
	}

	/**
	 * Gets the selected element
	 * 
	 * @return the selected element
	 */
	Object getSelectedElement() {
		return tree.getSelectionPath().getLastPathComponent();
	}

	/**
	 * The model of the configuration tree
	 * 
	 * @author victorzinho
	 */
	private class ConfigurationTreeModel implements TreeModel {
		private List<TreeModelListener> listeners;
		private IMenu root;
		private List<ConfigurationDecorator> configs;

		/**
		 * Creates a new configuration tree model
		 * 
		 * @param root
		 *            the root menu of the configuration tree
		 * @param configs
		 *            the installed configurations
		 */
		public ConfigurationTreeModel(IMenu root,
				List<ConfigurationDecorator> configs) {
			this.root = root;
			this.configs = configs;
			listeners = new ArrayList<TreeModelListener>();
		}

		@Override
		public void addTreeModelListener(TreeModelListener l) {
			listeners.add(l);
		}

		@Override
		public Object getChild(Object parent, int index) {
			if (parent instanceof IMenu) {
				IMenu menuParent = (IMenu) parent;
				IMenu[] children = menuParent.getChildren();
				if (index < children.length) {
					return children[index];
				} else {
					int rest = index - children.length;
					for (ConfigurationDecorator config : configs) {
						if (config.getParentId().equals(menuParent.getId())) {
							if (rest == 0) {
								return config;
							} else {
								rest--;
							}
						}
					}

					return null;
				}
			} else {
				Services.getErrorManager().error("bug!",
						new RuntimeException("Only menus can have children"));
				return null;
			}
		}

		@Override
		public int getChildCount(Object parent) {
			if (parent instanceof IMenu) {
				IMenu menuParent = (IMenu) parent;
				int childCount = menuParent.getChildren().length;
				for (ConfigurationDecorator config : configs) {
					if (config.getParentId().equals(menuParent.getId())) {
						childCount++;
					}
				}

				return childCount;
			} else {
				Services.getErrorManager().error("bug!",
						new RuntimeException("Only menus can have children"));
				return 0;
			}
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			if (parent instanceof IMenu) {
				IMenu menuParent = (IMenu) parent;
				IMenu[] children = menuParent.getChildren();
				for (int i = 0; i < children.length; i++) {
					if (children[i] == child) {
						return i;
					}
				}

				int index = children.length;

				for (ConfigurationDecorator config : configs) {
					if (config.getParentId().equals(menuParent.getId())) {
						if (config == child) {
							return index;
						} else {
							index++;
						}
					}
				}

				return -1;
			} else {
				Services.getErrorManager().error("bug!",
						new RuntimeException("Only menus can have children"));
				return -1;
			}
		}

		@Override
		public Object getRoot() {
			return root;
		}

		@Override
		public boolean isLeaf(Object node) {
			return (node instanceof ConfigurationDecorator);
		}

		@Override
		public void removeTreeModelListener(TreeModelListener l) {
			listeners.remove(l);
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
			// do nothing
		}
	}

	/**
	 * The renderer of the configuration tree
	 * 
	 * @author victorzinho
	 */
	private class ConfigurationTreeRenderer extends DefaultTreeCellRenderer {
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			if (value instanceof IMenu) {
				IMenu menu = (IMenu) value;
				return super.getTreeCellRendererComponent(tree, menu.getText(),
						selected, expanded, leaf, row, hasFocus);
			} else if (value instanceof ConfigurationDecorator) {
				ConfigurationDecorator config = (ConfigurationDecorator) value;
				return super.getTreeCellRendererComponent(tree, config
						.getText(), selected, expanded, leaf, row, hasFocus);
			} else {
				return null;
			}
		}
	}
}

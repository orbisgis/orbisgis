package org.orbisgis.core.ui.components.sif;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.orbisgis.sif.SQLUIPanel;
import org.orbisgis.sif.UIFactory;

public class CategorizedChoosePanel extends JPanel implements SQLUIPanel {

	private String id;
	private String title;
	private HashMap<Option, ArrayList<Option>> categories = new HashMap<Option, ArrayList<Option>>();
	private JTree tree;
	private CategoriesTreeModel categoriesTreeModel;

	public CategorizedChoosePanel(String title, String id) {
		this.title = title;
		this.id = id;

		initComponents();
	}

	public void addOption(String categoryId, String categoryName, String name,
			String id, String icon) {
		Option category = new Option(categoryId, categoryName, true, null);
		ArrayList<Option> options = categories.get(category);
		if (options == null) {
			options = new ArrayList<Option>();
		}
		options.add(new Option(id, name, false, icon));

		categories.put(category, options);

		categoriesTreeModel.refresh();
	}

	private void initComponents() {
		tree = new JTree();
		categoriesTreeModel = new CategoriesTreeModel();
		tree.setModel(categoriesTreeModel);
		tree.setRootVisible(false);
		tree.setCellRenderer(new IconRenderer());
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(tree), BorderLayout.CENTER);
	}

	@Override
	public String[] getErrorMessages() {
		return null;
	}

	@Override
	public String[] getFieldNames() {
		return new String[] { "selection" };
	}

	@Override
	public int[] getFieldTypes() {
		return new int[] { STRING };
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String[] getValidationExpressions() {
		return null;
	}

	@Override
	public String[] getValues() {
		return new String[] { getSelectedElement() };
	}

	@Override
	public void setValue(String fieldName, String fieldValue) {
		Iterator<Option> it = categories.keySet().iterator();
		while (it.hasNext()) {
			Option category = it.next();
			ArrayList<Option> options = categories.get(category);
			for (Option option : options) {
				if (option.getId().equals(fieldValue)) {
					tree.setSelectionPath(new TreePath(new Object[] {
							categoriesTreeModel.getRoot(), category, option }));
					return;
				}
			}
		}
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String validateInput() {
		TreePath selectionPath = tree.getSelectionPath();
		if ((selectionPath == null)
				|| (((Option) selectionPath.getLastPathComponent())
						.isCategory())) {
			return "An item must be selected";
		}

		return null;
	}

	@Override
	public boolean showFavorites() {
		return false;
	}

	@Override
	public URL getIconURL() {
		return UIFactory.getDefaultIcon();
	}

	public String getInfoText() {
		return UIFactory.getDefaultOkMessage();
	}

	@Override
	public String initialize() {
		return null;
	}

	@Override
	public String postProcess() {
		return null;
	}

	/**
	 * Returns the id of the currently selected option if it's valid. If there
	 * is no selection or the selection is not valid it returns null
	 * 
	 * @return
	 */
	public String getSelectedElement() {
		if (validateInput() == null) {
			Object selection = tree.getSelectionPath().getLastPathComponent();
			return ((Option) selection).getId();
		} else {
			return null;
		}
	}

	private class Option {
		private String id;
		private String name;
		private boolean category;
		private String icon;

		public Option(String id, String name, boolean category, String icon) {
			super();
			this.id = id;
			this.name = name;
			this.category = category;
			this.icon = icon;
		}

		public String getIcon() {
			return icon;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Option) {
				Option opt = (Option) obj;
				return id.equals(opt.id);
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return id.hashCode();
		}

		public boolean isCategory() {
			return category;
		}

	}

	private class CategoriesTreeModel implements TreeModel {

		private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

		@Override
		public void addTreeModelListener(TreeModelListener l) {
			listeners.add(l);
		}

		public void refresh() {
			for (TreeModelListener listener : listeners) {
				listener.treeStructureChanged(new TreeModelEvent(this,
						new Object[] { getRoot() }));
			}
		}

		@Override
		public Object getChild(Object parent, int index) {
			ArrayList<Object> names = getArray(parent);
			return names.get(index);
		}

		private ArrayList<Object> getArray(Object parent) {
			ArrayList<Object> names;
			names = new ArrayList<Object>();
			if (parent.toString().equals("ROOT")) {
				// Categories
				names.addAll(categories.keySet());
			} else {
				// Category content
				ArrayList<Option> options = categories.get(parent);
				if (options != null) {
					names.addAll(options);
				}
			}
			return names;
		}

		@Override
		public int getChildCount(Object parent) {
			ArrayList<Object> names = getArray(parent);
			return names.size();
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			ArrayList<Object> names = getArray(parent);
			return names.indexOf(child);
		}

		@Override
		public Object getRoot() {
			return new Option("ROOT", "ROOT", true, null);
		}

		@Override
		public boolean isLeaf(Object node) {
			return !((Option) node).isCategory();
		}

		@Override
		public void removeTreeModelListener(TreeModelListener l) {
			listeners.remove(l);
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {

		}

	}

	private class IconRenderer extends DefaultTreeCellRenderer implements
			TreeCellRenderer {
		private Icon defaultClosedFolderIcon;
		private Icon defaultOpenFolderIcon;
		private Icon defaultLeafIcon;

		public IconRenderer() {
			this.defaultClosedFolderIcon = this.getDefaultClosedIcon();
			this.defaultOpenFolderIcon = this.getDefaultOpenIcon();
			this.defaultLeafIcon = this.getLeafIcon();
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			Option option = (Option) value;
			if (option.getIcon() != null) {
				ImageIcon icon = new ImageIcon(this.getClass().getResource(
						option.getIcon()));
				this.setLeafIcon(icon);
			} else {
				if (option.isCategory()) {
					this.setOpenIcon(defaultOpenFolderIcon);
					this.setClosedIcon(defaultClosedFolderIcon);
				} else {
					this.setLeafIcon(defaultLeafIcon);
				}
			}
			return super.getTreeCellRendererComponent(tree, value, sel,
					expanded, leaf, row, hasFocus);
		}
	}
}

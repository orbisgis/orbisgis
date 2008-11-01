package org.orbisgis.editors.action;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.orbisgis.Services;
import org.orbisgis.edition.EditableElement;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editors.sql.JavaEditor;
import org.orbisgis.geocognition.actions.ActionCode;
import org.orbisgis.geocognition.actions.ActionPropertyChangeListener;
import org.orbisgis.geocognition.actions.GeocognitionActionElementFactory;
import org.orbisgis.ui.resourceTree.AbstractTreeModel;
import org.orbisgis.windows.mainFrame.UIManager;
import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;

public class ActionEditor extends JavaEditor implements IEditor {

	private JTabbedPane panel;
	private JTree menuTree;
	private JList lstGroups;
	private JTextField txtText;
	private boolean refreshing = false;
	private PropertyModifiacationListener changeListener = new PropertyModifiacationListener();

	public Component getComponent() {
		panel = new JTabbedPane(JTabbedPane.BOTTOM);
		panel.addTab("Java", super.getComponent());
		panel.addTab("Config", getConfPanel());
		refreshUI();
		return panel;
	}

	private Component getConfPanel() {
		JPanel confPanel = new JPanel();
		confPanel.add(getMenuPanel());
		confPanel.add(getGroupAndTextPanel());
		return confPanel;
	}

	private Component getGroupAndTextPanel() {
		UIManager ui = Services.getService(UIManager.class);
		lstGroups = new JList(ui.getInstalledMenuGroups());
		lstGroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstGroups.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						refreshModel();
					}
				});
		JPanel groupPanel = new JPanel();
		groupPanel.setBorder(BorderFactory
				.createTitledBorder("Select action group"));
		groupPanel.add(new JScrollPane(lstGroups));
		JPanel textPanel = new JPanel();
		textPanel.setBorder(BorderFactory.createTitledBorder("Menu text:"));
		txtText = new JTextField(20);
		txtText.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				insertUpdate(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				refreshModel();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				insertUpdate(e);
			}
		});
		textPanel.add(txtText);
		JPanel ret = new JPanel();
		ret.setLayout(new CRFlowLayout());
		ret.add(groupPanel);
		ret.add(new CarriageReturn());
		ret.add(textPanel);
		return ret;
	}

	private synchronized void refreshModel() {
		if (!refreshing) {
			refreshing = true;
			TreePath selectionPath = menuTree.getSelectionPath();
			String menuId = null;
			if (selectionPath != null) {
				menuId = selectionPath.getLastPathComponent().toString();
			}

			Object selectedValue = lstGroups.getSelectedValue();
			String groupId = null;
			if (selectedValue != null) {
				groupId = selectedValue.toString();
			}

			String text = txtText.getText();
			if (text.trim().length() == 0) {
				text = null;
			}

			ActionCode ac = (ActionCode) getElement().getObject();
			ac.setGroup(groupId);
			ac.setMenuId(menuId);
			ac.setText(text);
			refreshing = false;
		}
	}

	@Override
	public void setElement(EditableElement element) {
		super.setElement(element);
		ActionCode code = (ActionCode) element.getObject();
		code.addActionPropertyListener(changeListener);
	}

	@Override
	public void delete() {
		super.delete();
		ActionCode code = (ActionCode) getElement().getObject();
		code.removeActionPropertyListener(changeListener);
	}

	private synchronized void refreshUI() {
		if (!refreshing) {
			refreshing = true;
			ActionCode code = (ActionCode) getElement().getObject();

			// Tree selection
			TreePath selection = null;
			if (code.getMenuId() != null) {
				ArrayList<String> rootPath = new ArrayList<String>();
				rootPath.add((String) menuTree.getModel().getRoot());
				selection = getMenuPath(rootPath, code.getMenuId());
			}
			menuTree.setSelectionPath(selection);
			menuTree.scrollPathToVisible(selection);

			// List selection
			lstGroups.setSelectedValue(code.getGroup(), true);

			// Text
			txtText.setText(code.getText());
			refreshing = false;
		}
	}

	private TreePath getMenuPath(ArrayList<String> path, String menuId) {
		String lastMenuId = path.get(path.size() - 1);
		if ((lastMenuId != null) && lastMenuId.equals(menuId)) {
			return new TreePath(path.toArray(new String[0]));
		} else {
			if (lastMenuId.equals(MenuTreeModel.ROOT_ID)) {
				lastMenuId = null;
			}
			String[] children = getUIManager().getMenuChildren(lastMenuId);
			for (String menu : children) {
				ArrayList<String> newPath = new ArrayList<String>();
				newPath.addAll(path);
				newPath.add(menu);
				TreePath ret = getMenuPath(newPath, menuId);
				if (ret != null) {
					return ret;
				}
			}
		}

		return null;
	}

	private Component getMenuPanel() {
		menuTree = new JTree();
		menuTree.setRootVisible(false);
		menuTree.setShowsRootHandles(true);
		menuTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		menuTree.setModel(new MenuTreeModel(menuTree));
		for (int i = 0; i < menuTree.getRowCount(); i++) {
			menuTree.expandRow(i);
		}
		menuTree.setCellRenderer(new MenuTreeRenderer());
		menuTree.getSelectionModel().addTreeSelectionListener(
				new TreeSelectionListener() {

					@Override
					public void valueChanged(TreeSelectionEvent e) {
						refreshModel();
					}
				});
		JPanel ret = new JPanel();
		// ret.setBorder(BorderFactory
		// .createTitledBorder("Select menu to install action"));
		ret.add(new JScrollPane(menuTree));
		return ret;
	}

	@Override
	public boolean acceptElement(String typeId) {
		return GeocognitionActionElementFactory.ACTION_ID.equals(typeId);
	}

	private final class PropertyModifiacationListener implements
			ActionPropertyChangeListener {
		@Override
		public void propertyChanged(String propertyName, String newValue,
				String oldValue) {
			refreshUI();
		}
	}

	private class MenuTreeRenderer extends DefaultTreeCellRenderer implements
			TreeCellRenderer {
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			String menuId = value.toString();
			String menuName;
			if (menuId.equals(MenuTreeModel.ROOT_ID)) {
				menuName = "Root";
			} else {
				menuName = getUIManager().getMenuName(menuId);
			}
			return super.getTreeCellRendererComponent(tree, menuName, sel,
					expanded, leaf, row, hasFocus);
		}
	}

	private UIManager getUIManager() {
		return Services.getService(UIManager.class);
	}

	private class MenuTreeModel extends AbstractTreeModel implements TreeModel {

		static final String ROOT_ID = "ROOT";

		public MenuTreeModel(JTree tree) {
			super(tree);
		}

		@Override
		public Object getChild(Object parent, int index) {
			String id = getId(parent);
			String[] children = getUIManager().getMenuChildren(id);
			int count = 0;
			for (String child : children) {
				if (getUIManager().getMenuChildren(child).length > 0) {
					if (count == index) {
						return child;
					} else {
						count++;
					}
				}
			}
			return null;
		}

		private String getId(Object parent) {
			if (parent.equals(ROOT_ID)) {
				return null;
			} else {
				return parent.toString();
			}
		}

		@Override
		public int getChildCount(Object parent) {
			String id = getId(parent);
			String[] children = getUIManager().getMenuChildren(id);
			int count = 0;
			for (String child : children) {
				if (getUIManager().getMenuChildren(child).length > 0) {
					count++;
				}

			}
			return count;
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			for (int i = 0; i < getChildCount(parent); i++) {
				if (getChild(parent, i).equals(child)) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public Object getRoot() {
			return ROOT_ID;
		}

		@Override
		public boolean isLeaf(Object node) {
			String id = getId(node);
			String[] children = getUIManager().getMenuChildren(id);
			for (String child : children) {
				if (getUIManager().getMenuChildren(child).length > 0) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
		}

	}

}
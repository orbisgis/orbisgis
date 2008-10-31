package org.orbisgis.editors.action;

import java.awt.Component;
import java.awt.Dimension;
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

import org.orbisgis.action.EPActionHelper;
import org.orbisgis.action.IMenu;
import org.orbisgis.action.MenuTree;
import org.orbisgis.action.ToolBarArray;
import org.orbisgis.edition.EditableElement;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editors.sql.JavaEditor;
import org.orbisgis.geocognition.actions.ActionCode;
import org.orbisgis.geocognition.actions.GeocognitionActionElementFactory;
import org.orbisgis.ui.resourceTree.AbstractTreeModel;
import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;

public class ActionEditor extends JavaEditor implements IEditor {

	private JTabbedPane panel;
	private JTree menuTree;
	private JList lstGroups;
	private JTextField txtText;
	private boolean refreshing = false;

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
		String[] groups = EPActionHelper.getGroupList("org.orbisgis.Action",
				"action");
		lstGroups = new JList(groups);
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
				IMenu menu = (IMenu) selectionPath.getLastPathComponent();
				menuId = menu.getId();
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
	}

	private synchronized void refreshUI() {
		if (!refreshing) {
			refreshing = true;
			ActionCode code = (ActionCode) getElement().getObject();

			// Tree selection
			TreePath selection = null;
			if (code.getMenuId() != null) {
				ArrayList<IMenu> rootPath = new ArrayList<IMenu>();
				rootPath.add((IMenu) menuTree.getModel().getRoot());
				selection = getMenuPath(rootPath, code.getMenuId());
			}
			menuTree.setSelectionPath(selection);

			// List selection
			lstGroups.setSelectedValue(code.getGroup(), true);

			// Text
			txtText.setText(code.getText());
			refreshing = false;
		}
	}

	private TreePath getMenuPath(ArrayList<IMenu> path, String menuId) {
		IMenu last = path.get(path.size() - 1);
		String lastMenuId = last.getId();
		if ((lastMenuId != null) && lastMenuId.equals(menuId)) {
			return new TreePath(path.toArray(new IMenu[0]));
		} else {
			IMenu[] children = last.getChildren();
			for (IMenu menu : children) {
				ArrayList<IMenu> newPath = new ArrayList<IMenu>();
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
		MenuTree mt = getMenuStructure();
		menuTree = new JTree();
		menuTree.setPreferredSize(new Dimension(200, 100));
		menuTree.setRootVisible(false);
		menuTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		menuTree.setModel(new MenuTreeModel(menuTree, mt));
		menuTree.setCellRenderer(new MenuTreeRenderer());
		menuTree.getSelectionModel().addTreeSelectionListener(
				new TreeSelectionListener() {

					@Override
					public void valueChanged(TreeSelectionEvent e) {
						refreshModel();
					}
				});
		JPanel ret = new JPanel();
		ret.setBorder(BorderFactory
				.createTitledBorder("Select menu to install action"));
		ret.add(new JScrollPane(menuTree));
		return ret;
	}

	private MenuTree getMenuStructure() {
		ToolBarArray foo = new ToolBarArray();
		MenuTree mt = new MenuTree();
		EPActionHelper.configureParentMenusAndToolBars("org.orbisgis.Action",
				mt, foo);
		return mt;
	}

	@Override
	public boolean acceptElement(String typeId) {
		return GeocognitionActionElementFactory.ACTION_ID.equals(typeId);
	}

	private class MenuTreeRenderer extends DefaultTreeCellRenderer implements
			TreeCellRenderer {
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			return super.getTreeCellRendererComponent(tree, ((IMenu) value)
					.getText(), sel, expanded, leaf, row, hasFocus);
		}
	}

	private class MenuTreeModel extends AbstractTreeModel implements TreeModel {

		private MenuTree mt;

		public MenuTreeModel(JTree tree, MenuTree mt) {
			super(tree);
			this.mt = mt;
		}

		@Override
		public Object getChild(Object parent, int index) {
			return ((IMenu) parent).getChildren()[index];
		}

		@Override
		public int getChildCount(Object parent) {
			return ((IMenu) parent).getChildren().length;
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			IMenu[] children = ((IMenu) parent).getChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i] == child) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public Object getRoot() {
			return mt.getRoot();
		}

		@Override
		public boolean isLeaf(Object node) {
			return ((IMenu) node).getChildren().length == 0;
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
		}

	}

}
package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;

public class CatalogEditor implements TreeCellEditor {

	private OurJPanel ourJPanel = null;

	private JTree tree = null;

	private CatalogRenderer catalogRenderer = null;

	public CatalogEditor(JTree tree, CatalogRenderer renderer) {
		this.tree = tree;
		ourJPanel = new OurJPanel();
		this.catalogRenderer = renderer;
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {

		ourJPanel.setNodeCosmetic(tree, (MyNode) value, isSelected, expanded,
				leaf, row);
		return ourJPanel;

	}

	public void addCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub

	}

	public void cancelCellEditing() {
		// TODO Auto-generated method stub

	}

	public Object getCellEditorValue() {
		// return ourJPanel.textField.getText();
		return null;
	}

	public boolean isCellEditable(EventObject anEvent) {
		boolean ok = false;
		if (anEvent instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) anEvent;
			if (me.getClickCount() == 1 && me.getButton() == 1) {
				MyNode node = (MyNode) tree.getSelectionPath()
						.getLastPathComponent();
				// The offset is used to determine if we clicked on the icon or
				// on the string
				int offset = (node.getPath().length - 2) * 20 + 30;
				if (me.getX() >= offset) {
					int type = node.getType();
					if (node != null
							&& (type == MyNode.folder | type == MyNode.sqlquery)) {
						ok = true;
					}
				}

			}
		}
		return ok;
	}

	public void removeCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub

	}

	public boolean shouldSelectCell(EventObject anEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean stopCellEditing() {
		boolean ok = false;
		if (ourJPanel.textField.getText().length() != 0) {
			MyNode node = (MyNode) tree.getEditingPath().getLastPathComponent();
			node.setName(ourJPanel.textField.getText());
			ok = true;
		}
		return ok;
	}

	private class OurJPanel extends JPanel {

		private JLabel iconAndLabel;

		private JTextField textField;

		public OurJPanel() {
			setBackground(Color.white);
			FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
			fl.setHgap(0);
			setLayout(fl);
			iconAndLabel = new JLabel();
			textField = new JTextField(14);
			textField.addKeyListener(new KeyAdapter() {

				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ENTER) {
						tree.stopEditing();
					}
				}

			});
			add(iconAndLabel);
			add(textField);
		}

		public void setNodeCosmetic(JTree tree, MyNode node,
				boolean isSelected, boolean expanded, boolean leaf, int row) {

			Icon icon = catalogRenderer.getIcon(node, expanded, leaf);
			if (null != icon) {
				iconAndLabel.setIcon(icon);
			}

			iconAndLabel.setVisible(true);
			textField.setText(node.getName());
			textField.selectAll();
		}
	}

}

package org.orbisgis.plugin.view.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import org.orbisgis.plugin.view.layerModel.ILayer;

public class OurTreeCellEditor implements TreeCellEditor {
	private OurJPanel ourJPanel;

	private TOC toc;

	public class OurJPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private JCheckBox check;

		private JLabel iconAndLabel;

		private JTextField textField;

		public OurJPanel() {
			FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
			fl.setHgap(0);
			setLayout(fl);
			check = new JCheckBox();
			iconAndLabel = new JLabel();
			textField = new JTextField(14);
			textField.addKeyListener(new KeyAdapter() {

				@Override
				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ENTER) {
						toc.stopEditing();
					}
				}

			});
			add(check);
			add(iconAndLabel);
			add(textField);
		}

		public void setNodeCosmetic(JTree tree, ILayer node,
				boolean isSelected, boolean expanded, boolean leaf, int row) {
			check.setVisible(true);
			check.setSelected(node.isVisible());

			Icon icon = node.getIcon();
			if (null != icon) {
				iconAndLabel.setIcon(icon);
			}
			iconAndLabel.setVisible(true);

			// TreePath treePath = OurTreeCellEditor.this.toc.getEditingPath();
			TreePath treePath = OurTreeCellEditor.this.toc.getPathForRow(row);
			if (null != treePath) {
				ILayer layer = (ILayer) treePath.getLastPathComponent();
				System.err.println(layer);
			}
			textField.setText(node.getName());
			textField.selectAll();
			System.err.println("___");
		}
	}

	public OurTreeCellEditor(TOC toc) {
		ourJPanel = new OurJPanel();
		this.toc = toc;
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
		System.err.println("___");
		ourJPanel.setNodeCosmetic(tree, (ILayer) value, isSelected, expanded,
				leaf, row);
		return ourJPanel;
	}

	public void addCellEditorListener(CellEditorListener l) {

	}

	public void cancelCellEditing() {

	}

	public Object getCellEditorValue() {
		return ourJPanel.textField.getText();
	}

	public boolean isCellEditable(EventObject anEvent) {
		if (anEvent instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) anEvent;
			if (me.getClickCount() >= 2) {
				return true;
			}
		}
		return false;
	}

	public void removeCellEditorListener(CellEditorListener l) {

	}

	public boolean shouldSelectCell(EventObject anEvent) {
		return false;
	}

	public boolean stopCellEditing() {
		if (ourJPanel.textField.getText().length() == 0) {
			return false;
		} else {
			ILayer l = (ILayer)toc.getEditingPath().getLastPathComponent();
			l.setName(ourJPanel.textField.getText());
			return true;
		}
	}
}
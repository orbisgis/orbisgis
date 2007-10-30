package org.orbisgis.geoview.toc;

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

import org.orbisgis.geoview.layerModel.ILayer;

public class TocEditor implements TreeCellEditor {
	private EditorPanel editorPanel;

	private JTree tree;

	public class EditorPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private JCheckBox check;

		private JLabel iconAndLabel;

		private JTextField textField;

		public EditorPanel() {
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
						tree.stopEditing();
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

			textField.setText(node.getName());
			textField.selectAll();
		}
	}

	public TocEditor(JTree toc) {
		editorPanel = new EditorPanel();
		this.tree = toc;
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
		editorPanel.setNodeCosmetic(tree, ((ILayerResource) value).getLayer(),
				isSelected, expanded, leaf, row);
		return editorPanel;
	}

	public void addCellEditorListener(CellEditorListener l) {

	}

	public void cancelCellEditing() {

	}

	public Object getCellEditorValue() {
		return editorPanel.textField.getText();
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
		if (editorPanel.textField.getText().length() == 0) {
			return false;
		} else {
			ILayer l = ((ILayerResource) tree.getEditingPath()
					.getLastPathComponent()).getLayer();
			l.setName(editorPanel.textField.getText());
			return true;
		}
	}
}
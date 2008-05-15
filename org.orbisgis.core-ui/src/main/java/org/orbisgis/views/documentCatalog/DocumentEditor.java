package org.orbisgis.views.documentCatalog;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;

public class DocumentEditor implements TreeCellEditor {

	private JTextField editorPanel;
	private JTree tree;

	public DocumentEditor(final JTree tree) {
		this.tree = tree;
		editorPanel = new JTextField();
		editorPanel.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					tree.stopEditing();
				}
			}

		});

	}

	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
		String text = ((IDocument) value).getName();
		editorPanel.setText(text);
		editorPanel.setPreferredSize(tree.getPreferredSize());

		return editorPanel;
	}

	public void addCellEditorListener(CellEditorListener l) {
	}

	public void cancelCellEditing() {

	}

	public Object getCellEditorValue() {
		return editorPanel.getText();
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
		return true;
	}

	public boolean stopCellEditing() {
		IDocument doc = (IDocument) tree.getSelectionPath()
				.getLastPathComponent();
		doc.setName(getCellEditorValue().toString());
		return true;
	}

}

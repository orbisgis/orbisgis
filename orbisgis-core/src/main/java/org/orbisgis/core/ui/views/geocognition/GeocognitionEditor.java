package org.orbisgis.core.ui.views.geocognition;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;

import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.errorManager.ErrorManager;

public class GeocognitionEditor implements TreeCellEditor {

	private JTextField jtextField;
	private JTree tree;

	public GeocognitionEditor(final JTree tree) {
		this.tree = tree;
		jtextField = new JTextField();
		jtextField.addKeyListener(new KeyAdapter() {

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
		String text = ((GeocognitionElement) value).getId();
		jtextField.setText(text);
		Component renderComponent = tree.getCellRenderer()
				.getTreeCellRendererComponent(tree, value, isSelected,
						expanded, leaf, row, false);
		int height = renderComponent.getPreferredSize().height;
		jtextField.setPreferredSize(new Dimension(
				tree.getPreferredSize().width, height));

		return jtextField;
	}

	public void addCellEditorListener(CellEditorListener l) {
	}

	public void cancelCellEditing() {

	}

	public Object getCellEditorValue() {
		return jtextField.getText();
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
		GeocognitionElement doc = (GeocognitionElement) tree.getSelectionPath()
				.getLastPathComponent();
		try {
			doc.setId(getCellEditorValue().toString());
			return true;
		} catch (IllegalArgumentException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot change element id", e);
			return false;
		}
	}

}

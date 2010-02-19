package org.orbisgis.core.ui.components.resourceTree;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;

/**
 * Cell editor that doesn't allow cell editing
 */
public class ReadOnlyCellEditor implements TreeCellEditor {
	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
		return null;
	}

	@Override
	public void addCellEditorListener(CellEditorListener l) {
		// do nothing
	}

	@Override
	public void cancelCellEditing() {
		// do nothing
	}

	@Override
	public Object getCellEditorValue() {
		return null;
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		return false;
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {
		// do nothing
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		return false;
	}

	@Override
	public boolean stopCellEditing() {
		return false;
	}
}

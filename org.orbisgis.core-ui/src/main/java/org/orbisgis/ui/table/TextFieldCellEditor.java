package org.orbisgis.ui.table;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

public class TextFieldCellEditor extends JTextField implements TableCellEditor {

	private ArrayList<CellEditorListener> listeners = new ArrayList<CellEditorListener>();
	private String initialValue;

	public TextFieldCellEditor() {
		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					stopCellEditing();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					cancelCellEditing();
				}
			}
		});
	}

	/**
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable,
	 *      java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		this.setText(value.toString());
		return this;
	}

	/**
	 * @see javax.swing.CellEditor#cancelCellEditing()
	 */
	public void cancelCellEditing() {
		setText(initialValue);
		for (int i = 0; i < listeners.size(); i++) {
			CellEditorListener l = listeners.get(i);
			ChangeEvent evt = new ChangeEvent(this);
			l.editingCanceled(evt);
		}
	}

	/**
	 * @see javax.swing.CellEditor#stopCellEditing()
	 */
	public boolean stopCellEditing() {
		for (int i = 0; i < listeners.size(); i++) {
			CellEditorListener l = listeners.get(i);
			ChangeEvent evt = new ChangeEvent(this);
			l.editingStopped(evt);
		}

		return true;
	}

	/**
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		return getText();
	}

	/**
	 * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
	 */
	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}

	/**
	 * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
	 */
	public boolean shouldSelectCell(EventObject anEvent) {
		return false;
	}

	/**
	 * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void addCellEditorListener(CellEditorListener l) {
		listeners.add(l);
	}

	/**
	 * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void removeCellEditorListener(CellEditorListener l) {
		listeners.remove(l);
	}

}

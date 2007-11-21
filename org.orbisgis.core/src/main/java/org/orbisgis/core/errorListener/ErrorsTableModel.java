package org.orbisgis.core.errorListener;

import java.util.ArrayList;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class ErrorsTableModel implements TableModel {

	private ArrayList<ErrorMessage> errors = new ArrayList<ErrorMessage>();

	private ArrayList<TableModelListener> listeners = new ArrayList<TableModelListener>();

	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) {
			return "Date";
		} else {
			return "Error message";
		}
	}

	public int getRowCount() {
		return errors.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		rowIndex = errors.size() - rowIndex - 1;
		if (columnIndex == 0) {
			return errors.get(rowIndex).getDate();
		} else {
			return errors.get(rowIndex).getUserMessage();
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
	}

	public void removeError(int selectedRow) {
		selectedRow = errors.size() - selectedRow - 1;
		errors.remove(selectedRow);
		refresh();
	}

	private void refresh() {
		for (TableModelListener listener : listeners) {
			listener.tableChanged(new TableModelEvent(this));
		}
	}

	public String getTrace(int selectedRow) {
		selectedRow = errors.size() - selectedRow - 1;
		return errors.get(selectedRow).getTrace();
	}

	public void addError(ErrorMessage errorMessage) {
		errors.add(errorMessage);
		refresh();
	}

}

/**
 *
 */
package org.orbisgis.geoview.table;

import java.util.ArrayList;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.pluginManager.PluginManager;

public class DataSourceTableModel implements TableModel {

	private ArrayList<TableModelListener> listeners = new ArrayList<TableModelListener>();
	private DataSource ds;

	public DataSourceTableModel(DataSource ds) {
		this.ds = ds;
	}

	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public int getColumnCount() {
		try {
			return ds.getFieldCount();
		} catch (DriverException e) {
			PluginManager.error("Cannot render table", e);
			return 0;
		}
	}

	public String getColumnName(int columnIndex) {
		try {
			return ds.getFieldName(columnIndex);
		} catch (DriverException e) {
			PluginManager.error("Cannot render table", e);
			return "name!";
		}
	}

	public int getRowCount() {
		try {
			return (int) ds.getRowCount();
		} catch (DriverException e) {
			PluginManager.error("Cannot render table", e);
			return 0;
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			return ds.getFieldValue(rowIndex, columnIndex);
		} catch (DriverException e) {
			PluginManager.error("Cannot render table", e);
			return null;
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		throw new UnsupportedOperationException("Cannot edit table");
	}

	public DataSource getDataSource() {
		return ds;
	}

}
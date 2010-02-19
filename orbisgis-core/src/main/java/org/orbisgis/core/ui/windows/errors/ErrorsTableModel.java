/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui.windows.errors;

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
		return 3;
	}

	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) {
			return "Type";
		} else if (columnIndex == 1) {
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
		ErrorMessage errorMessage = errors.get(rowIndex);
		if (columnIndex == 0) {
			if (errorMessage.isError()) {
				return "ERROR";
			} else {
				return "WARNING";
			}
		} else if (columnIndex == 1) {
			return errorMessage.getDate();
		} else {
			return errorMessage.getUserMessage();
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

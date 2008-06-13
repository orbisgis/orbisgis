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
/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.gdms.sql.customQuery.showAttributes;

import java.awt.CardLayout;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class Table extends JPanel {

	private javax.swing.JScrollPane jScrollPane = null;

	private JTable table = null;

	private DataSource ds;

	private DataSourceDataModel tableModel;

	/**
	 * This is the default constructor
	 *
	 * @throws DriverException
	 */
	public Table(DataSource ds) {
		super();
		this.ds = ds;
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setLayout(new CardLayout());
		add(getJScrollPane(), "table");
		add(new JLabel(ds.getName()), "table");
	}

	/**
	 * This method initializes table
	 *
	 * @return javax.swing.JTable
	 */
	private javax.swing.JTable getTable() {
		if (table == null) {
			table = new JTable();
			table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			table.getSelectionModel().setSelectionMode(
					ListSelectionModel.SINGLE_SELECTION);

			table.setColumnSelectionAllowed(true);
			table.getColumnModel().setSelectionModel(
					new DefaultListSelectionModel());
			table.setModel(new DataSourceDataModel());
		}

		return table;
	}

	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getTable());
		}

		return jScrollPane;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @author Fernando Gonz�lez Cort�s
	 */
	public class DataSourceDataModel extends AbstractTableModel {
		private Metadata metadata;

		private Metadata getMetadata() throws DriverException {
			if (metadata == null) {
				metadata = ds.getMetadata();

			}

			return metadata;
		}

		/**
		 * Returns the name of the field.
		 *
		 * @param col
		 *            index of field
		 *
		 * @return Name of field
		 */
		public String getColumnName(int col) {
			try {
				return getMetadata().getFieldName(col);
			} catch (DriverException e) {
				return null;
			}
		}

		/**
		 * Returns the number of fields.
		 *
		 * @return number of fields
		 */
		public int getColumnCount() {
			try {
				return getMetadata().getFieldCount();
			} catch (DriverException e) {
				return 0;
			}
		}

		/**
		 * Returns number of rows.
		 *
		 * @return number of rows.
		 */
		public int getRowCount() {
			try {
				if (ds.isOpen()){
					return (int) ds.getRowCount();
				} else {
					return 0;
				}
			} catch (DriverException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int row, int col) {
			try {
				return ds.getFieldValue(row, col).toString();
			} catch (DriverException e) {
				return ""; //$NON-NLS-1$
			}
		}

		/**
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		/**
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int,
		 *      int)
		 */
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			throw new RuntimeException("Not supported");
		}
	}

	public boolean tableHasFocus() {
		return table.hasFocus() || table.isEditing();
	}

	public String[] getSelectedFieldNames() {
		final int[] selected = table.getSelectedColumns();
		final String[] ret = new String[selected.length];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = tableModel.getColumnName(selected[i]);
		}

		return ret;
	}
}
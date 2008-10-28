package org.orbisgis.editors.table;

import java.awt.BorderLayout;
import java.text.ParseException;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.gdms.data.DataSource;
import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.FieldEditionEvent;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.ui.table.TextFieldCellEditor;

public class TableComponent extends JPanel {
	private javax.swing.JScrollPane jScrollPane = null;
	private JTable table = null;

	private DataSourceDataModel tableModel;
	private DataSource dataSource;

	private ModificationListener listener = new ModificationListener();

	/**
	 * This is the default constructor
	 * 
	 * @throws DriverException
	 */
	public TableComponent() {
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		add(getJScrollPane(), BorderLayout.CENTER);
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
			TextFieldCellEditor ce = new TextFieldCellEditor();
			for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
				table.getColumnModel().getColumn(i).setCellEditor(ce);
			}

			table.getSelectionModel().setSelectionMode(
					ListSelectionModel.SINGLE_SELECTION);

			// TODO table.getSelectionModel().addListSelectionListener(
			// new ListSelectionListener() {
			//
			// public void valueChanged(ListSelectionEvent e) {
			// if (!e.getValueIsAdjusting()) {
			// ec.remark(table.getSelectedRow());
			// }
			// }
			//
			// });
			//
			table.setColumnSelectionAllowed(true);
			table.getColumnModel().setSelectionModel(
					new DefaultListSelectionModel());
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
	 * @author Fernando Gonzalez Cortes
	 */
	public class DataSourceDataModel extends AbstractTableModel {
		private Metadata metadata;

		private Metadata getMetadata() throws DriverException {
			if (metadata == null) {
				metadata = dataSource.getMetadata();

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
				return (int) dataSource.getRowCount();
			} catch (DriverException e) {
				return 0;
			}
		}

		/**
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int row, int col) {
			try {
				return dataSource.getFieldValue(row, col).toString();
			} catch (DriverException e) {
				return "";
			}
		}

		/**
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			try {
				Constraint c = getMetadata().getFieldType(columnIndex)
						.getConstraint(Constraint.READONLY);
				return c == null;
			} catch (DriverException e) {
				return false;
			}
		}

		/**
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int,
		 *      int)
		 */
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			try {
				Type type = getMetadata().getFieldType(columnIndex);
				String strValue = aValue.toString().trim();
				Value v = ValueFactory.createValueByType(strValue, type
						.getTypeCode());

				String inputError = dataSource.check(columnIndex, v);
				if (inputError != null) {
					inputError(inputError, null);
				} else {
					dataSource.setFieldValue(rowIndex, columnIndex, v);
				}
			} catch (DriverException e1) {
				throw new RuntimeException(e1);
			} catch (NumberFormatException e) {
				inputError(e.getMessage(), e);
			} catch (ParseException e) {
				inputError(e.getMessage(), e);
			}
		}
	}

	/**
	 * Shows a dialog with the error type
	 * 
	 * @param msg
	 */
	private void inputError(String msg, Exception e) {
		Services.getService(ErrorManager.class).error(msg);
		getTable().requestFocus();
	}

	public boolean tableHasFocus() {
		return table.hasFocus() || table.isEditing();
	}

	public String[] getSelectedFieldNames() {
		int[] selected = table.getSelectedColumns();
		String[] ret = new String[selected.length];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = tableModel.getColumnName(selected[i]);
		}

		return ret;
	}

	public void setDataSource(DataSource dataSource) {
		if (this.dataSource != null) {
			this.dataSource.removeEditionListener(listener);
			this.dataSource.removeMetadataEditionListener(listener);
		}
		this.dataSource = dataSource;
		if (this.dataSource == null) {
			table.setModel(new DefaultTableModel());
		} else {
			this.dataSource.addEditionListener(listener);
			this.dataSource.addMetadataEditionListener(listener);
			tableModel = new DataSourceDataModel();
			table.setModel(tableModel);
		}
	}

	private class ModificationListener implements EditionListener,
			MetadataEditionListener {

		@Override
		public void multipleModification(MultipleEditionEvent e) {
			tableModel.fireTableDataChanged();
		}

		@Override
		public void singleModification(EditionEvent e) {
			tableModel.fireTableCellUpdated((int) e.getRowIndex(), e
					.getFieldIndex());
		}

		@Override
		public void fieldAdded(FieldEditionEvent event) {
			tableModel.fireTableStructureChanged();
		}

		@Override
		public void fieldModified(FieldEditionEvent event) {
			tableModel.fireTableStructureChanged();
		}

		@Override
		public void fieldRemoved(FieldEditionEvent event) {
			tableModel.fireTableStructureChanged();
		}

	}

}

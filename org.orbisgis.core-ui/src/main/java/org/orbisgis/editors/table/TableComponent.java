package org.orbisgis.editors.table;

import java.awt.BorderLayout;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.data.values.ValueWriter;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.ui.table.TextFieldCellEditor;

import com.vividsolutions.jts.geom.Geometry;

public class TableComponent extends JPanel implements ValueWriter {
	private javax.swing.JScrollPane jScrollPane = null;
	private JTable table = null;

	private DataSourceDataModel tableModel;
	private DataSource dataSource;

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
//				return dataSource.getFieldValue(row, col).getStringValue(
//						TableComponent.this);
			} catch (DriverException e) {
				return ""; //$NON-NLS-1$
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

	/**
	 * Must show the value in a form it can be parsed later
	 * 
	 * @see com.hardcode.gdbms.engine.values.ValueWriter#getStatementString(long)
	 */
	public String getStatementString(long i) {
		return Long.toString(i);
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.ValueWriter#getStatementString(int,
	 *      int)
	 */
	public String getStatementString(int i, int sqlType) {
		return Integer.toString(i);
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.ValueWriter#getStatementString(double,
	 *      int)
	 */
	public String getStatementString(double d, int sqlType) {
		DecimalFormat df = new DecimalFormat();
		df.setGroupingUsed(false);
		df.setMaximumFractionDigits(Integer.MAX_VALUE);
		return df.format(d);
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.ValueWriter#getStatementString(java.lang.String,
	 *      int)
	 */
	public String getStatementString(String str, int sqlType) {
		return str;
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.ValueWriter#getStatementString(java.sql.Date)
	 */
	public String getStatementString(Date d) {
		return DateFormat.getDateInstance(DateFormat.SHORT).format(d);
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.ValueWriter#getStatementString(java.sql.Time)
	 */
	public String getStatementString(Time t) {
		return DateFormat.getTimeInstance().format(t);
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.ValueWriter#getStatementString(java.sql.Timestamp)
	 */
	public String getStatementString(Timestamp ts) {
		return DateFormat.getDateTimeInstance().format(ts);
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.ValueWriter#getStatementString(byte[])
	 */
	public String getStatementString(byte[] binary) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < binary.length; i++) {
			int byte_ = binary[i];
			if (byte_ < 0)
				byte_ = byte_ + 256;
			String b = Integer.toHexString(byte_);
			if (b.length() == 1)
				sb.append("0").append(b); //$NON-NLS-1$
			else
				sb.append(b);
		}

		return sb.toString();
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.ValueWriter#getStatementString(boolean)
	 */
	public String getStatementString(boolean b) {
		return (b) ? "true" : "false"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.ValueWriter#getStatementString(com.hardcode.gdbms.engine.spatial.Geometry)
	 */
	public String getStatementString(Geometry g) {
		throw new RuntimeException(
				"We don't show any spatial field on this table"); //$NON-NLS-1$
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.ValueWriter#getNullStatementString()
	 */
	public String getNullStatementString() {
		return null;
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
		this.dataSource = dataSource;
		table.setModel(new DataSourceDataModel());
	}

}

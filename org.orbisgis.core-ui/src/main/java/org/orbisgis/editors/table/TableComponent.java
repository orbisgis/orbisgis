package org.orbisgis.editors.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

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
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.ui.sif.AskValue;
import org.orbisgis.ui.table.TextFieldCellEditor;
import org.sif.SQLUIPanel;
import org.sif.UIFactory;

public class TableComponent extends JPanel {
	private static final String OPTIMALWIDTH = "OPTIMALWIDTH";
	private static final String SETWIDTH = "SETWIDTH";
	private javax.swing.JScrollPane jScrollPane = null;
	private JTable table = null;
	private int selectedColumn = -1;
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

			table.getTableHeader().setReorderingAllowed(false);
			final ActionListener menuListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (OPTIMALWIDTH.equals(e.getActionCommand())) {
						BackgroundManager bm = Services
								.getService(BackgroundManager.class);
						bm.backgroundOperation(new BackgroundJob() {

							@Override
							public void run(IProgressMonitor pm) {
								final int width = getColumnOptimalWidth(table
										.getRowCount(), Integer.MAX_VALUE,
										selectedColumn, pm);
								final TableColumn col = table.getColumnModel()
										.getColumn(selectedColumn);
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {
										col.setPreferredWidth(width);
									}
								});
							}

							@Override
							public String getTaskName() {
								return "Calculating optimal width";
							}
						});
					} else if (SETWIDTH.equals(e.getActionCommand())) {
						TableColumn selectedTableColumn = table
								.getTableHeader().getColumnModel().getColumn(
										selectedColumn);
						AskValue av = new AskValue("New column width", null,
								null, Integer.toString(selectedTableColumn
										.getPreferredWidth()));
						av.setType(SQLUIPanel.INT);
						if (UIFactory.showDialog(av)) {
							selectedTableColumn.setPreferredWidth(Integer
									.parseInt(av.getValue()));
						}
					}
				}
			};
			table.getTableHeader().addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					popup(e);
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					popup(e);
				}

				private void popup(MouseEvent e) {
					selectedColumn = table.getTableHeader().columnAtPoint(
							e.getPoint());
					table.getTableHeader().repaint();
					if (e.isPopupTrigger()) {
						JPopupMenu pop = new JPopupMenu();
						addMenu(pop, "Optimal width", OPTIMALWIDTH);
						addMenu(pop, "Set width", SETWIDTH);
						pop.show(table.getTableHeader(), e.getX(), e.getY());
					}
				}

				private void addMenu(JPopupMenu pop, String text,
						String actionCommand) {
					JMenuItem menu = new JMenuItem(text);
					menu.setActionCommand(actionCommand);
					menu.addActionListener(menuListener);
					pop.add(menu);
				}
			});

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
			autoResizeColWidth(Math.min(5, tableModel.getRowCount()));
		}
	}

	private void autoResizeColWidth(int rowsToCheck) {
		DefaultTableColumnModel colModel = new DefaultTableColumnModel();
		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn col = new TableColumn(i);
			col.setHeaderValue(table.getColumnName(i));
			col.setHeaderRenderer(new ButtonHeaderRenderer());
			colModel.addColumn(col);
		}
		table.setColumnModel(colModel);
		int maxWidth = 200;
		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn col = table.getColumnModel().getColumn(i);
			int width = getColumnOptimalWidth(rowsToCheck, maxWidth, i,
					new NullProgressMonitor());
			col.setPreferredWidth(width);
		}
	}

	private int getColumnOptimalWidth(int rowsToCheck, int maxWidth, int i,
			IProgressMonitor pm) {
		TableColumn col = table.getColumnModel().getColumn(i);
		int margin = 5;
		int width = 0;

		// Get width of column header
		TableCellRenderer renderer = col.getHeaderRenderer();

		if (renderer == null) {
			renderer = table.getTableHeader().getDefaultRenderer();
		}

		Component comp = renderer.getTableCellRendererComponent(table, col
				.getHeaderValue(), false, false, 0, 0);

		width = comp.getPreferredSize().width;

		// Check header
		comp = renderer.getTableCellRendererComponent(table, col
				.getHeaderValue(), false, false, 0, i);
		width = Math.max(width, comp.getPreferredSize().width);
		// Get maximum width of column data
		for (int r = 0; r < rowsToCheck; r++) {
			if (i / 100 == i / 100.0) {
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo(100 * i / rowsToCheck);
				}
			}
			renderer = table.getCellRenderer(r, i);
			comp = renderer.getTableCellRendererComponent(table, table
					.getValueAt(r, i), false, false, r, i);
			width = Math.max(width, comp.getPreferredSize().width);
		}

		// limit
		width = Math.min(width, maxWidth);

		// Add margin
		width += 2 * margin;

		return width;
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

	class ButtonHeaderRenderer extends JButton implements TableCellRenderer {

		public ButtonHeaderRenderer() {
			setMargin(new Insets(0, 0, 0, 0));
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			setText((value == null) ? "" : value.toString());
			boolean isPressed = (column == selectedColumn);
			getModel().setPressed(isPressed);
			getModel().setArmed(isPressed);
			return this;
		}

		public void setPressedColumn(int col) {
			selectedColumn = col;
		}
	}

}

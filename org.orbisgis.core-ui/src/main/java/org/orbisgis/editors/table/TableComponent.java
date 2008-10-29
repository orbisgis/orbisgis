package org.orbisgis.editors.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
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
import javax.swing.table.TableColumnModel;

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
import org.gdms.sql.strategies.SortComparator;
import org.orbisgis.Services;
import org.orbisgis.action.IActionAdapter;
import org.orbisgis.action.IActionFactory;
import org.orbisgis.action.ISelectableActionAdapter;
import org.orbisgis.action.MenuTree;
import org.orbisgis.editors.table.action.ITableColumnAction;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.ui.resourceTree.ContextualActionExtensionPointHelper;
import org.orbisgis.ui.sif.AskValue;
import org.orbisgis.ui.table.TextFieldCellEditor;
import org.sif.SQLUIPanel;
import org.sif.UIFactory;

public class TableComponent extends JPanel {

	private static final String OPTIMALWIDTH = "OPTIMALWIDTH";
	private static final String SETWIDTH = "SETWIDTH";
	private static final String SORTUP = "SORTUP";
	private static final String SORTDOWN = "SORTDOWN";
	private static final String NOSORT = "NOSORT";
	private javax.swing.JScrollPane jScrollPane = null;
	private JTable table = null;
	private int selectedColumn = -1;
	private DataSourceDataModel tableModel;
	private DataSource dataSource;

	private ModificationListener listener = new ModificationListener();
	public ArrayList<Integer> indexes = null;
	private Selection selection;

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
					} else if (SORTUP.equals(e.getActionCommand())) {
						BackgroundManager bm = Services
								.getService(BackgroundManager.class);
						bm.backgroundOperation(new SortJob(true));
					} else if (SORTDOWN.equals(e.getActionCommand())) {
						BackgroundManager bm = Services
								.getService(BackgroundManager.class);
						bm.backgroundOperation(new SortJob(false));
					} else if (NOSORT.equals(e.getActionCommand())) {
						indexes = null;
						tableModel.fireTableDataChanged();
					}
					table.getTableHeader().repaint();
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
						pop.addSeparator();
						addMenu(pop, "Sort ascending", SORTUP);
						addMenu(pop, "Sort descending", SORTDOWN);
						addMenu(pop, "No Sort", NOSORT);
						pop.addSeparator();
						MenuTree menuTree = new MenuTree();
						String epid = "org.orbisgis.editors.table.ColumnAction";
						ContextualActionExtensionPointHelper.createPopup(
								menuTree, new ColumnActionFactory(), epid);
						JComponent[] menus = menuTree.getJMenus();
						for (JComponent menu : menus) {
							pop.add(menu);
						}
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

	public void setDataSource(DataSource dataSource, ILayer layer) {
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
			autoResizeColWidth(Math.min(5, tableModel.getRowCount()),
					new HashMap<String, Integer>(),
					new HashMap<String, TableCellRenderer>());
			if (layer == null) {
				this.selection = new ResourceSelection(table.getSelectedRows());
			} else {
				this.selection = new LayerSelection(layer);
			}
		}
	}

	private void autoResizeColWidth(int rowsToCheck,
			HashMap<String, Integer> widths,
			HashMap<String, TableCellRenderer> renderers) {
		DefaultTableColumnModel colModel = new DefaultTableColumnModel();
		int maxWidth = 200;
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			TableColumn col = new TableColumn(i);
			String columnName = tableModel.getColumnName(i);
			col.setHeaderValue(columnName);
			TableCellRenderer renderer = renderers.get(columnName);
			if (renderer == null) {
				renderer = new ButtonHeaderRenderer();
			}
			col.setHeaderRenderer(renderer);
			Integer width = widths.get(columnName);
			if (width == null) {
				width = getColumnOptimalWidth(rowsToCheck, maxWidth, i,
						new NullProgressMonitor());
			}
			col.setPreferredWidth(width);
			colModel.addColumn(col);
		}
		table.setColumnModel(colModel);
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
			fieldRemoved(null);
		}

		@Override
		public void fieldModified(FieldEditionEvent event) {
			fieldRemoved(null);
		}

		@Override
		public void fieldRemoved(FieldEditionEvent event) {
			TableColumnModel columnModel = table.getColumnModel();
			HashMap<String, Integer> widths = new HashMap<String, Integer>();
			HashMap<String, TableCellRenderer> renderers = new HashMap<String, TableCellRenderer>();
			try {
				for (int i = 0; i < dataSource.getMetadata().getFieldCount(); i++) {
					String columnName = null;
					try {
						columnName = dataSource.getMetadata().getFieldName(i);
					} catch (DriverException e) {
					}
					int columnIndex = -1;
					if (columnName != null) {
						try {
							columnIndex = columnModel
									.getColumnIndex(columnName);
						} catch (IllegalArgumentException e) {
							columnIndex = -1;
						}
						if (columnIndex != -1) {
							TableColumn column = columnModel
									.getColumn(columnIndex);
							widths.put(columnName, column.getPreferredWidth());
							renderers.put(columnName, column
									.getHeaderRenderer());
						}
					}
				}
			} catch (DriverException e) {
				Services.getService(ErrorManager.class).warning(
						"Cannot keep table configuration", e);
			}
			autoResizeColWidth(Math.min(5, tableModel.getRowCount()), widths,
					renderers);
		}

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
				return dataSource.getFieldValue(getRowIndex(row), col)
						.toString();
			} catch (DriverException e) {
				return "";
			}
		}

		private int getRowIndex(int row) {
			if (indexes != null) {
				row = indexes.get(row);
			}
			return row;
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
					dataSource.setFieldValue(getRowIndex(rowIndex),
							columnIndex, v);
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

	private final class SortJob implements BackgroundJob {

		private boolean ascending;

		public SortJob(boolean ascending) {
			this.ascending = ascending;
		}

		@Override
		public void run(IProgressMonitor pm) {
			try {
				int rowCount = (int) dataSource.getRowCount();
				Value[][] cache = new Value[rowCount][1];
				for (int i = 0; i < rowCount; i++) {
					cache[i][0] = dataSource.getFieldValue(i, selectedColumn);
				}
				ArrayList<Boolean> order = new ArrayList<Boolean>();
				order.add(ascending);
				TreeSet<Integer> sortset = new TreeSet<Integer>(
						new SortComparator(cache, order));
				for (int i = 0; i < rowCount; i++) {
					if (i / 100 == i / 100.0) {
						if (pm.isCancelled()) {
							break;
						} else {
							pm.progressTo(100 * i / rowCount);
						}
					}
					sortset.add(new Integer(i));
				}
				ArrayList<Integer> indexes = new ArrayList<Integer>();
				Iterator<Integer> it = sortset.iterator();
				while (it.hasNext()) {
					Integer integer = (Integer) it.next();
					indexes.add(integer);
				}
				TableComponent.this.indexes = indexes;
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						tableModel.fireTableDataChanged();
					}
				});
			} catch (DriverException e) {
				Services.getService(ErrorManager.class).error("Cannot sort", e);
			}
		}

		@Override
		public String getTaskName() {
			return "Sorting";
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

	private class ColumnActionFactory implements IActionFactory {

		@Override
		public IActionAdapter getAction(Object action,
				HashMap<String, String> attributes) {
			return new ColumnActionAdapter((ITableColumnAction) action);
		}

		@Override
		public ISelectableActionAdapter getSelectableAction(Object action,
				HashMap<String, String> attributes) {
			throw new RuntimeException("Selectable action not allowed");
		}

	}

	private class ColumnActionAdapter implements IActionAdapter {

		private ITableColumnAction action;

		public ColumnActionAdapter(ITableColumnAction action) {
			this.action = action;
		}

		@Override
		public void actionPerformed() {
			action.execute(dataSource, selection, selectedColumn);
		}

		@Override
		public boolean isEnabled() {
			return action.accepts(dataSource, selection, selectedColumn);
		}

		@Override
		public boolean isVisible() {
			return true;
		}

	}

}

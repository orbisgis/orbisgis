package org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.orbisgis.renderer.legend.carto.LegendFactory;
import org.orbisgis.renderer.legend.carto.UniqueValueLegend;
import org.orbisgis.renderer.symbol.Symbol;

public class UniqueValueLegendTableModel extends AbstractTableModel implements
		TableModel {

	private UniqueValueLegend legend = LegendFactory.createUniqueValueLegend();
	private boolean ordered = false;
	private Integer[] valueIndex;
	private boolean showRestOfValues;

	public void setLegend(UniqueValueLegend legend) {
		this.legend = legend;
		invalidateOrder();
		fireTableDataChanged();
	}

	public void setOrdered(boolean selected) {
		ordered = selected;
		invalidateOrder();
	}

	public int getColumnCount() {
		return 3;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return Symbol.class;
		case 1:
			return String.class;
		case 2:
			return String.class;
		default:
			throw new RuntimeException("bug!");
		}
	}

	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "Symbol";
		case 1:
			return "Value";
		case 2:
			return "Label";
		default:
			throw new RuntimeException("bug!");
		}
	}

	public int getRowCount() {
		int ret = legend.getValueCount();
		if (showRestOfValues) {
			ret++;
		}
		return ret;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex == legend.getValueCount()) {
			switch (columnIndex) {
			case 0:
				return legend.getDefaultSymbol();
			case 1:
				return ValueFactory.createNullValue();
			case 2:
				return legend.getDefaultLabel();
			default:
				throw new RuntimeException("bug!");
			}
		}

		if (ordered) {
			rowIndex = getValueIndex(rowIndex);
		}

		switch (columnIndex) {
		case 0:
			return legend.getSymbol(rowIndex);
		case 1:
			return legend.getValue(rowIndex);
		case 2:
			return legend.getLabel(rowIndex);
		default:
			throw new RuntimeException("bug!");
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex > 0;
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (rowIndex == legend.getValueCount()) {
			switch (columnIndex) {
			case 0:
				legend.setDefaultSymbol((Symbol) value);
				break;
			case 1:
				JOptionPane.showMessageDialog(null, "Cannot modify "
						+ "'rest of values'", "Wrong input value",
						JOptionPane.ERROR_MESSAGE);
				break;
			case 2:
				legend.setDefaultLabel(value.toString());
				break;
			}
		} else {
			if (ordered) {
				rowIndex = getValueIndex(rowIndex);
			}

			switch (columnIndex) {
			case 0:
				legend.setSymbol(rowIndex, (Symbol) value);
				break;
			case 1:
				Value currentValue = legend.getValue(rowIndex);
				int type = currentValue.getType();
				try {
					Value val = ValueFactory.createValueByType(
							value.toString(), type);
					legend.setValue(rowIndex, val);
					invalidateOrder();
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, value.toString()
							+ " is not valid.", "Wrong input value",
							JOptionPane.ERROR_MESSAGE);
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(null, value.toString()
							+ " is not valid. " + e.getMessage(),
							"Wrong input value", JOptionPane.ERROR_MESSAGE);
				}
				break;
			case 2:
				legend.setLabel(rowIndex, value.toString());
				break;
			default:
				break;
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	private int getValueIndex(int index) {
		if (valueIndex == null) {
			TreeSet<Integer> values = new TreeSet<Integer>(
					new Comparator<Integer>() {

						public int compare(Integer i1, Integer i2) {
							Value v1 = legend.getValue(i1.intValue());
							Value v2 = legend.getValue(i2.intValue());
							try {
								if (v1.isNull())
									return -1;
								if (v2.isNull())
									return 1;
								if (v1.less(v2).getAsBoolean()) {
									return -1;
								} else if (v2.less(v1).getAsBoolean()) {
									return 1;
								}
							} catch (IncompatibleTypesException e) {
								throw new RuntimeException(e);
							}

							return -1;
						}
					});

			for (int i = 0; i < legend.getValueCount(); i++) {
				values.add(new Integer(i));
			}

			valueIndex = values.toArray(new Integer[0]);
		}

		return valueIndex[index];
	}

	public void insertRow(Symbol symbol, Value value, String label) {
		legend.addClassification(value, symbol, label);
		invalidateOrder();
		fireTableRowsInserted(legend.getValueCount() - 1, legend
				.getValueCount());
	}

	public void deleteRows(int[] rows) {
		Arrays.sort(rows);
		for (int i = rows.length - 1; i >= 0; i--) {
			int row = rows[i];
			if (ordered) {
				row = getValueIndex(row);
			}
			if (row == legend.getValueCount()) {
				JOptionPane.showMessageDialog(null,
						"Cannot delete 'Rest of values'", "Wrong input value",
						JOptionPane.ERROR_MESSAGE);
			} else {
				legend.removeClassification(row);
			}
		}
		invalidateOrder();
		fireTableStructureChanged();
	}

	private void invalidateOrder() {
		valueIndex = null;
		fireTableDataChanged();
	}

	public void setShowRestOfValues(boolean showRestOfValues) {
		this.showRestOfValues = showRestOfValues;
		if (!showRestOfValues) {
			legend.setDefaultSymbol(null);
			legend.setDefaultLabel(null);
		}
		fireTableDataChanged();
	}
}

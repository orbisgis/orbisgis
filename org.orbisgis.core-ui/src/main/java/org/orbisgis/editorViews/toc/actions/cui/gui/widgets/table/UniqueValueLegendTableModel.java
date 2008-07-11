package org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table;

import java.text.ParseException;

import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.orbisgis.Services;
import org.orbisgis.renderer.legend.carto.ClassifiedLegend;
import org.orbisgis.renderer.legend.carto.LegendFactory;
import org.orbisgis.renderer.legend.carto.UniqueValueLegend;
import org.orbisgis.renderer.symbol.Symbol;

public class UniqueValueLegendTableModel extends ClassifiedLegendTableModel
		implements TableModel {

	private UniqueValueLegend legend = LegendFactory.createUniqueValueLegend();

	@Override
	public void setLegend(ClassifiedLegend legend) {
		this.legend = (UniqueValueLegend) legend;
		super.setLegend(legend);
	}

	public int getColumnCount() {
		return 3;
	}

	protected Value getOrderValue(int index) {
		return legend.getValue(index);
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

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex == legend.getClassificationCount()) {
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

		rowIndex = getSortedIndex(rowIndex);

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
		if (rowIndex == legend.getClassificationCount()) {
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
			rowIndex = getSortedIndex(rowIndex);

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
					Services.getErrorManager().error(
							value.toString() + " is not valid.", e);
				} catch (ParseException e) {
					Services.getErrorManager().error(
							value.toString() + " is not valid.", e);
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

	public void insertRow(Symbol symbol, Value value, String label) {
		legend.addClassification(value, symbol, label);
		invalidateOrder();
		fireTableRowsInserted(legend.getClassificationCount() - 1, legend
				.getClassificationCount());
	}
}

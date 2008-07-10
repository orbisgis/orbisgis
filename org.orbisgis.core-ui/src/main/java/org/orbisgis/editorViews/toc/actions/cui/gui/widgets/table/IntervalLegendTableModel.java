package org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table;

import java.text.ParseException;

import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.orbisgis.renderer.legend.carto.ClassifiedLegend;
import org.orbisgis.renderer.legend.carto.Interval;
import org.orbisgis.renderer.legend.carto.IntervalLegend;
import org.orbisgis.renderer.legend.carto.LegendFactory;
import org.orbisgis.renderer.symbol.Symbol;

public class IntervalLegendTableModel extends ClassifiedLegendTableModel
		implements TableModel {

	private IntervalLegend legend = LegendFactory.createIntervalLegend();

	@Override
	public void setLegend(ClassifiedLegend legend) {
		this.legend = (IntervalLegend) legend;
		super.setLegend(legend);
	}

	public int getColumnCount() {
		return 4;
	}

	protected Value getOrderValue(int index) {
		return legend.getInterval(index).getMinValue();
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
		case 3:
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
			return "Init";
		case 2:
			return "End";
		case 3:
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
				return ValueFactory.createNullValue();
			case 3:
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
			return legend.getInterval(rowIndex).getMinValue();
		case 2:
			return legend.getInterval(rowIndex).getMaxValue();
		case 3:
			return legend.getLabel(rowIndex);
		default:
			throw new RuntimeException("bug!");
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex > 0;
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		String txt = value.toString();
		if (rowIndex == legend.getClassificationCount()) {
			switch (columnIndex) {
			case 0:
				legend.setDefaultSymbol((Symbol) value);
				break;
			case 1:
			case 2:
				JOptionPane.showMessageDialog(null, "Cannot modify "
						+ "'rest of values'", "Wrong input value",
						JOptionPane.ERROR_MESSAGE);
				break;
			case 3:
				legend.setDefaultLabel(txt);
				break;
			}
		} else {
			rowIndex = getSortedIndex(rowIndex);

			Interval interval = legend.getInterval(rowIndex);
			switch (columnIndex) {
			case 0:
				legend.setSymbol(rowIndex, (Symbol) value);
				break;
			case 1:
				try {
					Value valMin;
					if (txt.trim().length() == 0) {
						valMin = null;
					} else {
						valMin = ValueFactory.createValueByType(txt, legend
								.getClassificationFieldType());
					}
					legend.setInterval(rowIndex, new Interval(valMin, true,
							interval.getMaxValue(), false));
					invalidateOrder();
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, txt + " is not valid.",
							"Wrong input value", JOptionPane.ERROR_MESSAGE);
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(null, txt + " is not valid. "
							+ e.getMessage(), "Wrong input value",
							JOptionPane.ERROR_MESSAGE);
				}
				break;
			case 2:
				try {
					Value valMax;
					if (txt.trim().length() == 0) {
						valMax = null;
					} else {
						valMax = ValueFactory.createValueByType(txt, legend
								.getClassificationFieldType());
					}
					legend.setInterval(rowIndex, new Interval(interval
							.getMinValue(), true, valMax, false));
					invalidateOrder();
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, txt + " is not valid.",
							"Wrong input value", JOptionPane.ERROR_MESSAGE);
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(null, txt + " is not valid. "
							+ e.getMessage(), "Wrong input value",
							JOptionPane.ERROR_MESSAGE);
				}
				break;
			case 3:
				legend.setLabel(rowIndex, txt);
				break;
			default:
				break;
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	public void insertRow(Symbol symbol, Value min, Value max, String label) {
		legend.addInterval(min, true, max, false, symbol, label);
		invalidateOrder();
		int classificationCount = legend.getClassificationCount();
		fireTableRowsInserted(classificationCount - 1, classificationCount);
	}
}

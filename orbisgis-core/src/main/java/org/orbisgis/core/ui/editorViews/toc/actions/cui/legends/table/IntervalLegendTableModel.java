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
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.table;

import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.orbisgis.core.renderer.legend.carto.ClassifiedLegend;
import org.orbisgis.core.renderer.legend.carto.Interval;
import org.orbisgis.core.renderer.legend.carto.IntervalLegend;
import org.orbisgis.core.renderer.legend.carto.LegendFactory;
import org.orbisgis.core.renderer.symbol.Symbol;

public class IntervalLegendTableModel extends ClassifiedLegendTableModel
		implements TableModel {

	private static Logger logger = Logger
			.getLogger(IntervalLegendTableModel.class);

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
		DecimalFormat df = new DecimalFormat("00000000000");
		Interval interval = legend.getInterval(index);
		String ini = df.format(interval.getMinValue().getAsDouble());
		String end = df.format(interval.getMaxValue().getAsDouble());
		String s = ini + end;
		return ValueFactory.createValue(s);
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
			return "Init (included)";
		case 2:
			return "End (excluded)";
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
		logger.error("Setting value in classified legend: " + value + ". at "
				+ rowIndex + "," + columnIndex);
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
			int realIndex = getSortedIndex(rowIndex);

			Interval interval = legend.getInterval(realIndex);
			try {
				switch (columnIndex) {
				case 0:
					legend.setSymbol(realIndex, (Symbol) value);
					break;
				case 1:
					Value valMin;
					if (txt.trim().length() == 0) {
						valMin = null;
					} else {
						valMin = ValueFactory.createValueByType(txt, legend
								.getClassificationFieldType());
					}
					legend.setInterval(realIndex, new Interval(valMin, true,
							interval.getMaxValue(), false));
					updateLabel(realIndex, rowIndex);
					invalidateOrder();
					break;
				case 2:
					Value valMax;
					if (txt.trim().length() == 0) {
						valMax = null;
					} else {
						valMax = ValueFactory.createValueByType(txt, legend
								.getClassificationFieldType());
					}
					boolean maxIncluded = rowIndex == getRowCount() - 1;
					legend.setInterval(realIndex, new Interval(interval
							.getMinValue(), true, valMax, maxIncluded));
					updateLabel(realIndex, rowIndex);
					invalidateOrder();
					break;
				case 3:
					legend.setLabel(realIndex, txt);
					break;
				default:
					break;
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, txt + " is not valid.",
						"Wrong input value", JOptionPane.ERROR_MESSAGE);
			} catch (ParseException e) {
				JOptionPane.showMessageDialog(null, txt + " is not valid. "
						+ e.getMessage(), "Wrong input value",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	private void updateLabel(int realIndex, int rowIndex) {
		setValueAt(legend.getInterval(realIndex).getIntervalString(), rowIndex,
				3);
	}

	public void insertRow(Symbol symbol, Value min, Value max, String label) {
		legend.addInterval(min, true, max, false, symbol, label);
		invalidateOrder();
		int classificationCount = legend.getClassificationCount();
		fireTableRowsInserted(classificationCount - 1, classificationCount);
	}
}

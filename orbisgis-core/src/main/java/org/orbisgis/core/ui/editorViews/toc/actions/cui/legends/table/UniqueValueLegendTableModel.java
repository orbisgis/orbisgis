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

import java.text.ParseException;

import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.legend.carto.ClassifiedLegend;
import org.orbisgis.core.renderer.legend.carto.LegendFactory;
import org.orbisgis.core.renderer.legend.carto.UniqueValueLegend;
import org.orbisgis.core.renderer.symbol.Symbol;

public class UniqueValueLegendTableModel extends ClassifiedLegendTableModel
		implements TableModel {

	private static Logger logger = Logger
			.getLogger(UniqueValueLegendTableModel.class);

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
		logger.error("Setting value in classified legend: " + value + ". at "
				+ rowIndex + "," + columnIndex);
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
			int realIndex = getSortedIndex(rowIndex);

			switch (columnIndex) {
			case 0:
				legend.setSymbol(realIndex, (Symbol) value);
				break;
			case 1:
				Value currentValue = legend.getValue(realIndex);
				int type = currentValue.getType();
				try {
					Value val = ValueFactory.createValueByType(
							value.toString(), type);
					legend.setValue(realIndex, val);
					updateLabel(realIndex, rowIndex);
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
				legend.setLabel(realIndex, value.toString());
				break;
			default:
				break;
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	private void updateLabel(int realIndex, int rowIndex) {
		setValueAt(legend.getValue(realIndex).toString(), rowIndex, 2);
	}

	public void insertRow(Symbol symbol, Value value, String label) {
		legend.addClassification(value, symbol, label);
		invalidateOrder();
		fireTableRowsInserted(legend.getClassificationCount() - 1, legend
				.getClassificationCount());
	}
}

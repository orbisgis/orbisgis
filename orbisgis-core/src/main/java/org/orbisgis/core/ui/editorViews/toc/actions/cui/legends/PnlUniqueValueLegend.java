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
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.carto.LegendFactory;
import org.orbisgis.core.renderer.legend.carto.UniqueValueLegend;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.ui.components.sif.ChoosePanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.table.UniqueValueLegendTableModel;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.sif.UIFactory;

public class PnlUniqueValueLegend extends PnlAbstractClassifiedLegend {

	public PnlUniqueValueLegend() {
		super(new UniqueValueLegendTableModel(), LegendFactory
				.createUniqueValueLegend());
	}

	private UniqueValueLegend legend;
	private JComboBox cmbFields;

	@Override
	protected void addAllAction() {
		ILayer layer = legendContext.getLayer();
		SpatialDataSourceDecorator sdsd = layer.getDataSource();
		String selitem = (String) cmbFields.getSelectedItem();

		legend.clear();

		try {
			int fieldIndex = sdsd.getFieldIndexByName(selitem);

			long rowCount = sdsd.getRowCount();

			HashSet<Value> added = new HashSet<Value>();
			for (int i = 0; i < rowCount; i++) {
				if (added.size() == 32) {
					JOptionPane.showMessageDialog(this,
							"More than 32 differnt values "
									+ "found. Showing only 32");
					break;
				}

				Value val = sdsd.getFieldValue(i, fieldIndex);

				if (val.isNull()) {
					continue;
				}

				if (!added.contains(val)) {
					added.add(val);
					Symbol sym = createRandomSymbol();
					legend.addClassification(val, sym, val.toString());
				}

			}

			tableModel.setLegend(this.legend);
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot access the values in the layer", e);
		}
	}

	@Override
	protected void addOneAction() {
		int rowCount = tableModel.getRowCount();

		if (rowCount < 32) {
			try {
				SpatialDataSourceDecorator ds = legendContext.getLayer()
						.getDataSource();
				ArrayList<String> options = new ArrayList<String>();
				ArrayList<Value> values = new ArrayList<Value>();
				for (int i = 0; i < ds.getRowCount() && values.size() < 32; i++) {
					String classificationField = legend
							.getClassificationField();
					int index = ds.getFieldIndexByName(classificationField);
					Value value = ds.getFieldValue(i, index);
					boolean exists = false;
					for (int j = 0; j < tableModel.getRowCount(); j++) {
						if (tableModel.getValueAt(j, 1).equals(value)) {
							exists = true;
							break;
						}
					}
					if (exists || values.contains(value)) {
						continue;
					}
					values.add(value);
					options.add(value.toString());
				}
				options.add("Null");
				values.add(ValueFactory.createNullValue());
				Symbol sym = createRandomSymbol();
				if (options.size() == 1) {
					getTableModel().insertRow(sym, values.get(0),
							options.get(0));
				} else {
					ChoosePanel cp = new ChoosePanel("Select the class value",
							options.toArray(new String[0]), values
									.toArray(new Value[0]));
					if (UIFactory.showDialog(cp)) {
						Value val = (Value) cp.getSelected();
						String label = val.toString();
						getTableModel().insertRow(sym, val, label);
					}
				}
			} catch (DriverException e) {
				Services.getErrorManager().error("Cannot access layer data", e);
			}
		} else {
			Services.getErrorManager().error(
					"Cannot have more than 32 classifications");
		}
	}

	private UniqueValueLegendTableModel getTableModel() {
		return (UniqueValueLegendTableModel) tableModel;
	}

	@Override
	protected boolean canAdd() {
		return cmbFields.getSelectedIndex() != -1;
	}

	@Override
	protected JPanel getTopPanel() {
		JPanel pnlTop = new JPanel();
		pnlTop.add(new JLabel("Classification field:"));

		cmbFields = new JComboBox();
		cmbFields.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					legend.setClassificationField((String) cmbFields
							.getSelectedItem(), legendContext.getLayer()
							.getDataSource());
				} catch (DriverException e1) {
					Services.getErrorManager().error(
							"Cannot access the type of the field", e1);
				}
			}

		});
		pnlTop.add(cmbFields);

		return pnlTop;
	}

	public ILegendPanel newInstance() {
		return new PnlUniqueValueLegend();
	}

	public void setLegend(Legend legend) {
		this.legend = (UniqueValueLegend) legend;
		syncFieldsWithLegend();
		super.setLegend(legend);
	}

	/**
	 * init the combo box
	 */
	private void syncFieldsWithLegend() {

		ArrayList<String> comboValuesArray = new ArrayList<String>();
		try {
			ILayer layer = legendContext.getLayer();
			int numFields = layer.getDataSource().getFieldCount();
			for (int i = 0; i < numFields; i++) {
				int fieldType = layer.getDataSource().getFieldType(i)
						.getTypeCode();
				if (fieldType != Type.GEOMETRY && fieldType != Type.RASTER) {
					comboValuesArray.add(layer.getDataSource().getFieldName(i));
				}
			}
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot populate field names", e);
		}

		String[] comboValues = new String[comboValuesArray.size()];

		comboValues = comboValuesArray.toArray(comboValues);
		cmbFields.setModel(new DefaultComboBoxModel(comboValues));

		String field = legend.getClassificationField();
		if (field != null) {
			cmbFields.setSelectedItem(field);
		} else if (comboValues.length > 0) {
			cmbFields.setSelectedIndex(0);
		}
	}
}

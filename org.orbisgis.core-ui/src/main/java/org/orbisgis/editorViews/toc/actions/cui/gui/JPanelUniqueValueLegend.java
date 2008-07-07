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
/*
 * JPanelUniqueSymbolLegend.java
 *
 * Created on 27 de febrero de 2008, 18:20
 */

package org.orbisgis.editorViews.toc.actions.cui.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.SymbolValueCellRenderer;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.UniqueValueLegendTableModel;
import org.orbisgis.editorViews.toc.actions.cui.ui.SymbolEditor;
import org.orbisgis.images.IconLoader;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.carto.LegendFactory;
import org.orbisgis.renderer.legend.carto.UniqueValueLegend;
import org.orbisgis.renderer.symbol.NullSymbol;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.SymbolFactory;
import org.sif.UIFactory;

/**
 *
 * @author david
 */
public class JPanelUniqueValueLegend extends javax.swing.JPanel implements
		ILegendPanelUI {

	private UniqueValueLegend legend;
	private LegendContext legendContext;
	private UniqueValueLegendTableModel tableModel;

	public JPanelUniqueValueLegend(LegendContext legendContext) {
		legend = LegendFactory.createUniqueValueLegend();
		legend.setName(getLegendTypeName());
		this.legendContext = legendContext;
		initComponents();
		initList();
	}

	/**
	 * init the combo box
	 */
	private void initCombo() {

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
			System.out.println("Driver Exception: " + e.getMessage());
		}

		String[] comboValues = new String[comboValuesArray.size()];

		comboValues = comboValuesArray.toArray(comboValues);
		jComboBox1.setModel(new DefaultComboBoxModel(comboValues));

		String field = legend.getClassificationField();
		if (field != null) {
			jComboBox1.setSelectedItem(field);
		} else if (comboValues.length > 0) {
			jComboBox1.setSelectedIndex(0);
		}

		refreshButtons();
	}

	private void refreshButtons() {
		boolean someField = jComboBox1.getSelectedIndex() != -1;
		jButtonAddAll.setEnabled(someField);
		jButtonAddOne.setEnabled(someField);

		jButtonDel.setEnabled(table.getSelectedRow() != -1);
	}

	/**
	 * init the table and their events
	 */
	private void initList() {
		tableModel = new UniqueValueLegendTableModel();
		table.setModel(tableModel);
		table.setRowHeight(25);

		table.setDefaultRenderer(Symbol.class, new SymbolValueCellRenderer());

		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					int col = table.getSelectedColumn();
					if (col == 0) {
						int row = table.getSelectedRow();
						SymbolEditor symbolEditor = new SymbolEditor(true,
								legendContext);
						symbolEditor.setSymbol((Symbol) tableModel.getValueAt(
								row, 0));

						if (UIFactory.showDialog(symbolEditor)) {
							Symbol sym = symbolEditor.getSymbolComposite();
							tableModel.setValueAt(sym, row, col);
						}
					}
				}
			}

		});

		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					public void valueChanged(ListSelectionEvent e) {
						refreshButtons();
					}

				});
	}

	/**
	 * Creates a random symbol with a random color for the fill and black
	 * outline.
	 *
	 * @param constraint
	 * @return Symbol
	 */
	protected Symbol createRandomSymbol() {
		Symbol s = SymbolFactory.createNullSymbol();

		Random rand = new Random();

		int g2 = rand.nextInt(255);
		int r2 = rand.nextInt(255);
		int b2 = rand.nextInt(255);

		Color outline = Color.black;
		Color fill = new Color(r2, g2, b2);

		Symbol lineSymbol = SymbolFactory.createLineSymbol(fill, 1);
		Symbol pointSymbol = SymbolFactory.createCirclePointSymbol(outline,
				fill, 10);
		Symbol polygonSymbol = SymbolFactory.createPolygonSymbol(outline, fill);
		GeometryConstraint geometryConstraint = legendContext
				.getGeometryConstraint();
		if (geometryConstraint == null) {
			s = SymbolFactory.createSymbolComposite(polygonSymbol, lineSymbol,
					pointSymbol);
		} else {
			switch (geometryConstraint.getGeometryType()) {
			case GeometryConstraint.LINESTRING:
			case GeometryConstraint.MULTI_LINESTRING:
				s = lineSymbol;
				break;
			case GeometryConstraint.POINT:
			case GeometryConstraint.MULTI_POINT:
				s = pointSymbol;
				break;
			case GeometryConstraint.POLYGON:
			case GeometryConstraint.MULTI_POLYGON:
				s = polygonSymbol;
				break;
			default:
				throw new RuntimeException("bug");
			}
		}

		return s;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents() {

		jPanelTop = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jComboBox1 = new javax.swing.JComboBox();
		jCheckBoxRestOfValues = new javax.swing.JCheckBox();
		jCheckBoxOrder = new javax.swing.JCheckBox();
		jPanelTable = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		table = new javax.swing.JTable();
		jPanelButtons = new javax.swing.JPanel();
		jButtonAddAll = new javax.swing.JButton();
		jButtonAddOne = new javax.swing.JButton();
		jButtonDel = new javax.swing.JButton();

		setLayout(new javax.swing.BoxLayout(this,
				javax.swing.BoxLayout.PAGE_AXIS));

		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		jLabel1.setText("Classification field:");
		jPanelTop.add(jLabel1);

		jComboBox1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					legend.setClassificationField((String) jComboBox1
							.getSelectedItem(), legendContext.getLayer()
							.getDataSource());
				} catch (DriverException e1) {
					JOptionPane.showMessageDialog(null,
							"Cannot access the type of the field", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}

		});
		jPanelTop.add(jComboBox1);

		jCheckBoxRestOfValues.setText("rest of values");
		jCheckBoxRestOfValues.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		jCheckBoxRestOfValues
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jCheckBoxRestOfValuesActionPerformed(evt);
					}
				});
		jPanelTop.add(jCheckBoxRestOfValues);

		jCheckBoxOrder.setText("order");
		jCheckBoxOrder.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				0, 0, 0));
		jCheckBoxOrder.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCheckBoxOrderActionPerformed(evt);
			}
		});
		jPanelTop.add(jCheckBoxOrder);

		add(jPanelTop);

		jScrollPane1.setPreferredSize(new java.awt.Dimension(454, 175));

		table.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {

		}, new String[] { "Symbol", "Value", "Label" }));
		jScrollPane1.setViewportView(table);

		jPanelTable.add(jScrollPane1);

		add(jPanelTable);

		jButtonAddAll.setIcon(IconLoader.getIcon("addall.png"));
		jButtonAddAll.setToolTipText("Add all");
		jButtonAddAll.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonAddAllActionPerformed(evt);
			}
		});
		jPanelButtons.add(jButtonAddAll);

		jButtonAddOne.setIcon(IconLoader.getIcon("add.png"));
		jButtonAddOne.setToolTipText("Add");
		jButtonAddOne.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonAddOneActionPerformed(evt);
			}
		});
		jPanelButtons.add(jButtonAddOne);

		jButtonDel.setIcon(IconLoader.getIcon("delete.png"));
		jButtonDel.setToolTipText("Delete");
		jButtonDel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonDelActionPerformed(evt);
			}
		});
		jPanelButtons.add(jButtonDel);

		add(jPanelButtons);
	}

	/**
	 * adds all the values in the layer to the table.
	 *
	 * @param evt
	 */
	private void jButtonAddAllActionPerformed(java.awt.event.ActionEvent evt) {
		ILayer layer = legendContext.getLayer();
		SpatialDataSourceDecorator sdsd = layer.getDataSource();
		String selitem = (String) jComboBox1.getSelectedItem();

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
			System.out.println("Driver Exception: " + e.getMessage());
		}

	}

	/**
	 * adds one more value to the table. will be a copy of the last (if exists
	 * any) or a new one with a NullValue.
	 *
	 * @param evt
	 */
	private void jButtonAddOneActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonAddOneActionPerformed

		int rowCount = tableModel.getRowCount();

		if (rowCount < 32) {
			Symbol sym = createRandomSymbol();
			Value val = ValueFactory.createNullValue();
			String label = "Rest of values";
			if (rowCount > 0) {
				sym = (Symbol) tableModel.getValueAt(0, 0);
				val = (Value) tableModel.getValueAt(0, 1);
				label = (String) tableModel.getValueAt(0, 2);
			}
			tableModel.insertRow(sym, val, label);
		} else {
			JOptionPane.showMessageDialog(this,
					"Cannot have more than 32 classifications");
		}

	}

	private void jButtonDelActionPerformed(java.awt.event.ActionEvent evt) {
		int[] rows = table.getSelectedRows();
		tableModel.deleteRows(rows);
	}

	/**
	 *
	 * @param evt
	 */
	private void jCheckBoxRestOfValuesActionPerformed(
			java.awt.event.ActionEvent evt) {
		boolean isSelected = jCheckBoxRestOfValues.isSelected();
		tableModel.setShowRestOfValues(isSelected);
	}

	private void jCheckBoxOrderActionPerformed(java.awt.event.ActionEvent evt) {
		tableModel.setOrdered(jCheckBoxOrder.isSelected());
	}

	private javax.swing.JButton jButtonAddAll;
	private javax.swing.JButton jButtonAddOne;
	private javax.swing.JButton jButtonDel;
	private javax.swing.JCheckBox jCheckBoxOrder;
	private javax.swing.JCheckBox jCheckBoxRestOfValues;
	private javax.swing.JComboBox jComboBox1;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JPanel jPanelButtons;
	private javax.swing.JPanel jPanelTable;
	private javax.swing.JPanel jPanelTop;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable table;

	public Component getComponent() {
		return this;
	}

	public Legend getLegend() {
		return legend;
	}

	public boolean acceptsGeometryType(int geometryType) {
		return true;
	}

	public String getLegendTypeName() {
		return UniqueValueLegend.NAME;
	}

	public ILegendPanelUI newInstance(LegendContext legendContext) {
		return new JPanelUniqueValueLegend(legendContext);
	}

	public void setLegend(Legend legend) {
		this.legend = (UniqueValueLegend) legend;
		this.tableModel.setLegend(this.legend);
		table.setModel(this.tableModel);

		syncWithLegend();
	}

	private void syncWithLegend() {
		jCheckBoxRestOfValues
				.setSelected(!(this.legend.getDefaultSymbol() instanceof NullSymbol));
		initCombo();
	}

	public void setLegendContext(LegendContext lc) {
		this.legendContext = lc;
	}

}

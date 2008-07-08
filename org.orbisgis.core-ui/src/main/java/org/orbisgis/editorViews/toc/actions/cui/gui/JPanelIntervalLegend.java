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
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.ColorPicker;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.IntervalLegendTableModel;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.SymbolValueCellRenderer;
import org.orbisgis.editorViews.toc.actions.cui.ui.SymbolEditor;
import org.orbisgis.images.IconLoader;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.renderer.classification.RangeMethod;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.carto.Interval;
import org.orbisgis.renderer.legend.carto.IntervalLegend;
import org.orbisgis.renderer.legend.carto.LegendFactory;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.SymbolFactory;
import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;
import org.sif.UIFactory;

/**
 *
 * @author david
 */
public class JPanelIntervalLegend extends javax.swing.JPanel implements
		ILegendPanelUI {

	private IntervalLegend legend;
	private LegendContext legendContext;
	private IntervalLegendTableModel tableModel;

	public JPanelIntervalLegend(LegendContext legendContext) {
		legend = LegendFactory.createIntervalLegend();
		legend.setName(getLegendTypeName());
		this.legendContext = legendContext;
		initComponents();
		initList();
	}

	/**
	 * init the combo box
	 */
	private void initCombo() {

		ArrayList<String> validFieldNames = new ArrayList<String>();
		try {
			ILayer layer = legendContext.getLayer();
			int numFields = layer.getDataSource().getFieldCount();
			for (int i = 0; i < numFields; i++) {
				int fieldType = layer.getDataSource().getFieldType(i)
						.getTypeCode();
				if (fieldType != Type.GEOMETRY && fieldType != Type.RASTER) {
					validFieldNames.add(layer.getDataSource().getFieldName(i));
				}
			}
		} catch (DriverException e) {
			System.out.println("Driver Exception: " + e.getMessage());
		}

		cmbFieldNames.setModel(new DefaultComboBoxModel(validFieldNames
				.toArray(new String[0])));

		String field = legend.getClassificationField();
		if (field != null) {
			cmbFieldNames.setSelectedItem(field);
		} else if (validFieldNames.size() > 0) {
			cmbFieldNames.setSelectedIndex(0);
		}

		refreshButtons();
	}

	private void refreshButtons() {
		boolean someField = cmbFieldNames.getSelectedIndex() != -1;
		jButtonAddAll.setEnabled(someField);
		jButtonAddOne.setEnabled(someField);

		jButtonDel.setEnabled(table.getSelectedRow() != -1);
	}

	/**
	 * init the table and their events
	 */
	private void initList() {
		tableModel = new IntervalLegendTableModel();
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
		Symbol s;
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

	private void initComponents() {

		cmbFieldNames = new javax.swing.JComboBox();
		jCheckBoxRestOfValues = new javax.swing.JCheckBox();
		jCheckBoxOrder = new javax.swing.JCheckBox();
		jPanelTable = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		table = new javax.swing.JTable();
		jPanelButtons = new javax.swing.JPanel();
		jButtonAddAll = new javax.swing.JButton();
		jButtonAddOne = new javax.swing.JButton();
		jButtonDel = new javax.swing.JButton();

		setLayout(new CRFlowLayout());

		add(getTopPanel());

		JPanel pnlChecks = new JPanel();
		jCheckBoxRestOfValues.setText("rest of values");
		jCheckBoxRestOfValues.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				jCheckBoxRestOfValuesActionPerformed();
			}
		});
		pnlChecks.add(jCheckBoxRestOfValues);
		pnlChecks.add(new CarriageReturn());

		jCheckBoxOrder.setText("order");
		jCheckBoxOrder.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCheckBoxOrderActionPerformed(evt);
			}
		});
		pnlChecks.add(jCheckBoxOrder);
		add(pnlChecks);
		add(new CarriageReturn());

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

	private JPanel getTopPanel() {
		JPanel pnl = new JPanel();
		CRFlowLayout flowLayout = new CRFlowLayout();
		flowLayout.setAlignment(CRFlowLayout.LEFT);
		pnl.setLayout(flowLayout);
		pnl.add(new JLabel("Classification field:"));

		cmbFieldNames.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					legend.setClassificationField((String) cmbFieldNames
							.getSelectedItem(), legendContext.getLayer()
							.getDataSource());
				} catch (DriverException e1) {
					JOptionPane.showMessageDialog(null,
							"Cannot access the type of the field", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}

		});
		pnl.add(cmbFieldNames);

		pnl.add(new JLabel("Type of interval: "));
		cmbIntervalCount = new javax.swing.JComboBox();
		cmbIntervalType = new javax.swing.JComboBox();
		cmbIntervalType.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jComboBoxTypeOfIntervalActionPerformed(evt);
			}
		});
		cmbIntervalType.setModel(new DefaultComboBoxModel(new Object[] {
				"Quantiles", "Equivalences", "Moyennes", "Standard" }));
		cmbIntervalType.setSelectedIndex(0);
		pnl.add(cmbIntervalType);

		pnl.add(new CarriageReturn());

		pnl.add(new JLabel("Number of intervals:"));
		pnl.add(cmbIntervalCount);

		pnl.add(new JLabel("First color:"));
		jButtonFirstColor = new JButton();
		jButtonFirstColor.setMaximumSize(new java.awt.Dimension(19, 19));
		jButtonFirstColor.setMinimumSize(new java.awt.Dimension(19, 19));
		jButtonFirstColor.setPreferredSize(new java.awt.Dimension(19, 19));
		jButtonFirstColor
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jButtonFirstColorActionPerformed(evt);
					}
				});
		jButtonFirstColor.setBackground(Color.BLUE);
		pnl.add(jButtonFirstColor);

		pnl.add(new JLabel("Final color:"));
		jButtonSecondColor = new JButton();
		jButtonSecondColor.setMaximumSize(new java.awt.Dimension(19, 19));
		jButtonSecondColor.setMinimumSize(new java.awt.Dimension(19, 19));
		jButtonSecondColor.setPreferredSize(new java.awt.Dimension(19, 19));
		jButtonSecondColor
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jButtonSecondColorActionPerformed(evt);
					}
				});
		jButtonSecondColor.setBackground(Color.RED);
		pnl.add(jButtonSecondColor);

		return pnl;
	}

	/**
	 * Creates the symbol with the specified fillColor and with a black outline
	 *
	 * @param constraint
	 * @param fillColor
	 * @return Symbol
	 */
	private Symbol createSymbol(Color fillColor) {
		Symbol s;

		Color outline = Color.black;

		Symbol lineSymbol = SymbolFactory.createLineSymbol(fillColor, 1);
		Symbol pointSymbol = SymbolFactory.createCirclePointSymbol(outline,
				fillColor, 10);
		Symbol polygonSymbol = SymbolFactory.createPolygonSymbol(outline,
				fillColor);
		GeometryConstraint geometryConstraint = legendContext
				.getGeometryConstraint();
		if (geometryConstraint == null) {
			s = SymbolFactory.createSymbolComposite(polygonSymbol, lineSymbol,
					pointSymbol);
		} else {
			int geometry = geometryConstraint.getGeometryType();
			switch (geometry) {
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
				throw new RuntimeException("bug!");
			}
		}
		return s;
	}

	/**
	 * adds all the values in the layer to the table.
	 *
	 * @param evt
	 */
	private void jButtonAddAllActionPerformed(java.awt.event.ActionEvent evt) {
		RangeMethod rm = null;
		try {
			rm = new RangeMethod(legendContext.getLayer().getDataSource(),
					(String) cmbFieldNames.getSelectedItem(),
					(Integer) cmbIntervalCount.getSelectedItem());

			int typeOfIntervals = cmbIntervalType.getSelectedIndex();

			switch (typeOfIntervals) {
			case 0:
				rm.disecEquivalences();
				break;
			case 1:
				rm.disecQuantiles();
				break;
			case 2:
				rm.disecMean();
				break;
			case 3:
				rm.disecStandard();
				break;
			}

		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot calculate intervals", e);
			return;
		}

		Interval[] intervals = rm.getIntervals();

		legend.clear();

		int numberOfIntervals = intervals.length;

		int r1 = jButtonFirstColor.getBackground().getRed();
		int g1 = jButtonFirstColor.getBackground().getGreen();
		int b1 = jButtonFirstColor.getBackground().getBlue();

		int r2 = jButtonSecondColor.getBackground().getRed();
		int g2 = jButtonSecondColor.getBackground().getGreen();
		int b2 = jButtonSecondColor.getBackground().getBlue();

		int incR = (r2 - r1) / numberOfIntervals;
		int incG = (g2 - g1) / numberOfIntervals;
		int incB = (b2 - b1) / numberOfIntervals;

		int r = r1;
		int g = g1;
		int b = b1;

		Color color = new Color(r, g, b);

		for (int i = 0; i < intervals.length; i++) {
			Interval inter = intervals[i];

			Symbol s = createSymbol(color);

			legend.addInterval(inter.getMinValue(), inter.isMinIncluded(),
					inter.getMaxValue(), inter.isMaxIncluded(), s, inter
							.getIntervalString());

			r += incR;
			g += incG;
			b += incB;
			if (i != intervals.length - 2) {
				color = new Color(r, g, b);
			} else {
				color = new Color(r2, g2, b2);
			}

		}
		jCheckBoxRestOfValues.setSelected(false);

		tableModel.setLegend(legend);
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
			Value minValue = ValueFactory.createNullValue();
			Value maxValue = ValueFactory.createNullValue();
			String label = "Rest of values";
			if (rowCount > 0) {
				sym = (Symbol) tableModel.getValueAt(0, 0);
				minValue = (Value) tableModel.getValueAt(0, 1);
				maxValue = (Value) tableModel.getValueAt(0, 2);
				label = (String) tableModel.getValueAt(0, 3);
			}
			tableModel.insertRow(sym, minValue, maxValue, label);
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
	private void jCheckBoxRestOfValuesActionPerformed() {
		boolean isSelected = jCheckBoxRestOfValues.isSelected();
		tableModel.setShowRestOfValues(isSelected);
	}

	private void jCheckBoxOrderActionPerformed(java.awt.event.ActionEvent evt) {
		tableModel.setOrdered(jCheckBoxOrder.isSelected());
	}

	private void jButtonFirstColorActionPerformed(ActionEvent evt) {
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			jButtonFirstColor.setBackground(color);
		}
	}

	private void jButtonSecondColorActionPerformed(ActionEvent evt) {
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			jButtonSecondColor.setBackground(color);
		}
	}

	/**
	 * Creates the number of intervals according to the type
	 *
	 * @param evt
	 */
	private void jComboBoxTypeOfIntervalActionPerformed(ActionEvent evt) {
		int idx = cmbIntervalType.getSelectedIndex();
		ArrayList<Integer> mod = new ArrayList<Integer>();
		switch (idx) {
		case 0: // Quantiles
		case 1: // Equivalences
			for (int i = 2; i < 12; i++) {
				mod.add(i);
			}
			break;
		case 2: // Moyennes
			mod.add(2);
			mod.add(4);
			mod.add(8);
			break;
		case 3: // Standard
			mod.add(3);
			mod.add(5);
			mod.add(7);
			break;
		}

		cmbIntervalCount.setModel(new DefaultComboBoxModel(mod
				.toArray(new Integer[0])));
	}

	private javax.swing.JButton jButtonAddAll;
	private javax.swing.JButton jButtonAddOne;
	private javax.swing.JButton jButtonDel;
	private javax.swing.JCheckBox jCheckBoxOrder;
	private javax.swing.JCheckBox jCheckBoxRestOfValues;
	private javax.swing.JComboBox cmbFieldNames;
	private javax.swing.JPanel jPanelButtons;
	private javax.swing.JPanel jPanelTable;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable table;

	private javax.swing.JComboBox cmbIntervalType;
	private javax.swing.JComboBox cmbIntervalCount;
	private javax.swing.JButton jButtonFirstColor;
	private javax.swing.JButton jButtonSecondColor;

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
		return IntervalLegend.NAME;
	}

	public ILegendPanelUI newInstance(LegendContext legendContext) {
		return new JPanelIntervalLegend(legendContext);
	}

	public void setLegend(Legend legend) {
		this.legend = (IntervalLegend) legend;
		this.tableModel.setLegend(this.legend);
		table.setModel(this.tableModel);

		syncWithLegend();
	}

	private void syncWithLegend() {
		jCheckBoxRestOfValues
				.setSelected(!(this.legend.getDefaultSymbol() == null));
		initCombo();
	}

	public void setLegendContext(LegendContext lc) {
		this.legendContext = lc;
	}

	public String validateInput() {
		if (legend.getClassificationCount() == 0) {
			return "At least a value classification should be added";
		}
		return null;
	}
}

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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.editorViews.toc.actions.cui.gui.factory.LegendPanelFactory;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.LegendListDecorator;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.SymbolValueCellRenderer;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.SymbolValuePOJO;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.SymbolValueTableModel;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.LegendFactory;
import org.orbisgis.renderer.legend.NullSymbol;
import org.orbisgis.renderer.legend.Symbol;
import org.orbisgis.renderer.legend.SymbolFactory;
import org.orbisgis.renderer.legend.UniqueSymbolLegend;
import org.orbisgis.renderer.legend.UniqueValueLegend;
import org.sif.UIFactory;

/**
 *
 * @author david
 */
public class JPanelUniqueValueLegend extends javax.swing.JPanel implements
		ILegendPanelUI {

	private Integer constraint = 0;
	UniqueValueLegend leg = null;
	ILayer layer = null;

	private LegendListDecorator dec = null;

	public JPanelUniqueValueLegend(Legend leg, Integer constraint, ILayer layer) {
		this.constraint = constraint;
		this.leg = (UniqueValueLegend) leg;
		this.layer = layer;
		initComponents();
		initList();
		initCombo();
	}

	/**
	 * init the combo box
	 */
	private void initCombo() {

		ArrayList<String> comboValuesArray = new ArrayList<String>();
		try {
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

		DefaultComboBoxModel model = (DefaultComboBoxModel) jComboBox1
				.getModel();

		for (int i = 0; i < comboValues.length; i++) {
			model.addElement(comboValues[i]);
		}

		String field = leg.getClassificationField();
		jComboBox1.setSelectedItem(field);

	}

	/**
	 * init the table and their events
	 */
	private void initList() {
		jTable1.setModel(new SymbolValueTableModel());
		jTable1.setRowHeight(25);

		SymbolValueTableModel mod = (SymbolValueTableModel) jTable1.getModel();

		Value[] val = leg.getClassificationValues();

		// jTable1.setDefaultEditor(Symbol.class, new SymbolCellEditor());
		jTable1.setDefaultRenderer(Symbol.class, new SymbolValueCellRenderer());

		for (int i = 0; i < val.length; i++) {

			SymbolValuePOJO poj = new SymbolValuePOJO();
			poj.setSym(leg.getValueSymbol(val[i]));
			poj.setVal(val[i]);
			poj.setLabel(leg.getValueSymbol(val[i]).getName());
			mod.addSymbolValue(poj);
		}

		if (!(leg.getDefaultSymbol() instanceof NullSymbol)) {
			jCheckBoxRestOfValues.setSelected(true);

			SymbolValuePOJO poj = new SymbolValuePOJO();
			poj.setSym(leg.getDefaultSymbol());
			poj.setVal(ValueFactory.createNullValue());
			poj.setLabel("Default");
			mod.addSymbolValue(poj);

		} else {
			jCheckBoxRestOfValues.setSelected(false);
		}

		jTable1.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					int col = jTable1.getSelectedColumn();
					if (col == 0) {
						// FlowLayoutPreviewWindow flpw = new
						// FlowLayoutPreviewWindow();
						// flpw.setConstraint(constraint);
						int row = jTable1.getSelectedRow();
						SymbolValueTableModel mod = (SymbolValueTableModel) jTable1
								.getModel();
						UniqueSymbolLegend usl = LegendFactory
								.createUniqueSymbolLegend();
						usl.setSymbol((Symbol) mod.getValueAt(row, 0));
						JPanelUniqueSymbolLegend jpusl = (JPanelUniqueSymbolLegend) LegendPanelFactory
								.createPanel(
										LegendPanelFactory.UNIQUE_SYMBOL_LEGEND,
										usl, constraint, null, true);

						if (UIFactory.showDialog(jpusl)) {
							Symbol sym = jpusl.getSymbolComposite();

							mod.setValueAt(sym, row, col);
						}
					}
				}
			}

			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});

		mod.addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent e) {
				jTable1Changed(e);
			}

		});
	}

	protected void jTable1Changed(TableModelEvent e) {
		dec.setLegend(getLegend());
	}

	/**
	 * Creates a random symbol with a random color for the fill and black outline.
	 * @param constraint
	 * @return Symbol
	 */
	protected Symbol createRandomSymbol(Integer constraint) {
		Symbol s=SymbolFactory.createNullSymbol();

		Random rand = new Random();

		int g2 = rand.nextInt(255);
		int r2 = rand.nextInt(255);
		int b2 = rand.nextInt(255);

		Color outline = Color.black;
		Color fill = new Color(r2, g2, b2);


		if (constraint == null) {
			Symbol sl = createRandomSymbol(GeometryConstraint.LINESTRING);
			Symbol sc = createRandomSymbol(GeometryConstraint.POINT);
			Symbol sp = createRandomSymbol(GeometryConstraint.POLYGON);
			Symbol[] arraySym = { sl, sc, sp };
			s = SymbolFactory.createSymbolComposite(arraySym);
		} else {
			switch (constraint) {
			case GeometryConstraint.LINESTRING:
			case GeometryConstraint.MULTI_LINESTRING:
				Stroke stroke = new BasicStroke(1);
				s = SymbolFactory.createLineSymbol(fill, (BasicStroke) stroke);
				break;
			case GeometryConstraint.POINT:
			case GeometryConstraint.MULTI_POINT:
				int size = 10;
				s = SymbolFactory.createCirclePointSymbol(outline, fill, size);
				break;
			case GeometryConstraint.POLYGON:
			case GeometryConstraint.MULTI_POLYGON:
				Stroke strokeP = new BasicStroke(1);
				s = SymbolFactory.createPolygonSymbol(strokeP, outline, fill);
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
	// <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jCheckBoxRestOfValues = new javax.swing.JCheckBox();
        jCheckBoxOrder = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonAddAll = new javax.swing.JButton();
        jButtonAddOne = new javax.swing.JButton();
        jButtonDel = new javax.swing.JButton();

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Classification field:");

        jCheckBoxRestOfValues.setText("rest of values");
        jCheckBoxRestOfValues.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxRestOfValues.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxRestOfValuesActionPerformed(evt);
            }
        });

        jCheckBoxOrder.setText("order");
        jCheckBoxOrder.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxOrderActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Symbol", "Value", "Label"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButtonAddAll.setText("Add all");
        jButtonAddAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddAllActionPerformed(evt);
            }
        });

        jButtonAddOne.setText("Add");
        jButtonAddOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddOneActionPerformed(evt);
            }
        });

        jButtonDel.setText("Delete");
        jButtonDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jCheckBoxRestOfValues))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxOrder)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(179, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonAddAll, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAddOne, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDel, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                        .addGap(216, 216, 216))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxRestOfValues)
                    .addComponent(jCheckBoxOrder))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAddAll)
                    .addComponent(jButtonAddOne)
                    .addComponent(jButtonDel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * adds all the values in the layer to the table.
     * @param evt
     */
    
	private void jButtonAddAllActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonAddAllActionPerformed
		SpatialDataSourceDecorator sdsd = layer.getDataSource();
		String selitem = (String) jComboBox1.getSelectedItem();
		if (jComboBox1.getSelectedIndex() == -1) {
			return;
		}

		((SymbolValueTableModel) jTable1.getModel()).deleteAllSymbols();

		ArrayList<String> alreadyAdded = new ArrayList<String>();
		try {
			int fieldindex = sdsd.getFieldIndexByName(selitem);

			long rowcount = sdsd.getRowCount();

			for (int i = 0; i < rowcount; i++) {
				if (alreadyAdded.size() == 32) {
					JOptionPane
							.showMessageDialog(this,
									"More than 32 differnt values found. Showing only 32");
					break;
				}

				Value[] vals = sdsd.getRow(i);

				Value val = vals[fieldindex];

				if (val.isNull()) {
					continue;
				}

				if (!alreadyAdded.contains(val.toString())) {

					alreadyAdded.add(val.toString());

					Symbol sym = createRandomSymbol(constraint);

					SymbolValuePOJO poj = new SymbolValuePOJO();

					poj.setSym(sym);
					poj.setVal(val);
					poj.setLabel(val.toString());

					((SymbolValueTableModel) jTable1.getModel())
							.addSymbolValue(poj);
				}

			}

		} catch (DriverException e) {
			System.out.println("Driver Exception: " + e.getMessage());
		}

	}// GEN-LAST:event_jButtonAddAllActionPerformed

	/**
	 * adds one more value to the table.
	 * will be a copy of the last (if exists any) or a new one with a NullValue.
	 * @param evt
	 */
	private void jButtonAddOneActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonAddOneActionPerformed
		SymbolValueTableModel mod = (SymbolValueTableModel) jTable1.getModel();

		int rowCount = mod.getRowCount();

		SymbolValuePOJO poj = new SymbolValuePOJO();
		if (rowCount < 32) {
			Symbol sym = createRandomSymbol(constraint);
			Value val = ValueFactory.createNullValue();
			String label = "DummyValue";
			if (rowCount > 0) {
				sym = (Symbol) mod.getValueAt(rowCount - 1, 0);
				val = (Value) mod.getValueAt(rowCount - 1, 3);
				label = (String) mod.getValueAt(rowCount - 1, 2);
			}
			poj.setSym(sym);
			poj.setVal(val);
			poj.setLabel(label);
		} else {
			JOptionPane.showMessageDialog(this,
					"More than 32 differnt values found. Showing only 32");
		}

		((SymbolValueTableModel) jTable1.getModel()).addSymbolValue(poj);
	}// GEN-LAST:event_jButtonAddOneActionPerformed

	private void jButtonDelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonDelActionPerformed
		SymbolValueTableModel mod = (SymbolValueTableModel) jTable1.getModel();
		int[] rows = jTable1.getSelectedRows();
		SymbolValuePOJO[] pojos = new SymbolValuePOJO[rows.length];
		for (int i = 0; i < rows.length; i++) {
			pojos[i] = (SymbolValuePOJO) mod.getValueAt(rows[i], -1);
		}
		mod.deleteSymbolPojos(pojos);

		jTable1.getSelectionModel().clearSelection();
	}// GEN-LAST:event_jButtonDelActionPerformed

	/**
	 * 
	 * @param evt
	 */
	private void jCheckBoxRestOfValuesActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxRestOfValuesActionPerformed

		boolean isSelected = jCheckBoxRestOfValues.isSelected();
		SymbolValueTableModel mod = (SymbolValueTableModel) jTable1.getModel();
		if (isSelected) {
			SymbolValuePOJO poj = new SymbolValuePOJO();
			Symbol sym = createRandomSymbol(constraint);
			Value val = ValueFactory.createNullValue();
			String label = "Default";
			poj.setSym(sym);
			poj.setVal(val);
			poj.setLabel(label);

			((SymbolValueTableModel) jTable1.getModel()).addSymbolValue(poj);
		} else {
			int rowcount = mod.getRowCount();
			for (int i = 0; i < rowcount; i++) {
				String label = (String) mod.getValueAt(i, 2);
				if (label.equals("Default")) {
					mod.deleteSymbolValue(i);
					break;
				}
			}
		}

		dec.setLegend(getLegend());
	}// GEN-LAST:event_jCheckBoxRestOfValuesActionPerformed

	private void jCheckBoxOrderActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxOrderActionPerformed
		SymbolValueTableModel mod = (SymbolValueTableModel) jTable1.getModel();
		mod.setOrdered(jCheckBoxOrder.isSelected());
		jTable1.validate();
		jTable1.repaint();
	}// GEN-LAST:event_jCheckBoxOrderActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddAll;
    private javax.swing.JButton jButtonAddOne;
    private javax.swing.JButton jButtonDel;
    private javax.swing.JCheckBox jCheckBoxOrder;
    private javax.swing.JCheckBox jCheckBoxRestOfValues;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
	// public String toString() {
	// // return "Unique symbol";
	// return identity;
	// }

	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	public Legend getLegend() {

		UniqueValueLegend legend = LegendFactory.createUniqueValueLegend();

		SymbolValueTableModel mod = (SymbolValueTableModel) jTable1.getModel();

		int rowcount = mod.getRowCount();

		for (int i = 0; i < rowcount; i++) {
			SymbolValuePOJO pojo = (SymbolValuePOJO) mod.getValueAt(i, -1);

			Symbol s = pojo.getSym();
			s.setName(pojo.getLabel());
			Value val = ValueFactory.createNullValue();
			try {
				val = pojo.getVal();
			} catch (NumberFormatException e) {
				System.out
						.println("Number Format Exception: " + e.getMessage());
			}

			if (jCheckBoxRestOfValues.isSelected()) {
				if (!pojo.getLabel().equals("Default")) {
					legend.addClassification(val, s);
				} else {
					legend.setDefaultSymbol(pojo.getSym());
				}
			} else {
				legend.addClassification(val, s);
			}
		}
		try {
			legend
					.setClassificationField((String) jComboBox1
							.getSelectedItem());
		} catch (DriverException e) {
			System.out.println("Driver Exception: " + e.getMessage());
		}
		legend.setName(dec.getLegend().getName());

		return legend;
	}

	public void setDecoratorListener(LegendListDecorator dec) {
		this.dec = dec;
	}

}

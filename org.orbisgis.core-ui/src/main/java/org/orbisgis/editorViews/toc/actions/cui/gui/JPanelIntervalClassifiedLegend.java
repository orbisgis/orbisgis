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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.editorViews.toc.actions.cui.gui.factory.LegendPanelFactory;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.ColorPicker;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.LegendListDecorator;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.SymbolIntervalPOJO;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.SymbolIntervalTableModel;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.SymbolValueCellRenderer;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.renderer.classification.RangeMethod;
import org.orbisgis.renderer.legend.DefaultIntervalLegend;
import org.orbisgis.renderer.legend.Interval;
import org.orbisgis.renderer.legend.IntervalLegend;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.LegendFactory;
import org.orbisgis.renderer.legend.NullSymbol;
import org.orbisgis.renderer.legend.Symbol;
import org.orbisgis.renderer.legend.SymbolFactory;
import org.orbisgis.renderer.legend.UniqueSymbolLegend;
import org.sif.UIFactory;

/**
 * 
 * @author david
 */
public class JPanelIntervalClassifiedLegend extends javax.swing.JPanel
		implements ILegendPanelUI {

	private Integer constraint = 0;
	private ILayer layer = null;
	private IntervalLegend leg = null;

	private LegendListDecorator dec = null;

	/**
	 * Constructor of the Interval Classified Legend panel
	 * @param leg The IntervalClassifiedLegend object.
	 * @param constraint The Geometry type of the layer
	 * @param layer The ILayer object of the selected layer.
	 */
	public JPanelIntervalClassifiedLegend(Legend leg, Integer constraint,
			ILayer layer) {
		this.constraint = constraint;
		this.layer = layer;
		this.leg = (IntervalLegend) leg;
		initComponents();
		initCombo();
		initList();
	}

	
	/**
	 * Initializes the combo boxes
	 */
	
	private void initCombo() {

		ArrayList<String> comboValuesArray = new ArrayList<String>();
		try {
			int numFields = layer.getDataSource().getFieldCount();
			for (int i = 0; i < numFields; i++) {
				int fieldType = layer.getDataSource().getFieldType(i)
						.getTypeCode();
				if (fieldType == Type.BYTE || fieldType == Type.SHORT
						|| fieldType == Type.INT || fieldType == Type.LONG
						|| fieldType == Type.FLOAT || fieldType == Type.DOUBLE) {
					comboValuesArray.add(layer.getDataSource().getFieldName(i));
				}
			}
		} catch (DriverException e) {
			System.out.println("Driver Exception: " + e.getMessage());
		}

		String[] comboValues = new String[comboValuesArray.size()];

		comboValues = comboValuesArray.toArray(comboValues);

		DefaultComboBoxModel model = (DefaultComboBoxModel) jComboBoxClasificationField
				.getModel();
		DefaultComboBoxModel modelType = (DefaultComboBoxModel) jComboBoxTypeOfInterval
				.getModel();

		for (int i = 0; i < comboValues.length; i++) {
			model.addElement(comboValues[i]);
		}

		modelType.addElement("Quantiles");
		modelType.addElement("Equivalences");
		modelType.addElement("Moyennes");
		modelType.addElement("Standard");

		modelType.setSelectedItem("Standard");

		String field = leg.getClassificationField();
		jComboBoxClasificationField.setSelectedItem(field);

		jButtonFirstColor.setBackground(Color.BLUE);
		jButtonSecondColor.setBackground(Color.RED);

	}

	/**
	 * Initializes the table and add the listeners
	 */
	private void initList() {
		jTable1.setModel(new SymbolIntervalTableModel());
		jTable1.setRowHeight(25);

		SymbolIntervalTableModel mod = (SymbolIntervalTableModel) jTable1
				.getModel();

		ArrayList<Interval> intervals = ((DefaultIntervalLegend) leg)
				.getIntervals();

		jTable1.setDefaultRenderer(Symbol.class, new SymbolValueCellRenderer());

		DefaultIntervalLegend dleg = (DefaultIntervalLegend) leg;
		for (int i = 0; i < intervals.size(); i++) {
			Symbol sOfInterval = dleg.getSymbolFor(intervals.get(i)
					.getMinValue());

			SymbolIntervalPOJO poj = new SymbolIntervalPOJO();
			poj.setSym(sOfInterval);
			poj.setVal(intervals.get(i));
			poj.setLabel(sOfInterval.getName());
			mod.addSymbolValue(poj);
		}

		if (!(leg.getDefaultSymbol() instanceof NullSymbol)) {
			jCheckBoxRestOfValues.setSelected(true);

			SymbolIntervalPOJO poj = new SymbolIntervalPOJO();
			poj.setSym(leg.getDefaultSymbol());
			poj.setVal(new Interval(ValueFactory.createNullValue(), false,
					ValueFactory.createNullValue(), false));
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
						int row = jTable1.getSelectedRow();
						SymbolIntervalTableModel mod = (SymbolIntervalTableModel) jTable1
								.getModel();
						UniqueSymbolLegend usl = LegendFactory
								.createUniqueSymbolLegend();
						usl.setSymbol((Symbol) mod.getValueAt(row, 0));
						JPanelUniqueSymbolLegend jpusl = (JPanelUniqueSymbolLegend)LegendPanelFactory.createPanel(LegendPanelFactory.UNIQUE_SYMBOL_LEGEND,
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

	/**
	 * When any data of the table changes we update the external legend
	 * by the decorator.
	 * @param e
	 */
	protected void jTable1Changed(TableModelEvent e) {
		dec.setLegend(getLegend());
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelClasifField = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxClasificationField = new javax.swing.JComboBox();
        jPanelSecond = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxTypeOfInterval = new javax.swing.JComboBox();
        jCheckBoxRestOfValues = new javax.swing.JCheckBox();
        jPanelThird = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jButtonFirstColor = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jButtonSecondColor = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jPanelTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanelButtons = new javax.swing.JPanel();
        jButtonCalculeIntervals = new javax.swing.JButton();
        jButtonAdd = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(450, 308));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Classification field:");
        jPanelClasifField.add(jLabel1);

        jComboBoxClasificationField.setMinimumSize(new java.awt.Dimension(200, 19));
        jComboBoxClasificationField.setPreferredSize(new java.awt.Dimension(200, 19));
        jComboBoxClasificationField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxClasificationFieldActionPerformed(evt);
            }
        });
        jPanelClasifField.add(jComboBoxClasificationField);

        add(jPanelClasifField);

        jLabel2.setText("Type of interval: ");
        jPanelSecond.add(jLabel2);

        jComboBoxTypeOfInterval.setMinimumSize(new java.awt.Dimension(100, 19));
        jComboBoxTypeOfInterval.setPreferredSize(new java.awt.Dimension(100, 19));
        jComboBoxTypeOfInterval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTypeOfIntervalActionPerformed(evt);
            }
        });
        jPanelSecond.add(jComboBoxTypeOfInterval);

        jCheckBoxRestOfValues.setText("Rest of values");
        jCheckBoxRestOfValues.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxRestOfValues.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxRestOfValuesActionPerformed(evt);
            }
        });
        jPanelSecond.add(jCheckBoxRestOfValues);

        add(jPanelSecond);

        jLabel3.setText("First color:");
        jPanelThird.add(jLabel3);

        jButtonFirstColor.setMaximumSize(new java.awt.Dimension(19, 19));
        jButtonFirstColor.setMinimumSize(new java.awt.Dimension(19, 19));
        jButtonFirstColor.setPreferredSize(new java.awt.Dimension(19, 19));
        jButtonFirstColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFirstColorActionPerformed(evt);
            }
        });
        jPanelThird.add(jButtonFirstColor);

        jLabel4.setText("Final color:");
        jPanelThird.add(jLabel4);

        jButtonSecondColor.setMaximumSize(new java.awt.Dimension(19, 19));
        jButtonSecondColor.setMinimumSize(new java.awt.Dimension(19, 19));
        jButtonSecondColor.setPreferredSize(new java.awt.Dimension(19, 19));
        jButtonSecondColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSecondColorActionPerformed(evt);
            }
        });
        jPanelThird.add(jButtonSecondColor);

        jLabel5.setText("N. Intervals:");
        jPanelThird.add(jLabel5);

        jComboBox1.setMinimumSize(new java.awt.Dimension(75, 19));
        jComboBox1.setPreferredSize(new java.awt.Dimension(75, 19));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanelThird.add(jComboBox1);

        add(jPanelThird);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(454, 120));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Symbol", "Value", "Label"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jPanelTable.add(jScrollPane1);

        add(jPanelTable);

        jButtonCalculeIntervals.setText("Calcule intervals");
        jButtonCalculeIntervals.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCalculeIntervalsActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonCalculeIntervals);

        jButtonAdd.setText("Add");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonAdd);

        jButtonDelete.setText("Delete");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonDelete);

        add(jPanelButtons);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The event that is executed when the button of final color is pressed.
     * @param evt
     */
	private void jButtonSecondColorActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSecondColorActionPerformed
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			jButtonSecondColor.setBackground(color);
			dec.setLegend(getLegend());
		}
	}// GEN-LAST:event_jButtonSecondColorActionPerformed

	/**
     * The event that is executed when the button of initial color is pressed.
     * @param evt
     */
	private void jButtonFirstColorActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonFirstColorActionPerformed
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			jButtonFirstColor.setBackground(color);
			dec.setLegend(getLegend());
		}
	}// GEN-LAST:event_jButtonFirstColorActionPerformed

	/**
	 * Creates the number of intervals according to the type
	 * @param evt
	 */
	private void jComboBoxTypeOfIntervalActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxTypeOfIntervalActionPerformed
		int idx = jComboBoxTypeOfInterval.getSelectedIndex();
		DefaultComboBoxModel mod = new DefaultComboBoxModel();
		switch (idx) {
		case 0: // Quantiles
		case 1: // Equivalences
			mod.addElement(1);
			mod.addElement(2);
			mod.addElement(3);
			mod.addElement(4);
			mod.addElement(5);
			mod.addElement(6);
			mod.addElement(7);
			mod.addElement(8);
			mod.addElement(9);
			mod.addElement(10);
			mod.addElement(11);
			break;
		case 2: // Moyennes
			mod.addElement(2);
			mod.addElement(4);
			mod.addElement(8);
			break;
		case 3: // Standard
			mod.addElement(3);
			mod.addElement(5);
			mod.addElement(7);
			break;
		}

		jComboBox1.setModel(mod);
	}// GEN-LAST:event_jComboBoxTypeOfIntervalActionPerformed

	private void jComboBoxClasificationFieldActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxClasificationFieldActionPerformed

	}// GEN-LAST:event_jComboBoxClasificationFieldActionPerformed

	private void jButtonCalculeIntervalsActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonCalculeIntervalsActionPerformed
		RangeMethod rm = null;
		try {
			rm = new RangeMethod(layer.getDataSource(),
					(String) jComboBoxClasificationField.getSelectedItem(),
					(Integer) jComboBox1.getSelectedItem());

			int typeOfIntervals = jComboBoxTypeOfInterval.getSelectedIndex();

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
			System.out.println("Driver Exception: " + e.getMessage());
			return;
		}

		// Range[] ranges = rm.getRanges();
		Interval[] intervals = null;
		try {
			intervals = rm.getIntervals();
		} catch (NumberFormatException e) {
			System.out.println("NumberFormatException: " + e.getMessage());
			return;
		} catch (ParseException e) {
			System.out.println("ParseException: " + e.getMessage());
			return;
		}

		SymbolIntervalTableModel mod = (SymbolIntervalTableModel) jTable1
				.getModel();

		mod.removeAll();

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
			// Range r = ranges[i];
			Interval inter = intervals[i];

			Symbol s = createSymbol(constraint, color);

			SymbolIntervalPOJO poj = new SymbolIntervalPOJO();
			poj.setSym(s);
			poj.setVal(inter);
			poj.setLabel(inter.getIntervalString());

			mod.addSymbolValue(poj);

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

	}// GEN-LAST:event_jButtonCalculeIntervalsActionPerformed

	/**
	 * Creates a default symbol (black outline and randomized fill).
	 * @param constraint
	 * @return Symbol
	 */
	protected Symbol createDefaultSymbol(Integer constraint) {
		Symbol s;

		Random rand = new Random();

		int r1 = 0;
		int r2 = rand.nextInt(255);
		int g1 = 0;
		int g2 = rand.nextInt(255);
		int b1 = 0;
		int b2 = rand.nextInt(255);

		Color outline = new Color(r1, g1, b1);
		Color fill = new Color(r2, g2, b2);

		if (constraint != null) {
			switch (constraint) {
			case GeometryConstraint.LINESTRING:
			case GeometryConstraint.MULTI_LINESTRING:
				Stroke stroke = new BasicStroke(1);
				s = SymbolFactory.createLineSymbol(outline,
						(BasicStroke) stroke);
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
				Symbol sl = createDefaultSymbol(GeometryConstraint.LINESTRING);
				Symbol sc = createDefaultSymbol(GeometryConstraint.POINT);
				Symbol sp = createDefaultSymbol(GeometryConstraint.POLYGON);
				Symbol[] arraySym = { sl, sc, sp };

				s = SymbolFactory.createSymbolComposite(arraySym);
				break;
			}
		} else {
			Symbol sl = createDefaultSymbol(GeometryConstraint.LINESTRING);
			Symbol sc = createDefaultSymbol(GeometryConstraint.POINT);
			Symbol sp = createDefaultSymbol(GeometryConstraint.POLYGON);
			Symbol[] arraySym = { sl, sc, sp };

			s = SymbolFactory.createSymbolComposite(arraySym);
		}
		return s;
	}

	
	/**
	 * Creates the symbol with the specified fillColor and with a black outline
	 * @param constraint
	 * @param fillColor
	 * @return Symbol
	 */
	protected Symbol createSymbol(Integer constraint, Color fillColor) {
		Symbol s;

		Stroke stroke = new BasicStroke(1);
		Color outline = Color.black;

		if (constraint != null) {
			switch (constraint) {
			case GeometryConstraint.LINESTRING:
			case GeometryConstraint.MULTI_LINESTRING:
				s = SymbolFactory.createLineSymbol(fillColor,
						(BasicStroke) stroke);
				break;
			case GeometryConstraint.POINT:
			case GeometryConstraint.MULTI_POINT:
				int size = 10;
				s = SymbolFactory.createCirclePointSymbol(outline, fillColor,
						size);
				break;
			case GeometryConstraint.POLYGON:
			case GeometryConstraint.MULTI_POLYGON:
				s = SymbolFactory.createPolygonSymbol(stroke, outline,
						fillColor);
				break;
			default:
				Symbol sl = createSymbol(GeometryConstraint.LINESTRING,
						fillColor);
				Symbol sc = createSymbol(GeometryConstraint.POINT,
						fillColor);
				Symbol sp = createSymbol(GeometryConstraint.POLYGON,
						fillColor);
				Symbol[] arraySym = { sl, sc, sp };

				s = SymbolFactory.createSymbolComposite(arraySym);
				break;
			}
		} else {
			Symbol sl = createSymbol(GeometryConstraint.LINESTRING,
					fillColor);
			Symbol sc = createSymbol(GeometryConstraint.POINT, fillColor);
			Symbol sp = createSymbol(GeometryConstraint.POLYGON,
					fillColor);
			Symbol[] arraySym = { sl, sc, sp };

			s = SymbolFactory.createSymbolComposite(arraySym);
		}
		return s;
	}

	/**
	 * adds a new row that will be a copy of the last one or a default line with 
	 * NullInterval if the table is empty.
	 * @param evt
	 */
	private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonAddActionPerformed
		SymbolIntervalTableModel mod = (SymbolIntervalTableModel) jTable1
				.getModel();

		int rowCount = mod.getRowCount();

		SymbolIntervalPOJO poj = new SymbolIntervalPOJO();
		if (rowCount < 32) {
			Symbol sym = SymbolFactory.createNullSymbol();
			Interval val = null;
			String label = "";
			if (rowCount > 0) {
				sym = (Symbol) mod.getValueAt(rowCount - 1, 0);
				val = (Interval) mod.getValueAt(rowCount - 1, 3);
				label = (String) mod.getValueAt(rowCount - 1, 2);
			}
			poj.setSym(sym);
			poj.setVal(val);
			poj.setLabel(label);
		} else {
			JOptionPane.showMessageDialog(this,
					"More than 32 different intervals found. Showing only 32");
		}

		((SymbolIntervalTableModel) jTable1.getModel()).addSymbolValue(poj);
	}// GEN-LAST:event_jButtonAddActionPerformed

	/**
	 * Delete the selected rows.
	 * @param evt
	 */
	private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonDeleteActionPerformed
		SymbolIntervalTableModel mod = (SymbolIntervalTableModel) jTable1
				.getModel();
		int[] rows = jTable1.getSelectedRows();
		while (rows.length > 0) {
			mod.deleteSymbolValue(rows[0]);
			rows = jTable1.getSelectedRows();
		}
	}// GEN-LAST:event_jButtonDeleteActionPerformed

	/**
	 * This event is fired when the CB Rest of Values is pressed.
	 * It creates a new Row with a default value or deletes the previous created.
	 * @param evt
	 */
	private void jCheckBoxRestOfValuesActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxRestOfValuesActionPerformed

		boolean isSelected = jCheckBoxRestOfValues.isSelected();
		SymbolIntervalTableModel mod = (SymbolIntervalTableModel) jTable1
				.getModel();
		if (isSelected) {
			SymbolIntervalPOJO poj = new SymbolIntervalPOJO();
			Symbol sym = createDefaultSymbol(constraint);
			Interval val = new Interval(ValueFactory.createNullValue(), false,
					ValueFactory.createNullValue(), false);
			String label = "Default";
			poj.setSym(sym);
			poj.setVal(val);
			poj.setLabel(label);

			((SymbolIntervalTableModel) jTable1.getModel()).addSymbolValue(poj);
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

	private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBox1ActionPerformed
		// TODO add your handling code here:
	}// GEN-LAST:event_jComboBox1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonCalculeIntervals;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonFirstColor;
    private javax.swing.JButton jButtonSecondColor;
    private javax.swing.JCheckBox jCheckBoxRestOfValues;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBoxClasificationField;
    private javax.swing.JComboBox jComboBoxTypeOfInterval;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelClasifField;
    private javax.swing.JPanel jPanelSecond;
    private javax.swing.JPanel jPanelTable;
    private javax.swing.JPanel jPanelThird;
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
		IntervalLegend legend = LegendFactory.createIntervalLegend();

		SymbolIntervalTableModel mod = (SymbolIntervalTableModel) jTable1
				.getModel();

		for (int i = 0; i < mod.getRowCount(); i++) {
			SymbolIntervalPOJO pojo = (SymbolIntervalPOJO) mod
					.getValueAt(i, -1);

			Symbol s = pojo.getSym();
			s.setName(pojo.getLabel());

			if (jCheckBoxRestOfValues.isSelected()) {
				if (!pojo.getLabel().equals("Default")) {
					legend.addInterval(pojo.getVal().getMinValue(), true, pojo
							.getVal().getMaxValue(), false, s);
				} else {
					legend.setDefaultSymbol(pojo.getSym());
				}
			} else {
				legend.addInterval(pojo.getVal().getMinValue(), true, pojo
						.getVal().getMaxValue(), false, s);
			}

		}
		try {
			legend.setClassificationField((String) jComboBoxClasificationField
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

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

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;

import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.Canvas;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.ColorPicker;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.LegendListDecorator;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.carto.LegendFactory;
import org.orbisgis.renderer.legend.carto.ProportionalLegend;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.SymbolFactory;
import org.sif.UIFactory;

/**
 *
 * @author david
 */
public class JPanelProportionalLegend extends javax.swing.JPanel implements
		ILegendPanelUI {

	private ILayer layer = null;
	private ProportionalLegend leg = null;
	private Canvas canvas = null;

	private LegendListDecorator dec = null;

	public JPanelProportionalLegend(Legend leg, int constraint, ILayer layer) {
		this.layer = layer;
		this.leg = (ProportionalLegend) leg;
		initComponents();
		initCombo();
		setCanvas();
		refreshCanvas();
	}

	private void setCanvas() {
		canvas = new Canvas();
		jPanel1.add(canvas);
	}

	/**
	 * refresh the preview with the new values of the legend.
	 */
	private void refreshCanvas() {
		Symbol sym = createDefaultSymbol();
		canvas.setSymbol(sym);
		canvas.validate();
		canvas.repaint();

		if (dec != null) {
			dec.setLegend(getLegend());
		}
	}

	/**
	 * Initializes the combo box.
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

		jComboBoxClasificationField.setModel(new DefaultComboBoxModel(
				comboValues));
		jComboBoxMethod.setModel(new DefaultComboBoxModel());

		DefaultComboBoxModel modelType = (DefaultComboBoxModel) jComboBoxMethod
				.getModel();

		modelType.addElement("Linear");
		modelType.addElement("Logarithmic");
		modelType.addElement("Square");

		modelType.setSelectedItem("Linear");

		String field = leg.getClassificationField();
		jComboBoxClasificationField.setSelectedItem(field);

		jButtonFirstColor.setBackground(leg.getOutlineColor());
		jButtonSecondColor.setBackground(leg.getFillColor());

	}

	public Legend getLegend() {
		ProportionalLegend leg = LegendFactory.createProportionalLegend();

		leg.setFillColor(jButtonSecondColor.getBackground());
		leg.setOutlineColor(jButtonFirstColor.getBackground());
		leg.setMinSymbolArea(Integer.parseInt(jTextFieldArea.getText()));
		try {
			int method = jComboBoxMethod.getSelectedIndex();
			switch (method) {
			case 0:
				leg.setLinearMethod();
				break;
			case 1:
				leg.setLogarithmicMethod();
				break;
			case 2:
				// TODO what is the Sqr Factor???
				leg.setSquareMethod(2);
				break;
			}

			leg.setClassificationField((String) jComboBoxClasificationField
					.getSelectedItem());
		} catch (DriverException e) {
			System.out.println("Driver Exception");
		}

		return leg;
	}

	protected Symbol createDefaultSymbol() {
		Symbol s;

		Color outline = jButtonFirstColor.getBackground();
		Color fillColor = jButtonSecondColor.getBackground();
		int size = 25;
		s = SymbolFactory.createCirclePointSymbol(outline, fillColor, size);

		return s;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed" desc="Generated
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jPanelFirstLine = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jComboBoxClasificationField = new javax.swing.JComboBox();
		jPanelSecondLine = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		jComboBoxMethod = new javax.swing.JComboBox();
		jPanelThirdLine = new javax.swing.JPanel();
		jPanelThirdLeft = new javax.swing.JPanel();
		jPanelFirstColor = new javax.swing.JPanel();
		jLabel3 = new javax.swing.JLabel();
		jButtonFirstColor = new javax.swing.JButton();
		jPanelSecondColor = new javax.swing.JPanel();
		jLabel4 = new javax.swing.JLabel();
		jButtonSecondColor = new javax.swing.JButton();
		jPanelArea = new javax.swing.JPanel();
		jLabel5 = new javax.swing.JLabel();
		jTextFieldArea = new javax.swing.JTextField();
		jPanel1 = new javax.swing.JPanel();

		setLayout(new javax.swing.BoxLayout(this,
				javax.swing.BoxLayout.PAGE_AXIS));

		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		jLabel1.setText("Classification field:");
		jPanelFirstLine.add(jLabel1);

		jComboBoxClasificationField.setPreferredSize(new java.awt.Dimension(
				225, 19));
		jComboBoxClasificationField
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jComboBoxClasificationFieldActionPerformed(evt);
					}
				});
		jPanelFirstLine.add(jComboBoxClasificationField);

		add(jPanelFirstLine);

		jLabel2.setText("Method:");
		jPanelSecondLine.add(jLabel2);

		jComboBoxMethod.setPreferredSize(new java.awt.Dimension(225, 19));
		jComboBoxMethod.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jComboBoxMethodActionPerformed(evt);
			}
		});
		jPanelSecondLine.add(jComboBoxMethod);

		add(jPanelSecondLine);

		jPanelThirdLeft.setLayout(new javax.swing.BoxLayout(jPanelThirdLeft,
				javax.swing.BoxLayout.PAGE_AXIS));

		jLabel3.setText("Line color:");
		jPanelFirstColor.add(jLabel3);

		jButtonFirstColor.setMaximumSize(new java.awt.Dimension(20, 20));
		jButtonFirstColor.setMinimumSize(new java.awt.Dimension(20, 20));
		jButtonFirstColor.setPreferredSize(new java.awt.Dimension(20, 20));
		jButtonFirstColor
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jButtonFirstColorActionPerformed(evt);
					}
				});
		jPanelFirstColor.add(jButtonFirstColor);

		jPanelThirdLeft.add(jPanelFirstColor);

		jLabel4.setText("Fill color:");
		jPanelSecondColor.add(jLabel4);

		jButtonSecondColor.setMaximumSize(new java.awt.Dimension(20, 20));
		jButtonSecondColor.setMinimumSize(new java.awt.Dimension(20, 20));
		jButtonSecondColor.setPreferredSize(new java.awt.Dimension(20, 20));
		jButtonSecondColor
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jButtonSecondColorActionPerformed(evt);
					}
				});
		jPanelSecondColor.add(jButtonSecondColor);

		jPanelThirdLeft.add(jPanelSecondColor);

		jLabel5.setText("Area: ");
		jPanelArea.add(jLabel5);

		jTextFieldArea.setText("1000");
		jTextFieldArea.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jTextFieldAreaActionPerformed(evt);
			}
		});
		jPanelArea.add(jTextFieldArea);

		jPanelThirdLeft.add(jPanelArea);

		jPanelThirdLine.add(jPanelThirdLeft);

		jPanel1.setPreferredSize(new java.awt.Dimension(130, 78));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 130,
				Short.MAX_VALUE));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 78,
				Short.MAX_VALUE));

		jPanelThirdLine.add(jPanel1);

		add(jPanelThirdLine);
	}// </editor-fold>//GEN-END:initComponents

	private void jComboBoxClasificationFieldActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxClasificationFieldActionPerformed
		if (dec != null)
			dec.setLegend(getLegend());
	}// GEN-LAST:event_jComboBoxClasificationFieldActionPerformed

	private void jComboBoxMethodActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxMethodActionPerformed
		if (dec != null)
			dec.setLegend(getLegend());
	}// GEN-LAST:event_jComboBoxMethodActionPerformed

	private void jTextFieldAreaActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextFieldAreaActionPerformed
		if (dec != null)
			dec.setLegend(getLegend());
	}// GEN-LAST:event_jTextFieldAreaActionPerformed

	private void jButtonSecondColorActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSecondColorActionPerformed
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			jButtonSecondColor.setBackground(color);
			refreshCanvas();
		}
	}// GEN-LAST:event_jButtonSecondColorActionPerformed

	private void jButtonFirstColorActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonFirstColorActionPerformed
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			jButtonFirstColor.setBackground(color);
			refreshCanvas();
		}
	}// GEN-LAST:event_jButtonFirstColorActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jButtonFirstColor;
	private javax.swing.JButton jButtonSecondColor;
	private javax.swing.JComboBox jComboBoxClasificationField;
	private javax.swing.JComboBox jComboBoxMethod;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanelArea;
	private javax.swing.JPanel jPanelFirstColor;
	private javax.swing.JPanel jPanelFirstLine;
	private javax.swing.JPanel jPanelSecondColor;
	private javax.swing.JPanel jPanelSecondLine;
	private javax.swing.JPanel jPanelThirdLeft;
	private javax.swing.JPanel jPanelThirdLine;
	private javax.swing.JTextField jTextFieldArea;

	// End of variables declaration//GEN-END:variables

	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	public void setDecoratorListener(LegendListDecorator dec) {
		this.dec = dec;
	}

	public boolean acceptsGeometryType(int geometryType) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getLegendTypeName() {
		// TODO Auto-generated method stub
		return null;
	}

	public ILegendPanelUI newInstance(LegendContext legendContext) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setLegend(Legend legend) {
		// TODO Auto-generated method stub

	}

	public void setLegendContext(LegendContext lc) {
		// TODO Auto-generated method stub

	}

	public String validateInput() {
		// TODO Auto-generated method stub
		return null;
	}

}
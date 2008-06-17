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
 * JPanelLegend.java
 *
 * Created on 22 de febrero de 2008, 15:36
 */

package org.orbisgis.editorViews.toc.actions.cui.gui;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.gdms.data.types.GeometryConstraint;
import org.gdms.driver.DriverException;
import org.orbisgis.editorViews.toc.actions.cui.gui.factory.LegendPanelFactory;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.ImageLegend;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.JPanelComboLegendPicker;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.LegendListDecorator;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.renderer.legend.IntervalLegend;
import org.orbisgis.renderer.legend.LabelLegend;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.LegendFactory;
import org.orbisgis.renderer.legend.ProportionalLegend;
import org.orbisgis.renderer.legend.Symbol;
import org.orbisgis.renderer.legend.SymbolFactory;
import org.orbisgis.renderer.legend.UniqueSymbolLegend;
import org.orbisgis.renderer.legend.UniqueValueLegend;
import org.orbisgis.ui.sif.AskValue;
import org.sif.UIFactory;
import org.sif.UIPanel;

/**
 *
 * @author david
 */
public class JPanelLegendList extends javax.swing.JPanel implements UIPanel {

	String infoText = "Legend";
	String title = "legend";
	Integer constraint;
	Legend[] legendC = null;
	ILayer layer = null;

	private static final int UNIQUESYMBOL = 1;
	private static final int INTERVAL = 2;
	private static final int LABEL = 3;
	private static final int PROPORTIONAL = 4;
	private static final int UNIQUEVALUE = 5;
	private static final int UNKNOWN = 9;

	/**
	 * Creates new form JPanelLegend
	 *
	 * @param paneles
	 *            is the list of the possible panels that you could open.
	 * @param nombres
	 *            is the list of names of the panes that you have in the actual
	 *            layer. This panel names will be shown on the left list.
	 *
	 */

	public JPanelLegendList(Integer geomConstraint, ILayer layer) {
		this.constraint = geomConstraint;
		Legend[] leg = null;
		try {
			leg = layer.getLegend();
		} catch (DriverException e) {
			System.out.println("Driver Exception: " + e.getMessage());
		}

		this.legendC = leg;
		this.layer = layer;
		initComponents();
		// jPanel2.setSize(jPanel2.getPreferredSize());
		initList();
		// refreshButtons();
	}

	private void initList() {
		Legend[] legs = legendC;
		jList1.setModel(new DefaultListModel());
		DefaultListModel mod = (DefaultListModel) jList1.getModel();

		for (int i = 0; i < legs.length; i++) {
			mod.addElement(new LegendListDecorator(legs[i]));
		}

		if (legs.length > 0)
			jList1.setSelectedIndex(0);

	}

	private int getLegendType(Legend legend) {
		if (legend instanceof UniqueSymbolLegend) {
			return UNIQUESYMBOL;
		}
		if (legend instanceof IntervalLegend) {
			return INTERVAL;
		}
		if (legend instanceof LabelLegend) {
			return LABEL;
		}
		if (legend instanceof ProportionalLegend) {
			return PROPORTIONAL;
		}
		if (legend instanceof UniqueValueLegend) {
			return UNIQUEVALUE;
		}
		return UNKNOWN;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jCheckBox1 = new javax.swing.JCheckBox();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jTextField1 = new javax.swing.JTextField();
		jTextField2 = new javax.swing.JTextField();
		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		jPanel2 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jList1 = new javax.swing.JList();
		jToolBar1 = new javax.swing.JToolBar();
		jButtonMenuUp = new javax.swing.JButton();
		jButtonMenuDown = new javax.swing.JButton();
		jButtonMenuAdd = new javax.swing.JButton();
		jButtonMenuDel = new javax.swing.JButton();
		jButtonMenuRename = new javax.swing.JButton();

		jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		jCheckBox1.setText("Only show layer when scale is between");
		jCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0,
				0, 0));
		jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCheckBox1ActionPerformed(evt);
			}
		});

		jLabel1.setText("Minium scale 1:");

		jLabel2.setText("Maxium scale 1:");

		jTextField1.setHorizontalAlignment(javax.swing.JTextField.LEFT);

		jButton1.setText("Current scale");

		jButton2.setText("Current scale");

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jCheckBox1)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING)
																						.addComponent(
																								jLabel2,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								133,
																								Short.MAX_VALUE)
																						.addComponent(
																								jLabel1,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								133,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING,
																								false)
																						.addComponent(
																								jTextField2)
																						.addComponent(
																								jTextField1,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								232,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING,
																								false)
																						.addComponent(
																								jButton2,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								jButton1,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								203,
																								Short.MAX_VALUE))))
										.addContainerGap()));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jCheckBox1)
										.addGap(10, 10, 10)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jButton1)
														.addComponent(
																jTextField1,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																23,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel1))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jButton2)
														.addComponent(
																jTextField2,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																22,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel2))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		jPanel2.setBorder(null);
		jPanel2.setMinimumSize(new java.awt.Dimension(613, 309));
		jPanel2.setPreferredSize(new java.awt.Dimension(613, 309));

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 614,
				Short.MAX_VALUE));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 309,
				Short.MAX_VALUE));

		jList1
				.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		jList1
				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						jList1ValueChanged(evt);
					}
				});
		jScrollPane1.setViewportView(jList1);

		jToolBar1.setFloatable(false);

		jButtonMenuUp.setText("up");
		jButtonMenuUp.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonMenuUpActionPerformed(evt);
			}
		});
		jToolBar1.add(jButtonMenuUp);

		jButtonMenuDown.setText("down");
		jButtonMenuDown.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonMenuDownActionPerformed(evt);
			}
		});
		jToolBar1.add(jButtonMenuDown);

		jButtonMenuAdd.setText("add");
		jButtonMenuAdd.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonMenuAddActionPerformed(evt);
			}
		});
		jToolBar1.add(jButtonMenuAdd);

		jButtonMenuDel.setText("del");
		jButtonMenuDel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonMenuDelActionPerformed(evt);
			}
		});
		jToolBar1.add(jButtonMenuDel);

		jButtonMenuRename.setText("rename");
		jButtonMenuRename
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jButtonMenuRenameActionPerformed(evt);
					}
				});
		jToolBar1.add(jButtonMenuRename);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jScrollPane1,
																0, 0,
																Short.MAX_VALUE)
														.addComponent(
																jToolBar1,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jPanel1,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																jPanel2,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																614,
																Short.MAX_VALUE))
										.addContainerGap()));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jPanel2,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jPanel1,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE))
														.addGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jToolBar1,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				29,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jScrollPane1,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				400,
																				Short.MAX_VALUE)))
										.addContainerGap()));
	}// </editor-fold>//GEN-END:initComponents

	private void jButtonMenuRenameActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonMenuRenameActionPerformed
		DefaultListModel mod = (DefaultListModel) jList1.getModel();
		LegendListDecorator dec = (LegendListDecorator) jList1
				.getSelectedValue();
		int idx = jList1.getSelectedIndex();

		// String new_name=JOptionPane.showInputDialog("Insert the new name",
		// dec.getLegend().getLegendTypeName());
		AskValue ask = new AskValue("Insert the new name", "txt is not null",
				"A name must be specified", dec.getLegend().getName());
		String new_name = "";
		if (UIFactory.showDialog(ask)) {
			new_name = ask.getValue();
		}

		if (new_name != null && new_name != "") {
			dec.getLegend().setName(new_name);
			mod.remove(idx);
			mod.add(idx, dec);
			jList1.setSelectedIndex(idx);

			refreshButtons();
		}
	}// GEN-LAST:event_jButtonMenuRenameActionPerformed

	private void jButtonMenuDelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonMenuDelActionPerformed
		DefaultListModel mod = (DefaultListModel) jList1.getModel();
		int idx = jList1.getSelectedIndex();
		mod.remove(idx);
		if (mod.size() > 0)
			jList1.setSelectedIndex(0);
		else {
			jPanel2.removeAll();
			jPanel2.validate();
			jPanel2.repaint();
		}

		refreshButtons();
	}// GEN-LAST:event_jButtonMenuDelActionPerformed

	private void jButtonMenuAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonMenuAddActionPerformed
		ArrayList<String> paneNames = new ArrayList<String>();
		paneNames.add("Unique symbol legend");
		paneNames.add("Unique value legend");
		paneNames.add("Interval classified legend");
		if (constraint != GeometryConstraint.LINESTRING
				&& constraint != GeometryConstraint.MULTI_LINESTRING) {
			paneNames.add("Proportional legend");
		}
		paneNames.add("Label legend");
		// show the combo legend picker
		JPanelComboLegendPicker legendPicker = new JPanelComboLegendPicker(
				paneNames.toArray());

		if (!UIFactory.showDialog(legendPicker)) {
			return;
		}

		String value = legendPicker.getSelected();

		DefaultListModel mod = (DefaultListModel) jList1.getModel();

		Legend leg = null;
		if (value.equals("Unique symbol legend")) {
			Symbol symbol = SymbolFactory.createSymbolComposite();
			leg = LegendFactory.createUniqueSymbolLegend();
			((UniqueSymbolLegend) leg).setSymbol(symbol);
		}
		if (value.equals("Unique value legend")) {
			leg = LegendFactory.createUniqueValueLegend();
		}
		if (value.equals("Interval classified legend")) {
			leg = LegendFactory.createIntervalLegend();
		}
		if (value.equals("Proportional legend")) {
			leg = LegendFactory.createProportionalLegend();
		}
		if (value.equals("Label legend")) {
			leg = LegendFactory.createLabelLegend();
		}
		// leg.setName(value);

		LegendListDecorator dec = new LegendListDecorator(leg);

		mod.addElement(dec);

		jList1.setSelectedIndex(mod.getSize() - 1);

		refreshButtons();

	}// GEN-LAST:event_jButtonMenuAddActionPerformed

	private void jButtonMenuDownActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonMenuDownActionPerformed
		DefaultListModel mod = (DefaultListModel) jList1.getModel();
		int idx = 0;
		idx = jList1.getSelectedIndex();
		if (idx < mod.size() - 1) {
			LegendListDecorator element = (LegendListDecorator) mod.get(idx);
			mod.remove(idx);
			mod.add(idx + 1, element);
		}
		jList1.setSelectedIndex(idx + 1);

		refreshButtons();
	}// GEN-LAST:event_jButtonMenuDownActionPerformed

	private void jButtonMenuUpActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonMenuUpActionPerformed
		DefaultListModel mod = (DefaultListModel) jList1.getModel();
		int idx = 0;
		idx = jList1.getSelectedIndex();
		if (idx > 0) {
			LegendListDecorator element = (LegendListDecorator) mod.get(idx);
			mod.remove(idx);
			mod.add(idx - 1, element);
		}
		jList1.setSelectedIndex(idx - 1);

		refreshButtons();

	}// GEN-LAST:event_jButtonMenuUpActionPerformed

	private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_jList1ValueChanged

		LegendListDecorator dec = (LegendListDecorator) jList1
				.getSelectedValue();

		if (dec != null) {
			int legType = getLegendType(dec.getLegend());

			ILegendPanelUI pan = null;

			switch (legType) {
			case UNIQUESYMBOL:
				pan = LegendPanelFactory.createPanel(LegendPanelFactory.UNIQUE_SYMBOL_LEGEND, (UniqueSymbolLegend) dec
						.getLegend(), constraint, null, true);
				break;
			case UNIQUEVALUE:
				pan = LegendPanelFactory.createPanel(LegendPanelFactory.UNIQUE_VALUE_LEGEND, (UniqueValueLegend) dec
						.getLegend(), constraint, layer, false);
				break;
			case INTERVAL:
				pan = LegendPanelFactory.createPanel(LegendPanelFactory.INTERVAL_LEGEND, (IntervalLegend) dec
						.getLegend(), constraint, layer, false);
				break;
			case PROPORTIONAL:
				pan = LegendPanelFactory.createPanel(LegendPanelFactory.PROPORTIONAL_LEGEND, (ProportionalLegend) dec
						.getLegend(), constraint, layer, false);
				break;
			case LABEL:
				pan = LegendPanelFactory.createPanel(LegendPanelFactory.LABEL_LEGEND, (LabelLegend) dec
						.getLegend(), constraint, layer, false);
				break;
			default:
				break;
			}

			if (pan != null && dec != null) {
				pan.setDecoratorListener(dec);
				jPanel2.removeAll();
				JPanel comp = (JPanel) pan.getComponent();
				// System.out.println(jPanel2.getSize().height+","+jPanel2.getSize().width);
				comp.setSize(jPanel2.getMinimumSize());
				comp.setBorder(new EtchedBorder());
				jPanel2.add(comp);
				jPanel2.validate();
				jPanel2.repaint();
			}
		}

		refreshButtons();
	}// GEN-LAST:event_jList1ValueChanged

	private void refreshButtons() {
		int idx = jList1.getSelectedIndex();
		int maximo = jList1.getModel().getSize() - 1;
		int minimo = 0;

		if (idx == -1) {
			jButtonMenuUp.setEnabled(false);
			jButtonMenuDown.setEnabled(false);
			jButtonMenuDel.setEnabled(false);
			jButtonMenuRename.setEnabled(false);
		} else {
			jButtonMenuDel.setEnabled(true);
			jButtonMenuRename.setEnabled(true);
			if (idx == minimo) {
				if (idx == maximo)
					jButtonMenuDown.setEnabled(false);
				else
					jButtonMenuDown.setEnabled(true);
				jButtonMenuUp.setEnabled(false);
			} else {
				if (idx == maximo) {
					jButtonMenuUp.setEnabled(true);
					jButtonMenuDown.setEnabled(false);
				} else {
					jButtonMenuUp.setEnabled(true);
					jButtonMenuDown.setEnabled(true);
				}
			}
		}

	}

	private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {

	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JButton jButtonMenuAdd;
	private javax.swing.JButton jButtonMenuDel;
	private javax.swing.JButton jButtonMenuDown;
	private javax.swing.JButton jButtonMenuRename;
	private javax.swing.JButton jButtonMenuUp;
	private javax.swing.JCheckBox jCheckBox1;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JList jList1;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JTextField jTextField2;
	private javax.swing.JToolBar jToolBar1;

	// End of variables declaration//GEN-END:variables
	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	public URL getIconURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInfoText() {
		// TODO Auto-generated method stub
		return infoText;
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return title;
	}

	public String initialize() {
		// TODO Auto-generated method stub
		return null;
	}

	public String postProcess() {
		return null;
	}

	public String validateInput() {
		DefaultListModel mod = (DefaultListModel) jList1.getModel();
		if (mod.getSize() == 0) {
			return "You must create almost one legend";
		}
		return null;
	}

	public Legend[] getLegend() {
		ArrayList<Legend> legends = new ArrayList<Legend>();

		DefaultListModel mod = (DefaultListModel) jList1.getModel();
		for (int i = 0; i < mod.getSize(); i++) {
			legends
					.add(((LegendListDecorator) mod.getElementAt(i))
							.getLegend());
		}

		Legend[] legendsL = new Legend[legends.size()];
		for (int j = 0; j < legendsL.length; j++) {
			legendsL[j] = legends.get(j);
		}

		ImageLegend im = new ImageLegend(legendsL);
		BufferedImage [] image = im.getIm();

		for (int i=0; i<image.length; i++){
			try {
				ImageIO.write(image[i], "png", new File("/tmp/"+legendsL[i].getName()+"__"+i+".png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return legendsL;
	}
}

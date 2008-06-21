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
 * JPanelSimpleLegend.java
 *
 * Created on 22 de febrero de 2008, 16:33
 */

package org.orbisgis.editorViews.toc.actions.cui.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import org.gdms.data.types.GeometryConstraint;
import org.orbisgis.ExtendedWorkspace;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.Canvas;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.ColorPicker;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.FlowLayoutPreviewWindow;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.LegendListDecorator;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.SymbolListDecorator;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.jPanelTypeOfGeometrySelection;
import org.orbisgis.editorViews.toc.actions.cui.persistence.ObjectFactory;
import org.orbisgis.editorViews.toc.actions.cui.persistence.Symbolcollection;
import org.orbisgis.renderer.legend.CircleSymbol;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.LegendFactory;
import org.orbisgis.renderer.legend.LineSymbol;
import org.orbisgis.renderer.legend.PolygonSymbol;
import org.orbisgis.renderer.legend.Symbol;
import org.orbisgis.renderer.legend.SymbolComposite;
import org.orbisgis.renderer.legend.SymbolFactory;
import org.orbisgis.renderer.legend.UniqueSymbolLegend;
import org.orbisgis.ui.sif.AskValue;
import org.sif.UIFactory;
import org.sif.UIPanel;

/**
 *
 * @author david
 */
public class JPanelUniqueSymbolLegend extends javax.swing.JPanel implements
		ILegendPanelUI, UIPanel {

	private String identity = "Unique symbol legend";
	private Integer constraint = 0;
	private Integer layerConstraint = 0;
	private Canvas canvas = null;
	private UniqueSymbolLegend leg = null;
	private LegendListDecorator dec = null;
	private boolean showCollection = false;

	/** Creates new form JPanelSimpleSimbolLegend */
	public JPanelUniqueSymbolLegend(Legend leg, Integer constraint, boolean showCollection) {
		this.constraint = constraint;
		this.layerConstraint = constraint;
		this.leg = (UniqueSymbolLegend)leg;
		this.showCollection = showCollection;
		initComponents();
		jLabelFillPreview.setBackground(Color.LIGHT_GRAY);
		jLabelLinePreview.setBackground(Color.BLUE);
		jList1.setModel(new DefaultListModel());
		refreshComponents();
	}

	/**
	 * refresh all the components: 
	 * preview, symbol list and the state of all the components.
	 */
	private void refreshComponents() {
		setCanvas();
		updateSymbolList();
		refreshCanvas();
		lookIfWeHaveSymbols();
	}

	/**
	 * sets all the components visibility to false and calls to refresh selections
	 * in order to enable some according to the type of the selected symbol.
	 */
	private void lookIfWeHaveSymbols() {
		DefaultListModel mod = (DefaultListModel) jList1.getModel();

		jButtonFillColorPicker.setVisible(false);
		jButtonFromCollection.setVisible(true);
		jButtonLineColorPicker.setVisible(false);
		jButtonSymbolDel.setEnabled(false);
		jButtonSymbolDown.setEnabled(false);
		jButtonSymbolRename.setEnabled(false);
		jButtonSymbolUp.setEnabled(false);
		jCheckBoxFill.setVisible(false);
		jCheckBoxLine.setVisible(false);
		jButtonSyncLineWithFill.setVisible(false);
		jLabel1.setVisible(false);
		jLabel2.setVisible(false);
		jLabelFillPreview.setVisible(false);
		jLabelLinePreview.setVisible(false);
		jLabel7.setVisible(false);
		jSliderLineWidth.setVisible(false);
		jSliderTransparency.setVisible(false);
		jSliderVertices.setVisible(false);
		jTextFieldLine.setVisible(false);
		jTextFieldTransparency.setVisible(false);
		jTextFieldVertices.setVisible(false);
		// }else{
		if (mod.size() != 0) {
			refreshSelections();
		}

	}

	/**
	 * if the symbol is not a composite symbol creates a composite and sets in
	 * it the non composite symbol.
	 *
	 * When we have a composite we fill the list.
	 */
	private void updateSymbolList() {
		Symbol sym = leg.getSymbol();

		DefaultListModel mod = (DefaultListModel) jList1.getModel();
		mod.removeAllElements();

		if (!(sym instanceof SymbolComposite)) {
			Symbol[] syms = new Symbol[1];
			syms[0] = sym;
			sym = SymbolFactory.createSymbolComposite(syms);
			leg.setSymbol(sym);
		}

		SymbolComposite symbolComp = (SymbolComposite) sym;
		int numSymbols = symbolComp.getSymbolCount();

		for (int i = 0; i < numSymbols; i++) {
			Symbol simpSym = symbolComp.getSymbol(i);
			if (simpSym != null) {
				SymbolListDecorator symbolUni = new SymbolListDecorator(
						symbolComp.getSymbol(i));
				mod.addElement(symbolUni);
			}
		}

		if (mod.getSize() > 0) {
			jList1.setSelectedIndex(0);
			updateLegendValues(symbolComp.getSymbol(0));
		}
		refreshButtons();
	}

	/**
	 * if the symbol is not a composite symbol creates a composite and sets in
	 * it the non composite symbol.
	 *
	 * When we have a composite we fill the list.
	 */
	private void addToSymbolList(Symbol sym) {

		DefaultListModel mod = (DefaultListModel) jList1.getModel();

		if (sym instanceof SymbolComposite) {
			SymbolComposite symbolComp = (SymbolComposite) sym;
			int numSymbols = symbolComp.getSymbolCount();

			for (int i = 0; i < numSymbols; i++) {
				Symbol simpSym = symbolComp.getSymbol(i);
				if (simpSym != null) {
					SymbolListDecorator symbolUni = new SymbolListDecorator(
							symbolComp.getSymbol(i));
					mod.addElement(symbolUni);
				}
			}
		} else {
			SymbolListDecorator symbolUni = new SymbolListDecorator(sym);
			mod.addElement(symbolUni);
		}

		if (mod.getSize() > 0) {
			jList1.setSelectedIndex(0);
			updateLegendValues(((SymbolListDecorator) jList1.getSelectedValue())
					.getSymbol());
		}
		refreshButtons();
	}


	private void refreshSelections() {
		if (leg.getName() != null)
			identity = leg.getName();

		disableComponents();
		refreshButtons();

	}

	/**
	 * Change the values of the components in the panel according to the symbol
	 * @param sym
	 */
	
	private void updateLegendValues(Symbol sym) {

		setAllChecksToFalse();

		if (sym instanceof CircleSymbol) {
			CircleSymbol symbol = (CircleSymbol) sym;

			Color fillColor = symbol.getFillColor();
			Color outlineColor = symbol.getOutlineColor();
			jLabelFillPreview.setBackground(fillColor);
			jLabelFillPreview.setOpaque(true);
			jLabelLinePreview.setBackground(outlineColor);
			jLabelLinePreview.setOpaque(true);
			jSliderVertices.setValue(symbol.getSize());

			if (fillColor.getAlpha() != 0) {
				jCheckBoxFill.setSelected(true);
				jSliderTransparency.setValue(255 - fillColor.getAlpha());
			} else {
				jCheckBoxFill.setSelected(false);
			}

			if (outlineColor.getAlpha() != 0) {
				jCheckBoxLine.setSelected(true);
				jSliderTransparency.setValue(255 - outlineColor.getAlpha());
			} else {
				jCheckBoxLine.setSelected(false);

			}

		}

		if (sym instanceof LineSymbol) {
			LineSymbol symbol = (LineSymbol) sym;
			jLabelLinePreview.setBackground(symbol.getColor());
			jLabelLinePreview.setOpaque(true);
			jSliderLineWidth.setValue((int) symbol.getSize());
			Color col = symbol.getColor();

			if (col.getAlpha() != 0) {
				jCheckBoxLine.setSelected(true);
				jSliderTransparency.setValue(255 - col.getAlpha());
			} else {
				jCheckBoxLine.setSelected(false);

			}
		}

		if (sym instanceof PolygonSymbol) {
			PolygonSymbol symbol = (PolygonSymbol) sym;
			Color fillColor = symbol.getFillColor();
			Color outlineColor = symbol.getOutlineColor();

			jLabelFillPreview.setBackground(fillColor);
			jLabelFillPreview.setOpaque(true);
			jLabelLinePreview.setBackground(symbol.getOutlineColor());
			jLabelLinePreview.setOpaque(true);
			jSliderLineWidth.setValue((int) ((BasicStroke) symbol.getStroke())
					.getLineWidth());

			if (fillColor.getAlpha() != 0) {
				jCheckBoxFill.setSelected(true);
				jSliderTransparency.setValue(255 - fillColor.getAlpha());
			} else {
				jCheckBoxFill.setSelected(false);

			}

			if (outlineColor.getAlpha() != 0) {
				jCheckBoxLine.setSelected(true);
				jSliderTransparency.setValue(255 - outlineColor.getAlpha());
			} else {
				jCheckBoxLine.setSelected(false);

			}

		}

	}

	private void setAllChecksToFalse() {
		jCheckBoxFill.setSelected(true);
		jCheckBoxLine.setSelected(true);
	}

	private void setCanvas() {
		canvas = new Canvas();
		canvasPreview.add(canvas);
	}

	private void refreshCanvas() {
		getSymbol();
		Symbol sym = getSymbolComposite();
		canvas.setLegend(sym, constraint);
		canvas.validate();
		canvas.repaint();

		if (dec != null) {
			dec.setLegend(getLegend());
		}

	}

	/**
	 * Really disable or enable the components according with the type of the symbol
	 */
	private void disableComponents() {
		boolean enabledFill = false, enabledLine = false, enabledVertex = false;

		if (!showCollection) {
			jButtonToCollection.setVisible(false);
		}

		if (constraint == null) {
			enabledVertex = false;
			enabledFill = true;
			enabledLine = true;
		} else {
			switch (constraint) {
			case GeometryConstraint.LINESTRING:
			case GeometryConstraint.MULTI_LINESTRING:
				enabledFill = false;
				enabledLine = true;
				enabledVertex = false;
				break;
			case GeometryConstraint.POINT:
			case GeometryConstraint.MULTI_POINT:
				enabledVertex = true;
				enabledFill = true;
				enabledLine = true;
				break;
			case GeometryConstraint.POLYGON:
			case GeometryConstraint.MULTI_POLYGON:
				enabledVertex = false;
				enabledFill = true;
				enabledLine = true;
			}
		}

		jSliderLineWidth.setVisible(enabledLine);
		jTextFieldLine.setVisible(enabledLine);
		jLabel1.setVisible(enabledLine);

		jSliderVertices.setVisible(enabledVertex);
		jTextFieldVertices.setVisible(enabledVertex);
		jLabel7.setVisible(enabledVertex);

		jSliderTransparency.setVisible(enabledFill || enabledLine);
		jTextFieldTransparency.setVisible(enabledFill || enabledLine);
		jLabel2.setVisible(enabledFill || enabledLine);

		jLabelLinePreview.setVisible(enabledLine);
		jButtonLineColorPicker.setVisible(enabledLine);
		jCheckBoxLine.setVisible(enabledLine);

		jLabelFillPreview.setVisible(enabledFill);
		jButtonFillColorPicker.setVisible(enabledFill);
		jCheckBoxFill.setVisible(enabledFill);

		jButtonSyncLineWithFill.setVisible(enabledFill && enabledLine);

	}

	
	/**
	 * creates a symbol with the values of the components and returns it.
	 * @return Symbol
	 */
	public Symbol getSymbol() {
		Symbol sym = null;
		Color bgnd = Color.LIGHT_GRAY;
		Color nbgnd = Color.LIGHT_GRAY;
		Color lne = Color.BLUE;
		Color nlne = Color.BLUE;

		switch (constraint) {
		case GeometryConstraint.LINESTRING:
		case GeometryConstraint.MULTI_LINESTRING:
			if (jCheckBoxLine.isSelected()) {
				lne = jLabelLinePreview.getBackground();
				nlne = new Color(lne.getRed(), lne.getGreen(), lne.getBlue(),
						255 - jSliderTransparency.getValue());
			} else {
				nlne = new Color(lne.getRed(), lne.getGreen(), lne.getBlue(), 0);
			}
			sym = SymbolFactory.createLineSymbol(nlne, new BasicStroke(
					(float) jSliderLineWidth.getValue()));
			break;
		case GeometryConstraint.POINT:
		case GeometryConstraint.MULTI_POINT:
			if (jCheckBoxFill.isSelected()) {
				bgnd = jLabelFillPreview.getBackground();
				nbgnd = new Color(bgnd.getRed(), bgnd.getGreen(), bgnd
						.getBlue(), 255 - jSliderTransparency.getValue());
			} else {
				nbgnd = new Color(bgnd.getRed(), bgnd.getGreen(), bgnd
						.getBlue(), 0);
			}

			if (jCheckBoxLine.isSelected()) {
				lne = jLabelLinePreview.getBackground();
				nlne = new Color(lne.getRed(), lne.getGreen(), lne.getBlue(),
						255 - jSliderTransparency.getValue());
			} else {
				nlne = new Color(lne.getRed(), lne.getGreen(), lne.getBlue(), 0);
			}

			sym = SymbolFactory.createCirclePointSymbol(nlne, nbgnd,
					jSliderVertices.getValue());
			break;
		case GeometryConstraint.POLYGON:
		case GeometryConstraint.MULTI_POLYGON:
		default:
			if (jCheckBoxFill.isSelected()) {
				bgnd = jLabelFillPreview.getBackground();
				nbgnd = new Color(bgnd.getRed(), bgnd.getGreen(), bgnd
						.getBlue(), 255 - jSliderTransparency.getValue());
			} else {
				nbgnd = new Color(bgnd.getRed(), bgnd.getGreen(), bgnd
						.getBlue(), 0);
			}

			if (jCheckBoxLine.isSelected()) {
				lne = jLabelLinePreview.getBackground();
				nlne = new Color(lne.getRed(), lne.getGreen(), lne.getBlue(),
						255 - jSliderTransparency.getValue());
			} else {
				nlne = new Color(lne.getRed(), lne.getGreen(), lne.getBlue(), 0);
			}

			sym = SymbolFactory.createPolygonSymbol(new BasicStroke(
					(float) jSliderLineWidth.getValue()), nlne, nbgnd);

			break;
		}

		int selIdx = jList1.getSelectedIndex();
		if (selIdx != -1) {
			SymbolListDecorator symbolDec = (SymbolListDecorator) jList1
					.getSelectedValue();
			sym.setName(symbolDec.getSymbol().getName());
			symbolDec.setSymbol(sym);
			// updateLegendValues(sym);
		}

		return sym;
	}

	/**
	 * Returns an UniqueSymbolLegend with a composite symbol with all the symbols in the list.
	 */
	public Legend getLegend() {
		UniqueSymbolLegend leg = LegendFactory.createUniqueSymbolLegend();

		Symbol sym = getSymbolComposite();

		leg.setSymbol(sym);

		leg.setName(identity);

		return leg;
	}

	/**
	 * returns a symbolcomposite with all the symbols in the list
	 * @return
	 */
	public Symbol getSymbolComposite() {
		DefaultListModel mod = (DefaultListModel) jList1.getModel();
		int size = mod.getSize();
		Symbol[] syms = new Symbol[size];
		for (int i = 0; i < size; i++) {
			SymbolListDecorator dec = (SymbolListDecorator) mod.getElementAt(i);
			syms[i] = dec.getSymbol();
		}
		Symbol comp = SymbolFactory.createSymbolComposite(syms);
		return comp;
	}

	public void setDecoratorListener(LegendListDecorator dec) {
		this.dec = dec;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButtonSyncLineWithFill = new javax.swing.JButton();
        jLabelLinePreview = new javax.swing.JLabel();
        jButtonLineColorPicker = new javax.swing.JButton();
        jLabelFillPreview = new javax.swing.JLabel();
        jButtonFillColorPicker = new javax.swing.JButton();
        jCheckBoxLine = new javax.swing.JCheckBox();
        jCheckBoxFill = new javax.swing.JCheckBox();
        jComboBoxLine1 = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        jTextFieldVertices = new javax.swing.JTextField();
        jSliderVertices = new javax.swing.JSlider();
        jSliderTransparency = new javax.swing.JSlider();
        jTextFieldTransparency = new javax.swing.JTextField();
        jSliderLineWidth = new javax.swing.JSlider();
        jTextFieldLine = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jToolBar1 = new javax.swing.JToolBar();
        jButtonSymbolUp = new javax.swing.JButton();
        jButtonSymbolDown = new javax.swing.JButton();
        jButtonSymbolAdd = new javax.swing.JButton();
        jButtonSymbolDel = new javax.swing.JButton();
        jButtonSymbolRename = new javax.swing.JButton();
        canvasPreview = new javax.swing.JPanel();
        jButtonFromCollection = new javax.swing.JButton();
        jButtonToCollection = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(450, 280));
        setPreferredSize(new java.awt.Dimension(450, 280));

        jPanel2.setMinimumSize(new java.awt.Dimension(70, 100));
        jPanel2.setPreferredSize(new java.awt.Dimension(70, 100));

        jPanel4.setBorder(null);

        jButtonSyncLineWithFill.setText("Sync line with fill");
        jButtonSyncLineWithFill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSyncLineWithFillActionPerformed(evt);
            }
        });

        jLabelLinePreview.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButtonLineColorPicker.setText("select");
        jButtonLineColorPicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLineColorPickerActionPerformed(evt);
            }
        });

        jLabelFillPreview.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButtonFillColorPicker.setText("select");
        jButtonFillColorPicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFillColorPickerActionPerformed(evt);
            }
        });

        jCheckBoxLine.setText("Line:");
        jCheckBoxLine.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxLineActionPerformed(evt);
            }
        });

        jCheckBoxFill.setText("Fill:");
        jCheckBoxFill.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxFill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFillActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jCheckBoxFill, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCheckBoxLine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButtonLineColorPicker, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonFillColorPicker, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelLinePreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelFillPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)))
                    .addComponent(jButtonSyncLineWithFill, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
                .addGap(59, 59, 59))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonFillColorPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCheckBoxFill))
                    .addComponent(jLabelFillPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonLineColorPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelLinePreview, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxLine))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSyncLineWithFill, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(6, Short.MAX_VALUE))
        );

        jPanel5.setBorder(null);

        jTextFieldVertices.setText("0");
        jTextFieldVertices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldVerticesActionPerformed(evt);
            }
        });

        jSliderVertices.setMaximum(20);
        jSliderVertices.setMinorTickSpacing(1);
        jSliderVertices.setValue(0);
        jSliderVertices.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderVerticesStateChanged(evt);
            }
        });

        jSliderTransparency.setMaximum(255);
        jSliderTransparency.setMinorTickSpacing(1);
        jSliderTransparency.setValue(0);
        jSliderTransparency.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderTransparencyStateChanged(evt);
            }
        });

        jTextFieldTransparency.setText("0");
        jTextFieldTransparency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldTransparencyActionPerformed(evt);
            }
        });

        jSliderLineWidth.setMaximum(30);
        jSliderLineWidth.setMinorTickSpacing(1);
        jSliderLineWidth.setPaintLabels(true);
        jSliderLineWidth.setValue(1);
        jSliderLineWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderLineWidthStateChanged(evt);
            }
        });

        jTextFieldLine.setText("1");
        jTextFieldLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldLineActionPerformed(evt);
            }
        });

        jLabel1.setText("Line width: ");

        jLabel7.setText("Symbol size:");

        jLabel2.setText("Transparency: ");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSliderVertices, 0, 0, Short.MAX_VALUE)
                    .addComponent(jSliderTransparency, 0, 0, Short.MAX_VALUE)
                    .addComponent(jSliderLineWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldVertices, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jTextFieldTransparency)
                        .addComponent(jTextFieldLine, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSliderLineWidth, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                    .addComponent(jTextFieldLine, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSliderTransparency, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldTransparency, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSliderVertices, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(jTextFieldVertices, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jButtonSymbolUp.setText("up");
        jButtonSymbolUp.setFocusable(false);
        jButtonSymbolUp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSymbolUp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSymbolUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSymbolUpActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonSymbolUp);

        jButtonSymbolDown.setText("down");
        jButtonSymbolDown.setFocusable(false);
        jButtonSymbolDown.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSymbolDown.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSymbolDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSymbolDownActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonSymbolDown);

        jButtonSymbolAdd.setText("add");
        jButtonSymbolAdd.setFocusable(false);
        jButtonSymbolAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSymbolAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSymbolAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSymbolAddActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonSymbolAdd);

        jButtonSymbolDel.setText("del");
        jButtonSymbolDel.setFocusable(false);
        jButtonSymbolDel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSymbolDel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSymbolDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSymbolDelActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonSymbolDel);

        jButtonSymbolRename.setText("rename");
        jButtonSymbolRename.setFocusable(false);
        jButtonSymbolRename.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSymbolRename.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSymbolRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSymbolRenameActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonSymbolRename);

        canvasPreview.setBorder(null);
        canvasPreview.setMinimumSize(new java.awt.Dimension(126, 70));
        canvasPreview.setLayout(new java.awt.GridLayout(1, 0));

        jButtonFromCollection.setText("add");
        jButtonFromCollection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFromCollectionActionPerformed(evt);
            }
        });

        jButtonToCollection.setText("save");
        jButtonToCollection.setFocusable(false);
        jButtonToCollection.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToCollection.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonToCollection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonToCollectionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(canvasPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonToCollection, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonFromCollection, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonFromCollection)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonToCollection))
                            .addComponent(canvasPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(76, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * open the collection window in order to select one symbol.
     * @param evt
     */
	private void jButtonFromCollectionActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonFromCollectionActionPerformed
		FlowLayoutPreviewWindow coll = new FlowLayoutPreviewWindow();
		coll.setConstraint(constraint);
		if (UIFactory.showDialog(coll)) {
			Symbol sym = coll.getSelectedSymbol();
			addToSymbolList(sym);
		}
		refreshCanvas();
		lookIfWeHaveSymbols();
	}// GEN-LAST:event_jButtonFromCollectionActionPerformed

	/**
	 * This function will enable or disable the buttons in the list menu according
	 * to the position of the selected value.
	 */
	private void refreshButtons() {
		int[] idxs = jList1.getSelectedIndices();
		int maximo = jList1.getModel().getSize() - 1;
		int minimo = 0;

		if (idxs.length == 1) {
			int idx = idxs[0];
			if (idx == -1) {
				jButtonSymbolUp.setEnabled(false);
				jButtonSymbolDown.setEnabled(false);
				jButtonSymbolDel.setEnabled(false);
				jButtonSymbolRename.setEnabled(false);
			} else {
				jButtonSymbolDel.setEnabled(true);
				jButtonSymbolRename.setEnabled(true);
				if (idx == minimo) {
					if (idx == maximo)
						jButtonSymbolDown.setEnabled(false);
					else
						jButtonSymbolDown.setEnabled(true);
					jButtonSymbolUp.setEnabled(false);
				} else {
					if (idx == maximo) {
						jButtonSymbolUp.setEnabled(true);
						jButtonSymbolDown.setEnabled(false);
					} else {
						jButtonSymbolUp.setEnabled(true);
						jButtonSymbolDown.setEnabled(true);
					}
				}
			}
		} else {
			if (idxs.length > 0) {
				jButtonSymbolDel.setEnabled(true);
			} else {
				jButtonSymbolDel.setEnabled(false);
			}
			jButtonSymbolUp.setEnabled(false);
			jButtonSymbolDown.setEnabled(false);
			jButtonSymbolRename.setEnabled(false);

		}
	}

	/**
	 * move up the selected symbol in the list
	 * @param evt
	 */
	private void jButtonSymbolUpActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSymbolUpActionPerformed
		DefaultListModel mod = (DefaultListModel) jList1.getModel();
		int idx = 0;
		idx = jList1.getSelectedIndex();
		if (idx > 0) {
			SymbolListDecorator element = (SymbolListDecorator) mod.get(idx);
			mod.remove(idx);
			mod.add(idx - 1, element);
		}
		jList1.setSelectedIndex(idx - 1);
		refreshCanvas();
		refreshButtons();
	}// GEN-LAST:event_jButtonSymbolUpActionPerformed

	/**
	 * When the selected value in the list is changed we will call to the refresh functions with the new symbol.
	 * @param evt
	 */
	private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_jList1ValueChanged
		DefaultListModel mod = (DefaultListModel) jList1.getModel();
		if ((mod.getSize() > 0) && (jList1.getSelectedIndex() != -1)) {
			SymbolListDecorator dec = (SymbolListDecorator) jList1
					.getSelectedValue();
			constraint = canvas.getConstraint(dec.getSymbol());
			updateLegendValues(dec.getSymbol());
			lookIfWeHaveSymbols();
			refreshCanvas();
			refreshButtons();
		}
	}// GEN-LAST:event_jList1ValueChanged

	/**
	 * moves down the symbol
	 * @param evt
	 */
	private void jButtonSymbolDownActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSymbolDownActionPerformed
		DefaultListModel mod = (DefaultListModel) jList1.getModel();
		int idx = 0;
		idx = jList1.getSelectedIndex();
		if (idx < mod.size() - 1) {
			SymbolListDecorator element = (SymbolListDecorator) mod.get(idx);
			mod.remove(idx);
			mod.add(idx + 1, element);
		}
		jList1.setSelectedIndex(idx + 1);
		refreshCanvas();
		refreshButtons();
	}// GEN-LAST:event_jButtonSymbolDownActionPerformed

	/**
	 * adds a symbol in the list
	 * @param evt
	 */
	private void jButtonSymbolAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSymbolAddActionPerformed
		Symbol sy = null;
		if (layerConstraint == null) {
			jPanelTypeOfGeometrySelection sel = new jPanelTypeOfGeometrySelection();
			if (UIFactory.showDialog(sel)) {
				int constr = sel.getConstraint();
				switch (constr) {
				case GeometryConstraint.LINESTRING:
				case GeometryConstraint.MULTI_LINESTRING:
					sy = SymbolFactory.createLineSymbol(Color.LIGHT_GRAY,
							new BasicStroke());
					break;
				case GeometryConstraint.POINT:
				case GeometryConstraint.MULTI_POINT:
					sy = SymbolFactory.createCirclePointSymbol(Color.BLUE,
							Color.LIGHT_GRAY, 10);
					break;
				case GeometryConstraint.POLYGON:
				case GeometryConstraint.MULTI_POLYGON:
					sy = SymbolFactory.createPolygonSymbol(new BasicStroke(),
							Color.BLUE, Color.LIGHT_GRAY);
					break;
				}
			}
		} else {
			switch (layerConstraint) {
			case GeometryConstraint.LINESTRING:
			case GeometryConstraint.MULTI_LINESTRING:
				sy = SymbolFactory.createLineSymbol(Color.LIGHT_GRAY,
						new BasicStroke());
				break;
			case GeometryConstraint.POINT:
			case GeometryConstraint.MULTI_POINT:
				sy = SymbolFactory.createCirclePointSymbol(Color.BLUE,
						Color.LIGHT_GRAY, 10);
				break;
			case GeometryConstraint.POLYGON:
			case GeometryConstraint.MULTI_POLYGON:
				sy = SymbolFactory.createPolygonSymbol(new BasicStroke(),
						Color.BLUE, Color.LIGHT_GRAY);
				break;
			}
		}

		if (sy != null) {
			SymbolListDecorator deco = new SymbolListDecorator(sy);

			((DefaultListModel) jList1.getModel()).addElement(deco);

			jList1.setSelectedValue(deco, true);

			updateLegendValues(deco.getSymbol());
			lookIfWeHaveSymbols();
			refreshCanvas();
			refreshButtons();
		}

	}// GEN-LAST:event_jButtonSymbolAddActionPerformed

	/**
	 * delete the selected symbols
	 * @param evt
	 */
	private void jButtonSymbolDelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSymbolDelActionPerformed
		DefaultListModel mod = (DefaultListModel) jList1.getModel();
		int[] indexes = jList1.getSelectedIndices();
		// for (int i=0; i<indexes.length; i++){
		while (indexes.length > 0) {
			mod.removeElementAt(indexes[0]);
			indexes = jList1.getSelectedIndices();
		}
		if (mod.getSize() > 0) {
			jList1.setSelectedIndex(0);
			updateLegendValues(((SymbolListDecorator) mod.getElementAt(0))
					.getSymbol());
		}
		refreshCanvas();
		lookIfWeHaveSymbols();
		// refreshButtons();

	}// GEN-LAST:event_jButtonSymbolDelActionPerformed

	/**
	 * rename the selected symbol
	 * @param evt
	 */
	private void jButtonSymbolRenameActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSymbolRenameActionPerformed
		DefaultListModel mod = (DefaultListModel) jList1.getModel();
		SymbolListDecorator dec = (SymbolListDecorator) jList1
				.getSelectedValue();
		int idx = jList1.getSelectedIndex();

		AskValue ask = new AskValue("Insert the new name", "txt is not null",
				"A name must be specified", dec.getSymbol().getName());
		String new_name = "";
		if (UIFactory.showDialog(ask)) {
			new_name = ask.getValue();
		}

		if (new_name != null && new_name != "") {
			dec.getSymbol().setName(new_name);
			mod.remove(idx);
			mod.add(idx, dec);
			jList1.setSelectedIndex(idx);

			updateLegendValues(dec.getSymbol());
			refreshCanvas();
			lookIfWeHaveSymbols();
		}
	}// GEN-LAST:event_jButtonSymbolRenameActionPerformed

	private void jButtonSyncLineWithFillActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSyncLineWithFillActionPerformed
		jLabelLinePreview.setBackground(jLabelFillPreview.getBackground()
				.darker());
		jLabelFillPreview.setOpaque(true);
		refreshCanvas();
		lookIfWeHaveSymbols();
	}// GEN-LAST:event_jButtonSyncLineWithFillActionPerformed

	/**
	 * gets the symbols and adds to the collection as a new Composite
	 * @param evt
	 */
	private void jButtonToCollectionActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonToCollectionActionPerformed
		Symbolcollection coll = null;
		FlowLayoutPreviewWindow flow = new FlowLayoutPreviewWindow();
		try {
			ExtendedWorkspace ew = (ExtendedWorkspace) Services
					.getService("org.orbisgis.ExtendedWorkspace");
			FileInputStream is = new FileInputStream(ew
					.getFile(FlowLayoutPreviewWindow.SYMBOL_COLLECTION_FILE));
			coll = flow.loadCollection(is);
			is.close();
		} catch (FileNotFoundException e) {
			System.out.println("Collection not loaded: " + e.getMessage());
			JOptionPane.showMessageDialog(this,
					"Cannot save symbol in collection", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		} catch (JAXBException e) {
			System.out.println("Collection not loaded: " + e.getMessage());
			JOptionPane.showMessageDialog(this,
					"Cannot save symbol in collection", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		} catch (NullPointerException e) {
			System.out.println("Collection not loaded: " + e.getMessage());
			JOptionPane.showMessageDialog(this,
					"Cannot save symbol in collection", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		} catch (IOException e) {
			System.out.println("Collection not loaded: " + e.getMessage());
			JOptionPane.showMessageDialog(this,
					"Cannot save symbol in collection", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (coll.getCompositeSymbol() == null) {
			ObjectFactory of = new ObjectFactory();
			coll = of.createSymbolcollection();
		}
		
		Object[] values = ((DefaultListModel) jList1.getModel()).toArray();

		Symbol[] sym = new Symbol[values.length];
		for (int i = 0; i < values.length; i++) {
			SymbolListDecorator dec = (SymbolListDecorator) values[i];
			sym[i] = dec.getSymbol();
		}

		SymbolComposite comp = (SymbolComposite) SymbolFactory
				.createSymbolComposite(sym);

		coll.getCompositeSymbol().add(flow.createComposite(comp));

		try {
			ExtendedWorkspace ew = (ExtendedWorkspace) Services
					.getService("org.orbisgis.ExtendedWorkspace");
			FileOutputStream os = new FileOutputStream(ew
					.getFile(FlowLayoutPreviewWindow.SYMBOL_COLLECTION_FILE));
			flow.saveCollection(coll, os);
			os.close();
		} catch (FileNotFoundException e) {
			System.out.println("Collection not saved: " + e.getMessage());
			JOptionPane.showMessageDialog(this,
					"Cannot save symbol in collection", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		} catch (JAXBException e) {
			System.out.println("Collection not saved: " + e.getMessage());
			JOptionPane.showMessageDialog(this,
					"Cannot save symbol in collection", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		} catch (NullPointerException e) {
			System.out.println("Collection not saved: " + e.getMessage());
			JOptionPane.showMessageDialog(this,
					"Cannot save symbol in collection", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		} catch (IOException e) {
			System.out.println("Collection not saved: " + e.getMessage());
			JOptionPane.showMessageDialog(this,
					"Cannot save symbol in collection", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(this, "Symbol saved in collection",
				"Operation succesful", JOptionPane.INFORMATION_MESSAGE);

	}// GEN-LAST:event_jButtonToCollectionActionPerformed

	private void jCheckBoxLineActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxLineActionPerformed

		jLabelLinePreview.setOpaque(true);
		refreshCanvas();
		lookIfWeHaveSymbols();
	}// GEN-LAST:event_jCheckBoxLineActionPerformed

	private void jCheckBoxFillActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxFillActionPerformed

		jLabelFillPreview.setOpaque(true);
		refreshCanvas();
		lookIfWeHaveSymbols();
	}// GEN-LAST:event_jCheckBoxFillActionPerformed

	private void jTextFieldVerticesActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextFieldVerticesActionPerformed
		int value = Integer.parseInt(jTextFieldVertices.getText());
		jSliderVertices.setValue(value);
		lookIfWeHaveSymbols();
		refreshCanvas();
	}// GEN-LAST:event_jTextFieldVerticesActionPerformed

	private void jTextFieldTransparencyActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextFieldTransparencyActionPerformed
		int value = Integer.parseInt(jTextFieldTransparency.getText());
		jSliderTransparency.setValue(value);
		refreshCanvas();
		lookIfWeHaveSymbols();
	}// GEN-LAST:event_jTextFieldTransparencyActionPerformed

	private void jTextFieldLineActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextFieldLineActionPerformed
		int value = Integer.parseInt(jTextFieldLine.getText());
		jSliderLineWidth.setValue(value);
		refreshCanvas();
		lookIfWeHaveSymbols();
	}// GEN-LAST:event_jTextFieldLineActionPerformed

	private void jSliderVerticesStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_jSliderVerticesStateChanged
		int value = jSliderVertices.getValue();
		jTextFieldVertices.setText(String.valueOf(value));
		refreshCanvas();
		lookIfWeHaveSymbols();
	}// GEN-LAST:event_jSliderVerticesStateChanged

	private void jSliderTransparencyStateChanged(
			javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_jSliderTransparencyStateChanged
		int value = jSliderTransparency.getValue();
		jTextFieldTransparency.setText(String.valueOf(value));
		refreshCanvas();
		lookIfWeHaveSymbols();
	}// GEN-LAST:event_jSliderTransparencyStateChanged

	private void jSliderLineWidthStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_jSliderLineWidthStateChanged
		int value = jSliderLineWidth.getValue();
		jTextFieldLine.setText(String.valueOf(value));
		refreshCanvas();
		lookIfWeHaveSymbols();
	}// GEN-LAST:event_jSliderLineWidthStateChanged

	private void jButtonLineColorPickerActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonLineColorPickerActionPerformed
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			jLabelLinePreview.setBackground(color);
			jLabelLinePreview.setOpaque(true);
		}
		refreshCanvas();
		lookIfWeHaveSymbols();

	}// GEN-LAST:event_jButtonLineColorPickerActionPerformed

	private void jButtonFillColorPickerActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonFillColorPickerActionPerformed
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			jLabelFillPreview.setBackground(color);
			jLabelFillPreview.setOpaque(true);
		}
		refreshCanvas();
		lookIfWeHaveSymbols();

	}// GEN-LAST:event_jButtonFillColorPickerActionPerformed

	public Component getComponent() {
		return this;
	}


	public String getInfoText() {
		// TODO Auto-generated method stub
		return "Set a Unique symbol legend to the selected layer";
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return "Unique symbol legend";
	}


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel canvasPreview;
    private javax.swing.JButton jButtonFillColorPicker;
    private javax.swing.JButton jButtonFromCollection;
    private javax.swing.JButton jButtonLineColorPicker;
    private javax.swing.JButton jButtonSymbolAdd;
    private javax.swing.JButton jButtonSymbolDel;
    private javax.swing.JButton jButtonSymbolDown;
    private javax.swing.JButton jButtonSymbolRename;
    private javax.swing.JButton jButtonSymbolUp;
    private javax.swing.JButton jButtonSyncLineWithFill;
    private javax.swing.JButton jButtonToCollection;
    private javax.swing.JCheckBox jCheckBoxFill;
    private javax.swing.JCheckBox jCheckBoxLine;
    private javax.swing.JComboBox jComboBoxLine1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelFillPreview;
    private javax.swing.JLabel jLabelLinePreview;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSlider jSliderLineWidth;
    private javax.swing.JSlider jSliderTransparency;
    private javax.swing.JSlider jSliderVertices;
    private javax.swing.JTextField jTextFieldLine;
    private javax.swing.JTextField jTextFieldTransparency;
    private javax.swing.JTextField jTextFieldVertices;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

	public URL getIconURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public String initialize() {
		// TODO Auto-generated method stub
		return null;
	}

	public String postProcess() {
		return null;
	}

	public String validateInput() {
		switch (constraint) {
		case GeometryConstraint.LINESTRING:
			if (!jCheckBoxLine.isSelected())
				return "You have not selected a line color";
			break;
		}
		return null;
	}

}
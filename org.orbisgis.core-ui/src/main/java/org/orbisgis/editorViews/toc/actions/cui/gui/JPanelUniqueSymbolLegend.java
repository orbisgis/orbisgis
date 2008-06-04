/*
 * JPanelSimpleLegend.java
 *
 * Created on 22 de febrero de 2008, 16:33
 */

package org.orbisgis.editorViews.toc.actions.cui.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import org.gdms.data.types.GeometryConstraint;
import org.orbisgis.ExtendedWorkspace;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.Canvas;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.ColorPicker;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.FlowLayoutPreviewWindow;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.ImageRenderer;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.LegendListDecorator;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.SymbolListDecorator;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.jPanelTypeOfGeometrySelection;
import org.orbisgis.editorViews.toc.actions.cui.persistence.Compositesymboltype;
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
	private int constraint = 0;
	private int layerConstraint = 0;
	private Canvas canvas = null;
	private UniqueSymbolLegend leg = null;
	private LegendListDecorator dec = null;
	private boolean showCollection=false;

	/** Creates new form JPanelSimpleSimbolLegend */
	public JPanelUniqueSymbolLegend(UniqueSymbolLegend leg, int constraint, boolean showCollection) {
		this.constraint = constraint;
		this.layerConstraint = constraint;
		this.leg = leg;
		this.showCollection=showCollection;
		initComponents();
		jLabelFillPreview.setBackground(Color.LIGHT_GRAY);
		jLabelLinePreview.setBackground(Color.BLUE);
		jList1.setModel(new DefaultListModel());
		refreshComponents();
	}


	private void refreshComponents() {
		setCanvas();
		updateSymbolList();
		refreshCanvas();
		lookIfWeHaveSymbols();
	}


	private void lookIfWeHaveSymbols() {
		DefaultListModel mod = (DefaultListModel)jList1.getModel();
		
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
		jLabelSync.setVisible(false);
		jSliderLineWidth.setVisible(false);
		jSliderTransparency.setVisible(false);
		jSliderVertices.setVisible(false);
		jTextFieldLine.setVisible(false);
		jTextFieldTransparency.setVisible(false);
		jTextFieldVertices.setVisible(false);
		//}else{
		if (mod.size()!=0){
			refreshSelections();
		}

	}


	/**
	 * if the symbol is not a composite symbol creates a composite
	 * and sets in it the non composite symbol.
	 *
	 * When we have a composite we fill the list.
	 */
	private void updateSymbolList() {
		Symbol sym = leg.getSymbol();

		DefaultListModel mod = (DefaultListModel)jList1.getModel();
		mod.removeAllElements();

		if (!(sym instanceof SymbolComposite)) {
			Symbol [] syms = new Symbol[1];
			syms[0]=sym;
			sym=SymbolFactory.createSymbolComposite(syms);
			leg.setSymbol(sym);
		}

		SymbolComposite symbolComp = (SymbolComposite) sym;
		int numSymbols = symbolComp.getSymbolCount();

		for (int i=0; i<numSymbols; i++){
			Symbol simpSym = symbolComp.getSymbol(i);
			if (simpSym!=null){
				SymbolListDecorator symbolUni = new SymbolListDecorator(symbolComp.getSymbol(i));
				mod.addElement(symbolUni);
			}
		}

		if (mod.getSize()>0){
			jList1.setSelectedIndex(0);
			updateLegendValues(symbolComp.getSymbol(0));
		}
		refreshButtons();
	}
	
	/**
	 * if the symbol is not a composite symbol creates a composite
	 * and sets in it the non composite symbol.
	 *
	 * When we have a composite we fill the list.
	 */
	private void addToSymbolList(Symbol sym) {

		DefaultListModel mod = (DefaultListModel)jList1.getModel();

		if (sym instanceof SymbolComposite) {
			SymbolComposite symbolComp = (SymbolComposite) sym;
			int numSymbols = symbolComp.getSymbolCount();

			for (int i=0; i<numSymbols; i++){
				Symbol simpSym = symbolComp.getSymbol(i);
				if (simpSym!=null){
					SymbolListDecorator symbolUni = new SymbolListDecorator(symbolComp.getSymbol(i));
					mod.addElement(symbolUni);
				}
			}
		}else{
			SymbolListDecorator symbolUni = new SymbolListDecorator(sym);
			mod.addElement(symbolUni);
		}

		

		if (mod.getSize()>0){
			jList1.setSelectedIndex(0);
			updateLegendValues(((SymbolListDecorator)jList1.getSelectedValue()).getSymbol());
		}
		refreshButtons();
	}

	public JPanelUniqueSymbolLegend(UniqueSymbolLegend leg, int constraint) {
		this(leg, constraint, true);
	}

	private void refreshSelections( ) {
		if (leg.getName() != null)
			identity = leg.getName();

		disableComponents();
		refreshButtons();

	}

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
			
			if (fillColor.getAlpha()!=0){
				jCheckBoxFill.setSelected(true);
				jSliderTransparency.setValue(255 - fillColor.getAlpha());
			}else{
				jCheckBoxFill.setSelected(false);
			}

			if (outlineColor.getAlpha()!=0){
				jCheckBoxLine.setSelected(true);
				jSliderTransparency.setValue(255 - outlineColor.getAlpha());
			}else{
				jCheckBoxLine.setSelected(false);

			}

			//jCheckBoxVertices.setSelected(true);


		}

		if (sym instanceof LineSymbol) {
			LineSymbol symbol = (LineSymbol) sym;
			jLabelLinePreview.setBackground(symbol.getColor());
			jLabelLinePreview.setOpaque(true);
			jSliderLineWidth.setValue((int) symbol.getSize());
			Color col = symbol.getColor();

			if (col.getAlpha()!=0){
				jCheckBoxLine.setSelected(true);
				jSliderTransparency.setValue(255 - col.getAlpha());
			}else{
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



			if (fillColor.getAlpha()!=0){
				jCheckBoxFill.setSelected(true);
				jSliderTransparency.setValue(255 - fillColor.getAlpha());
			}else{
				jCheckBoxFill.setSelected(false);

			}

			if (outlineColor.getAlpha()!=0){
				jCheckBoxLine.setSelected(true);
				jSliderTransparency.setValue(255 - outlineColor.getAlpha());
			}else{
				jCheckBoxLine.setSelected(false);

			}


		}

	}

	private void setAllChecksToFalse() {
		jCheckBoxFill.setSelected(true);
		jCheckBoxLine.setSelected(true);
	}


	public JPanelUniqueSymbolLegend(int constraint) {
		this(LegendFactory.createUniqueSymbolLegend(), constraint);
	}

	public JPanelUniqueSymbolLegend(int constraint, boolean showCollection) {
		this(LegendFactory.createUniqueSymbolLegend(), constraint, showCollection);
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

		if (dec!=null){
			dec.setLegend(getLegend());
		}
		
	}

	private void disableComponents() {
		boolean enabledFill = false, enabledLine = false, enabledVertex = false;

		if (!showCollection){
			jButtonToCollection.setVisible(false);
		}

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
		case GeometryConstraint.MIXED:
		default:
			enabledVertex = false;
			enabledFill = true;
			enabledLine = true;
			break;
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

	public Symbol getSymbol() {
		Symbol sym = null;
		Color bgnd = Color.LIGHT_GRAY;
		Color nbgnd = Color.LIGHT_GRAY;
		Color lne = Color.BLUE;
		Color nlne = Color.BLUE;

		switch (constraint) {
		case GeometryConstraint.LINESTRING:
		case GeometryConstraint.MULTI_LINESTRING:
			if (jCheckBoxLine.isSelected()){
				lne = jLabelLinePreview.getBackground();
				nlne = new Color(lne.getRed(), lne.getGreen(), lne.getBlue(),
						255 - jSliderTransparency.getValue());
			}else{
				nlne = new Color(lne.getRed(), lne.getGreen(), lne.getBlue(),
						0);
			}
			sym = SymbolFactory.createLineSymbol(nlne, new BasicStroke((float) jSliderLineWidth
					.getValue()));
			break;
		case GeometryConstraint.POINT:
		case GeometryConstraint.MULTI_POINT:
			if (jCheckBoxFill.isSelected()){
				bgnd = jLabelFillPreview.getBackground();
				nbgnd = new Color(bgnd.getRed(), bgnd.getGreen(), bgnd.getBlue(),
					255 - jSliderTransparency.getValue());
			}else{
				nbgnd = new Color(bgnd.getRed(), bgnd.getGreen(), bgnd.getBlue(),
						0);
			}

			if (jCheckBoxLine.isSelected()){
				lne = jLabelLinePreview.getBackground();
				nlne = new Color(lne.getRed(), lne.getGreen(), lne.getBlue(),
						255 - jSliderTransparency.getValue());
			}else{
				nlne = new Color(lne.getRed(), lne.getGreen(), lne.getBlue(),
						0);
			}

			sym = SymbolFactory.createCirclePointSymbol(nlne, nbgnd, jSliderVertices.getValue());
			break;
		case GeometryConstraint.POLYGON:
		case GeometryConstraint.MULTI_POLYGON:
		default:
			if (jCheckBoxFill.isSelected()){
				bgnd = jLabelFillPreview.getBackground();
				nbgnd = new Color(bgnd.getRed(), bgnd.getGreen(), bgnd.getBlue(),
					255 - jSliderTransparency.getValue());
			}else{
				nbgnd = new Color(bgnd.getRed(), bgnd.getGreen(), bgnd.getBlue(),
						0);
			}

			if (jCheckBoxLine.isSelected()){
				lne = jLabelLinePreview.getBackground();
				nlne = new Color(lne.getRed(), lne.getGreen(), lne.getBlue(),
						255 - jSliderTransparency.getValue());
			}else{
				nlne = new Color(lne.getRed(), lne.getGreen(), lne.getBlue(),
						0);
			}

			sym = SymbolFactory.createPolygonSymbol(new BasicStroke(
					(float) jSliderLineWidth.getValue()), nlne, nbgnd);

			break;
		}

		int selIdx=jList1.getSelectedIndex();
		if (selIdx!=-1){
			SymbolListDecorator symbolDec = (SymbolListDecorator)jList1.getSelectedValue();
			sym.setName(symbolDec.getSymbol().getName());
			symbolDec.setSymbol(sym);
//			updateLegendValues(sym);
		}

		return sym;
	}

	public Legend getLegend() {
		UniqueSymbolLegend leg = LegendFactory.createUniqueSymbolLegend();

		Symbol sym = getSymbolComposite();

		leg.setSymbol(sym);

		leg.setName(identity);

		return leg;
	}



	public Symbol getSymbolComposite() {
		DefaultListModel mod = (DefaultListModel) jList1.getModel();
		int size = mod.getSize();
		Symbol [] syms = new Symbol[size];
		for (int i=0; i<size; i++){
			SymbolListDecorator dec = (SymbolListDecorator)mod.getElementAt(i);
			syms[i]=dec.getSymbol();
		}
		Symbol comp = SymbolFactory.createSymbolComposite(syms);
		return comp;
	}


	public void setDecoratorListener(LegendListDecorator dec){
		this.dec=dec;
	}

	

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
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
        jLabelSync = new javax.swing.JLabel();
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

        setMinimumSize(new java.awt.Dimension(611, 309));
        setPreferredSize(new java.awt.Dimension(611, 309));

        jPanel2.setMinimumSize(new java.awt.Dimension(70, 100));
        jPanel2.setPreferredSize(new java.awt.Dimension(70, 100));

        jPanel4.setBorder(null);

        jButtonSyncLineWithFill.setText("Sync");
        jButtonSyncLineWithFill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSyncLineWithFillActionPerformed(evt);
            }
        });

        jLabelLinePreview.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButtonLineColorPicker.setText("Select line color");
        jButtonLineColorPicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLineColorPickerActionPerformed(evt);
            }
        });

        jLabelFillPreview.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButtonFillColorPicker.setText("Select fill color");
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

        jLabelSync.setText("Sync line color with fill color:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabelSync, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSyncLineWithFill, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jCheckBoxFill, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCheckBoxLine, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButtonLineColorPicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonFillColorPicker, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelFillPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                            .addComponent(jLabelLinePreview, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jCheckBoxFill)
                        .addComponent(jButtonFillColorPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabelFillPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jCheckBoxLine)
                        .addComponent(jButtonLineColorPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabelLinePreview, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSync)
                    .addComponent(jButtonSyncLineWithFill, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSliderVertices, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                    .addComponent(jSliderTransparency, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                    .addComponent(jSliderLineWidth, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldLine, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                    .addComponent(jTextFieldVertices, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                    .addComponent(jTextFieldTransparency, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSliderLineWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(jTextFieldLine, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSliderTransparency, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldTransparency, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSliderVertices, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jTextFieldVertices)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(canvasPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButtonToCollection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonFromCollection, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonFromCollection)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonToCollection))
                            .addComponent(canvasPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE))))
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonFromCollectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFromCollectionActionPerformed
    	FlowLayoutPreviewWindow coll = new FlowLayoutPreviewWindow();
    	coll.setConstraint(constraint);
    	if (UIFactory.showDialog(coll)){
    		Symbol sym = coll.getSelectedSymbol();
    		//leg.setSymbol(sym);
    		addToSymbolList(sym);
    	}
    	refreshCanvas();
		lookIfWeHaveSymbols();
    }//GEN-LAST:event_jButtonFromCollectionActionPerformed

    private void refreshButtons() {
		int idx = jList1.getSelectedIndex();
		int maximo=jList1.getModel().getSize()-1;
		int minimo=0;

		if (idx==-1){
			jButtonSymbolUp.setEnabled(false);
			jButtonSymbolDown.setEnabled(false);
			jButtonSymbolDel.setEnabled(false);
			jButtonSymbolRename.setEnabled(false);
		}else{
			jButtonSymbolDel.setEnabled(true);
			jButtonSymbolRename.setEnabled(true);
			if (idx==minimo){
				if (idx==maximo)
					jButtonSymbolDown.setEnabled(false);
				else
					jButtonSymbolDown.setEnabled(true);
				jButtonSymbolUp.setEnabled(false);
			}else{
				if (idx==maximo){
					jButtonSymbolUp.setEnabled(true);
					jButtonSymbolDown.setEnabled(false);
				}else{
					jButtonSymbolUp.setEnabled(true);
					jButtonSymbolDown.setEnabled(true);
				}
			}
		}
	}


    private void jButtonSymbolUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSymbolUpActionPerformed
    	DefaultListModel mod = (DefaultListModel)jList1.getModel();
    	int idx=0;
    	idx=jList1.getSelectedIndex();
    	if (idx>0){
    		SymbolListDecorator element=(SymbolListDecorator)mod.get(idx);
    		mod.remove(idx);
    		mod.add(idx-1, element);
    	}
    	jList1.setSelectedIndex(idx-1);
    	refreshCanvas();
    	refreshButtons();
}//GEN-LAST:event_jButtonSymbolUpActionPerformed



    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        DefaultListModel mod = (DefaultListModel) jList1.getModel();
        if ((mod.getSize()>0) && (jList1.getSelectedIndex()!=-1)){
	        SymbolListDecorator dec = (SymbolListDecorator) jList1.getSelectedValue();
	        constraint = canvas.getConstraint(dec.getSymbol());
	        updateLegendValues(dec.getSymbol());
	        lookIfWeHaveSymbols();
    		refreshCanvas();
    		refreshButtons();
        }
    }//GEN-LAST:event_jList1ValueChanged

    private void jButtonSymbolDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSymbolDownActionPerformed
    	DefaultListModel mod = (DefaultListModel)jList1.getModel();
    	int idx=0;
    	idx=jList1.getSelectedIndex();
    	if (idx<mod.size()-1){
    		SymbolListDecorator element=(SymbolListDecorator)mod.get(idx);
    		mod.remove(idx);
    		mod.add(idx+1, element);
    	}
    	jList1.setSelectedIndex(idx+1);
    	refreshCanvas();
    	refreshButtons();
    }//GEN-LAST:event_jButtonSymbolDownActionPerformed

    private void jButtonSymbolAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSymbolAddActionPerformed
    	Symbol sy=null;
    	switch (layerConstraint){
    	case GeometryConstraint.LINESTRING:
    	case GeometryConstraint.MULTI_LINESTRING:
    		sy = SymbolFactory.createLineSymbol(Color.LIGHT_GRAY, new BasicStroke());
    		break;
    	case GeometryConstraint.POINT:
    	case GeometryConstraint.MULTI_POINT:
    		sy = SymbolFactory.createCirclePointSymbol(Color.BLUE, Color.LIGHT_GRAY, 10);
    		break;
    	case GeometryConstraint.POLYGON:
    	case GeometryConstraint.MULTI_POLYGON:
    		sy = SymbolFactory.createPolygonSymbol(new BasicStroke(), Color.BLUE, Color.LIGHT_GRAY);
    		break;
    	case GeometryConstraint.MIXED:
	    	jPanelTypeOfGeometrySelection sel = new jPanelTypeOfGeometrySelection();
	        if (UIFactory.showDialog(sel)){
	        	int constr = sel.getConstraint();
	        	switch (constr){
		        	case GeometryConstraint.LINESTRING:
		        	case GeometryConstraint.MULTI_LINESTRING:
		        		sy = SymbolFactory.createLineSymbol(Color.LIGHT_GRAY, new BasicStroke());
		        		break;
		        	case GeometryConstraint.POINT:
		        	case GeometryConstraint.MULTI_POINT:
		        		sy = SymbolFactory.createCirclePointSymbol(Color.BLUE, Color.LIGHT_GRAY, 10);
		        		break;
		        	case GeometryConstraint.POLYGON:
		        	case GeometryConstraint.MULTI_POLYGON:
		        		sy = SymbolFactory.createPolygonSymbol(new BasicStroke(), Color.BLUE, Color.LIGHT_GRAY);
		        		break;
	        	}
	        	break;
	        }

        }
    	
    	if (sy!=null){
	    	SymbolListDecorator deco = new SymbolListDecorator(sy);
	
	    	((DefaultListModel)jList1.getModel()).addElement(deco);
	
	    	jList1.setSelectedValue(deco, true);
	
	    	updateLegendValues(deco.getSymbol());
	    	lookIfWeHaveSymbols();
			refreshCanvas();
			refreshButtons();
    	}

    }//GEN-LAST:event_jButtonSymbolAddActionPerformed

    private void jButtonSymbolDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSymbolDelActionPerformed
        DefaultListModel mod = (DefaultListModel) jList1.getModel();
        int [] indexes=jList1.getSelectedIndices();
        //for (int i=0; i<indexes.length; i++){
        while (indexes.length>0){
        	mod.removeElementAt(indexes[0]);
        	indexes=jList1.getSelectedIndices();
        }
        if (mod.getSize()>0){
        	jList1.setSelectedIndex(0);
        	updateLegendValues(((SymbolListDecorator)mod.getElementAt(0)).getSymbol());
        }
        refreshCanvas();
		lookIfWeHaveSymbols();
//		refreshButtons();

    }//GEN-LAST:event_jButtonSymbolDelActionPerformed

    private void jButtonSymbolRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSymbolRenameActionPerformed
    	DefaultListModel mod = (DefaultListModel)jList1.getModel();
    	SymbolListDecorator dec=(SymbolListDecorator)jList1.getSelectedValue();
    	int idx=jList1.getSelectedIndex();
    	
    	//String new_name=JOptionPane.showInputDialog("Insert the new name", dec.getLegend().getLegendTypeName());
    	//String new_name=JOptionPane.showInputDialog("Insert the new name", dec.getSymbol().getName());
    	AskValue ask = new AskValue("Insert the new name", "txt is not null", "A name must be specified", dec.getSymbol().getName());
    	String new_name="";
    	if (UIFactory.showDialog(ask)){
    		new_name=ask.getValue();
    	}
    	
    	if (new_name!=null && new_name!=""){
	    	dec.getSymbol().setName(new_name);
	    	mod.remove(idx);
	        mod.add(idx, dec);
	        jList1.setSelectedIndex(idx);

	        updateLegendValues(dec.getSymbol());
	        refreshCanvas();
			lookIfWeHaveSymbols();
//			refreshButtons();
    	}
    }//GEN-LAST:event_jButtonSymbolRenameActionPerformed

    private void jButtonSyncLineWithFillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSyncLineWithFillActionPerformed
    	jLabelLinePreview.setBackground(jLabelFillPreview.getBackground().darker());
        jLabelFillPreview.setOpaque(true);
        refreshCanvas();
		lookIfWeHaveSymbols();
}//GEN-LAST:event_jButtonSyncLineWithFillActionPerformed

    private void jButtonToCollectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonToCollectionActionPerformed
    	Symbolcollection coll = null;
    	FlowLayoutPreviewWindow flow = new FlowLayoutPreviewWindow();
    	try {
			ExtendedWorkspace ew = (ExtendedWorkspace) Services
					.getService("org.orbisgis.ExtendedWorkspace");
			FileInputStream is = new FileInputStream(ew.getFile(FlowLayoutPreviewWindow.SYMBOL_COLLECTION_FILE));
			coll = flow.loadCollection(is);			
			is.close();
		} catch (FileNotFoundException e) {
			System.out.println("Collection not loaded: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Cannot save symbol in collection", "Error", JOptionPane.ERROR_MESSAGE );
			return;
		} catch (JAXBException e) {
			System.out.println("Collection not loaded: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Cannot save symbol in collection", "Error", JOptionPane.ERROR_MESSAGE );
			return;
		} catch (NullPointerException e){
			System.out.println("Collection not loaded: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Cannot save symbol in collection", "Error", JOptionPane.ERROR_MESSAGE );
			return;
		} catch (IOException e) {
			System.out.println("Collection not loaded: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Cannot save symbol in collection", "Error", JOptionPane.ERROR_MESSAGE );
			return;
		}
    	
		if (coll.getCompositeSymbol()==null){
    		ObjectFactory of = new ObjectFactory();
    		coll=of.createSymbolcollection();
    	}
		
        //Object[] values = jList1.getSelectedValues();
		Object[] values = ((DefaultListModel)jList1.getModel()).toArray();
		
        Symbol [] sym = new Symbol [values.length];
        for (int i=0; i<values.length; i++){
        	SymbolListDecorator dec = (SymbolListDecorator)values[i];
        	 sym[i]=dec.getSymbol();
        }
        
        SymbolComposite comp = (SymbolComposite)SymbolFactory.createSymbolComposite( sym );
     	
    	coll.getCompositeSymbol().add(flow.createComposite(comp));
        
        try {
			ExtendedWorkspace ew = (ExtendedWorkspace) Services
					.getService("org.orbisgis.ExtendedWorkspace");
			FileOutputStream os = new FileOutputStream(ew.getFile(FlowLayoutPreviewWindow.SYMBOL_COLLECTION_FILE));
			flow.saveCollection(coll, os);
			os.close();
		} catch (FileNotFoundException e) {
			System.out.println("Collection not saved: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Cannot save symbol in collection", "Error", JOptionPane.ERROR_MESSAGE );
			return;
		} catch (JAXBException e) {
			System.out.println("Collection not saved: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Cannot save symbol in collection", "Error", JOptionPane.ERROR_MESSAGE );
			return;
		} catch (NullPointerException e){
			System.out.println("Collection not saved: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Cannot save symbol in collection", "Error", JOptionPane.ERROR_MESSAGE );
			return;
		} catch (IOException e) {
			System.out.println("Collection not saved: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Cannot save symbol in collection", "Error", JOptionPane.ERROR_MESSAGE );
			return;
		}
		JOptionPane.showMessageDialog(this, "Symbol saved in collection", "Operation succesful", JOptionPane.INFORMATION_MESSAGE);
        
    }//GEN-LAST:event_jButtonToCollectionActionPerformed
    
	private void jCheckBoxLineActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxLineActionPerformed
//		boolean enabled = jCheckBoxLine.isSelected();
//		if (enabled)
//			jLabelLinePreview.setBackground(Color.BLUE);
//		else{
//			Color col = new Color(0,0,0,0);
//			jLabelLinePreview.setBackground(col);
//		}
		jLabelLinePreview.setOpaque(true);
		refreshCanvas();
		lookIfWeHaveSymbols();
	}// GEN-LAST:event_jCheckBoxLineActionPerformed

	private void jCheckBoxFillActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxFillActionPerformed
//		boolean enabled = jCheckBoxFill.isSelected();
//		if (enabled)
//			jLabelFillPreview.setBackground(Color.LIGHT_GRAY);
//		else{
//			Color col = new Color(0,0,0,0);
//			jLabelFillPreview.setBackground(col);
//		}
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

		// jButtonLineColor.setBorder(BorderFactory.createLineBorder(Color.WHITE,
		// 3));
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
		// jButtonFillColor.setBorder(BorderFactory.createLineBorder(Color.WHITE,
		// 3));
	}// GEN-LAST:event_jButtonFillColorPickerActionPerformed

	public Component getComponent() {
		return this;
	}

	public String toString() {
		// return "Simple legend";
		if (identity == null)
			return "Unique Symbol Legend";
		return identity;
	}

	public String getInfoText() {
		// TODO Auto-generated method stub
		return "Set a Unique symbol legend to the selected layer";
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return "Unique symbol legend";
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String id) {
		identity = id;
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
    private javax.swing.JLabel jLabelSync;
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
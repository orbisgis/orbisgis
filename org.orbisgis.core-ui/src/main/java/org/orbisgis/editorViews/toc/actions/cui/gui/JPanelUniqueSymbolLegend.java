/*
 * JPanelSimpleLegend.java
 *
 * Created on 22 de febrero de 2008, 16:33
 */

package org.orbisgis.editorViews.toc.actions.cui.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.gdms.data.types.GeometryConstraint;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.Canvas;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.ColorPicker;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.FlowLayoutPreviewWindow;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.ImageRenderer;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.LegendListDecorator;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.SymbolListDecorator;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.jPanelTypeOfGeometrySelection;
import org.orbisgis.renderer.legend.CircleSymbol;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.LegendFactory;
import org.orbisgis.renderer.legend.LineSymbol;
import org.orbisgis.renderer.legend.PolygonSymbol;
import org.orbisgis.renderer.legend.Symbol;
import org.orbisgis.renderer.legend.SymbolComposite;
import org.orbisgis.renderer.legend.SymbolFactory;
import org.orbisgis.renderer.legend.UniqueSymbolLegend;
import org.sif.UIFactory;
import org.sif.UIPanel;

/**
 *
 * @author david
 */
public class JPanelUniqueSymbolLegend extends javax.swing.JPanel implements
		ILegendPanelUI, UIPanel {

	private String identity = "Unique symbol legend";
	private final String panelType = "Unique symbol legend";
	private int constraint = 0;
	private Canvas canvas = null;
	private UniqueSymbolLegend leg = null;
	private LegendListDecorator dec = null;
	private boolean showCollection=false;

	/** Creates new form JPanelSimpleSimbolLegend */
	public JPanelUniqueSymbolLegend(UniqueSymbolLegend leg, int constraint, boolean showCollection) {
		this.constraint = constraint;
		this.leg = leg;
		this.showCollection=showCollection;
		myInitComponents();
		initComponents();
		jLabelFillPreview.setBackground(Color.LIGHT_GRAY);
		jLabelLinePreview.setBackground(Color.BLUE);
		jList1.setModel(new DefaultListModel());
		disableComponents();
		setCanvas();
		updateSymbolList();
		refreshSelections();
		refreshCanvas();
		lookIfWeHaveSymbols();
	}


	private void lookIfWeHaveSymbols() {
		DefaultListModel mod = (DefaultListModel)jList1.getModel();
		if (mod.size()==0){
			jButtonFillColorPicker.setEnabled(false);
			jButtonFromCollection.setEnabled(false);
			jButtonLineColorPicker.setEnabled(false);
			jButtonSymbolDel.setEnabled(false);
			jButtonSymbolDown.setEnabled(false);
			jButtonSymbolRename.setEnabled(false);
			jButtonSymbolUp.setEnabled(false);
			jCheckBoxFill.setEnabled(false);
			jCheckBoxLine.setEnabled(false);
			jCheckBoxSync.setEnabled(false);
			jCheckBoxVertices.setEnabled(false);
			jComboBoxFill.setEnabled(false);
			jComboBoxLine.setEnabled(false);
			jLabel1.setEnabled(false);
			jLabel2.setEnabled(false);
			jLabel3.setEnabled(false);
			jLabel6.setEnabled(false);
			jLabel5.setEnabled(false);
			jLabelFillPreview.setEnabled(false);
			jLabelLinePreview.setEnabled(false);
		}else{
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

	}

	public JPanelUniqueSymbolLegend(UniqueSymbolLegend leg, int constraint) {
		this(leg, constraint, true);
	}

	private void refreshSelections() {
		if (leg.getName() != null)
			identity = leg.getName();

		disableComponents();

		boolean fillSelected = jCheckBoxFill.isSelected();
		boolean lineSelected = jCheckBoxLine.isSelected();
		boolean vertexSelected = jCheckBoxVertices.isSelected();
		boolean vertexEnabled = jCheckBoxVertices.isEnabled();

		jLabelFillPreview.setEnabled(fillSelected);
		jButtonFillColorPicker.setEnabled(fillSelected);
		jLabel3.setEnabled(fillSelected);

		jLabelLinePreview.setEnabled(lineSelected);
		jButtonLineColorPicker.setEnabled(lineSelected);
		jLabel5.setEnabled(lineSelected);

		jSliderLineWidth.setEnabled(lineSelected && !vertexEnabled);
		jLabel1.setEnabled(lineSelected && !vertexEnabled);
		jTextFieldLine.setEnabled(lineSelected && !vertexEnabled);

		jSliderVertices.setEnabled(vertexSelected);
		jTextFieldVertices.setEnabled(vertexSelected);

		jSliderTransparency.setEnabled(fillSelected || lineSelected);
		jLabel2.setEnabled(fillSelected || lineSelected);
		jTextFieldTransparency.setEnabled(fillSelected || lineSelected);

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

			jCheckBoxVertices.setSelected(true);


		}

		if (sym instanceof LineSymbol) {
			LineSymbol symbol = (LineSymbol) sym;
			jLabelLinePreview.setBackground(symbol.getColor());
			jLabelLinePreview.setOpaque(true);
			jSliderLineWidth.setValue((int) symbol.getSize());
			Color col = symbol.getColor();
			jSliderTransparency.setValue(255 - col.getAlpha());

			jCheckBoxLine.setSelected(true);
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
		jCheckBoxFill.setSelected(false);
		jCheckBoxLine.setSelected(false);
		jCheckBoxSync.setSelected(false);
		jCheckBoxVertices.setSelected(false);
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
		Symbol sym = getSymbol();
		canvas.setLegend(sym, constraint);
		canvasPreview.validate();
		canvasPreview.repaint();

		if (dec!=null){
			dec.setLegend(getLegend());
		}
	}

	private void disableComponents() {
		boolean enabledFill = false, enabledLine = false, enabledVertex = false;

		if (!showCollection){
			jButtonFromCollection.setVisible(false);
		}


		jSliderVertices.setEnabled(enabledVertex);
		jSliderLineWidth.setEnabled(enabledLine);
		jSliderTransparency.setEnabled(enabledFill || enabledLine);
		jTextFieldVertices.setEnabled(enabledVertex);
		jLabelLinePreview.setEnabled(enabledLine);
		jButtonLineColorPicker.setEnabled(enabledLine);
		jComboBoxLine.setEnabled(enabledLine);
		jLabelFillPreview.setEnabled(enabledFill);
		jButtonFillColorPicker.setEnabled(enabledFill);
		jComboBoxFill.setEnabled(enabledFill);

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

		jCheckBoxFill.setEnabled(enabledFill);
		jCheckBoxVertices.setEnabled(enabledVertex);
		jCheckBoxLine.setEnabled(enabledLine);
		// TODO what should i do with this??
		jCheckBoxSync.setEnabled(false);// (enabledFill && enabledLine);

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
			lne = jLabelLinePreview.getBackground();
			nlne = new Color(lne.getRed(), lne.getGreen(), lne.getBlue(),
					255 - jSliderTransparency.getValue());
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

	public void myInitComponents() {

		BufferedImage im = new BufferedImage(40, 8, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = im.createGraphics();
		gr.setColor(Color.red);
		float[] flotantes = { new Float(2.0), new Float(3.0), new Float(4.0) };
		gr.setStroke(new BasicStroke(new Float(2.0).floatValue(), 2, 2,
				new Float(2.0).floatValue(), flotantes, new Float(3.0)
						.floatValue()));
		gr.drawLine(2, 2, 36, 2);
		gr.drawImage(im, null, 0, 0);

		// BufferedImage im2 = new BufferedImage(40, 8,
		// BufferedImage.TYPE_INT_ARGB);
		// Graphics2D gr2 = im2.createGraphics();
		// gr2.setColor(Color.blue);
		// gr2.drawLine(2, 2, 36, 2 );
		// gr2.drawImage(im2, null, 0,0);
		//
		// BufferedImage im3 = new BufferedImage(40, 8,
		// BufferedImage.TYPE_INT_ARGB);
		// Graphics2D gr3 = im3.createGraphics();
		// gr3.setColor(Color.black);
		// gr3.drawLine(2, 2, 36, 2 );
		// gr3.drawImage(im3, null, 0,0);
		//
		// BufferedImage im4 = new BufferedImage(40, 8,
		// BufferedImage.TYPE_INT_ARGB);
		// Graphics2D gr4 = im4.createGraphics();
		// gr4.setColor(Color.green);
		// gr4.drawLine(2, 2, 36, 2 );
		// gr4.drawImage(im4, null, 0,0);
		BufferedImage[] imgs = { im };

		jComboBoxFill = new JComboBox(imgs);
		jComboBoxLine = new JComboBox(imgs);

		jComboBoxFill.setRenderer(new ImageRenderer());
		jComboBoxLine.setRenderer(new ImageRenderer());

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButtonLineColorPicker = new javax.swing.JButton();
        jButtonFillColorPicker = new javax.swing.JButton();
        jComboBoxFill = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jComboBoxLine = new javax.swing.JComboBox();
        jCheckBoxSync = new javax.swing.JCheckBox();
        jCheckBoxFill = new javax.swing.JCheckBox();
        jCheckBoxLine = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jSliderLineWidth = new javax.swing.JSlider();
        jTextFieldLine = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jSliderTransparency = new javax.swing.JSlider();
        jTextFieldTransparency = new javax.swing.JTextField();
        jCheckBoxVertices = new javax.swing.JCheckBox();
        jSliderVertices = new javax.swing.JSlider();
        jTextFieldVertices = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        canvasPreview = new javax.swing.JPanel();
        jLabelFillPreview = new javax.swing.JLabel();
        jLabelLinePreview = new javax.swing.JLabel();
        jButtonFromCollection = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jToolBar1 = new javax.swing.JToolBar();
        jButtonSymbolUp = new javax.swing.JButton();
        jButtonSymbolDown = new javax.swing.JButton();
        jButtonSymbolAdd = new javax.swing.JButton();
        jButtonSymbolDel = new javax.swing.JButton();
        jButtonSymbolRename = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(750, 400));
        setPreferredSize(new java.awt.Dimension(750, 400));

        jButtonLineColorPicker.setText("Select line color");
        jButtonLineColorPicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLineColorPickerActionPerformed(evt);
            }
        });

        jButtonFillColorPicker.setText("Select fill color");
        jButtonFillColorPicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFillColorPickerActionPerformed(evt);
            }
        });

        jLabel3.setText("Fill pattern:");

        jLabel5.setText("Line pattern: ");

        jCheckBoxSync.setText("Sync line color with fill color");
        jCheckBoxSync.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jCheckBoxFill.setText("Fill:");
        jCheckBoxFill.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxFill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFillActionPerformed(evt);
            }
        });

        jCheckBoxLine.setText("Line:");
        jCheckBoxLine.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxLineActionPerformed(evt);
            }
        });

        jLabel1.setText("Line width: ");

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

        jLabel2.setText("Transparency: ");

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

        jCheckBoxVertices.setText("Symbol size");
        jCheckBoxVertices.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxVertices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxVerticesActionPerformed(evt);
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

        jTextFieldVertices.setText("0");
        jTextFieldVertices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldVerticesActionPerformed(evt);
            }
        });

        jLabel6.setText("Preview: ");

        canvasPreview.setBorder(null);
        canvasPreview.setMinimumSize(new java.awt.Dimension(126, 70));

        javax.swing.GroupLayout canvasPreviewLayout = new javax.swing.GroupLayout(canvasPreview);
        canvasPreview.setLayout(canvasPreviewLayout);
        canvasPreviewLayout.setHorizontalGroup(
            canvasPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 233, Short.MAX_VALUE)
        );
        canvasPreviewLayout.setVerticalGroup(
            canvasPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 70, Short.MAX_VALUE)
        );

        jLabelFillPreview.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelLinePreview.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jCheckBoxSync)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                .addGap(363, 363, 363))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel3))
                    .addComponent(jCheckBoxFill)
                    .addComponent(jCheckBoxLine)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jCheckBoxVertices, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(canvasPreview, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSliderVertices, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                    .addComponent(jSliderTransparency, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                    .addComponent(jComboBoxLine, javax.swing.GroupLayout.Alignment.LEADING, 0, 233, Short.MAX_VALUE)
                    .addComponent(jComboBoxFill, javax.swing.GroupLayout.Alignment.LEADING, 0, 233, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButtonLineColorPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelLinePreview, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButtonFillColorPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelFillPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE))
                    .addComponent(jSliderLineWidth, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldVertices, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                    .addComponent(jTextFieldLine, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                    .addComponent(jTextFieldTransparency, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jCheckBoxFill)
                                .addComponent(jButtonFillColorPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabelFillPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jComboBoxFill, javax.swing.GroupLayout.PREFERRED_SIZE, 18, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jCheckBoxLine)
                                .addComponent(jButtonLineColorPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabelLinePreview, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBoxLine, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxSync)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jSliderLineWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(112, 112, 112)
                        .addComponent(jTextFieldLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jSliderTransparency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldTransparency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBoxVertices)
                    .addComponent(jSliderVertices, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldVertices, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(canvasPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jButtonFromCollection.setText("Load from collection");
        jButtonFromCollection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFromCollectionActionPerformed(evt);
            }
        });

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                    .addComponent(jButtonFromCollection, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonFromCollection))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonFromCollectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFromCollectionActionPerformed
    	FlowLayoutPreviewWindow coll = new FlowLayoutPreviewWindow();
    	//coll.setConstraint(constraint);
    	if (UIFactory.showDialog(coll)){
    		Symbol sym = coll.getSelectedSymbol();
    		leg.setSymbol(sym);
    		updateSymbolList();
    		lookIfWeHaveSymbols();
    		refreshCanvas();
    	}
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
    	refreshButtons();
    }//GEN-LAST:event_jButtonSymbolDownActionPerformed

    private void jButtonSymbolAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSymbolAddActionPerformed
        jPanelTypeOfGeometrySelection sel = new jPanelTypeOfGeometrySelection();
        if (UIFactory.showDialog(sel)){
        	int constr = sel.getConstraint();
        	Symbol sy=null;
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
        mod.removeElementAt(jList1.getSelectedIndex());
        if (mod.getSize()>0){
        	jList1.setSelectedIndex(0);
        	updateLegendValues(((SymbolListDecorator)mod.getElementAt(0)).getSymbol());
        }
        lookIfWeHaveSymbols();
		refreshCanvas();
		refreshButtons();

    }//GEN-LAST:event_jButtonSymbolDelActionPerformed

    private void jButtonSymbolRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSymbolRenameActionPerformed
    	DefaultListModel mod = (DefaultListModel)jList1.getModel();
    	SymbolListDecorator dec=(SymbolListDecorator)jList1.getSelectedValue();
    	int idx=jList1.getSelectedIndex();

    	//String new_name=JOptionPane.showInputDialog("Insert the new name", dec.getLegend().getLegendTypeName());
    	String new_name=JOptionPane.showInputDialog("Insert the new name", dec.getSymbol().getName());
    	if (new_name!=null){
	    	while (new_name.equals("")){
	    		new_name=JOptionPane.showInputDialog("Sorry, you cannot set a void name", dec.getSymbol().getName());
	    	}

	    	dec.getSymbol().setName(new_name);
	    	mod.remove(idx);
	        mod.add(idx, dec);
	        jList1.setSelectedIndex(idx);

	        updateLegendValues(dec.getSymbol());
	        lookIfWeHaveSymbols();
			refreshCanvas();
			refreshButtons();
    	}
    }//GEN-LAST:event_jButtonSymbolRenameActionPerformed

	private void jCheckBoxVerticesActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxVerticesActionPerformed
		boolean enabled = jCheckBoxVertices.isSelected();
		jSliderVertices.setEnabled(enabled);
		jTextFieldVertices.setEnabled(enabled);
		lookIfWeHaveSymbols();
		refreshCanvas();
	}// GEN-LAST:event_jCheckBoxVerticesActionPerformed

	private void jCheckBoxLineActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxLineActionPerformed
		boolean enabled = jCheckBoxLine.isSelected();
		jLabelLinePreview.setEnabled(enabled);
		if (enabled)
			jLabelLinePreview.setBackground(Color.BLUE);
		else
			jLabelLinePreview.setBackground(jButtonLineColorPicker.getBackground());
		jLabelLinePreview.setOpaque(true);
		jButtonLineColorPicker.setEnabled(enabled);
		jComboBoxLine.setEnabled(enabled);
		jSliderLineWidth.setEnabled(enabled);
		jSliderTransparency.setEnabled(enabled || jCheckBoxFill.isSelected());
		lookIfWeHaveSymbols();
		refreshCanvas();
	}// GEN-LAST:event_jCheckBoxLineActionPerformed

	private void jCheckBoxFillActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxFillActionPerformed
		boolean enabled = jCheckBoxFill.isSelected();
		jLabelFillPreview.setEnabled(enabled);
		if (enabled)
			jLabelFillPreview.setBackground(Color.LIGHT_GRAY);
		else
			jLabelFillPreview.setBackground(jButtonFillColorPicker.getBackground());
		jLabelFillPreview.setOpaque(true);
		jButtonFillColorPicker.setEnabled(enabled);
		jComboBoxFill.setEnabled(enabled);
		jSliderTransparency.setEnabled(enabled || jCheckBoxLine.isSelected());
		lookIfWeHaveSymbols();
		refreshCanvas();
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
		lookIfWeHaveSymbols();
		refreshCanvas();
	}// GEN-LAST:event_jTextFieldTransparencyActionPerformed

	private void jTextFieldLineActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextFieldLineActionPerformed
		int value = Integer.parseInt(jTextFieldLine.getText());
		jSliderLineWidth.setValue(value);
		lookIfWeHaveSymbols();
		refreshCanvas();
	}// GEN-LAST:event_jTextFieldLineActionPerformed

	private void jSliderVerticesStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_jSliderVerticesStateChanged
		int value = jSliderVertices.getValue();
		jTextFieldVertices.setText(String.valueOf(value));
		refreshCanvas();
	}// GEN-LAST:event_jSliderVerticesStateChanged

	private void jSliderTransparencyStateChanged(
			javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_jSliderTransparencyStateChanged
		int value = jSliderTransparency.getValue();
		jTextFieldTransparency.setText(String.valueOf(value));
		lookIfWeHaveSymbols();
		refreshCanvas();
	}// GEN-LAST:event_jSliderTransparencyStateChanged

	private void jSliderLineWidthStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_jSliderLineWidthStateChanged
		int value = jSliderLineWidth.getValue();
		jTextFieldLine.setText(String.valueOf(value));
		lookIfWeHaveSymbols();
		refreshCanvas();
	}// GEN-LAST:event_jSliderLineWidthStateChanged

	private void jButtonLineColorPickerActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonLineColorPickerActionPerformed
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			jLabelLinePreview.setBackground(color);
			jLabelLinePreview.setOpaque(true);
			lookIfWeHaveSymbols();
			refreshCanvas();
		}

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
			lookIfWeHaveSymbols();
			refreshCanvas();
		}
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
    private javax.swing.JCheckBox jCheckBoxFill;
    private javax.swing.JCheckBox jCheckBoxLine;
    private javax.swing.JCheckBox jCheckBoxSync;
    private javax.swing.JCheckBox jCheckBoxVertices;
    private javax.swing.JComboBox jComboBoxFill;
    private javax.swing.JComboBox jComboBoxLine;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelFillPreview;
    private javax.swing.JLabel jLabelLinePreview;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
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
		switch (constraint) {
		case GeometryConstraint.LINESTRING:
			if (!jCheckBoxLine.isSelected())
				return "You have not selected a line color";
			break;
		case GeometryConstraint.POINT:
			if (!jCheckBoxVertices.isSelected())
				return "You have not selected a symbol size";
			break;
		}
		return null;
	}

	public String validateInput() {
		// TODO Auto-generated method stub

		return null;
	}

}
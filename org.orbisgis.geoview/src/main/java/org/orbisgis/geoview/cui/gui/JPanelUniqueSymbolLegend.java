/*
 * JPanelSimpleLegend.java
 *
 * Created on 22 de febrero de 2008, 16:33
 */

package org.orbisgis.geoview.cui.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComboBox;

import org.gdms.data.types.GeometryConstraint;
import org.orbisgis.geoview.cui.gui.widgets.Canvas;
import org.orbisgis.geoview.cui.gui.widgets.ColorPicker;
import org.orbisgis.geoview.cui.gui.widgets.ImageRenderer;
import org.orbisgis.geoview.renderer.legend.CircleSymbol;
import org.orbisgis.geoview.renderer.legend.Legend;
import org.orbisgis.geoview.renderer.legend.LegendFactory;
import org.orbisgis.geoview.renderer.legend.LineSymbol;
import org.orbisgis.geoview.renderer.legend.PolygonSymbol;
import org.orbisgis.geoview.renderer.legend.Symbol;
import org.orbisgis.geoview.renderer.legend.SymbolFactory;
import org.orbisgis.geoview.renderer.legend.UniqueSymbolLegend;
import org.sif.UIFactory;

/**
 *
 * @author david
 */
public class JPanelUniqueSymbolLegend extends javax.swing.JPanel implements
		ILegendPanelUI {

	private String identity = "Unique symbol legend";
	private final String panelType = "Unique symbol legend";
	private int constraint = 0;
	private Canvas canvas = null;
	private UniqueSymbolLegend leg = null;

	/** Creates new form JPanelSimpleSimbolLegend */
	public JPanelUniqueSymbolLegend(UniqueSymbolLegend leg, int constraint) {
		this.constraint = constraint;
		this.leg = leg;
		myInitComponents();
		initComponents();
		disableComponents();
		setCanvas();
		updateLegendValues();
		refreshSelections();
		refreshCanvas();
	}

	private void refreshSelections() {
		if (leg.getName() != null)
			identity = leg.getName();

		boolean fillSelected = jCheckBoxFill.isSelected();
		boolean lineSelected = jCheckBoxLine.isSelected();
		boolean vertexSelected = jCheckBoxVertices.isSelected();
		boolean vertexEnabled = jCheckBoxVertices.isEnabled();

		jLabelFillPreview.setEnabled(fillSelected);
		jButtonFillColorPicker.setEnabled(fillSelected);

		jLabelLinePreview.setEnabled(lineSelected);
		jButtonLineColorPicker.setEnabled(lineSelected);

		jSliderLineWidth.setEnabled(lineSelected && !vertexEnabled);

		jSliderVertices.setEnabled(vertexSelected);

		jSliderTransparency.setEnabled(fillSelected);

	}

	private void updateLegendValues() {
		Symbol sym = leg.getSymbol();

		if (sym instanceof CircleSymbol) {
			CircleSymbol symbol = (CircleSymbol) sym;

			Color fillColor = symbol.getFillColor();
			jLabelFillPreview.setBackground(new Color(fillColor.getRGB()));
			jLabelFillPreview.setOpaque(true);
			jLabelLinePreview.setBackground(symbol.getOutlineColor());
			jLabelLinePreview.setOpaque(true);
			jSliderVertices.setValue(symbol.getSize());
			jSliderTransparency.setValue(255 - fillColor.getAlpha());

			jCheckBoxFill.setSelected(true);
			jCheckBoxLine.setSelected(true);
			jCheckBoxVertices.setSelected(true);

		}

		if (sym instanceof LineSymbol) {
			LineSymbol symbol = (LineSymbol) sym;
			jLabelLinePreview.setBackground(symbol.getColor());
			jLabelLinePreview.setOpaque(true);
			jSliderLineWidth.setValue((int) symbol.getSize());

			jCheckBoxLine.setSelected(true);
		}

		if (sym instanceof PolygonSymbol) {
			PolygonSymbol symbol = (PolygonSymbol) sym;
			Color fillColor = symbol.getFillColor();

			if (fillColor != null) {
				jLabelFillPreview.setBackground(new Color(fillColor.getRGB()));
				jLabelFillPreview.setOpaque(true);
				jSliderTransparency.setValue(255 - fillColor.getAlpha());
				jCheckBoxFill.setSelected(true);
			}
			jLabelLinePreview.setBackground(symbol.getOutlineColor());
			jLabelLinePreview.setOpaque(true);
			jSliderLineWidth.setValue((int) ((BasicStroke) symbol.getStroke())
					.getLineWidth());

			jCheckBoxLine.setSelected(true);
		}

	}

	public JPanelUniqueSymbolLegend(int constraint) {
		this(LegendFactory.createUniqueSymbolLegend(), constraint);
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

	}

	private void disableComponents() {
		boolean enabledFill = false, enabledLine = false, enabledVertex = false;

		jSliderVertices.setEnabled(enabledVertex);
		jSliderLineWidth.setEnabled(enabledLine);
		jSliderTransparency.setEnabled(enabledFill);
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
		Color bgnd = Color.white;
		Color nbgnd = Color.white;

		switch (constraint) {
		case GeometryConstraint.LINESTRING:
		case GeometryConstraint.MULTI_LINESTRING:
			sym = SymbolFactory.createLineSymbol(jLabelLinePreview
					.getBackground(), new BasicStroke((float) jSliderLineWidth
					.getValue()));
			break;
		case GeometryConstraint.POINT:
		case GeometryConstraint.MULTI_POINT:
			bgnd = jLabelFillPreview.getBackground();
			nbgnd = new Color(bgnd.getRed(), bgnd.getGreen(), bgnd.getBlue(),
					255 - jSliderTransparency.getValue());
			sym = SymbolFactory.createCirclePointSymbol(jLabelLinePreview
					.getBackground(), nbgnd, jSliderVertices.getValue());
			break;
		case GeometryConstraint.POLYGON:
		case GeometryConstraint.MULTI_POLYGON:
		default:
			if (jCheckBoxFill.isSelected()) {
				bgnd = jLabelFillPreview.getBackground();
				nbgnd = new Color(bgnd.getRed(), bgnd.getGreen(), bgnd
						.getBlue(), 255 - jSliderTransparency.getValue());
				sym = SymbolFactory.createPolygonSymbol(new BasicStroke(
						(float) jSliderLineWidth.getValue()), jLabelLinePreview
						.getBackground(), nbgnd);
			} else {
				sym = SymbolFactory.createPolygonSymbol(jLabelLinePreview
						.getBackground());
			}
			break;
		}

		return sym;
	}

	public Legend getLegend() {
		UniqueSymbolLegend leg = LegendFactory.createUniqueSymbolLegend();

		Symbol sym = getSymbol();

		leg.setSymbol(sym);

		leg.setName(identity);

		return leg;
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
	// ">//GEN-BEGIN:initComponents
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

		jButtonLineColorPicker.setText("Select line color");
		jButtonLineColorPicker
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jButtonLineColorPickerActionPerformed(evt);
					}
				});

		jButtonFillColorPicker.setText("Select fill color");
		jButtonFillColorPicker
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jButtonFillColorPickerActionPerformed(evt);
					}
				});

		jLabel3.setText("Fill pattern:");

		jLabel5.setText("Line pattern: ");

		jCheckBoxSync.setText("Sync line color with fill color");
		jCheckBoxSync.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				0, 0, 0));
		jCheckBoxSync.setMargin(new java.awt.Insets(0, 0, 0, 0));

		jCheckBoxFill.setText("Fill:");
		jCheckBoxFill.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				0, 0, 0));
		jCheckBoxFill.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jCheckBoxFill.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCheckBoxFillActionPerformed(evt);
			}
		});

		jCheckBoxLine.setText("Line:");
		jCheckBoxLine.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				0, 0, 0));
		jCheckBoxLine.setMargin(new java.awt.Insets(0, 0, 0, 0));
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
		jSliderLineWidth
				.addChangeListener(new javax.swing.event.ChangeListener() {
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
		jSliderTransparency
				.addChangeListener(new javax.swing.event.ChangeListener() {
					public void stateChanged(javax.swing.event.ChangeEvent evt) {
						jSliderTransparencyStateChanged(evt);
					}
				});

		jTextFieldTransparency.setText("0");
		jTextFieldTransparency
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jTextFieldTransparencyActionPerformed(evt);
					}
				});

		jCheckBoxVertices.setText("Symbol size");
		jCheckBoxVertices.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		jCheckBoxVertices.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jCheckBoxVertices
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jCheckBoxVerticesActionPerformed(evt);
					}
				});

		jSliderVertices.setMaximum(20);
		jSliderVertices.setMinorTickSpacing(1);
		jSliderVertices.setValue(0);
		jSliderVertices
				.addChangeListener(new javax.swing.event.ChangeListener() {
					public void stateChanged(javax.swing.event.ChangeEvent evt) {
						jSliderVerticesStateChanged(evt);
					}
				});

		jTextFieldVertices.setText("0");
		jTextFieldVertices
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jTextFieldVerticesActionPerformed(evt);
					}
				});

		jLabel6.setText("Preview: ");

		canvasPreview.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		javax.swing.GroupLayout canvasPreviewLayout = new javax.swing.GroupLayout(
				canvasPreview);
		canvasPreview.setLayout(canvasPreviewLayout);
		canvasPreviewLayout.setHorizontalGroup(canvasPreviewLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 154, Short.MAX_VALUE));
		canvasPreviewLayout.setVerticalGroup(canvasPreviewLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 40, Short.MAX_VALUE));

		jLabelFillPreview.setBorder(javax.swing.BorderFactory
				.createEtchedBorder());

		jLabelLinePreview.setBorder(javax.swing.BorderFactory
				.createEtchedBorder());

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout.createSequentialGroup()
										.addComponent(jCheckBoxSync)
										.addContainerGap())
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												jLabel6,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE).addGap(363,
												363, 363))
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGap(
																				17,
																				17,
																				17)
																		.addComponent(
																				jLabel3))
														.addComponent(
																jCheckBoxFill)
														.addComponent(
																jCheckBoxLine)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				jLabel5,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				89,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				jLabel1,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				97,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				jLabel2,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				102,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addComponent(
																jCheckBoxVertices,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																101,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																canvasPreview,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jSliderVertices,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								233,
																								Short.MAX_VALUE)
																						.addComponent(
																								jSliderTransparency,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								233,
																								Short.MAX_VALUE)
																						.addComponent(
																								jComboBoxLine,
																								0,
																								233,
																								Short.MAX_VALUE)
																						.addComponent(
																								jComboBoxFill,
																								0,
																								233,
																								Short.MAX_VALUE)
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addComponent(
																												jButtonLineColorPicker,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												132,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												jLabelLinePreview,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												87,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addComponent(
																												jButtonFillColorPicker,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												126,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												jLabelFillPreview,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												95,
																												Short.MAX_VALUE))
																						.addComponent(
																								jSliderLineWidth,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								233,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jTextFieldVertices,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								47,
																								Short.MAX_VALUE)
																						.addComponent(
																								jTextFieldTransparency,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								47,
																								Short.MAX_VALUE)
																						.addComponent(
																								jTextFieldLine,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								47,
																								Short.MAX_VALUE))))
										.addContainerGap()));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								jPanel1Layout
																										.createParallelGroup(
																												javax.swing.GroupLayout.Alignment.BASELINE)
																										.addComponent(
																												jCheckBoxFill)
																										.addComponent(
																												jButtonFillColorPicker,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												17,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addComponent(
																								jLabelFillPreview,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								15,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jLabel3)
																						.addComponent(
																								jComboBoxFill,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								18,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								jPanel1Layout
																										.createParallelGroup(
																												javax.swing.GroupLayout.Alignment.BASELINE)
																										.addComponent(
																												jCheckBoxLine)
																										.addComponent(
																												jButtonLineColorPicker,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												17,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addComponent(
																								jLabelLinePreview,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								15,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jComboBoxLine,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								18,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								jLabel5,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jCheckBoxSync)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING)
																						.addComponent(
																								jLabel1)
																						.addComponent(
																								jSliderLineWidth,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE)))
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGap(
																				112,
																				112,
																				112)
																		.addComponent(
																				jTextFieldLine,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel2)
														.addComponent(
																jSliderTransparency,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																jTextFieldTransparency,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																jCheckBoxVertices)
														.addComponent(
																jSliderVertices,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																19,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																jTextFieldVertices,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel6)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												canvasPreview,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addContainerGap().addComponent(
						jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addContainerGap().addComponent(
						jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
	}// </editor-fold>//GEN-END:initComponents

	private void jCheckBoxVerticesActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxVerticesActionPerformed
		boolean enabled = jCheckBoxVertices.isSelected();
		jSliderVertices.setEnabled(enabled);
		jTextFieldVertices.setEnabled(enabled);
		refreshSelections();
		refreshCanvas();
	}// GEN-LAST:event_jCheckBoxVerticesActionPerformed

	private void jCheckBoxLineActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxLineActionPerformed
		boolean enabled = jCheckBoxLine.isSelected();
		jLabelLinePreview.setEnabled(enabled);
		jButtonLineColorPicker.setEnabled(enabled);
		jComboBoxLine.setEnabled(enabled);
		jSliderLineWidth.setEnabled(enabled);
		refreshSelections();
		refreshCanvas();
	}// GEN-LAST:event_jCheckBoxLineActionPerformed

	private void jCheckBoxFillActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBoxFillActionPerformed
		boolean enabled = jCheckBoxFill.isSelected();
		jLabelFillPreview.setEnabled(enabled);
		jButtonFillColorPicker.setEnabled(enabled);
		jComboBoxFill.setEnabled(enabled);
		jSliderTransparency.setEnabled(enabled);
		refreshSelections();
		refreshCanvas();
	}// GEN-LAST:event_jCheckBoxFillActionPerformed

	private void jTextFieldVerticesActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextFieldVerticesActionPerformed
		int value = Integer.parseInt(jTextFieldVertices.getText());
		jSliderVertices.setValue(value);
		refreshSelections();
		refreshCanvas();
	}// GEN-LAST:event_jTextFieldVerticesActionPerformed

	private void jTextFieldTransparencyActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextFieldTransparencyActionPerformed
		int value = Integer.parseInt(jTextFieldTransparency.getText());
		jSliderTransparency.setValue(value);
		refreshSelections();
		refreshCanvas();
	}// GEN-LAST:event_jTextFieldTransparencyActionPerformed

	private void jTextFieldLineActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextFieldLineActionPerformed
		int value = Integer.parseInt(jTextFieldLine.getText());
		jSliderLineWidth.setValue(value);
		refreshSelections();
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
		refreshSelections();
		refreshCanvas();
	}// GEN-LAST:event_jSliderTransparencyStateChanged

	private void jSliderLineWidthStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_jSliderLineWidthStateChanged
		int value = jSliderLineWidth.getValue();
		jTextFieldLine.setText(String.valueOf(value));
		refreshSelections();
		refreshCanvas();
	}// GEN-LAST:event_jSliderLineWidthStateChanged

	private void jButtonLineColorPickerActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonLineColorPickerActionPerformed
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			jLabelLinePreview.setBackground(color);
			jLabelFillPreview.setOpaque(true);
			refreshSelections();
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
			refreshSelections();
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
	private javax.swing.JButton jButtonLineColorPicker;
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
	private javax.swing.JPanel jPanel1;
	private javax.swing.JSlider jSliderLineWidth;
	private javax.swing.JSlider jSliderTransparency;
	private javax.swing.JSlider jSliderVertices;
	private javax.swing.JTextField jTextFieldLine;
	private javax.swing.JTextField jTextFieldTransparency;
	private javax.swing.JTextField jTextFieldVertices;
	// End of variables declaration//GEN-END:variables

}

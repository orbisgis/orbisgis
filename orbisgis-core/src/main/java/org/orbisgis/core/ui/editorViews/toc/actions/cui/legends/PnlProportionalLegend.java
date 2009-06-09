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
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.RenderException;
import org.orbisgis.core.renderer.legend.carto.LegendFactory;
import org.orbisgis.core.renderer.legend.carto.ProportionalLegend;
import org.orbisgis.core.renderer.symbol.StandardPointSymbol;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.CompositeSymbolFilter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.ConstraintSymbolFilter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendContext;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.SymbolBuilder;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.SymbolEditionValidation;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.SymbolFilter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.Canvas;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;
import org.orbisgis.sif.UIFactory;

import com.vividsolutions.jts.geom.Envelope;

public class PnlProportionalLegend extends JPanel implements ILegendPanel {

	private ProportionalLegend legend;
	private LegendContext legendContext;
	private JComboBox cmbField;
	private JTextField txtMaxSize;
	private Canvas canvas;
	private JComboBox cmbMethod;
	private JButton btnPreview;

	private BufferedImage previewImage;
	private JComponent legendPreview;
	private JCheckBox chkMapUnits;

	private void init() {

		JPanel confPanel = new JPanel();
		JPanel lblPanel = new JPanel();
		CRFlowLayout flowLayout = new CRFlowLayout();
		flowLayout.setAlignment(CRFlowLayout.RIGHT);
		flowLayout.setVgap(12);
		lblPanel.setLayout(flowLayout);
		lblPanel.add(new JLabel("Field:"));
		lblPanel.add(new CarriageReturn());
		lblPanel.add(new JLabel("Proportional method:"));
		lblPanel.add(new CarriageReturn());
		lblPanel.add(new JLabel("Maximum size:"));
		lblPanel.add(new CarriageReturn());
		lblPanel.add(new JLabel("Symbol:"));

		confPanel.add(lblPanel);

		JPanel inputPanel = new JPanel();
		CRFlowLayout flowLayout2 = new CRFlowLayout();
		flowLayout2.setAlignment(CRFlowLayout.LEFT);
		inputPanel.setLayout(flowLayout2);

		cmbField = new JComboBox();
		cmbField.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				legend.setClassificationField((String) cmbField
						.getSelectedItem());
				syncWithLegend();
			}

		});
		JLabel spaceLabel = new JLabel("");
		spaceLabel.setPreferredSize(new Dimension(2, 22));
		inputPanel.add(spaceLabel);
		inputPanel.add(new CarriageReturn());
		inputPanel.add(cmbField);
		inputPanel.add(new CarriageReturn());

		cmbMethod = new JComboBox(new String[] { "Linear", "Logarithmic",
				"Square" });
		cmbMethod.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					switch (cmbMethod.getSelectedIndex()) {
					case 0:
						legend.setMethod(ProportionalLegend.LINEAR);
						break;
					case 1:
						legend.setMethod(ProportionalLegend.LOGARITHMIC);
						break;
					case 2:
						legend.setMethod(ProportionalLegend.SQUARE);
						break;
					}
				} catch (DriverException e1) {
					Services.getErrorManager().error("Cannot set the method",
							e1);
				}

				refreshPreviewButton();
			}

		});
		inputPanel.add(cmbMethod);
		inputPanel.add(new CarriageReturn());
		txtMaxSize = new JTextField(5);
		txtMaxSize.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				insertUpdate(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					legend.setMaxSize(Integer.parseInt(txtMaxSize.getText()));
				} catch (NumberFormatException e1) {
				}
				refreshPreviewButton();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				insertUpdate(e);
			}

		});
		inputPanel.add(txtMaxSize);
		inputPanel.add(new CarriageReturn());
		canvas = new Canvas();
		canvas.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				editSymbol();
				refreshPreviewButton();
			}

		});
		canvas.setPreferredSize(new Dimension(50, 50));
		inputPanel.add(canvas);
		chkMapUnits = new JCheckBox("Map units");
		chkMapUnits.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				StandardPointSymbol sampleSymbol = legend.getSampleSymbol();
				sampleSymbol.setMapUnits(chkMapUnits.isSelected());
				legend.setSampleSymbol(sampleSymbol);
				refreshPreviewButton();
			}
		});
		inputPanel.add(chkMapUnits);

		confPanel.add(inputPanel);
		this.add(confPanel);

		JPanel pnlSymbol = new JPanel();
		pnlSymbol.setLayout(new CRFlowLayout());
		btnPreview = new JButton("Preview");
		btnPreview.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					legend.preprocess(legendContext.getLayer().getDataSource());

					// Transform max to pixels
					int maxSize = Integer.parseInt(txtMaxSize.getText());
					int pixelHeight;
					if (legend.getSampleSymbol().isMapUnits()) {
						MapTransform mt = legendContext
								.getCurrentMapTransform();
						Envelope env = new Envelope(0, maxSize, 0, maxSize);
						Envelope pixelEnv = mt.toPixel(env);
						pixelHeight = (int) pixelEnv.getHeight();
					} else {
						pixelHeight = (int) maxSize;

					}

					// draw the image
					BufferedImage dummy = new BufferedImage(1, 1,
							BufferedImage.TYPE_INT_ARGB);
					Graphics2D dummyGraphics = dummy.createGraphics();
					int[] previewSize = legend.getImageSize(dummyGraphics,
							pixelHeight);
					previewImage = new BufferedImage(Math.min(previewSize[0],
							500), Math.min(previewSize[1], 500),
							BufferedImage.TYPE_INT_ARGB);
					legend
							.drawImage(previewImage.createGraphics(),
									pixelHeight);
				} catch (RenderException e1) {
					previewImage = new BufferedImage(100, 30,
							BufferedImage.TYPE_INT_ARGB);
					previewImage.createGraphics().drawString(
							"Cannot generate preview", 0, 0);
				}

				legendPreview.repaint();
			}

		});
		pnlSymbol.add(btnPreview);
		pnlSymbol.add(new CarriageReturn());
		JPanel imgPanel = new JPanel();
		imgPanel.setBorder(BorderFactory.createTitledBorder("Symbol"));
		imgPanel.setPreferredSize(new Dimension(150, 230));
		legendPreview = new JComponent() {

			@Override
			protected void paintComponent(Graphics g) {
				if (previewImage != null) {
					g.drawImage(previewImage, 0, 0, null);
				}
			}

		};
		legendPreview.setBorder(BorderFactory.createLineBorder(Color.red));
		imgPanel.setLayout(new BorderLayout());
		imgPanel.add(legendPreview, BorderLayout.CENTER);
		pnlSymbol.add(imgPanel);
		this.add(pnlSymbol, BorderLayout.CENTER);
	}

	private void refreshPreviewButton() {
		btnPreview.setEnabled(validateInput() == null);
	}

	private void editSymbol() {
		SymbolBuilder editor = new SymbolBuilder(false, legendContext,
				getSymbolFilter());
		editor.setValidation(new SymbolEditionValidation() {

			public String isValid(Symbol symbol) {
				if (symbol.acceptsChildren() && (symbol.getSymbolCount() != 1)) {
					return "One and only one symbol is accepted";
				}

				return null;
			}

		});
		editor.setSymbol(canvas.getSymbol());
		if (UIFactory.showDialog(editor)) {
			legend.setSampleSymbol((StandardPointSymbol) editor
					.getSymbolComposite().getSymbol(0));
			syncWithLegend();
		}
	}

	private SymbolFilter getSymbolFilter() {
		return new CompositeSymbolFilter(new ConstraintSymbolFilter(
				new GeometryConstraint(GeometryConstraint.POINT),
				new GeometryConstraint(GeometryConstraint.MULTI_POINT),
				new GeometryConstraint(GeometryConstraint.POLYGON),
				new GeometryConstraint(GeometryConstraint.MULTI_POLYGON)),
				new SymbolFilter() {

					@Override
					public boolean accept(Symbol symbol) {
						return symbol instanceof StandardPointSymbol;
					}
				});
	}

	public boolean acceptsGeometryType(int geometryType) {
		return (geometryType == ILegendPanel.POLYGON)
				|| (geometryType == ILegendPanel.POINT)
				|| (geometryType == ILegendPanel.ALL);
	}

	public Component getComponent() {
		return this;
	}

	public Legend getLegend() {
		return legend;
	}

	public ILegendPanel newInstance() {
		return new PnlProportionalLegend();
	}

	public void setLegend(Legend legend) {
		this.legend = (ProportionalLegend) legend;
		syncWithLegend();
	}

	private void syncWithLegend() {
		try {
			SpatialDataSourceDecorator sds = legendContext.getLayer()
					.getDataSource();
			Metadata m = sds.getMetadata();
			ArrayList<String> fieldNames = new ArrayList<String>();

			for (int i = 0; i < m.getFieldCount(); i++) {
				int fieldType = m.getFieldType(i).getTypeCode();
				if (fieldType == Type.BYTE || fieldType == Type.SHORT
						|| fieldType == Type.INT || fieldType == Type.LONG
						|| fieldType == Type.FLOAT || fieldType == Type.DOUBLE) {
					fieldNames.add(m.getFieldName(i));
				}
			}
			cmbField.setModel(new DefaultComboBoxModel(fieldNames
					.toArray(new String[0])));
			cmbField.setSelectedItem(legend.getClassificationField());

			// max size
			txtMaxSize.setText("" + legend.getMaxSize());

			// map units
			chkMapUnits.setSelected(legend.getSampleSymbol().isMapUnits());

			// symbol
			canvas.setSymbol(legend.getSampleSymbol());

			// method
			switch (legend.getMethod()) {
			case ProportionalLegend.LINEAR:
				cmbMethod.setSelectedIndex(0);
				break;
			case ProportionalLegend.LOGARITHMIC:
				cmbMethod.setSelectedIndex(1);
				break;
			case ProportionalLegend.SQUARE:
				cmbMethod.setSelectedIndex(2);
				break;
			default:
				throw new RuntimeException("Unknown method");
			}

			// preview
			// int[] imgSize = legend.getImageSize(new BufferedImage(10, 10,
			// BufferedImage.TYPE_INT_ARGB).createGraphics());
			// if ((imgSize[0] != 0) && (imgSize[1] != 0)) {
			// BufferedImage img = new BufferedImage(imgSize[0], imgSize[1],
			// BufferedImage.TYPE_INT_ARGB);
			// legend.drawImage(img.createGraphics());
			// lblPreview.setIcon(new ImageIcon(img));
			// }
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot access layer fields", e);
		}

	}

	public void initialize(LegendContext lc) {
		this.legendContext = lc;
		legend = LegendFactory.createProportionalLegend();
		legend.setName(legend.getLegendTypeName());
		init();
	}

	public String validateInput() {
		if (legend.getClassificationField() == null) {
			return "A field must be selected";
		}

		String maxSize = txtMaxSize.getText();
		try {
			Integer.parseInt(maxSize);
		} catch (NumberFormatException e) {
			return "Max size must be an integer";
		}

		return null;
	}

}

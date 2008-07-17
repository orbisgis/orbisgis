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
package org.orbisgis.editorViews.toc.actions.cui.extensions;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.actions.cui.CompositeSymbolFilter;
import org.orbisgis.editorViews.toc.actions.cui.ConstraintSymbolFilter;
import org.orbisgis.editorViews.toc.actions.cui.EditableSymbolFilter;
import org.orbisgis.editorViews.toc.actions.cui.SymbolEditor;
import org.orbisgis.editorViews.toc.actions.cui.SymbolFilter;
import org.orbisgis.editorViews.toc.actions.cui.components.Canvas;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.carto.LegendFactory;
import org.orbisgis.renderer.legend.carto.ProportionalLegend;
import org.orbisgis.renderer.symbol.EditablePointSymbol;
import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;
import org.sif.UIFactory;

public class PnlProportionalLegend extends JPanel implements ILegendPanelUI {

	private ProportionalLegend legend;
	private LegendContext legendContext;
	private JComboBox cmbField;
	private JTextField txtMinArea;
	private Canvas canvas;
	private JComboBox cmbMethod;

	public PnlProportionalLegend(LegendContext legendContext) {
		legend = LegendFactory.createProportionalLegend();
		legend.setName(getLegendTypeName());
		this.legendContext = legendContext;
		init();
	}

	private void init() {

		this.setLayout(new CRFlowLayout());

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
		lblPanel.add(new JLabel("Maximum area:"));

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
		inputPanel.add(cmbField);
		inputPanel.add(new CarriageReturn());

		cmbMethod = new JComboBox(new String[] { "Linear", "logarithmic",
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
			}

		});
		inputPanel.add(cmbMethod);
		inputPanel.add(new CarriageReturn());
		txtMinArea = new JTextField(5);
		txtMinArea.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					legend.setMinSymbolArea(Integer.parseInt(txtMinArea
							.getText()));
				} catch (NumberFormatException e1) {
				}
			}

		});
		inputPanel.add(txtMinArea);
		confPanel.add(inputPanel);
		this.add(confPanel);
		this.add(new CarriageReturn());

		JPanel pnlSymbol = new JPanel();
		pnlSymbol.setLayout(new CRFlowLayout());
		pnlSymbol.setPreferredSize(new Dimension(350, 200));
		pnlSymbol.setBorder(BorderFactory.createTitledBorder("Symbol"));
		pnlSymbol.add(new JLabel("Select symbol:"));
		canvas = new Canvas();
		canvas.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				editSymbol();
			}

		});
		canvas.setPreferredSize(new Dimension(50, 50));
		pnlSymbol.add(canvas);
		this.add(pnlSymbol, BorderLayout.CENTER);

	}

	private void editSymbol() {
		SymbolEditor editor = new SymbolEditor(false, legendContext,
				getSymbolFilter());
		editor.setSymbol(canvas.getSymbol());
		if (UIFactory.showDialog(editor)) {
			legend.setSampleSymbol((EditablePointSymbol) editor
					.getSymbolComposite().getSymbol(0));
			syncWithLegend();
		}
	}

	private SymbolFilter getSymbolFilter() {
		return new CompositeSymbolFilter(new EditableSymbolFilter(),
				new ConstraintSymbolFilter(new GeometryConstraint(
						GeometryConstraint.POINT)));
	}

	public boolean acceptsGeometryType(int geometryType) {
		return (geometryType == ILegendPanelUI.POLYGON)
				|| (geometryType == ILegendPanelUI.POINT);
	}

	public Component getComponent() {
		return this;
	}

	public Legend getLegend() {
		return legend;
	}

	public String getLegendTypeName() {
		return ProportionalLegend.NAME;
	}

	public ILegendPanelUI newInstance(LegendContext legendContext) {
		return new PnlProportionalLegend(legendContext);
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

			// min area
			txtMinArea.setText("" + legend.getMinSymbolArea());

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
//			int[] imgSize = legend.getImageSize(new BufferedImage(10, 10,
//					BufferedImage.TYPE_INT_ARGB).createGraphics());
//			if ((imgSize[0] != 0) && (imgSize[1] != 0)) {
//				BufferedImage img = new BufferedImage(imgSize[0], imgSize[1],
//						BufferedImage.TYPE_INT_ARGB);
//				legend.drawImage(img.createGraphics());
//				lblPreview.setIcon(new ImageIcon(img));
//			}
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot access layer fields", e);
		}

	}

	public void setLegendContext(LegendContext lc) {
		this.legendContext = lc;
	}

	public String validateInput() {
		if (legend.getClassificationField() == null) {
			return "A field must be selected";
		}

		String minArea = txtMinArea.getText();
		try {
			Integer.parseInt(minArea);
		} catch (NumberFormatException e) {
			return "min area must be an integer";
		}

		return null;
	}

}

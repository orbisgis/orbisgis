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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.batik.ext.swing.GridBagConstants;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.carto.LabelLegend;
import org.orbisgis.core.renderer.legend.carto.LegendFactory;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendContext;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;

public class PnlLabelLegend extends JPanel implements ILegendPanel {

	private LabelLegend legend;
	private LegendContext legendContext;
	private JComboBox cmbField;
	private JTextField txtSymbolSize;
	private JCheckBox chkIntelligentLabelPlacing;
	private boolean syncing = false;

	private void init() {

		GridBagLayout mgr = new GridBagLayout();
		this.setLayout(mgr);

		cmbField = new JComboBox();
		cmbField.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				legend.setClassificationField((String) cmbField
						.getSelectedItem());
				syncWithLegend();
			}

		});
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstants.SOUTHEAST;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(8, 5, 8, 5);
		this.add(new JLabel("Field:"), c);

		c.anchor = GridBagConstants.SOUTHWEST;
		c.gridx = 1;
		c.gridy = 0;
		this.add(cmbField, c);

		c.anchor = GridBagConstants.NORTHEAST;
		c.gridx = 0;
		c.gridy = 1;
		this.add(new JLabel("Symbol size:"), c);

		txtSymbolSize = new JTextField(2);
		c.anchor = GridBagConstants.NORTHWEST;
		c.gridx = 1;
		c.gridy = 1;
		this.add(txtSymbolSize, c);

		chkIntelligentLabelPlacing = new JCheckBox(
				"Intelligent label placing (slow)");
		chkIntelligentLabelPlacing.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				legend.setSmartPlacing(chkIntelligentLabelPlacing.isSelected());
				syncWithLegend();
			}
		});
		c.anchor = GridBagConstants.NORTH;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		this.add(chkIntelligentLabelPlacing, c);
	}

	public boolean acceptsGeometryType(int geometryType) {
		return true;
	}

	public Component getComponent() {
		return this;
	}

	public Legend getLegend() {
		try {
			int fontSize = Integer.parseInt(txtSymbolSize.getText());
			legend.setFontSize(fontSize);
		} catch (NumberFormatException e) {
		}
		return legend;
	}

	public ILegendPanel newInstance() {
		return new PnlLabelLegend();
	}

	public void setLegend(Legend legend) {
		this.legend = (LabelLegend) legend;
		syncWithLegend();
	}

	private void syncWithLegend() {
		if (!syncing) {
			syncing = true;
			try {
				SpatialDataSourceDecorator sds = legendContext.getLayer()
						.getDataSource();
				Metadata m = sds.getMetadata();
				ArrayList<String> fieldNames = new ArrayList<String>();

				HashSet<String> names = GeometryProperties
						.getPropertiesName(legendContext.getGeometryType());

				if (names != null) {
					for (Iterator iterator = names.iterator(); iterator
							.hasNext();) {
						String name = (String) iterator.next();
						fieldNames.add(name);
					}

				}

				for (int i = 0; i < m.getFieldCount(); i++) {
					int fieldType = m.getFieldType(i).getTypeCode();
					if ((fieldType != Type.RASTER)
							&& (fieldType != Type.GEOMETRY)) {
						fieldNames.add(m.getFieldName(i));
					}
				}
				cmbField.setModel(new DefaultComboBoxModel(fieldNames
						.toArray(new String[0])));
				cmbField.setSelectedItem(legend.getClassificationField());

				// symbol size
				txtSymbolSize.setText("" + legend.getFontSize());

				chkIntelligentLabelPlacing.setSelected(legend.isSmartPlacing());

			} catch (DriverException e) {
				Services.getErrorManager().error("Cannot access layer fields",
						e);
			}
			syncing = false;
		}
	}

	public void initialize(LegendContext lc) {
		this.legendContext = lc;
		legend = LegendFactory.createLabelLegend();
		legend.setName(legend.getLegendTypeName());
		init();
	}

	public String validateInput() {
		if (legend.getClassificationField() == null) {
			return "A field must be selected";
		}

		String minArea = txtSymbolSize.getText();
		try {
			Integer.parseInt(minArea);
		} catch (NumberFormatException e) {
			return "min area must be an integer";
		}

		return null;
	}

}

/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.rasterProcessing.action.defaultStyle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.grap.io.GeoreferencingException;
import org.grap.lut.LutDisplay;
import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.orbisgis.pluginManager.PluginManager;
import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;

public class RasterDefaultStylePanel extends JPanel {
	private JComboBox jComboBox;
	private JLabel jLabel;
	private JSlider opacitySlider;
	private ColorModel currentColorModel;

	public RasterDefaultStylePanel(final GeoRaster geoRaster) {
		final Vector<String> colorModelNames = new Vector<String>(Arrays
				.asList(LutGenerator.getDefaultLUTS()));
		colorModelNames.add(0, "current");

		try {
			final LutDisplay lutDisplay = new LutDisplay(geoRaster
					.getColorModel());

			jLabel = new JLabel();
			jLabel.setIcon(new ImageIcon(lutDisplay.getImagePlus().getImage()));

			jComboBox = new JComboBox(colorModelNames);
			jComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final String lutName = (String) jComboBox.getSelectedItem();
					final ColorModel cm = "current".equals(lutName) ? currentColorModel
							: LutGenerator.colorModel(lutName);
					final LutDisplay lutDisplay = new LutDisplay(cm);
					jLabel.setIcon(new ImageIcon(lutDisplay.getImagePlus()
							.getImage()));
				}
			});

			opacitySlider = new JSlider(0, 100);
			opacitySlider.setBorder(BorderFactory
					.createTitledBorder("Opacity (in %)"));
			opacitySlider.setMajorTickSpacing(25);
			opacitySlider.setMinorTickSpacing(5);
			opacitySlider.setPaintTicks(true);
			opacitySlider.setPaintLabels(true);

			final CRFlowLayout flowLayout = new CRFlowLayout();
			flowLayout.setAlignment(CRFlowLayout.CENTER);
			setLayout(flowLayout);
			add(jComboBox);
			add(new CarriageReturn());
			add(jLabel);
			add(new CarriageReturn());
			add(opacitySlider);
			add(new CarriageReturn());
		} catch (IOException e) {
			PluginManager.error(
					"Unable to retrieve the current layer color model !", e);
		} catch (GeoreferencingException e) {
			PluginManager.error(
					"Unable to retrieve the current layer color model !", e);
		}
	}

	public String getJComboBoxSelection() {
		return (String) jComboBox.getSelectedItem();
	}

	public String getJTextFieldEntry() {
		return jLabel.getText();
	}

	public void setLut(String fieldValue) {
		jComboBox.setSelectedItem(fieldValue);
	}

	public String getOpacity() {
		return new Integer(opacitySlider.getValue()).toString();
	}

	public void setOpacity(String fieldValue) {
		opacitySlider.setValue(new Integer(fieldValue));
	}
}
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
package org.orbisgis.geoprocessing.editorViews.toc.actions.defaultStyle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ColorModel;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.grap.lut.LutDisplay;
import org.grap.lut.LutGenerator;
import org.orbisgis.core.renderer.legend.RasterLegend;
import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;

public class RasterDefaultStylePanel extends JPanel {
	private static final String DEFAULT_COLOR_MODEL = "default";
	private JComboBox cmbColorModels;
	private JLabel jLabel;
	private JSlider opacitySlider;
	private ColorModel selectedColorModel;
	private ColorModel defaultColorModel;

	public RasterDefaultStylePanel(final RasterLegend legend,
			ColorModel defaultCM) {
		this.defaultColorModel = defaultCM;

		final Vector<String> colorModelNames = new Vector<String>(Arrays
				.asList(LutGenerator.getDefaultLUTS()));
		colorModelNames.add(0, DEFAULT_COLOR_MODEL);

		selectedColorModel = null;
		final LutDisplay lutDisplay = new LutDisplay(legend.getColorModel());

		jLabel = new JLabel();
		jLabel.setIcon(new ImageIcon(lutDisplay.getImagePlus().getImage()));

		cmbColorModels = new JComboBox(colorModelNames);
		cmbColorModels.setSelectedItem(null);
		cmbColorModels.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final String lutName = (String) cmbColorModels
						.getSelectedItem();
				ColorModel cm = null;
				if (DEFAULT_COLOR_MODEL.equals(lutName)) {
					cm = defaultColorModel;
				} else {
					cm = LutGenerator.colorModel(lutName, true);
				}
				final LutDisplay lutDisplay = new LutDisplay(cm);
				jLabel.setIcon(new ImageIcon(lutDisplay.getImagePlus()
						.getImage()));
				selectedColorModel = cm;
			}
		});

		opacitySlider = new JSlider(0, 100, 100);
		opacitySlider.setBorder(BorderFactory
				.createTitledBorder("Opacity (in %)"));
		opacitySlider.setMajorTickSpacing(25);
		opacitySlider.setMinorTickSpacing(5);
		opacitySlider.setPaintTicks(true);
		opacitySlider.setPaintLabels(true);
		opacitySlider.setValue((int) (100 * legend.getOpacity()));

		final CRFlowLayout flowLayout = new CRFlowLayout();
		flowLayout.setAlignment(CRFlowLayout.CENTER);
		setLayout(flowLayout);
		add(cmbColorModels);
		add(new CarriageReturn());
		add(jLabel);
		add(new CarriageReturn());
		add(opacitySlider);
		add(new CarriageReturn());
	}

	public ColorModel getColorModel() {
		return selectedColorModel;
	}

	public float getOpacity() {
		return (float) (opacitySlider.getValue() / 100.0);
	}
}